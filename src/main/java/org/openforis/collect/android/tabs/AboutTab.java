package org.openforis.collect.android.tabs;

import org.openforis.collect.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class AboutTab extends Activity {

	private static final String TAG = "AboutTab";
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.abouttab);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate"); 
	}

}