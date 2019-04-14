package com.nordicid.testapplication;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class Poster {
	// we need to keep track of the form URL because it contains status information
	protected String formUrl = "";
	
	public Poster() {
	}
	
	public void postTag(String tag) {
		
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(this.getRequestUrl());
		
		// add params
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("rfidstartnummerrunden", tag));
		params.add(new BasicNameValuePair("submit", "Z&Auml;HLEN"));
		
		// post
		HttpEntity entity = null;
		try {
			HttpResponse response = httpclient.execute(httppost);
			entity = response.getEntity();
			if (entity == null)
				return;
		}
		catch (Exception e) {
			System.err.println("Error posting tag to HTTP backend");
			System.err.println(e);
		}
		
		try {
			InputStream instream = entity.getContent();
			String body = IOUtils.toString(instream, "utf8");
			//System.out.println(body);
			this.parseResponse(body);
		}
		catch (Exception e) {
			System.err.println("Error parsing HTTP response");
			System.err.println(e);
		}
	}
	
	protected void parseResponse(String body) {
		String nextFormUrl = Helper.getBetween(body, " action=\"", "\"");
		if (nextFormUrl.length() == 0) {
			System.err.println("Received invalid HTML without form URL");
			return;
		}
		this.formUrl = nextFormUrl.trim();
	}
	
	protected String getRequestUrl() {
		String url = "";
		Config config = Config.getInstance();
		if (this.formUrl.length() == 0)
			url = config.getPrefs().node("App").get("PostUrl", null);
		else {
			URL urlParts = null;
			try {
				urlParts = new URL(config.getPrefs().node("App").get("PostUrl", null));
			}
			catch (MalformedURLException e) {
				System.err.println("Invalid PostUrl specified in config.");
				return config.getPrefs().node("App").get("PostUrl", null); // return it nevertheless. App won't work as expected
			}
			if (this.formUrl.matches("^https?:\\/\\/") == false) { // add domain from config URL
				url = urlParts.getProtocol() + "://" + urlParts.getHost();
				if (this.formUrl.length() == 0 || this.formUrl.charAt(0) != '/')
					url += "/";
				url += this.formUrl;
			}
			else
				url = this.formUrl; // server already returned a full URL
		}
		//System.out.println("Posting tag to: " + url);
		return url;
	}
}
