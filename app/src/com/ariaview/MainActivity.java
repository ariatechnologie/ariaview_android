package com.ariaview;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {

	private String UrlTest = "https://raw.githubusercontent.com/ariatechnologie/ariaview_android/master/testFile/";
	private String login1XML = "login.xml";
	private String login2XML = "login2.xml";
	private String datesXML = "dates.xml";
	
	private String host;
	private String url;
	private String datefile;
	private String model;
	private String site;
	private String nest;
	
	private Document document;
	private DocumentBuilderFactory fabrique;
	private DocumentBuilder constructeur;
	private NodeList sites;
	private String[] sitesTab;
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
				
		File fileXML = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()+"/AriaView/", login1XML);
		
		
		try {
			constructeur = fabrique.newDocumentBuilder();
			document = constructeur.parse(fileXML);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		sites = document.getElementsByTagName("site");		
		
		sitesTab = new String[sites.getLength()];
		for(int i = 0; sites.getLength()>i; i++)
		{
			sitesTab[i] = sites.item(i).getTextContent();
		}
		
		dialogSite(sitesTab);
		
		//		intent = new Intent(this, FavoriteActivity.class);
		//		startActivity(intent);
	}
	
	private void dialogSite(String[] sites){

		AlertDialog.Builder b = new Builder(this);
	    b.setTitle(getResources().getString(R.string.siteDialogTxt));

	    b.setItems(sites, new OnClickListener() {

	        public void onClick(DialogInterface dialog, int which) {

	        	authen(which);
	            dialog.dismiss();
	        }

	    });

	    b.show();
		
	}
	
	private void authen(int choice){
		//SEND LOGIN 2
		
		DownloadTask downloadTask = new DownloadTask(MainActivity.this);
		downloadTask.execute(UrlTest+login2XML);
		
		File fileXML = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()+"/AriaView/", login2XML);
		
		
		try {
			constructeur = fabrique.newDocumentBuilder();
			document = constructeur.parse(fileXML);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		host = document.getElementsByTagName("host").item(0).getTextContent();
		url = document.getElementsByTagName("url").item(0).getTextContent();
		datefile = document.getElementsByTagName("datefile").item(0).getTextContent();
		model = document.getElementsByTagName("model").item(0).getTextContent();
		site = document.getElementsByTagName("site").item(0).getTextContent();
		nest = document.getElementsByTagName("nest").item(0).getTextContent();
				
		DownloadTask downloadTaskDateFile = new DownloadTask(MainActivity.this);
		downloadTaskDateFile.execute(host+"/"+url+"/"+site+"/GEARTH/"+model+"_"+nest+"/"+datefile);
				
		fileXML = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()+"/AriaView/", datefile);
		
		
	}
}
