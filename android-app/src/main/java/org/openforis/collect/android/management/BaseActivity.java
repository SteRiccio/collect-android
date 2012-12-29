package org.openforis.collect.android.management;

import org.openforis.collect.android.R;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.screens.SettingsScreen;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
    @Override
	public void onResume()
	{
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
		this.backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);
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
	        /*case R.id.menu_open:
	        	AlertMessage.createPositiveNegativeDialog(BaseActivity.this, true, getResources().getDrawable(R.drawable.warningsign),
	    				getResources().getString(R.string.openingPlotListTitle), getResources().getString(R.string.openingPlotListMessage),
	    				getResources().getString(R.string.yes), getResources().getString(R.string.no),
	    	    		new DialogInterface.OnClickListener() {
	    					@Override
	    					public void onClick(DialogInterface dialog, int which) {
	    						//ApplicationManager.this.startActivityForResult(new Intent(ApplicationManager.this, ClusterChoiceActivity.class),getResources().getInteger(R.integer.clusterChoiceSuccessful));
	    						//showRecordsListScreen();
	    						BaseActivity.this.startActivityForResult(new Intent(BaseActivity.this, ClusterChoiceActivity.class),getResources().getInteger(R.integer.clusterChoiceSuccessful));
	    					}
	    				},
	    	    		new DialogInterface.OnClickListener() {
	    					@Override
	    					public void onClick(DialogInterface dialog, int which) {
	    						
	    					}
	    				},
	    				null).show();
	            return true;
	 		*/
	        case R.id.menu_save:
	        	//saveData();	
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
}