package com.ariaview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import modele.AriaViewDate;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MapActivity extends Activity {

	// Google Map
	private GoogleMap googleMap;

	private AriaViewDate ariaViewDate;

	private File ariaDirectory;
	private String url_ws_infosite = "http://web.aria.fr/webservices/ARIAVIEW/infosite.php";
	private String url_ws_extract = "http://web.aria.fr/OpenDapServicesRESTAT/GridGetTimeSerieByPointDomainVariablePeriod";
	private String model;
	private String nest;

	private Document document;
	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;

	private Button incrementButton;
	private Spinner dateSpinner;
	private Button playButton;
	private ImageView legendImageView;

	private PlayThread mPlayThread;
	private boolean inPlay = false;
	private ScheduledExecutorService executor;
	private ArrayAdapter<String> dataAdapter;

	private float zoom = 11;
	private CameraPosition cameraPosition = new CameraPosition.Builder()
			.target(new LatLng(0, 0)).zoom(zoom).build();

	public final int NB_MARKER = 1;
	public int currennt_nb_marker = 0;
	public Marker marker;

	private long[] dataValuesFieldKey;
	private float[] dataValuesFieldValue;
	private ArrayList<Long> dataValuesFieldListKey = new ArrayList<Long>();
	private ArrayList<Float> dataValuesFieldListValue = new ArrayList<Float>();
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		ariaDirectory = new File(getFilesDir(), "AriaView");

		legendImageView = new ImageView(this);
		this.addContentView(legendImageView, new LayoutParams(Gravity.RIGHT));

		incrementButton = (Button) findViewById(R.id.incrementDateButton);
		playButton = (Button) findViewById(R.id.playButton);

		documentBuilderFactory = DocumentBuilderFactory.newInstance();

		Intent intent = getIntent();
		ariaViewDate = (AriaViewDate) intent.getExtras().getSerializable(
				"AriaViewDate");
		ariaViewDate.fillAriaViewDate((File) intent.getExtras()
				.getSerializable("fileKML"));
		model = intent.getStringExtra("model");
		nest = intent.getStringExtra("nest");

		dateSpinner = (Spinner) findViewById(R.id.spinnerDate);

		dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dateSpinner.setAdapter(dataAdapter);

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

		// getExtract();
		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Refresh Spinner List
	private void setDateSpinner() {
		dataAdapter.clear();
		dataAdapter.addAll(ariaViewDate.getBeginTimeSpanList());
	}

	// Function to load map. If map is not created it will create it
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			readMap();

		}
	}

	private void readMap() {
		setDateSpinner();

		if (googleMap.getCameraPosition().zoom != 2.0)
			cameraPosition = googleMap.getCameraPosition();
		else
			cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(
							(ariaViewDate.getNorth() + ariaViewDate.getSouth()) / 2,
							(ariaViewDate.getEast() + ariaViewDate.getWest()) / 2))
					.zoom(zoom).build();

		googleMap.clear();
		googleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));

		try {

			String pathLegend = ariaViewDate.getAllPath()
					+ ariaViewDate.getListAriaViewDateTerm()
							.get(ariaViewDate.getCurrentAriaViewDateTerm())
							.getLegendPath();

			DownloadTask downloadTaskLegend = new DownloadTask(MapActivity.this);

			downloadTaskLegend.execute(pathLegend).get();

			File pngLegend = new File(ariaDirectory, ariaViewDate
					.getListAriaViewDateTerm()
					.get(ariaViewDate.getCurrentAriaViewDateTerm())
					.getLegendPath());

			if (pngLegend.exists()) {
				legendImageView.setImageBitmap(BitmapFactory
						.decodeFile(pngLegend.getAbsolutePath()));
				legendImageView.setAlpha(75);
			}

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
		case R.id.menu_marker:
			placeMarker();
			return true;
		case R.id.menu_extract:
			getExtract();
			return true;
		case R.id.menu_date:
			dialogDates(ariaViewDate.getListDate());
			return true;
		case R.id.menu_polluant:
			dialogPolluant(ariaViewDate.getListPolluant());
			return true;
		case R.id.menu_site:
			dialogSite(ariaViewDate.getSitesTabString());
			return true;
		case R.id.menu_deco:
			Intent intent = new Intent(getBaseContext(), MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			
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
				if (which != ariaViewDate.getCurrentSite())
					post(which, -1);
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
				if (which != ariaViewDate.getCurrentDate())
					post(ariaViewDate.getCurrentSite(), which);

				dialog.dismiss();
			}

		});

		builder.show();

	}

	private void dialogPolluant(ArrayList<String> listPolluant) {

		String[] tabStringPolluant = listPolluant
				.toArray(new String[listPolluant.size()]);

		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(getResources().getString(R.string.polluantDialogTxt));

		builder.setItems(tabStringPolluant, new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				if (which != ariaViewDate.getCurrentPolluant()) {
					ariaViewDate.setCurrentPolluant(which);
					readMap();
				}
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
			String type = document.getElementsByTagName("type").item(0)
					.getTextContent();
			String site = document.getElementsByTagName("site").item(0)
					.getTextContent();
			String scale = document.getElementsByTagName("scale").item(0)
					.getTextContent();
			String model = document.getElementsByTagName("model").item(0)
					.getTextContent(); 
			String nest =  document.getElementsByTagName("nest").item(0)
					.getTextContent();

			
			
			DownloadTask downloadTaskDateFile = new DownloadTask(
					MapActivity.this);
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

			String date = "";
			if (currentDate == -1) {
				date = listDate.get(listDate.size() - 1);
				currentDate = listDate.size() - 1;
			} else
				date = listDate.get(currentDate);

			DownloadTask downloadTaskKml = new DownloadTask(MapActivity.this);
			downloadTaskKml.execute(
					host + "/" + url + "/" + site + "/GEARTH/" + type + "_"
							+ scale + "/" + date + "/" + date + ".kml").get();

			File fileKML = new File(ariaDirectory, date + ".kml");

			ariaViewDate = new AriaViewDate(host + "/" + url + "/", "/GEARTH/"
					+ type + "_" + scale + "/", currentDate, currentSite,
					listDate, ariaViewDate.getSitesTabString(), nameValuePairs
							.get(0).getValue(), nameValuePairs.get(1)
							.getValue());

			Intent intent = new Intent(this, MapActivity.class);
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

	private void placeMarker() {

		currennt_nb_marker = 0;
		if (marker != null)
			marker.remove();

		googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				if (currennt_nb_marker < NB_MARKER) {
					currennt_nb_marker++;
					MarkerOptions markerOptions = new MarkerOptions().position(
							new LatLng(point.latitude, point.longitude)).title(
							"Marker N" + MapActivity.this.currennt_nb_marker);
					marker = googleMap.addMarker(markerOptions);
				}
			}
		});

	}

	private void getExtract() {
	
		if (marker != null) {
			double latitude = marker.getPosition().latitude;
			double longitude = marker.getPosition().longitude;
			String domainid = "_LENVIS_"
					+ model
					+ "_"
					+ ariaViewDate.getSitesTabString()[ariaViewDate
							.getCurrentSite()] + "_reference_" + nest
					+ "_dataset";
			String variableid = ariaViewDate.getListAriaViewDateTerm()
					.get(ariaViewDate.getCurrentAriaViewDateTerm())
					.getPolluant_id();
			String startdate = ariaViewDate.getListAriaViewDateTerm()
					.get(0)
					.getBeginTimeSpan();
			String enddate = ariaViewDate.getListAriaViewDateTerm()
					.get(ariaViewDate.getListAriaViewDateTerm().size()-1)
					.getEndTimeSpan();

			//
			startdate = startdate.replace("T", " ").substring(0,
					startdate.length() - 6);
			enddate = enddate.replace("T", " ").substring(0,
					enddate.length() - 6);

			// url_ws_extract
			if (domainid != null && variableid != null && startdate != null
					&& enddate != null) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						6);
				nameValuePairs.add(new BasicNameValuePair("longitude", Double
						.toString(longitude)));
				nameValuePairs.add(new BasicNameValuePair("latitude", Double
						.toString(latitude)));
				nameValuePairs
						.add(new BasicNameValuePair("domainid", domainid));
				nameValuePairs.add(new BasicNameValuePair("variableid",
						variableid));
				nameValuePairs.add(new BasicNameValuePair("startdate",
						startdate));
				nameValuePairs.add(new BasicNameValuePair("enddate", enddate));

				File fileJSON = new File(ariaDirectory, "extract.json");

				PostTask postTask = new PostTask(MapActivity.this,
						nameValuePairs, "extract.json");
				try {
					postTask.execute(url_ws_extract).get();

					String contentsJson = readFile(fileJSON);
					
						
					if(!contentsJson.contentEquals("Exception null"))
					{
						parseJsonData(contentsJson);
						
						dataValuesFieldKey = new long[dataValuesFieldListKey.size()];
						dataValuesFieldValue = new float[dataValuesFieldListValue.size()];
						
						for(int i = 0; i<dataValuesFieldListKey.size();i++){
							dataValuesFieldKey[i] = dataValuesFieldListKey.get(i);
							dataValuesFieldValue[i] = dataValuesFieldListValue.get(i);
						}
						
						Intent intent = new Intent(this, GraphViewActivity.class);
						intent.putExtra("dataValuesFieldMapKey", dataValuesFieldKey);
						intent.putExtra("dataValuesFieldMapValue", dataValuesFieldValue);
						intent.putExtra("polluant",ariaViewDate.getListAriaViewDateTerm()
								.get(ariaViewDate.getCurrentAriaViewDateTerm())
								.getPolluant());
						intent.putExtra("startdate", startdate);
						intent.putExtra("site", ariaViewDate.getSitesTabString()[ariaViewDate.getCurrentSite()]);
						intent.putExtra("latitude", latitude);
						intent.putExtra("longitude", longitude);
						
						startActivity(intent);
					}
					else
						Toast.makeText(MapActivity.this,
								getResources().getString(R.string.errorWebService),
								Toast.LENGTH_LONG).show();

					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public void parseJsonData(String json) {
		JsonParser parser = new JsonParser();
		
		// Converts the string in json object
		JsonObject jsonObject = parser.parse(json).getAsJsonObject();

		// Recuperates the object GetDataResult
		JsonObject dataResult = jsonObject.getAsJsonObject("GetDataResult");

		// Table recovered dataValuesField contained in the object GetDataResult
		JsonArray dataValues = dataResult.getAsJsonArray("dataValuesField");
		
		dataValuesFieldKey = new long[dataValues.size()];
		dataValuesFieldValue = new float[dataValues.size()];
		
		// Inserting table data in the map
		for (JsonElement item : dataValues) {
			JsonObject obj = item.getAsJsonObject();

			if(!dataValuesFieldListKey.contains(Long.parseLong(obj.get("dateTimeField").getAsString().replaceAll("[^x0-9]", "")))){
				dataValuesFieldListKey.add(Long.parseLong(obj.get("dateTimeField").getAsString().replaceAll("[^x0-9]", "")));
				dataValuesFieldListValue.add(obj.get("valueField").getAsFloat());
			}
		}
		
	}

	public static String readFile(File file) throws IOException {
		int len;
		char[] chr = new char[4096];
		final StringBuffer buffer = new StringBuffer();
		final FileReader reader = new FileReader(file);
		try {
			while ((len = reader.read(chr)) > 0) {
				buffer.append(chr, 0, len);
			}
		} finally {
			reader.close();
		}
		return buffer.toString();
	}
}