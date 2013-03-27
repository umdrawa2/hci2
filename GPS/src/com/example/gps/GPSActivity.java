package com.example.gps;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GPSActivity extends Activity {
	
	final double NORTH_LIMIT = -97.1;
	final double SOUTH_LIMIT = -97.17;
	final double EAST_LIMIT = 49.82;
	final double WEST_LIMIT = 49.79;
	
	final double LAT_START = 49.808;
	final double LONG_START = -97.14;
	
	LocationManager lcnMgr;
	Location location;
	double latitude, longitude;
	String provider;
	
	GoogleMap map;
	Marker pos;
	
	LatLngBounds bounds;// = new LatLngBounds(new LatLng(SOUTH_LIMIT, WEST_LIMIT), new LatLng(NORTH_LIMIT, EAST_LIMIT));
	CameraPosition camPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		camPos = new CameraPosition.Builder().target(new LatLng(LAT_START, LONG_START)).zoom(15).bearing(75).build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
		
		//map.getUiSettings().setScrollGesturesEnabled(false);
		//map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(49.82, -97.1), 15));
	    //map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
	    //map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition().bearing(90)));
		
		class locListener implements LocationListener {

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				if(pos != null)
					pos.remove();
				pos = map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your current coordinates are: " + (int)location.getLatitude() + "," + (int)location.getLongitude()));
				//map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
				//map.animateCamera(CameraUpdateFactory.zoomTo(map.getCameraPosition().zoom), 2000, null);	
				
				//LatLng mapBounds = map.getCameraPosition().target;
				bounds = map.getProjection().getVisibleRegion().latLngBounds;
				System.out.println(bounds.northeast.latitude + "," + bounds.southwest.latitude);
				if(bounds.northeast.longitude > NORTH_LIMIT || bounds.southwest.longitude < SOUTH_LIMIT || bounds.southwest.latitude < WEST_LIMIT || bounds.northeast.latitude > EAST_LIMIT) {
					if(map.getCameraPosition().zoom >= 15)
						camPos = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(map.getCameraPosition().zoom).bearing(75).build();
					else
						camPos = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15).bearing(75).build();
					map.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
				}

			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "GPS disabled", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
		};
		
		lcnMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
	    Criteria criteria = new Criteria();
	    provider = lcnMgr.getBestProvider(criteria, false);
	    location = lcnMgr.getLastKnownLocation(provider);		
		
		LocationListener listener = new locListener();
		lcnMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, listener);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
