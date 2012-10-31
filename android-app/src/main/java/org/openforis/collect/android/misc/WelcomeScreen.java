package org.openforis.collect.android.misc;

import org.openforis.collect.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class WelcomeScreen extends Activity {

	private static final String TAG = "WelcomeScreen";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
		setContentView(R.layout.welcomescreen);
		welcomeThread.start();
	}
	
	private Thread welcomeThread = new Thread() {
		@Override
		public void run() {
			try {
				super.run();
				Log.i(getResources().getString(R.string.app_name),TAG+":run");
				Thread.sleep(getIntent().getIntExtra(getResources().getString(R.string.sleepTime), 5000));
			} catch (Exception e) {
				RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":run",
	    				Environment.getExternalStorageDirectory().toString()
	    				+getResources().getString(R.string.logs_folder)
	    				+getResources().getString(R.string.logs_file_name)
	    				+System.currentTimeMillis()
	    				+getResources().getString(R.string.log_file_extension));
			} 	 finally {
				finish();
			}
		}
	};
}
