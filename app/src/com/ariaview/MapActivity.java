package com.ariaview;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;



public class MapActivity extends Activity {
 
    // Google Map
    private GoogleMap googleMap;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
 
        try {
            // Loading map
            initilizeMap();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
 
    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();


            CameraPosition cameraPosition = new CameraPosition.Builder().
            		target(new LatLng(31.621, -106.479)).zoom(12).build();
   
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    
            
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
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
	    		Toast.makeText(MapActivity.this,
						getResources().getString(R.string.title_menu_date),
						Toast.LENGTH_LONG).show();
	    		return true;
	    	case R.id.menu_polluant:
	    		Toast.makeText(MapActivity.this,
						getResources().getString(R.string.title_menu_polluant),
						Toast.LENGTH_LONG).show();

	    		return true;
	    	case R.id.menu_site:
	    		Toast.makeText(MapActivity.this,
						getResources().getString(R.string.title_menu_site),
						Toast.LENGTH_LONG).show();
		    		
	    		return true;
	    	case R.id.menu_deco:
	    		finish();
	    		return true;
	    	default:
	    		return super.onOptionsItemSelected(item);
    	}
    }
    
    
 
}