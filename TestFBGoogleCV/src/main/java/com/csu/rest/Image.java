package com.csu.rest;

import java.util.ArrayList;
import java.util.List;

public class Image {
	String url = "";
	
	List<String> labels = new ArrayList<String>();



	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}}