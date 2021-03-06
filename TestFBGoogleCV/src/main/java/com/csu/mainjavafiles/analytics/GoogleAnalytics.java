package com.csu.mainjavafiles.analytics;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.http.client.utils.URIBuilder;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;


public class GoogleAnalytics {

	public static void publishAnalytics(String action, String type)  {
	    String trackingId = "UA-195449031-1";
		URIBuilder builder = new URIBuilder();
		builder
		.setScheme("http")
		.setHost("www.google-analytics.com")
		.setPath("/collect")
		.addParameter("v", "1")
		.addParameter("tid", trackingId) 
		.addParameter("cid", "555")
		.addParameter("t", type)
		.addParameter("ec", "FB Serverside")
		.addParameter("ea", action); 
		URI uri = null;
		try {
			uri = builder.build();
		} catch (URISyntaxException e) {
		}
		URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
		try {
			URL url = uri.toURL();
			fetcher.fetch(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}