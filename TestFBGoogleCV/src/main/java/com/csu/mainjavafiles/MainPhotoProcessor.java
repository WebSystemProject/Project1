package com.csu.mainjavafiles;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.csu.mainjavafiles.analytics.GoogleAnalytics;
import com.csu.mainjavafiles.model.Datum_;
import com.csu.mainjavafiles.model.FbAlbums;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;

import com.google.protobuf.ByteString;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;


@Controller
@RequestMapping("/")
public class MainPhotoProcessor {

	@GetMapping("/")
	public String index(Model model) {
		GoogleAnalytics.publishAnalytics("Login","Login");
		return "index";
	}

	@PostMapping("/home")
	public String home(Model model, HttpServletRequest request, HttpServletResponse response) {
		System.out.println(request.getParameter("access_token"));
		GoogleAnalytics.publishAnalytics("Login","Login Success");
		model.addAttribute("access_token", request.getParameter("access_token"));
		model.addAttribute("user_name", request.getParameter("user_name"));
		model.addAttribute("userID", request.getParameter("userID"));
		return "home";
	}

	@GetMapping(value = "/images")
	public String getAllImages(Model model, @RequestParam String access_token, String userID){
		GoogleAnalytics.publishAnalytics("search","Search images");
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		fetchPhotoFbToGoogleDataStore(access_token, datastore, userID);
		Response imageDataResponse =  getPhotosFromDataStore(datastore, userID);
		model.addAttribute("imageDataResponse", imageDataResponse);

		return "jsonview";
	}

	// check if photo already present in Google Data store before upload from FB to Data store
	private Entity isPhotoPresent(DatastoreService datastore, String fbPhotoId) {
		Entity isPresent = datastore.prepare(new Query("User").setFilter(new FilterPredicate("fb_image_id", FilterOperator.EQUAL, fbPhotoId))).asSingleEntity();
		return isPresent;
	}

    // download image from data store for preview by FB-Google CV app
	public static byte[] downloadPhoto(URL url) throws Exception {
		try (InputStream in = url.openStream()) {
			byte[] bytes = IOUtils.toByteArray(in);            
			return bytes;
		}
	}

	//fetch photo from FB
	private FbAlbums fetchPhotosFromFb(String url) throws IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		FbAlbums albums = null;
		try {
			HttpGet request = new HttpGet(url);
			CloseableHttpResponse response = httpClient.execute(request);
			try {
				System.out.println(response.getStatusLine().getStatusCode()); 
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String result = EntityUtils.toString(entity);
					System.out.println(result);
					ObjectMapper mapper = new ObjectMapper();
					albums = mapper.readValue(result, FbAlbums.class);	
				}

			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpClient.close();
		}
		return albums;
	}

    // get photos for user from Google Store based on userID
	private Response getPhotosFromDataStore(DatastoreService datastore,  String userID ) {
		Query query = new Query("User");
		try {
			query.setFilter(new FilterPredicate("userID", FilterOperator.EQUAL, userID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Entity> result = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		Set<String> lables = new TreeSet<>();
		List<com.csu.mainjavafiles.Image> images = new ArrayList<>();
		if(null != result) {
			result.forEach(user -> {
				List<String> lablesFromStore = (List<String>) user.getProperty("lables");
				lables.addAll(lablesFromStore);
				com.csu.mainjavafiles.Image image = new com.csu.mainjavafiles.Image();
				image.setUrl(user.getProperty("image_url").toString());
				image.setLabels(lablesFromStore);
				images.add(image);
			});
		}
		Response response = new Response();
		response.setImages(images);
		response.setLables(lables);
		return response;
	}

    // add photo to Google data store
	private Entity addPhotoToDataStore(List<EntityAnnotation> Labels, Datum_ photo, DatastoreService datastore, String userID) {
		//check if GoogleCV match >= 0.96 accuracy
		List<String> lables = Labels.stream().filter(label -> label.getScore()  >= 0.96)
				.map(EntityAnnotation::getDescription).collect(Collectors.toList());

		if(null != lables && !lables.isEmpty()) {
			Entity user = new Entity("User");
			user.setProperty("userID", userID);
			user.setProperty("fb_image_id", photo.getId());
			user.setProperty("image_url", photo.getPicture());
			user.setProperty("lables", lables);
			datastore.put(user);
			return user;
		}
		return null;
	}

    // upload FB photo to Google Data Store
	private void fetchPhotoFbToGoogleDataStore(String access_token, DatastoreService datastore, String userID) {
		try {
			String rootUrl = "https://graph.facebook.com/v10.0/me/albums?fields=photos%7Bcreated_time%2Cid%2Cpicture%7D%2Cname&access_token=";
	
			String url = rootUrl  + access_token;
			int count = 0;
			while(StringUtils.isNotBlank(url) && count <= 5) {
				FbAlbums albums = fetchPhotosFromFb(url);
				
				if(null != albums && !albums.getData().isEmpty()) {
					count++;
					albums.getData().forEach(album -> {
						if(null !=  album.getPhotos() && null != album.getPhotos().getData() && !album.getPhotos().getData().isEmpty()) {
							album.getPhotos().getData().forEach( photo -> {

								Entity user = isPhotoPresent(datastore, photo.getId());
								if(null == user) {
									List<EntityAnnotation> imageLabels = getImageLabels(photo.getPicture());
									if(null != imageLabels) {
										user = addPhotoToDataStore(imageLabels, photo, datastore, userID);
									}
								}
							});
						}
					});
					url =  null != albums.getPaging() ? albums.getPaging().getNext() : null;	
				}else {
					url = null;
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    // get Image labels from Google CV
	private List<EntityAnnotation> getImageLabels(String imageUrl) {
		try {
			byte[]  imgBytes = downloadPhoto(new URL(imageUrl));
			Image image = Image.newBuilder().setContent(ByteString.copyFrom(imgBytes)).build();

			Feature feature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
			AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
			List<AnnotateImageRequest> requests = new ArrayList<>();
			requests.add(request);

			ImageAnnotatorClient client = ImageAnnotatorClient.create();
			BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
			client.close();
			List<AnnotateImageResponse> imageResponses = batchResponse.getResponsesList();
			AnnotateImageResponse imageResponse = imageResponses.get(0);
			if (imageResponse.hasError()) {
				return null;
			}
			return imageResponse.getLabelAnnotationsList();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
