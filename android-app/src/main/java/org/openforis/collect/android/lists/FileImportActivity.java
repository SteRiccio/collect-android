package org.openforis.collect.android.lists;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.DataManager;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.model.CollectSurvey;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FileImportActivity extends Activity{
	
	private static final String TAG = "FileImportActivity";

	private TextView activityLabel;
	private TextView columnLabel;
	
	private String[] filesList;
	
	private ListView lv;
	
	private String path;
	
	private ProgressDialog pd;
	
	private int filesCount;
	
	private List<DataFile> dataFilesList;
	private FileListAdapter adapter;
	
	ProgressBar pb;
    Dialog dialog;
    int downloadedSize = 0;
    int totalSize = 0;
    TextView cur_val;
    String dwnload_file_path = "http://ar5.arbonaut.com/awfdatademo/planned/";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        setContentView(R.layout.uploadactivity);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try{
        	this.activityLabel = (TextView)findViewById(R.id.lblList); 
        	this.columnLabel = (TextView)findViewById(R.id.lblHeaders);
        	this.activityLabel.setText(getResources().getString(R.string.dataToImport));
        	
        	
        	this.columnLabel.setText(getResources().getString(R.string.dataToImportColumnHeaders));
        	
        	this.lv = (ListView)findViewById(R.id.file_list);
        	
        	path = Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.exported_data_folder);
        	
        	Button btn =(Button) findViewById(R.id.btnUpload);
        	btn.setText(getResources().getString(R.string.importFromFileButton));
        	btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                	pd = ProgressDialog.show(FileImportActivity.this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.downloadingDataToServerMessage));
                	for (int i=0;i<dataFilesList.size();i++){
                		if (FileImportActivity.this.adapter.checkList.get(i)[0]){
                			final int number = i;
                					
                            new Thread(new Runnable() {
                                public void run() {
                                     importFile(adapter.getItem(number).getName());
                                }
                              }).start();
                            filesCount++;
                		}
                	}
                	if (filesCount==0){
			    		pd.dismiss();
			    	}
                }
            });
        	this.filesCount = 0;
        } catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onCreate",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
        }
    }
    
    public void onResume(){
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
		
		int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);	
		changeBackgroundColor(backgroundColor);
		
		dataFilesList = new ArrayList<DataFile>();
		File dataFilesFolder = new File(path);
		File[] dataFiles = dataFilesFolder.listFiles();
		int filesNo = dataFiles.length;
		for (int i=0;i<filesNo;i++) {
	        //filesList[i] = serverFiles.get(i);
	        dataFilesList.add(new DataFile(dataFiles[i].getName(),"xml_icon"));
		}
		if (filesNo==0){
			this.activityLabel.setText(getResources().getString(R.string.noDataToImport));
		}
		int layout = (backgroundColor!=Color.WHITE)?R.layout.download_list_item_white:R.layout.download_list_item_black;
		this.adapter = new FileListAdapter(this, layout, dataFilesList, "single_file_import");
		lv.setAdapter(this.adapter);
		this.filesCount = 0;
    }
       
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
	
	@Override
	public void onBackPressed() {
		FileImportActivity.this.finish();
	}
	
    private void changeBackgroundColor(int backgroundColor){
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
		this.activityLabel.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.columnLabel.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
    }
    
    void importFile(String fileName){
        
        try {        	
            DataManager dataManager = new DataManager((CollectSurvey) ApplicationManager.getSurvey(),ApplicationManager.getSurvey().getSchema().getRootEntityDefinition(ApplicationManager.currRootEntityId).getName(),ApplicationManager.getLoggedInUser());
            Log.e("fileNAMEtoLoad","=="+fileName);
            fileName = Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.exported_data_folder)+"/"+fileName;
            dataManager.loadRecordFromXml(fileName);
            filesCount--;
            if (filesCount==0){
            	pd.dismiss();
    			/*AlertMessage.createPositiveDialog(FileImportActivity.this, true, null,
    					getResources().getString(R.string.downloadToDeviceSuccessfulTitle), 
    					getResources().getString(R.string.downloadToDeviceSuccessfulMessage),
    						getResources().getString(R.string.okay),
    			    		new DialogInterface.OnClickListener() {
    							@Override
    							public void onClick(DialogInterface dialog, int which) {
    								
    							}
    						},
    						null).show();*/
            }            	
        } catch (Exception e) {
            e.printStackTrace();
        }      
    }
}