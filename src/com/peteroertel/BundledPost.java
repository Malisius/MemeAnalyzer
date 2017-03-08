package com.peteroertel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A container for, at its core, an image and the url at which
 * it was found. Not particularly useful by itself until given
 * the context of one of the extending classes.
 * 
 * @author pooter
 *
 */
public class BundledPost {
	/**
	 * The url of the image file contained in this post
	 */
	protected String url;
	/**
	 * The url of the page where we found the image
	 */
	protected String location;
	/**
	 * Google's best guess as to what the image is
	 */
	protected String bestGuess;
	
	/**
	 * Does a reverse Google Image search for images similar to
	 * the one found at {@link url}.
	 * 
	 * @return A {@link List} of {@link BundledPost}s containing
	 * similar images.
	 * @throws IOException
	 */
	public List<BundledPost> findSimilar() throws IOException {
		List<BundledPost> similar = new ArrayList<BundledPost>();
		String search;
		
		search = ImageSearcher.reverseSearch(this.url);
		similar = ImageSearcher.parseIntoSimilar(search);
		
		return similar;
	}
	
	/**
	 * Uses a google reverse image search to guess the content
	 * of this post, and stores it in {@link this.bestGuess}.
	 * @return True if succecsful, false otherwise.
	 * @throws IOException 
	 */
	public boolean guessContent() throws IOException {
		String response = ImageSearcher.reverseSearch(this.url);
		this.bestGuess = ImageSearcher.getBestGuess(response);
		return true;
	}
	

	@Override
	public String toString() {
		return "BundledPost [url=" + url + ", location=" + location + ", bestGuess=" + bestGuess + "]";
	}

	/**
	 * Creates a new basic post with the given url and location.
	 * @param url
	 * @param location
	 * @param guess If true, will immediately run bestGuess, which
	 * can get pretty resource intensive if done too much.
	 * @throws IOException if guess is true and this.guessContent fails.
	 */
	public BundledPost(String url, String location, boolean guess) throws IOException {
		super();
		this.url = url;
		this.location = location;
		if(guess) guessContent();
	}
	public BundledPost(String url, String location) {
		super();
		this.url = url;
		this.location = location;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
}
