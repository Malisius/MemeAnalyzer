package com.peteroertel;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * A simple extension of {@link BundledPost} that includes some
 * extra details specific to Reddit, such as the score, gild status,
 * post id, and other such shenanigans.
 *  
 * @author pooter
 *
 */
public class RedditPost extends BundledPost{

	private int score;
	private int createdUTC;
	private String title;
	private String id;
	private int upvoteRatio;
	
	public boolean gatherData() throws IOException {
		URL jsonUrl = new URL(url + ".json");
		HttpURLConnection conn = (HttpURLConnection) jsonUrl.openConnection();
		conn.connect();
		String response = ImageSearcher.getResponseString(conn);
		//System.out.println(response);
		System.out.println(jsonUrl);
		return true;
	}
	
	public RedditPost(String url, String location) throws IOException {
		super(url, location);
		gatherData();
	}

}
