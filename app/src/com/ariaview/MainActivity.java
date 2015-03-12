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
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private String login1XML;
	private Document document;
	private DocumentBuilderFactory fabrique;
	private DocumentBuilder constructeur;
	private NodeList sites;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		fabrique = DocumentBuilderFactory.newInstance();
		
		Button btn = (Button) findViewById(R.id.loginButton);
		
	}
	
	
	public void onClickLogin(View v) throws SAXException, IOException{

		//SEND LOGIN 1
		
		
		//login1XML = "bin/classes/login.xml";
		InputStream is = getResources().getAssets().open("testFile/login.xml");
		login1XML = getResources().getAssets().toString();
		
		System.out.println(login1XML);
		
		try {
			constructeur = fabrique.newDocumentBuilder();
			document = constructeur.parse(login1XML);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		sites = document.getElementsByTagName("site");
		
			
			//		intent = new Intent(this, FavoriteActivity.class);
//		startActivity(intent);
	}
	
	
}
