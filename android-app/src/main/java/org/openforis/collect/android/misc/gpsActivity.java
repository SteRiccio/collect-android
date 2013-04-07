package org.openforis.collect.android.misc;

import org.openforis.collect.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

public class GpsActivity extends Activity {
	
	private static final String TAG = "GpsActivity";

	private LocationManager lm;
	private LocationListener ll;
	public Location loc;
	private Dialog dialog;
	private int waitingTime;//how long to wait for new GPS coords

	public GpsActivity(){
		Log.i(TAG,"gpsActivity:constructor");
		try{
			String preferredGpsTimeout = "10";
        	if (preferredGpsTimeout.equals("")){
        		waitingTime = Integer.parseInt("5");
        	} else{
        		try{
        			waitingTime = Integer.parseInt(preferredGpsTimeout);
        			if (waitingTime<=0){
        				waitingTime = Integer.parseInt("5");
        			}
        			else{//waitingTime is correct, so do nothing   	
        				
        			}
        		}catch (NumberFormatException e){
        			waitingTime = Integer.parseInt("5");
        		}
        	}
        	if (waitingTime>Integer.parseInt("60")){//do not wait more than 5 minutes
        		waitingTime = Integer.parseInt("60"); 
        	}
        	waitingTime *= 1000;
		}
		catch (Exception e){
			waitingTime = Integer.parseInt("5");
		}		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
		setContentView(R.layout.welcomescreen);
		loc = null;
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if ( !lm.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
			buildAlertMessageNoGps();
		}
		else
		{
			ll = new MyLocationListener();
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 900000, 1, ll);
			// Create a Runnable which is used to determine when the new GPS coords were received	
			Runnable showWaitDialog = new Runnable() {

				@Override
				public void run() {
					while ((loc == null) && (SystemClock.currentThreadTimeMillis()<waitingTime)) {
						// Wait for first GPS coords change (do nothing until loc != null)
					}
					if (loc!=null){
						Intent resultHolder = new Intent();
						resultHolder.putExtra(getResources().getString(R.string.latitude), String.valueOf(loc.getLatitude()));
						resultHolder.putExtra(getResources().getString(R.string.longitude), String.valueOf(loc.getLongitude()));
						setResult(getResources().getInteger(R.integer.internalGpsLocationReceived),resultHolder);
					}
						
					// After receiving first GPS coordinates dismiss the Progress Dialog
					dialog.dismiss();
					lm.removeUpdates(ll);
					// 	and destroy activity which requests location updates					
					finish();
				}
			};

			// 	Create a dialog to let the user know that we're waiting for a GPS coordinates
			dialog = ProgressDialog.show(GpsActivity.this, "Please wait...",
					"Retrieving GPS data...", true);
			Thread t = new Thread(showWaitDialog);
			t.start();
		}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();   
		Log.i(TAG,"gpsActivity:onResume");
	}	
	
	  private void buildAlertMessageNoGps() {
		    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
		           .setCancelable(false)
		           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		               public void onClick(final DialogInterface dialog,  final int id) {
		                   launchGPS(); 
		               }
		           })
		           .setNegativeButton("No", new DialogInterface.OnClickListener() {
		               public void onClick(final DialogInterface dialog, final int id) {
		                    dialog.cancel();
		    				finish();
		               }
		           });
		    final AlertDialog alert = builder.create();
		    alert.show();
		}

	  private void launchGPS(){
		  Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		  startActivityForResult(myIntent, 1);
	  }
	  
	    protected void onActivityResult(int requestCode, int resultCode, Intent data){
	        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	        if(provider != null){
	            //Start searching for location and update the location text when update available.
	        	lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	        	if ( !lm.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {				
	        		finish();
	        	}
	        	else
	        	{
	        		ll = new MyLocationListener();
	        			
	        		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	        		// Create a Runnable which is used to determine when the new GPS coords were received	
	        		Runnable showWaitDialog = new Runnable() {

	        			@Override
	        			public void run() {
	        				while ((loc == null) && (SystemClock.currentThreadTimeMillis()<waitingTime)) {
	        					// Wait for first GPS coords change (do nothing until loc != null)
	        				}
	        				Log.e("lat=="+loc.getLatitude(),"location updates"+loc.getLongitude());
	        				// After receiving first GPS coordinates dismiss the Progress Dialog
	        				dialog.dismiss();
	        				stopListeningGpsUpdates();
	        				// 	and destroy activity which requests location updates	        				
	        				finish();
	        			}
	        		};

	        		dialog = ProgressDialog.show(GpsActivity.this, "Please wait...",
	        			"Retrieving GPS data...", true);
	        		Thread t = new Thread(showWaitDialog);
	        		t.start();
	        	}
	        }
	    }
	
	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				loc = location;
				Log.e("onLocationChanged "+loc.getLatitude(),"location updates"+loc.getLongitude());	
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
		

	}
	
	public void stopListeningGpsUpdates(){
        if(lm != null){
        	lm.removeUpdates(ll);
        }
    }
}