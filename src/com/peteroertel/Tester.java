package com.peteroertel;

import java.util.List;
import java.io.IOException;

public class Tester {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String s = ImageSearcher.reverseSearch("https://i.imgur.com/P8YVGiu.png");
		BundledPost p = new BundledPost(s, s, true);
		System.out.println(p);
	}

}
