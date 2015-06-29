package com.ariaview;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import modele.AriaViewDate;
import modele.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

//Main Activity, Login Page
//Use of Web Service login.php and infosite.php
public class MainActivity extends Activity {

	private AriaViewBDD ariaViewBDD;
	private User currentUser;
	private List<User> listUser;
	private boolean userRemember;
	private AriaViewDate ariaViewDate;

	private Intent intent;

	private EditText loginEditText;
	private EditText passwordEditText;
	private CheckBox checkBoxRemember;

	private String url_ws_login = "http://web.aria.fr/webservices/ARIAVIEW/login.php";
	private String url_ws_infosite = "http://web.aria.fr/webservices/ARIAVIEW/infosite.php";
	private String login1XML = "login.xml";
	private String login2XML = "login2.xml";

	private String host;
	private String url;
	private String datefile;
	private String type;
	private String site;
	private String scale;

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

		ariaDirectory = new File(getFilesDir(), "AriaView");
		ariaDirectory.mkdirs();

		documentBuilderFactory = DocumentBuilderFactory.newInstance();

		ariaViewBDD = new AriaViewBDD(this);
		ariaViewBDD.open();
		listUser = ariaViewBDD.getUser();
		ariaViewBDD.close();

		loginEditText = (EditText) findViewById(R.id.loginTxt);
		passwordEditText = (EditText) findViewById(R.id.passwordTxt);
		checkBoxRemember = (CheckBox) findViewById(R.id.checkBoxRemember);

