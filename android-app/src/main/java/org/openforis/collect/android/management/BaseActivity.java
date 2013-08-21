package org.openforis.collect.android.management;

import java.io.File;
import java.util.HashMap;

import org.openforis.collect.android.R;
import org.openforis.collect.android.config.Configuration;
import org.openforis.collect.android.database.DatabaseHelper;
import org.openforis.collect.android.fields.UIElement;
import org.openforis.collect.android.lists.DownloadActivity;
import org.openforis.collect.android.lists.FileImportActivity;
import org.openforis.collect.android.lists.UploadActivity;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.android.screens.SettingsScreen;
import org.openforis.collect.android.service.ServiceFactory;
//import org.openforis.collect.manager.codelistimport.CodeListImportProcess;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.User;
import org.openforis.idm.metamodel.CodeList.CodeScope;

import android.app.Activity;
import android.app.ProgressDialog;
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
	}
	
    @Override
	public void onResume()
	{
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
		try{
			this.backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);	
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
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    { 
        switch (item.getItemId())
        {
			case R.id.menu_exit:
				AlertMessage.createPositiveNegativeDialog(BaseActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
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
	 							if (ApplicationManager.formScreenActivityList!=null){
	 								for (Activity formScreenActivity : ApplicationManager.formScreenActivityList){
	 									formScreenActivity.finish();
	 								}
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
	        case R.id.menu_save:
	        	CollectSurvey collectSurveySave = (CollectSurvey)ApplicationManager.getSurvey();	        	
	        	DataManager dataManagerSave = new DataManager(collectSurveySave,collectSurveySave.getSchema().getRootEntityDefinitions().get(0).getName(),ApplicationManager.getLoggedInUser());
	        	boolean isSuccess = dataManagerSave.saveRecord(this);
	        	if (isSuccess){
	        		AlertMessage.createPositiveDialog(BaseActivity.this, true, null,
							getResources().getString(R.string.savingDataTitle), 
							getResources().getString(R.string.savingDataSuccessMessage),
								getResources().getString(R.string.okay),
					    		new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								},
								null).show();
	        	} else {
	        		AlertMessage.createPositiveDialog(BaseActivity.this, true, null,
							getResources().getString(R.string.savingDataTitle), 
							getResources().getString(R.string.savingDataFailureMessage),
								getResources().getString(R.string.okay),
					    		new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								},
								null).show();
	        	}
	        	return true;
	        case R.id.menu_export:
	        	CollectSurvey collectSurveyExport = (CollectSurvey)ApplicationManager.getSurvey();	        	
	        	DataManager dataManagerExport = new DataManager(collectSurveyExport,collectSurveyExport.getSchema().getRootEntityDefinitions().get(0).getName(),ApplicationManager.getLoggedInUser());
	        	dataManagerExport.saveRecordToXml(ApplicationManager.currentRecord, Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.exported_data_folder));
	        	return true;
	        case R.id.menu_export_all:
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
	        	return true;
			case R.id.menu_upload:
				startActivity(new Intent(BaseActivity.this, UploadActivity.class));
			    return true;
			case R.id.menu_download:
				startActivity(new Intent(BaseActivity.this, DownloadActivity.class));
			    return true;
			case R.id.menu_import_from_file:
				startActivity(new Intent(BaseActivity.this, FileImportActivity.class));
			    return true;
			/*case R.id.menu_import_codelist_from_file:				
				try {
					String sdcardPath = Environment.getExternalStorageDirectory().toString();
				    CodeListImportProcess codeListImportProcess = new CodeListImportProcess(
				    		ServiceFactory.getCodeListManager(),
				    		ApplicationManager.getSurvey().getCodeList("species_code"), CodeScope.LOCAL, "en",
							new File(sdcardPath+getResources().getString(R.string.codelists_folder)+"/treecodes.csv"), true);
					codeListImportProcess.startProcessing();
				} catch (Exception e) {
					e.printStackTrace();
				}
			    return true;*/
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
				String aboutText = 						getResources().getString(R.string.lblApplicationName)+getResources().getString(R.string.app_name)
						+"\n"
						+getResources().getString(R.string.lblProgramVersionName)+versionName
						+"\n"
						+getResources().getString(R.string.lblFormVersionName)
						+ApplicationManager.getSurvey().getProjectName(ApplicationManager.selectedLanguage);
				if (ApplicationManager.getSurvey().getVersions()!=null){
					if (ApplicationManager.getSurvey().getVersions().size()>0){
						aboutText += " "+ApplicationManager.getSurvey().getVersions().get(ApplicationManager.getSurvey().getVersions().size()-1).getName();	
					}					
				}
				AlertMessage.createPositiveDialog(BaseActivity.this, true, null,
						getResources().getString(R.string.aboutTabTitle), 
						aboutText,
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