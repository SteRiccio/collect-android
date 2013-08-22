package org.openforis.collect.android.misc;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.QwertyKeyListener;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ImportSpeciesFromCsvActivity extends Activity {

	private int backgroundColor;
	//UI elements
	private TextView lblPath;
	private EditText txtFileName;
	private Button btnImport;
	
	
//	public ImportSpeciesFromCsvActivity() {
//		// TODO Auto-generated constructor stub
//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getWindow().requestFeature(Window.FEATURE_OPTIONS_PANEL);
	    Log.i(getResources().getString(R.string.app_name), "ImportSpeciesFromCsvActivity onCreate");
	    setContentView(R.layout.import_species_form_csv);
	    //Create UI
	    this.lblPath = (TextView)findViewById(R.id.lblPathToSpeciesCsv);
	    this.txtFileName = (EditText)findViewById(R.id.txtPathToSpeciesCsv);
	    this.btnImport = (Button)findViewById(R.id.btnImportSpeciesFromCsv);
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(getResources().getString(R.string.app_name), "ImportSpeciesFromCsvActivity onResume");
    	// Set background color
		this.backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);		
		changeBackgroundColor(this.backgroundColor);        
		
		// Set onFocus listener for Search texbox
		this.txtFileName.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	// Get current settings about software keyboard for text fields
		    	if(hasFocus){
				    	boolean valueForText = false;				   
				    	if (ApplicationManager.appPreferences!=null){
				    		valueForText = ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnTextField), false);
				    	}
				    	// Switch on or off Software keyboard depend of settings
				    	if(valueForText){
				    		Log.i(getResources().getString(R.string.app_name), "From ClickListener: Setting path to file field is: " + valueForText);
				    		txtFileName.setKeyListener(new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false));
				        }
				    	else {
				    		Log.i(getResources().getString(R.string.app_name), "From ClickListener: Setting path to file field is: " + valueForText);
				    		txtFileName.setInputType(InputType.TYPE_NULL);
				    	}
		    	}
		    }
	    });
		
		//When user click inside txtSearch
		this.txtFileName.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
		    	//Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
		    	//Boolean valueForText = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnTextField));
		    	boolean valueForText = false;				   
		    	if (ApplicationManager.appPreferences!=null){
		    		valueForText = ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnTextField), false);
		    	}
		    	// Switch on or off Software keyboard depend of settings
		    	if(valueForText){
		    		Log.i(getResources().getString(R.string.app_name), "From ClickListener: Setting path to file field is: " + valueForText);
		    		txtFileName.setKeyListener(new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false));
		        }
		    	else {
		    		Log.i(getResources().getString(R.string.app_name), "From ClickListener: Setting path to file field is: " + valueForText);
		    		txtFileName.setInputType(InputType.TYPE_NULL);
		    	}
			}			
		});			
		
		// Add click listener for button Search
		this.btnImport.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Log.i(getResources().getString(R.string.app_name), "Import species from csv started");
				doImport(txtFileName.getText().toString());
			}});		
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	Log.i(getResources().getString(R.string.app_name), "Button BACK pressed from ImportSpeciesFromCsvActivity");
		    //Finish activity  	
		    finish();  	
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
    private void changeBackgroundColor(int backgroundColor){
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
		int color = (backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK;
		//Set text color
		this.lblPath.setTextColor(color);
		this.txtFileName.setTextColor(color);
		this.btnImport.setTextColor(color);
    }	
    
    private void doImport(String path){
    	//TODO: Implement import from csv
    	Log.i(getResources().getString(R.string.app_name), "Import species from: " + path);
    }
}
