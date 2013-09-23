package org.openforis.collect.android.management;

import java.io.File;
import java.io.IOException;

import org.openforis.collect.android.R;
import org.openforis.collect.android.database.DatabaseHelper;
import org.openforis.collect.android.filechooser.FileChooser;
import org.openforis.collect.android.lists.FileImportActivity;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.android.screens.SettingsScreen;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;


public class BaseListActivity extends ListActivity {
	
	private static final String TAG = "BaseListActivity";
	
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
		try {
			this.backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);
			if (ApplicationManager.getSurvey()==null){
				if (Build.VERSION.SDK_INT >= 11) {
					//invalidateOptionsMenu();
					ActivityCompat.invalidateOptionsMenu(BaseListActivity.this);
				}
			}
		}		
		catch (Exception e){
			e.printStackTrace();
		}
		Log.e("CMA-114","ApplicationManager.appPreferences==null"+(ApplicationManager.appPreferences==null));
		Log.e("CMA-114","getResources().getString(R.string.backgroundColor)==null"+(getResources().getString(R.string.backgroundColor)==null));
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
        menuInflater.inflate(R.layout.list_menu, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
    	Log.e("onPrepareOptionsMenu","=="+(ApplicationManager.getSurvey()!=null));
    	menu.findItem(R.id.menu_import_from_file).setVisible(ApplicationManager.getSurvey()!=null);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    { 
        switch (item.getItemId())
        {      
        	case R.id.menu_exit:
				AlertMessage.createPositiveNegativeDialog(BaseListActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
	 					getResources().getString(R.string.exitAppTitle), getResources().getString(R.string.exitAppMessage),
	 					getResources().getString(R.string.yes), getResources().getString(R.string.no),
	 		    		new DialogInterface.OnClickListener() {
	 						@Override
	 						public void onClick(DialogInterface dialog, int which) {
	 							if (ApplicationManager.rootEntitySelectionActivity!=null){
	 								ApplicationManager.rootEntitySelectionActivity.finish();
	 							}
	 							if (ApplicationManager.recordSelectionActivity!=null){
	 								ApplicationManager.recordSelectionActivity.finish();
	 							}
	 							if (ApplicationManager.formSelectionActivity!=null){
	 								ApplicationManager.formSelectionActivity.finish();
	 							}
	 							ApplicationManager.mainActivity.finish();					
	 						}
	 					},
	 		    		new DialogInterface.OnClickListener() {
	 						@Override
	 						public void onClick(DialogInterface dialog, int which) {
	 							
	 						}
	 					},
	 					null).show();
			    return true;
        	/*case R.id.menu_export_all:
	        	final ProgressDialog pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.backupingData));
	        	Thread backupThread = new Thread() {
	        		@Override
	        		public void run() {
	        			try {
	        				super.run();
	        				Log.i(getResources().getString(R.string.app_name),TAG+":run");
	        				CollectSurvey collectSurveyExportAll = (CollectSurvey)ApplicationManager.getSurvey();	        	
	        	        	DataManager dataManagerExportAll = new DataManager(collectSurveyExportAll,collectSurveyExportAll.getSchema().getRootEntityDefinitions().get(0).getName(),ApplicationManager.getLoggedInUser());
	        	        	dataManagerExportAll.saveAllRecordsToFile(Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.exported_data_folder));
	        			} catch (Exception e) {
	        				RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":run",
	        	    				Environment.getExternalStorageDirectory().toString()
	        	    				+getResources().getString(R.string.logs_folder)
	        	    				+getResources().getString(R.string.logs_file_name)
	        	    				+System.currentTimeMillis()
	        	    				+getResources().getString(R.string.log_file_extension));
	        			} finally {
	        				pd.dismiss();
	        			}
	        		}
	        	};
	        	backupThread.start();	        	
	        	return true;*/    
			/*case R.id.menu_upload:
				startActivity(new Intent(BaseListActivity.this, UploadActivity.class));
			    return true;
			case R.id.menu_download:
				startActivity(new Intent(BaseListActivity.this, DownloadActivity.class));
			    return true;*/
			case R.id.menu_import_from_file:
				startActivity(new Intent(BaseListActivity.this, FileImportActivity.class));
			    return true;
			case R.id.menu_import_database_from_file:
				//startActivity(new Intent(BaseListActivity.this, FileChooser.class));
				startActivityForResult(new Intent(BaseListActivity.this, FileChooser.class), getResources().getInteger(R.integer.chooseDatabaseFile));
			    return true;
			case R.id.menu_backup_database: 
				try {
					DatabaseHelper.backupDatabase(Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.application_folder), getResources().getString(R.string.database_file_name)+System.currentTimeMillis()+getResources().getString(R.string.database_file_extension));
					AlertMessage.createPositiveDialog(BaseListActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
		 					getResources().getString(R.string.backupingDatabaseTitle), getResources().getString(R.string.backupingDatabaseMessageSuccess),
		 					getResources().getString(R.string.okay), 
		 		    		null,
		 					null).show();
				} catch (NotFoundException e) {
					AlertMessage.createPositiveDialog(BaseListActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
		 					getResources().getString(R.string.backupingDatabaseTitle), getResources().getString(R.string.backupingDatabaseMessageFileNotFound),
		 					getResources().getString(R.string.okay), 
		 		    		null,
		 					null).show();
				} catch (IOException e) {
					AlertMessage.createPositiveDialog(BaseListActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
		 					getResources().getString(R.string.backupingDatabaseTitle), getResources().getString(R.string.backupingDatabaseMessageIOException),
		 					getResources().getString(R.string.okay), 
		 		    		null,
		 					null).show();
				}
				
			    return true; 				    
			case R.id.menu_settings:
				startActivity(new Intent(BaseListActivity.this, SettingsScreen.class));
			    return true;			    
			case R.id.menu_about:
				String versionName;
				try {
					versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
				} catch (NameNotFoundException e) {
					versionName = "";
				}
				String about = getResources().getString(R.string.lblApplicationName)+getResources().getString(R.string.app_name)
						+"\n"
						+getResources().getString(R.string.lblProgramVersionName)+versionName
						+"\n";
				if (ApplicationManager.getSurvey()!=null){
					String formVersionName = ApplicationManager.getSurvey().getProjectName(null)+" "+ApplicationManager.getSurvey().getVersions().get(ApplicationManager.getSurvey().getVersions().size()-1).getName();
					if (formVersionName!=null){
						about+= getResources().getString(R.string.lblFormVersionName)+formVersionName;
					}	
				}				
				AlertMessage.createPositiveDialog(BaseListActivity.this, true, null,
						getResources().getString(R.string.aboutTabTitle), 
						about,
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {    	
	    super.onActivityResult(requestCode, resultCode, data);
	    try{
	    	if (requestCode==getResources().getInteger(R.integer.chooseDatabaseFile)){
				Log.e("choosing database","=========================");
				Log.e("CHOSEN FILE","=="+data.getStringExtra(getResources().getString(R.string.databaseFileName)));
				String selectedFileName = data.getStringExtra(getResources().getString(R.string.databaseFileName));
				if (selectedFileName.endsWith(".db")){
					DatabaseHelper.copyDataBase(selectedFileName);	
				} else {
					AlertMessage.createPositiveDialog(BaseListActivity.this, true, null,
							getResources().getString(R.string.copyingDatabaseTitle), 
							getResources().getString(R.string.copyingDatabaseWrongFileExtensionMessage),
								getResources().getString(R.string.okay),
					    		new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								},
								null).show();	
				}
			}
	    } catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onActivityResult",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
	    }
    }

}