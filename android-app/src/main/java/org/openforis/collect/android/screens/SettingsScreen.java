package org.openforis.collect.android.screens;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.misc.RunnableHandler;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

public class SettingsScreen extends Activity{

	private static final String TAG = "SettingsScreen";
	
	private TextView tvScreenTitle;
	
	private CheckBox chckSoftKeyboardOnText;
	private CheckBox chckSoftKeyboardOnNumeric;
	private CheckBox chckWhiteBackground;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.settingstab);
        try{
        	Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        	
        	this.tvScreenTitle = (TextView)findViewById(R.id.lblTitle);
    		this.tvScreenTitle.setTextSize(getResources().getInteger(R.integer.breadcrumbFontSize));
        	
        	this.chckSoftKeyboardOnText = (CheckBox)findViewById(R.id.chkSoftKeyboardOnText);        	
    		this.chckSoftKeyboardOnText.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
    				editor.putBoolean(getResources().getString(R.string.showSoftKeyboardOnText), chckSoftKeyboardOnText.isChecked());
    				editor.commit();
      			}
    	    });
    		
        	this.chckSoftKeyboardOnNumeric = (CheckBox)findViewById(R.id.chkSoftKeyboardOnNumeric);        	
    		this.chckSoftKeyboardOnNumeric.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
    				editor.putBoolean(getResources().getString(R.string.showSoftKeyboardOnNumeric), chckSoftKeyboardOnNumeric.isChecked());
    				editor.commit();
      			}
    	    });
    		
    		this.chckWhiteBackground = (CheckBox)findViewById(R.id.chckWhiteBackground);        	
    		this.chckWhiteBackground.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
    				editor.putInt(getResources().getString(R.string.backgroundColor), (chckWhiteBackground.isChecked()?Color.WHITE:Color.BLACK));
    				editor.commit();
    				int backgroundColor = (ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE)==Color.WHITE)?Color.WHITE:Color.BLACK;
    				changeBackgroundColor(backgroundColor);
      			}
    	    });
    		    	  	
    		this.chckSoftKeyboardOnText.setChecked(ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnText), false));
    		this.chckSoftKeyboardOnNumeric.setChecked(ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnNumeric), false));
    		
            
        } catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onCreate",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
		}
	}
	
    @Override
	public void onResume()
	{
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
		
		int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);
		this.chckWhiteBackground.setChecked((backgroundColor==Color.WHITE)?true:false);		
		changeBackgroundColor(backgroundColor);
	}
    
    private void changeBackgroundColor(int backgroundColor){
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
		this.tvScreenTitle.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.chckSoftKeyboardOnText.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.chckSoftKeyboardOnNumeric.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.chckWhiteBackground.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
    }
}