		if (listUser.size() > 0) {
			currentUser = listUser.get(0);
			loginEditText.setText(currentUser.getLogin());
			passwordEditText.setText(currentUser.getPassword());
			checkBoxRemember.setChecked(true);
		} else
			currentUser = new User();

	}

	public void onClickLogin(View v) throws SAXException, IOException {

		if (!checkInput()) {
			return;
		}

		if (!checkDeviceConnected()) {
			Toast.makeText(MainActivity.this,
					getResources().getString(R.string.no_network),
					Toast.LENGTH_LONG).show();
			return;
		}

		clearDirectory();

		userRemember = checkBoxRemember.isChecked();

		// SEND LOGIN 1
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("login", loginEditText
				.getText().toString()));
		nameValuePairs.add(new BasicNameValuePair("password", passwordEditText
				.getText().toString()));

		File fileXML = new File(ariaDirectory, login1XML);

		try {

			PostTask postTask = new PostTask(MainActivity.this, nameValuePairs,
					login1XML);
			postTask.execute(url_ws_login).get();

			if (fileXML.isFile()) {
				documentBuilder = documentBuilderFactory.newDocumentBuilder();
				document = documentBuilder.parse(fileXML);

				sitesNodeList = document.getElementsByTagName("site");

				sitesTabString = new String[sitesNodeList.getLength()];
				for (int i = 0; sitesNodeList.getLength() > i; i++) {
					sitesTabString[i] = sitesNodeList.item(i).getTextContent();
				}

				boolean siteFind = false;
				if (!isNewUser()) {
					int i = 0;
					while (i < sitesTabString.length && !siteFind) {
						if (currentUser.getSite().equals(sitesTabString[i])) {
							authen(i);
							siteFind = true;
						}
						i++;
					}
				}
				if (!siteFind)
					dialogSite(sitesTabString);
			} else {
				Toast.makeText(MainActivity.this,
						getResources().getString(R.string.no_correct_iden),
						Toast.LENGTH_LONG).show();
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}

	}

	private void dialogSite(String[] tabStringSite) {

		AlertDialog.Builder builder = new Builder(this).setTitle(
				getResources().getString(R.string.siteDialogTxt)).setItems(
				tabStringSite, new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						authen(which);
						dialog.dismiss();
					}

				});

		builder.show();

	}

	private void authen(int currentSite) {

		String choiceSite = sitesTabString[currentSite];

		if (!checkDeviceConnected()) {
			Toast.makeText(MainActivity.this,
					getResources().getString(R.string.no_network),
					Toast.LENGTH_LONG).show();
			return;
		}

		// SEND LOGIN 2

		ariaViewBDD.open();
		ariaViewBDD.clearUser();

		if (userRemember) {
			currentUser.setLogin(((EditText) findViewById(R.id.loginTxt))
					.getText().toString());
			currentUser.setPassword(((EditText) findViewById(R.id.passwordTxt))
					.getText().toString());
			currentUser.setSite(choiceSite);

			ariaViewBDD.insertUser(currentUser);
		}

		ariaViewBDD.close();

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("login", loginEditText
				.getText().toString()));
		nameValuePairs.add(new BasicNameValuePair("password", passwordEditText
				.getText().toString()));
		nameValuePairs.add(new BasicNameValuePair("site", choiceSite));

		File fileXML = new File(ariaDirectory, login2XML);

		try {

			PostTask postTask = new PostTask(MainActivity.this, nameValuePairs,
					login2XML);
			postTask.execute(url_ws_infosite).get();

			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(fileXML);

			host = document.getElementsByTagName("host").item(0)
					.getTextContent();
			url = document.getElementsByTagName("url").item(0).getTextContent();
			datefile = document.getElementsByTagName("datefile").item(0)
					.getTextContent();
			type = document.getElementsByTagName("type").item(0)
					.getTextContent();
			site = document.getElementsByTagName("site").item(0)
					.getTextContent();
			scale = document.getElementsByTagName("scale").item(0)
					.getTextContent();
			String model = document.getElementsByTagName("model").item(0)
					.getTextContent(); 
			String nest =  document.getElementsByTagName("nest").item(0)
					.getTextContent();
			
			
			DownloadTask downloadTaskDateFile = new DownloadTask(
					MainActivity.this);
			downloadTaskDateFile.execute(
					host + "/" + url + "/" + site + "/GEARTH/" + type + "_"
							+ scale + "/" + datefile).get();

			fileXML = new File(ariaDirectory, datefile);

			Document documentDateFile = documentBuilder.parse(fileXML);

			NodeList dateNodeList = documentDateFile
					.getElementsByTagName("name");

			ArrayList<String> listDate = new ArrayList<String>();

			for (int i = 1; i < dateNodeList.getLength(); i++) {
				listDate.add(((Element) dateNodeList.item(i)).getTextContent());
			}

			String lastDate = listDate.get(listDate.size() - 1);

			DownloadTask downloadTaskKml = new DownloadTask(MainActivity.this);
			downloadTaskKml.execute(
					host + "/" + url + "/" + site + "/GEARTH/" + type + "_"
							+ scale + "/" + lastDate + "/" + lastDate + ".kml")
					.get();

			File fileKML = new File(ariaDirectory, lastDate + ".kml");

			ariaViewDate = new AriaViewDate(host + "/" + url + "/", "/GEARTH/"
					+ type + "_" + scale + "/", listDate.size() - 1,
					currentSite, listDate, sitesTabString, nameValuePairs
							.get(0).getValue(), nameValuePairs.get(1)
							.getValue());

			intent = new Intent(this, MapActivity.class);
			intent.putExtra("AriaViewDate", ariaViewDate);
			intent.putExtra("model", model);
			intent.putExtra("nest", nest);
			intent.putExtra("fileKML", fileKML);
			startActivity(intent);

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

	// Method to check whether the device has or not a network connection
	// @return True if device is connected to network and false else
	private boolean checkDeviceConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		return (networkInfo != null && networkInfo.isConnected());
	}

	// Clear AriaApp Directory
	private void clearDirectory() {

		if (ariaDirectory.isDirectory()) {
			String[] children = ariaDirectory.list();
			for (int i = 0; i < children.length; i++) {
				new File(ariaDirectory, children[i]).delete();
			}
		}
	}

	// Method to check if input is correctly completed and display message toast
	private boolean checkInput() {
		String loginTxt = ((EditText) findViewById(R.id.loginTxt)).getText()
				.toString();
		String passwordTxt = ((EditText) findViewById(R.id.passwordTxt))
				.getText().toString();
		Boolean isCheck = false;

		if (loginTxt.equals("") && passwordTxt.equals(""))
			Toast.makeText(MainActivity.this,
					getResources().getString(R.string.no_login_and_password),
					Toast.LENGTH_LONG).show();
		else if (passwordTxt.equals(""))
			Toast.makeText(MainActivity.this,
					getResources().getString(R.string.no_password),
					Toast.LENGTH_LONG).show();
		else if (loginTxt.equals(""))
			Toast.makeText(MainActivity.this,
					getResources().getString(R.string.no_login),
					Toast.LENGTH_LONG).show();
		else
			isCheck = true;

		return isCheck;
	}

	// Method to check if the user logging is different from the last user
	// backup
	private boolean isNewUser() {

		boolean isNewUser = true;

		if (currentUser != null
				&& ((EditText) findViewById(R.id.loginTxt)).getText()
						.toString().equals(currentUser.getLogin())
				&& ((EditText) findViewById(R.id.passwordTxt)).getText()
						.toString().equals(currentUser.getPassword()))
			isNewUser = false;

		return isNewUser;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_language:
			dialogLanguage();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void dialogLanguage() {

		String[] tabStringLanguage = {
				getResources().getString(R.string.ln_en),
				getResources().getString(R.string.ln_es),
				getResources().getString(R.string.ln_fr),
				getResources().getString(R.string.ln_pt),
				getResources().getString(R.string.ln_zh)

		};

		AlertDialog.Builder builder = new Builder(this).setTitle(
				getResources().getString(R.string.title_menu_language))
				.setItems(tabStringLanguage, new OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						String ln = "";
						if (which == 0)
							ln = "en";
						else if (which == 1)
							ln = "es";
						else if (which == 2)
							ln = "fr";
						else if (which == 3)
							ln = "pt";
						else if (which == 4)
							ln = "zh";

						Resources res = MainActivity.this.getResources();
						// Change locale settings in the app.
						DisplayMetrics dm = res.getDisplayMetrics();
						android.content.res.Configuration conf = res
								.getConfiguration();
						conf.locale = new Locale(ln);
						res.updateConfiguration(conf, dm);
						dialog.dismiss();
						finish();
						startActivity(getIntent());
					}

				});

		builder.show();

	}
}
