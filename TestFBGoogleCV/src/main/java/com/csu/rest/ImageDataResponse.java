package com.csu.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ImageDataResponse {
	
	Set<String> lables = new TreeSet<>();

	List<Image> images = new ArrayList<Image>();

	public Set<String> getLables() {
		return lables;
	}

	public void setLables(Set<String> lables) {
		this.lables = lables;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}
	
	
}