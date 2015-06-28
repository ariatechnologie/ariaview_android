package com.ariaview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer.LegendAlign;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ShareActionProvider;
import android.widget.Toast;


public class GraphViewActivity extends Activity {

private ShareActionProvider mShareActionProvider;
private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph_view);
		
		Intent intent = getIntent();
		long[] dataValuesFieldMapKey = intent.getLongArrayExtra("dataValuesFieldMapKey");
		float[] dataValuesFieldMapValue = intent.getFloatArrayExtra("dataValuesFieldMapValue");
		String polluant = intent.getStringExtra("polluant");
		String startdate = intent.getStringExtra("startdate");
		String site = intent.getStringExtra("site");
		double latitude = intent.getDoubleExtra("latitude", 0);
		double longitude = intent.getDoubleExtra("longitude",0);

		title = polluant+" "+startdate.split(" ")[0]+" ("+Math.round(latitude*10000.0)/10000.0+","+Math.round(longitude*10000.0)/10000.0+")";
		
		setTitle(title);
		DataPoint[] dataPoint = new DataPoint[46];
		//DataPoint[] dataPoint = new DataPoint[dataValuesFieldMapKey.length];
		Double value;
		int i = 0;
		long date_milliseconds;
		double m;
		int h;
		double mh;
		
		for(long date: dataValuesFieldMapKey){
			if(i<46){
				date_milliseconds = date;
				m = (double) ((date_milliseconds / (1000*60)) % 60);
				h = (int) ((date_milliseconds / (1000*60*60)) % 24);
				
				if(m == 30)
					m = 0.5;
					
				mh = h+m;
				
				value = Math.round(dataValuesFieldMapValue[i]*100.0)/100.0;
				if(value < 0)
					dataPoint[i] = new DataPoint(mh,0);
				else
					dataPoint[i] = new DataPoint(mh,value);
			}
			i++;
		}
		
		GraphView graph = (GraphView) findViewById(R.id.graph);
		graph.getViewport().setXAxisBoundsManual(true);
		graph.getViewport().setMinX(0);
		graph.getViewport().setMaxX(24);
		graph.getViewport().setScrollable(true);
		
		LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoint);
		series.setTitle(site);
		graph.getLegendRenderer().setVisible(true);
		graph.getLegendRenderer().setAlign(LegendAlign.TOP);
		graph.addSeries(series);
		
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
 
        /** Inflating the current activity's menu with res/menu/items.xml */
        getMenuInflater().inflate(R.menu.menu_graph, menu);
 
        /** Getting the actionprovider associated with the menu item whose id is share */
        mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.menu_item_share).getActionProvider();
 
        /** Setting a share intent */
        mShareActionProvider.setShareIntent(getDefaultShareIntent());
 
        return super.onCreateOptionsMenu(menu);
 
    }
 
    /** Returns a share intent */
    private Intent getDefaultShareIntent(){
    	takeScreenshot();
    	
    	File file = new File(Environment.getExternalStorageDirectory() + "/graph.png");
    	if (!file.exists() || !file.canRead()) {
    	    Toast.makeText(this, "Attachment Error", Toast.LENGTH_SHORT).show();
    	    finish();
    	}
    	
    	Uri uri = Uri.parse("file://"+file.getAbsolutePath());
    	
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        
        return intent;
    }
	
    public void takeScreenshot() {
    	
    	   View rootView = getWindow().getDecorView();
    	   rootView.setDrawingCacheEnabled(true);
    	   Bitmap bitmap = rootView.getDrawingCache();
    	   
    	   File imagePath = new File(Environment.getExternalStorageDirectory() + "/graph.png");
    	   FileOutputStream fos;
    	    try {
    	        fos = new FileOutputStream(imagePath);
    	        bitmap.compress(CompressFormat.JPEG, 100, fos);
    	        fos.flush();
    	        fos.close();
    	    } catch (FileNotFoundException e) {
    	        Log.e("FileNotFoundException", e.getMessage(), e);
    	    } catch (IOException e) {
    	        Log.e("IOException", e.getMessage(), e);
    	    }
    }
    
}