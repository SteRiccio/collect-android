package org.openforis.collect.android.misc;

import org.openforis.collect.android.R;
import org.openforis.collect.android.messages.AlertMessage;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class GpsReader extends Activity implements LocationListener{

	private static final String TAG = "GpsReader";
	private LocationManager locManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
	    locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    if (!locManager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {
	    	AlertMessage.createPositiveNegativeDialog(GpsReader.this, false, getResources().getDrawable(R.drawable.warningsign),
	    			getResources().getString(R.string.startGpsTitle),
	    			getResources().getString(R.string.startGpsMessage), 
	    			getResources().getString(R.string.yes), getResources().getString(R.string.no),
	    			new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						startUsingGPS();
    					}
    				},
    				new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						stopUsingGPS();
    						finish();
    					}
    				},null).show();
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, getResources().getInteger(R.integer.gpsMinUpdateTime), getResources().getInteger(R.integer.gpsMinUpdateDistance), this);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.i(getResources().getString(R.string.app_name),TAG+":onPause");
		stopUsingGPS();
	}
	
	@Override
	public void onLocationChanged(Location loc) {
		Log.i(getResources().getString(R.string.app_name),TAG+":onLocationChanged");
	    String lat = String.valueOf(loc.getLatitude());
	    String lon = String.valueOf(loc.getLongitude());
	    Log.e("GPS", "location changed: lat="+lat+", lon="+lon);
	    stopUsingGPS();
	    finish();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		Log.i(getResources().getString(R.string.app_name),TAG+":onProviderDisabled");
	}

	@Override
	public void onProviderEnabled(String arg0) {
		Log.i(getResources().getString(R.string.app_name),TAG+":onProviderEnabled");
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		Log.i(getResources().getString(R.string.app_name),TAG+":onStatusChanged");
	}
	
	private void startUsingGPS(){
		startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
	}
	
	public void stopUsingGPS(){
        if(locManager != null){
        	locManager.removeUpdates(GpsReader.this);
        }
    }
}