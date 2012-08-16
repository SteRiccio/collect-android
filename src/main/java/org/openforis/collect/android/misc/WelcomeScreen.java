package org.openforis.collect.android.misc;

import org.openforis.collect.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class WelcomeScreen extends Activity {

	private static final String TAG = "WelcomeScreen";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
		setContentView(R.layout.welcomescreen);
		
		Thread welcomeThread = new Thread() {
			@Override
			public void run() {
				try {
					super.run();
					Log.i(getResources().getString(R.string.app_name),TAG+":run");
					Thread.sleep(getIntent().getIntExtra("sleepTime", 5000));
				} catch (Exception e) {
				} 	 finally {
					finish();
				}
			}
		};
		welcomeThread.start();		
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.i(getResources().getString(R.string.app_name),TAG+":onDestroy");
	}
}
