package org.openforis.collect.android.management;

import org.openforis.collect.android.R;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.android.screens.SettingsScreen;
import org.openforis.collect.model.CollectSurvey;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;


public class BaseActivity extends Activity {
	
	private static final String TAG = "BaseActivity";
	
	protected int backgroundColor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //setContentView(R.layout.welcomescreen);
	}
	
    @Override
	public void onResume()
	{
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
		this.backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);
		Thread thread = new Thread(new RunnableHandler(0, Environment.getExternalStorageDirectory().toString()
				+getResources().getString(R.string.logs_folder)
				+getResources().getString(R.string.logs_file_name)
				+"DEBUG_LOG_SAFETY"
				+System.currentTimeMillis()
				+getResources().getString(R.string.log_file_extension)));
		thread.start();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    { 
        switch (item.getItemId())
        {
	        case R.id.menu_save:
	        	CollectSurvey collectSurveySave = (CollectSurvey)ApplicationManager.getSurvey();	        	
	        	DataManager dataManagerSave = new DataManager(collectSurveySave,collectSurveySave.getSchema().getRootEntityDefinitions().get(0).getName(),ApplicationManager.getLoggedInUser());
	        	dataManagerSave.saveRecord(this);
	        	return true;
	        case R.id.menu_export:
	        	CollectSurvey collectSurveyExport = (CollectSurvey)ApplicationManager.getSurvey();	        	
	        	DataManager dataManagerExport = new DataManager(collectSurveyExport,collectSurveyExport.getSchema().getRootEntityDefinitions().get(0).getName(),ApplicationManager.getLoggedInUser());
	        	dataManagerExport.saveRecordToXml(ApplicationManager.currentRecord, Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.exported_data_folder));
	        	return true;	        		
			case R.id.menu_settings:
				startActivity(new Intent(BaseActivity.this,SettingsScreen.class));
			    return true;			    
			case R.id.menu_about:
				String versionName;
				try {
					versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
				} catch (NameNotFoundException e) {
					versionName = "";
				}
				AlertMessage.createPositiveDialog(BaseActivity.this, true, null,
						getResources().getString(R.string.aboutTabTitle), 
						getResources().getString(R.string.lblApplicationName)+getResources().getString(R.string.app_name)
						+"\n"
						+getResources().getString(R.string.lblProgramVersionName)+versionName
						+"\n"
						+getResources().getString(R.string.lblFormVersionName)+ApplicationManager.getSurvey().getProjectName(null)+" "+ApplicationManager.getSurvey().getVersions().get(ApplicationManager.getSurvey().getVersions().size()-1).getName(),
							getResources().getString(R.string.okay),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							},
							null).show();
			        return true;
			        
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
    
    @Override
    public void onPause(){
    	Log.i(getResources().getString(R.string.app_name),TAG+":onPause");
    	super.onPause();
    }
}