package org.openforis.collect.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SandboxActivity extends Activity {
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		try{
			requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);	             
	        setContentView(R.layout.applicationwindow);			
		} catch (Exception e){
			e.printStackTrace();
		}
    }	
		
}
