package com.csu.rest.analytics;

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
		.addParameter("v", "1") // API Version.
		.addParameter("tid", trackingId) // Tracking ID / Property ID.
		// Anonymous Client Identifier. Ideally, this should be a UUID that
		// is associated with particular user, device, or browser instance.
		.addParameter("cid", "555")
		.addParameter("t", type) // Event hit type.
		.addParameter("ec", "FB Serverside") // Event category.
		.addParameter("ea", action); // Event action.
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}