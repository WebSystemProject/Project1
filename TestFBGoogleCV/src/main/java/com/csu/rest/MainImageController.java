package com.csu.rest;

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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.csu.rest.analytics.GoogleAnalytics;
import com.csu.rest.model.Datum_;
import com.csu.rest.model.FbAlbums;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;

@Controller
@RequestMapping("/")

public class MainImageController {

	@GetMapping("/")
	public String index(Model model) {
		GoogleAnalytics.publishAnalytics("Login","Login");
		return "index";
	}

	@PostMapping("/home")
	public String home(Model m, HttpServletRequest request, HttpServletResponse response) {
		
		GoogleAnalytics.publishAnalytics("Login","Login Success");
		m.addAttribute("access_token", request.getParameter("access_token"));
		m.addAttribute("user_name", request.getParameter("user_name"));
		m.addAttribute("user_id", request.getParameter("user_id"));
		return "home";
	}

	@GetMapping(value = "/images")
	
	
		public String getAllImages(Model m, @RequestParam String access_token, String user_id){
		GoogleAnalytics.publishAnalytics("search","Search images");
		
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		 FBphotosToDataStore(access_token, ds, user_id);


		
		 ImageDataResponse imageDataResponse =  getImagesFromStore(ds, user_id);
		m.addAttribute("imageDataResponse", imageDataResponse);

		return "jsonview";
	}
	

	private void FBphotosToDataStore(String access_token, DatastoreService ds, String user_id) {
		try {
			int limit = 5; 
			
			String UrlFirstPart = "https://graph.facebook.com/v10.0/me/albums?fields=photos%7Bcreated_time%2Cid%2Cpicture%7D%2Cname&access_token=";
			
			String url = UrlFirstPart+ access_token;

			
			int count = 0;
			while(StringUtils.isNotBlank(url) && count <= 5) {
				
				FbAlbums albums = fetchPhotosFromFacebook(url);
				
				if(null != albums && !albums.getData().isEmpty()) {
					count++;
					albums.getData().forEach(album -> {
						if(null !=  album.getPhotos() && null != album.getPhotos().getData() && !album.getPhotos().getData().isEmpty()) {
							album.getPhotos().getData().forEach( photo -> {

								
								Entity user = findIfAlreadyPresent(ds, photo.getId());
								if(null == user) {
									
									List<EntityAnnotation> imageLabels = getImageLabels(photo.getPicture());
									System.out.println(null != imageLabels);
									
									if(null != imageLabels) {
										user = saveToDataStore(imageLabels, photo, ds, user_id);
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


	
	private Entity findIfAlreadyPresent(DatastoreService datastore, String fbPhotoId) {
		Query q =
				new Query("User")
				//new Query("User_Data")
				.setFilter(new FilterPredicate("fb_image_id", FilterOperator.EQUAL, fbPhotoId));
		PreparedQuery pq = datastore.prepare(q);
		Entity result = pq.asSingleEntity();
		return result;
	}


	
		private ImageDataResponse getImagesFromStore(DatastoreService datastore, String user_id ) {



		Query query =

				new Query("User");
				


		
		try {
			
			
			
			Filter userFilter = new FilterPredicate("user_id", FilterOperator.EQUAL, user_id);

			Filter filter = CompositeFilterOperator.and(userFilter);

			query.setFilter(filter);

		} catch (Exception e) {
			e.printStackTrace();
		}


		PreparedQuery pq = datastore.prepare(query);
		List<Entity> results = pq.asList(FetchOptions.Builder.withDefaults());

		Set<String> lables = new TreeSet<>();
		List<com.csu.rest.Image> images = new ArrayList<>();

		if(null != results) {
			results.forEach(user -> {
				List<String> lablesFromStore = (List<String>) user.getProperty("lables");
				lables.addAll(lablesFromStore);
				com.csu.rest.Image image = new com.csu.rest.Image();
				image.setUrl(user.getProperty("image_url").toString());
				image.setLabels(lablesFromStore);
				images.add(image);
			});
		}


		ImageDataResponse imageDataResponse = new ImageDataResponse();

		imageDataResponse.setImages(images);
		imageDataResponse.setLables(lables);


		return imageDataResponse;
	}


	public static byte[] downloadFile(URL url) throws Exception {
		try (InputStream in = url.openStream()) {
			byte[] bytes = IOUtils.toByteArray(in);            
			return bytes;
		}
	}


	
	private Entity saveToDataStore(List<EntityAnnotation> imageLabels, Datum_ photo, DatastoreService datastore, String user_id) {

		System.out.println("image labels:"+imageLabels);
		
		List<String> lables = imageLabels.stream().filter(label -> label.getScore() * 100 >= 96.5)
				.map(EntityAnnotation::getDescription).collect(Collectors.toList());

		
		
		if(null != lables && !lables.isEmpty()) {

			Entity user = new Entity("User");
			

			
			user.setProperty("user_id", user_id);
			user.setProperty("fb_image_id", photo.getId());
			user.setProperty("image_url", photo.getPicture());
			user.setProperty("lables", lables);

			datastore.put(user);

			return user;

		}
		return null;
	}

	private List<EntityAnnotation> getImageLabels(String imageUrl) {
		try {
			
			byte[]  imgBytes = downloadFile(new URL(imageUrl));  //Download photo from FB
			
			ByteString byteString = ByteString.copyFrom(imgBytes);
			Image image = Image.newBuilder().setContent(byteString).build();

			Feature feature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
			AnnotateImageRequest request =
					AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
			List<AnnotateImageRequest> requests = new ArrayList<>();
			requests.add(request);

			ImageAnnotatorClient client = ImageAnnotatorClient.create();
			BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
			client.close();
			List<AnnotateImageResponse> imageResponses = batchResponse.getResponsesList();
			AnnotateImageResponse imageResponse = imageResponses.get(0);

			if (imageResponse.hasError()) {
				System.err.println("Error getting image labels: " + imageResponse.getError().getMessage());
				return null;
			}

			return imageResponse.getLabelAnnotationsList();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private FbAlbums fetchPhotosFromFacebook(String url) throws IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		FbAlbums album = null;
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
					album = mapper.readValue(result, FbAlbums.class);	
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

		return album;
	}

}
