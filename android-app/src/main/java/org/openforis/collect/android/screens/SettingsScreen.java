package org.openforis.collect.android.screens;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.misc.RunnableHandler;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsScreen extends Activity{

	private static final String TAG = "SettingsScreen";
	
	private TextView tvScreenTitle;
	
	private CheckBox chckSoftKeyboardOnText;
	private CheckBox chckSoftKeyboardOnNumeric;
	private CheckBox chckWhiteBackground;
	
	private TextView tvGpsMaxWaitingTime;
	private EditText txtGpsMaxWaitingTime;
	
	private TextView tvLanguage;
	private Spinner spinLanguage;
	private ArrayAdapter<String> languageAdapter;
	
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
    				editor.putBoolean(getResources().getString(R.string.showSoftKeyboardOnTextField), chckSoftKeyboardOnText.isChecked());
    				editor.commit();
      			}
    	    });
    		
        	this.chckSoftKeyboardOnNumeric = (CheckBox)findViewById(R.id.chkSoftKeyboardOnNumeric);        	
    		this.chckSoftKeyboardOnNumeric.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
    				editor.putBoolean(getResources().getString(R.string.showSoftKeyboardOnNumericField), chckSoftKeyboardOnNumeric.isChecked());
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
    		    	  	
    		this.chckSoftKeyboardOnText.setChecked(ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnTextField), false));
    		this.chckSoftKeyboardOnNumeric.setChecked(ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnNumericField), false));

			this.tvGpsMaxWaitingTime = (TextView)findViewById(R.id.lblGpsTimeout);
			this.txtGpsMaxWaitingTime = (EditText)findViewById(R.id.txtGpsTimeout);
			this.txtGpsMaxWaitingTime.setText(String.valueOf(ApplicationManager.appPreferences.getInt(getResources().getString(R.string.gpsTimeout), getResources().getInteger(R.integer.gpsTimeoutInMs))/1000));
            this.txtGpsMaxWaitingTime.addTextChangedListener(new TextWatcher(){
		        public void afterTextChanged(Editable s) {        			            
					SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
					editor = ApplicationManager.appPreferences.edit();
					try{
						editor.putInt(getResources().getString(R.string.gpsTimeout), Integer.valueOf(s.toString())*1000);
					} catch (Exception e){
						int gpsTimeout = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.gpsTimeout), getResources().getInteger(R.integer.gpsTimeoutInMs));
						editor.putInt(getResources().getString(R.string.gpsTimeout), gpsTimeout);	
					}
					editor.commit();
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		    });
            
            this.tvLanguage = (TextView)findViewById(R.id.lblLanguage);
            this.spinLanguage = (Spinner) findViewById(R.id.languageSpinner);  
         	this.spinLanguage.setOnItemSelectedListener(new OnItemSelectedListener() {
 			    @Override
 			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
 			    	SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
 			    	String language = spinLanguage.getAdapter().getItem(position).toString();
     				editor.putString(getResources().getString(R.string.selectedLanguage), language);
     				editor.commit();
     				ApplicationManager.selectedLanguage = language;
 			    }

 			    @Override
 			    public void onNothingSelected(AdapterView<?> parentView) {
 			    	
 			    }

 			});
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
		if (ApplicationManager.getSurvey()!=null){
        	List<String> languageList = ApplicationManager.getSurvey().getLanguages();
            this.languageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languageList);
         	this.languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         	this.spinLanguage.setAdapter(this.languageAdapter);
         	String language = ApplicationManager.appPreferences.getString(getResources().getString(R.string.selectedLanguage), getResources().getString(R.string.defaultLanguage));
    		for (int i=0;i<this.languageAdapter.getCount();i++){
    			if (this.languageAdapter.getItem(i).equals(language)){
    				this.spinLanguage.setSelection(i);
    				break;
    			}
    		}
        } else {
        	this.tvLanguage.setVisibility(View.GONE);
        	this.spinLanguage.setVisibility(View.GONE);
        }
		
	}
    
    private void changeBackgroundColor(int backgroundColor){
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
		this.tvScreenTitle.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.chckSoftKeyboardOnText.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.chckSoftKeyboardOnNumeric.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.chckWhiteBackground.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.tvGpsMaxWaitingTime.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.tvLanguage.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		//this.txtGpsMaxWaitingTime.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
    }
}