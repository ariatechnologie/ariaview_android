package com.ariaview;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import modele.AriaViewDate;
import modele.AriaViewDateTerm;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class MapActivity extends Activity {

	// Google Map
	private GoogleMap googleMap;
	private AriaViewDate ariaViewDate;
	private File ariaDirectory;
	private List<String> beginTimeSpanList;
	private Spinner dateSpinner;
	private String url_ws_infosite = "http://web.aria.fr/webservices/ARIAVIEW/infosite.php";
	private Document document;
	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;
	private Button incrementButton;
	private Button playButton;
	private PlayThread mPlayThread;
	private boolean inPlay = false;
	private ScheduledExecutorService executor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		incrementButton = (Button) findViewById(R.id.incrementDateButton);
		playButton = (Button) findViewById(R.id.playButton);

		documentBuilderFactory = DocumentBuilderFactory.newInstance();

		ariaDirectory = new File(getFilesDir(), "AriaView");

		Intent intent = getIntent();
		ariaViewDate = (AriaViewDate) intent.getExtras().getSerializable(
				"AriaViewDate");
		ariaViewDate.fillAriaViewDate((File) intent.getExtras()
				.getSerializable("fileKML"));
				
		dateSpinner = (Spinner) findViewById(R.id.spinnerDate);

		beginTimeSpanList = ariaViewDate.getBeginTimeSpanList();

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, beginTimeSpanList);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dateSpinner.setAdapter(dataAdapter);

		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}

		dateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				ariaViewDate.setCurrentAriaViewDateTerm(position);
				readMap();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				return;

			}

		});

	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			readMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private void readMap() {
		googleMap.clear();
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng((ariaViewDate.getNorth() + ariaViewDate
						.getSouth()) / 2,
						(ariaViewDate.getEast() + ariaViewDate.getWest()) / 2))
				.zoom(11).build();

		googleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));

		try {

			String pathLegend = ariaViewDate.getAllPath()
					+ ariaViewDate.getLegendPath();

			DownloadTask downloadTaskLegend = new DownloadTask(MapActivity.this);

			downloadTaskLegend.execute(pathLegend).get();

			File pngLegend = new File(ariaDirectory,
					ariaViewDate.getLegendPath());

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} 

		try {

			String nameFileIcon = ariaViewDate.getListAriaViewDateTerm()
					.get(ariaViewDate.getCurrentAriaViewDateTerm())
					.getIconPath();

			String pathIcon = ariaViewDate.getAllPath() + nameFileIcon;

			DownloadTask downloadTaskIcon = new DownloadTask(MapActivity.this);

			downloadTaskIcon.execute(pathIcon).get();

			File pngIcon = new File(ariaDirectory, nameFileIcon);

			LatLngBounds newarkBounds = new LatLngBounds(new LatLng(
					ariaViewDate.getSouth(), ariaViewDate.getWest()), // South
																		// west
																		// corner
					new LatLng(ariaViewDate.getNorth(), ariaViewDate.getEast())); // North
																					// east
																					// corner

			GroundOverlayOptions newarkMap = new GroundOverlayOptions()
					.image(BitmapDescriptorFactory.fromPath(pngIcon
							.getAbsolutePath())).positionFromBounds(
							newarkBounds);
			googleMap.addGroundOverlay(newarkMap);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}

	public void incrementDate(View v) {
		if (ariaViewDate.getCurrentAriaViewDateTerm() + 1 < ariaViewDate
				.getListAriaViewDateTerm().size()) {
			ariaViewDate.setCurrentAriaViewDateTerm(ariaViewDate
					.getCurrentAriaViewDateTerm() + 1);
			dateSpinner.setSelection(ariaViewDate.getCurrentAriaViewDateTerm());
			readMap();
		}
	}

	public void decrementDate(View v) {
		if (ariaViewDate.getCurrentAriaViewDateTerm() > 0) {
			ariaViewDate.setCurrentAriaViewDateTerm(ariaViewDate
					.getCurrentAriaViewDateTerm() - 1);
			dateSpinner.setSelection(ariaViewDate.getCurrentAriaViewDateTerm());
			readMap();
		}
	}

	public void play(View v) {

		if (!inPlay) {
			inPlay = true;
			playButton.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.stop));
			mPlayThread = new PlayThread(incrementButton);
			executor = Executors.newSingleThreadScheduledExecutor();
			executor.scheduleAtFixedRate(mPlayThread, 0, 2, TimeUnit.SECONDS);
		} else {
			inPlay = false;
			playButton.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.play));
			executor.shutdown();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_date:
			dialogDates(ariaViewDate.getListDate());
			return true;
		case R.id.menu_polluant:
			Toast.makeText(MapActivity.this,
					getResources().getString(R.string.title_menu_polluant),
					Toast.LENGTH_LONG).show();

			return true;
		case R.id.menu_site:
			dialogSite(ariaViewDate.getSitesTabString());
			return true;
		case R.id.menu_deco:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void dialogSite(String[] tabStringSite) {

		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(getResources().getString(R.string.siteDialogTxt));

		builder.setItems(tabStringSite, new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				post(which, ariaViewDate.getCurrentDate());
				dialog.dismiss();
			}

		});

		builder.show();

	}

	private void dialogDates(ArrayList<String> listDate) {

		String[] tabStringDate = listDate.toArray(new String[listDate.size()]);

		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(getResources().getString(R.string.dateDialogTxt));

		builder.setItems(tabStringDate, new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				post(ariaViewDate.getCurrentSite(), which);

				dialog.dismiss();
			}

		});

		builder.show();

	}

	private void post(int currentSite, int currentDate) {

		String[] sitesTab = ariaViewDate.getSitesTabString();
		String choiceSite = sitesTab[currentSite];

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("login", ariaViewDate
				.getLogin()));
		nameValuePairs.add(new BasicNameValuePair("password", ariaViewDate
				.getPassword()));
		nameValuePairs.add(new BasicNameValuePair("site", choiceSite));

		File fileXML = new File(ariaDirectory, "login2.xml");

		try {

			PostTask postTask = new PostTask(MapActivity.this, nameValuePairs,
					"login2.xml");
			postTask.execute(url_ws_infosite).get();

			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(fileXML);

			String host = document.getElementsByTagName("host").item(0)
					.getTextContent();
			String url = document.getElementsByTagName("url").item(0)
					.getTextContent();
			String datefile = document.getElementsByTagName("datefile").item(0)
					.getTextContent();
			String model = document.getElementsByTagName("model").item(0)
					.getTextContent();
			String site = document.getElementsByTagName("site").item(0)
					.getTextContent();
			String nest = document.getElementsByTagName("nest").item(0)
					.getTextContent();

			DownloadTask downloadTaskDateFile = new DownloadTask(
					MapActivity.this);
			downloadTaskDateFile.execute(
					host + "/" + url + "/" + site + "/GEARTH/" + model + "_"
							+ nest + "/" + datefile).get();

			fileXML = new File(ariaDirectory, datefile);

			Document documentDateFile = documentBuilder.parse(fileXML);

			NodeList dateNodeList = documentDateFile
					.getElementsByTagName("name");

			ArrayList<String> listDate = new ArrayList<String>();

			for (int i = 1; i < dateNodeList.getLength(); i++) {
				listDate.add(((Element) dateNodeList.item(i)).getTextContent());
			}

			String date = "";
			if (currentDate == -1) {
				date = listDate.get(listDate.size() - 1);
				currentDate = listDate.size() - 1;
			} else
				date = listDate.get(currentDate);

			DownloadTask downloadTaskKml = new DownloadTask(MapActivity.this);
			downloadTaskKml.execute(
					host + "/" + url + "/" + site + "/GEARTH/" + model + "_"
							+ nest + "/" + date + "/" + date + ".kml").get();

			File fileKML = new File(ariaDirectory, date + ".kml");

			fillAriaViewDate(fileKML, host + "/" + url + "/" + site
					+ "/GEARTH/" + model + "_" + nest + "/" + date + "/");

			ariaViewDate = new AriaViewDate(host + "/" + url + "/", "/GEARTH/"
					+ model + "_" + nest + "/", currentDate, currentSite,
					listDate, ariaViewDate.getSitesTabString(), nameValuePairs
							.get(0).getValue(), nameValuePairs.get(1)
							.getValue());
			ariaViewDate.setSitesTabString(sitesTab);
			// ariaViewDate.fillAriaViewDate(fileKML);

			Intent intent = new Intent(this, MapActivity.class);
			intent.putExtra("AriaViewDate", ariaViewDate);
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

	private void fillAriaViewDate(File fileKML, String hostPath) {

		try {

			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(fileKML);

			Double north = Double.parseDouble(document
					.getElementsByTagName("north").item(0).getTextContent());
			Double south = Double.parseDouble(document
					.getElementsByTagName("south").item(0).getTextContent());
			Double east = Double.parseDouble(document
					.getElementsByTagName("east").item(0).getTextContent());
			Double west = Double.parseDouble(document
					.getElementsByTagName("west").item(0).getTextContent());
			String legendPath = URLEncoder.encode(document.getElementsByTagName("href").item(0)
					.getTextContent(), "UTF-8")
					.replaceAll("\\+", "%20");

			NodeList beginTimeNodeList = document.getElementsByTagName("begin");
			NodeList endTimeNodeList = document.getElementsByTagName("end");
			NodeList iconPathNodeList = document.getElementsByTagName("href");

			ArrayList<AriaViewDateTerm> listAriaViewDateTerm = new ArrayList<AriaViewDateTerm>();

			for (int i = 0; i < beginTimeNodeList.getLength(); i++) {
				String beginTimeSpan = ((Element) beginTimeNodeList.item(i))
						.getTextContent();
				String endTimeSpan = ((Element) endTimeNodeList.item(i))
						.getTextContent();
				String iconPath = URLEncoder.encode(((Element) iconPathNodeList.item(i + 1))
						.getTextContent(), "UTF-8")
						.replaceAll("\\+", "%20");
				listAriaViewDateTerm.add(new AriaViewDateTerm(beginTimeSpan,
						endTimeSpan, iconPath, ""));
			}

			ariaViewDate = new AriaViewDate(north, south, east, west, hostPath,
					legendPath);
			ariaViewDate.setListAriaViewDateTerm(listAriaViewDateTerm);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}