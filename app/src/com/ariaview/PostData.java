package com.ariaview;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class PostData {

	private HttpResponse response;
	private HttpClient httpclient;
	private HttpPost httppost;
	private List<NameValuePair> nameValuePairs;
		
	public PostData(String httppost, List<NameValuePair> nameValuePairs ) {
		this.httpclient = new DefaultHttpClient();
		this.httppost = new HttpPost(httppost);
		this.nameValuePairs = nameValuePairs;
	}
	
	
	public void execute(){
		try {
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            
	        response = httpclient.execute(httppost);
	        
	    } catch (ClientProtocolException e) {
	        System.out.println(e.getMessage());
	    } catch (IOException e) {
	        System.out.println(e.getMessage());
	    }
	}
	
	
	public HttpResponse getResponse() {
		return response;
	}
	public void setResponse(HttpResponse response) {
		this.response = response;
	}
	public HttpClient getHttpclient() {
		return httpclient;
	}
	public void setHttpclient(HttpClient httpclient) {
		this.httpclient = httpclient;
	}
	public HttpPost getHttppost() {
		return httppost;
	}
	public void setHttppost(HttpPost httppost) {
		this.httppost = httppost;
	}
	
	@Override
	public String toString() {
		return "PostData [getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}
}
