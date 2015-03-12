package com.ariaview;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private String UrlTest = "https://raw.githubusercontent.com/ariatechnologie/ariaview_android/master/testFile/";
	private String login1XML = "login.xml";
	private String login2XML = "login2.xml";
	private String datesXML = "dates.xml";
	
	
	private Document document;
	private DocumentBuilderFactory fabrique;
	private DocumentBuilder constructeur;
	private NodeList sites;
	private File ariaDirectory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ariaDirectory = new File("/sdcard/AriaView/");
		ariaDirectory.mkdirs();
		
		fabrique = DocumentBuilderFactory.newInstance();
		
		Button btn = (Button) findViewById(R.id.loginButton);
		
	}
	
	
	public void onClickLogin(View v) throws SAXException, IOException{

		//SEND LOGIN 1
		
		final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
		downloadTask.execute(UrlTest+login1XML);
		

		//String fileXML = Environment.getExternalStorageDirectory()+"/AriaView/"+login1XML;
		
		File fileXML = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()+"/AriaView/", login1XML);
		
		
		try {
			constructeur = fabrique.newDocumentBuilder();
			document = constructeur.parse(fileXML);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		sites = document.getElementsByTagName("site");		
	
		//		intent = new Intent(this, FavoriteActivity.class);
		//		startActivity(intent);
	}
	
	
}
