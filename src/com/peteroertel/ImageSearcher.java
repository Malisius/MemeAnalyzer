package com.peteroertel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.CharStreams;
import com.google.common.net.HttpHeaders;

public class ImageSearcher {
	private static final String GOOGLE_IMG_SEARCH_PREFIX = "https://images.google.com/searchbyimage?image_url=";
	private static final String	USER_AGENT_HEADER_VALUE	= "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36";
	private static final Pattern IMAGE_URL_PATTERN = Pattern.compile("\\\"ou\\\":\\\"(http.+?)\\\"");
	private static final Pattern IMAGE_REF_PATTERN = Pattern.compile("\\\"ru\\\":\\\"(http.+?)\\\"");
	private static final Pattern RESULT_PAGE_URL_PATTERN = Pattern.compile("href=\"(\\/search\\?[^\\\"]*?tbs=simg:[^,]+?&amp;.+?)\"");
	
	/**
	 * Search for image copies with different size using Google search engine.
	 * Found images can contain small differences in aspect ratio and/or in
	 * picture itself - watermarks, small logos, etc.
	 *
	 * @param sourceImageUrl
	 *            URL of source image for search.
	 * @return A {@link String} containing the http response from Google,
	 * which can be used to pull similar images and other useful information.
	 * @throws IOException
	 *             if something went wrong.
	 */
	public static String reverseSearch(String sourceImageUrl) throws IOException {

		// 1. Run reverse image search in Google
		URL url = new URL(GOOGLE_IMG_SEARCH_PREFIX + sourceImageUrl); // sourceImageUrl encoding will be done automatically
		HttpURLConnection conn = prepareConnection(url, true);
		conn.connect();
		String response = getResponseString(conn);
		String redirectedHost = conn.getURL().getHost();

		// 2. Find a link to a results page
		Matcher resultPageMatcher = RESULT_PAGE_URL_PATTERN.matcher(response);
		if (!resultPageMatcher.find()) {
			throw new IOException("Result page URL not found");
		}
		String href = resultPageMatcher.group(1).replaceAll("&amp;", "&");

		// 3. Open the results page
		url = new URL("https://" + redirectedHost + href);
		conn = prepareConnection(url, false);
		conn.connect();
		response = getResponseString(conn);

		return response;
	}
	
	public static String getBestGuess(String response) {
		String guess;
		int start = response.indexOf("\"pq\"");
		guess = response.substring(start+6, response.indexOf('"', start+8));
		
		return guess;
	}
	
	public static List<BundledPost> parseIntoSimilar(String response) throws UnsupportedEncodingException {
		List<BundledPost> result = new ArrayList<BundledPost>();
		
		// 4. Extract image URLs & sizes
		Matcher imageUrlMatcher = IMAGE_URL_PATTERN.matcher(response);
		Matcher imageRefMatcher = IMAGE_REF_PATTERN.matcher(response);
		String tempU;
		String tempR;
		while (imageUrlMatcher.find() && imageRefMatcher.find()) {
			tempU = imageUrlMatcher.group(1);
			tempU = URLDecoder.decode(tempU, StandardCharsets.UTF_8.name());
			tempU = URLDecoder.decode(tempU, StandardCharsets.UTF_8.name());
			tempR = imageRefMatcher.group(1);
			tempR = URLDecoder.decode(tempR, StandardCharsets.UTF_8.name());
			tempR = URLDecoder.decode(tempR, StandardCharsets.UTF_8.name());

			result.add(new BundledPost(tempU, tempR));
		}

		return result;
	}
	
	public static HttpURLConnection prepareConnection(URL url, boolean followRedirects) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.addRequestProperty(HttpHeaders.USER_AGENT, USER_AGENT_HEADER_VALUE);
		conn.setInstanceFollowRedirects(followRedirects);
		return conn;
	}

	public static String getResponseString(HttpURLConnection connection) throws IOException {
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new IOException("Unexpected response status code " + connection.getResponseCode());
		}

		try (InputStreamReader streamReader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
			return CharStreams.toString(streamReader);
		}
	}
}
