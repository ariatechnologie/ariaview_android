package com.ariaview;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import modele.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import BDD.AriaViewBDD;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {


	private AriaViewBDD ariaViewBDD;
	private User currentUser;
	private List<User> listUser;
	private boolean userRemember;
	
	private Intent intent;
	
	private EditText loginEditText;
	private EditText passwordEditText;
	
	private String UrlTest = "https://raw.githubusercontent.com/ariatechnologie/ariaview_android/master/testFile/";
	private String url_ws_login = "http://web.aria.fr/webservices/ARIAVIEW/login.php";
	private String url_ws_infosite = "http://web.aria.fr/webservices/ARIAVIEW/infosite.php";
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
	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;
	private NodeList sitesNodeList;
	private String[] sitesTabString;
	private File ariaDirectory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ariaDirectory = new File(getFilesDir(),"AriaView");
		ariaDirectory.mkdirs();
		
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		
		ariaViewBDD = new AriaViewBDD(this);
		ariaViewBDD.open();
		listUser = ariaViewBDD.getUser();
		ariaViewBDD.close();
		
		loginEditText = (EditText) findViewById(R.id.loginTxt);
		passwordEditText = (EditText) findViewById(R.id.passwordTxt);
		
		if(listUser.size()>0){
			currentUser = listUser.get(0);	
			loginEditText.setText(currentUser.getLogin());
			passwordEditText.setText(currentUser.getPassword());
			((CheckBox) findViewById(R.id.checkBox1)).setChecked(true);
		}else
			currentUser = new User();
		
	}
	
	
	public void onClickLogin(View v) throws SAXException, IOException{

		CheckBox checkBoxRemember = (CheckBox) findViewById(R.id.checkBox1);
		
		userRemember = checkBoxRemember.isChecked();
		
		//SEND LOGIN 1 
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("login", loginEditText.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("password", passwordEditText.getText().toString()));

		File fileXML = new File(ariaDirectory, login1XML);
		
		try {
			
			PostTask postTask = new PostTask(MainActivity.this, nameValuePairs, login1XML);
			postTask.execute(url_ws_login).get();
			
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(fileXML);
			
			sitesNodeList = document.getElementsByTagName("site");		
			
			sitesTabString = new String[sitesNodeList.getLength()];
			for(int i = 0; sitesNodeList.getLength()>i; i++)
			{
				sitesTabString[i] = sitesNodeList.item(i).getTextContent();
			}
			
			dialogSite(sitesTabString);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}
			
	}
	
	private void dialogSite(String[] tabStringSite){

		AlertDialog.Builder builder = new Builder(this);
	    builder.setTitle(getResources().getString(R.string.siteDialogTxt));

	    builder.setItems(tabStringSite, new OnClickListener() {

	        public void onClick(DialogInterface dialog, int which) {

	        	authen(which);
	            dialog.dismiss();
	        }

	    });

	    builder.show();
		
	}
	
	private void authen(int choice){
		//SEND LOGIN 2
		
		ariaViewBDD.open();
		ariaViewBDD.clearUser();
		
		if(userRemember){
			currentUser.setLogin(((EditText) findViewById(R.id.loginTxt)).getText().toString());
			currentUser.setPassword(((EditText) findViewById(R.id.passwordTxt)).getText().toString());
			currentUser.setSite(sitesTabString[choice]);
			
			ariaViewBDD.insertUser(currentUser);
		}

		ariaViewBDD.close();
		
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("login", loginEditText.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("password", passwordEditText.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("site", sitesTabString[choice]));

		File fileXML = new File(ariaDirectory, login2XML);
		
		
		try {
			
			PostTask postTask = new PostTask(MainActivity.this, nameValuePairs, login2XML);
			postTask.execute(url_ws_infosite).get();
			
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(fileXML);
			
			host = document.getElementsByTagName("host").item(0).getTextContent();
			url = document.getElementsByTagName("url").item(0).getTextContent();
			datefile = document.getElementsByTagName("datefile").item(0).getTextContent();
			model = document.getElementsByTagName("model").item(0).getTextContent();
			site = document.getElementsByTagName("site").item(0).getTextContent();
			nest = document.getElementsByTagName("nest").item(0).getTextContent();
					
			DownloadTask downloadTaskDateFile = new DownloadTask(MainActivity.this);
			downloadTaskDateFile.execute(host+"/"+url+"/"+site+"/GEARTH/"+model+"_"+nest+"/"+datefile).get();
					
			fileXML = new File(ariaDirectory, datefile);
			
			String lastDate = "";
			
			Document documentDateFile = documentBuilder.parse(fileXML);

			lastDate = documentDateFile.getElementsByTagName("name").item(documentDateFile.getElementsByTagName("name").getLength()-1).getTextContent();
		
			DownloadTask downloadTaskKml = new DownloadTask(MainActivity.this);
			downloadTaskKml.execute(host+"/"+url+"/"+site+"/GEARTH/"+model+"_"+nest+"/"+lastDate+"/"+lastDate+".kml").get();
			
			File fileKML = new File(ariaDirectory, lastDate+".kml");
			
			intent = new Intent(this, MapActivity.class);
			//startActivity(intent);	
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
			
		
	}
	
	
	/**
	* Method to check whether my device has or not a network connection
	* Need permission : android.permission.ACCESS_NETWORK_STATE
	* @return True if device is connected to network and false else
	*/
	private boolean checkDeviceConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		
		return (networkInfo != null && networkInfo.isConnected());
	}
}
