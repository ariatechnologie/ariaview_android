package com.ariaview;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpException;
import org.apache.http.NameValuePair;

import android.content.Context;
import android.os.AsyncTask;

class PostTask extends AsyncTask<String, Integer, String> {

    private Context context;
	private List<NameValuePair> nameValuePairs;
	private String response = "";
	private String namefile;
	

    public PostTask(Context context, List<NameValuePair> nameValuePairs, String namefile) {
        this.context = context;
        this.nameValuePairs = nameValuePairs;
        this.namefile = namefile;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);

        	connection = (HttpURLConnection) url.openConnection();
        	connection.setReadTimeout(10000);
        	connection.setConnectTimeout(15000);
        	connection.setRequestMethod("POST");
        	connection.setDoInput(true);
        	connection.setDoOutput(true);

        	OutputStream os = connection.getOutputStream();
        	BufferedWriter writer = new BufferedWriter(
        	        new OutputStreamWriter(os, "UTF-8"));
        	writer.write(getQuery(nameValuePairs));
        	writer.flush();
        	writer.close();
        	os.close();

            int responseCode=connection.getResponseCode();
        	
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
                output = new FileOutputStream(context.getFilesDir().getPath()+"/AriaView/"+namefile);
                output.write(response.getBytes());
                output.close();
            }
            else {
                response="";
                throw new HttpException(responseCode+"");
            }
        	
            
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }
    
    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
