package org.openforis.collect.android.lists;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.BaseListActivity;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.android.misc.ServerInterface;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UploadActivity extends BaseListActivity{
	
	private static final String TAG = "UploadActivity";

	private TextView activityLabel;
	private TextView columnLabel;
	
	private ArrayAdapter<String> adapter;
	
	private Boolean[] selections;
	
	private String[] filesList;
	
	private ListView lv;
	
	private String path;
	
	private ProgressDialog pd;
	
	private int filesCount;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        setContentView(R.layout.uploadactivity);
        try{
        	if (isNetworkAvailable()){
        		this.activityLabel = (TextView)findViewById(R.id.lblList);        		
            	this.activityLabel.setText(getResources().getString(R.string.dataToUpload));
            	
            	this.columnLabel = (TextView)findViewById(R.id.lblHeaders);
            	this.columnLabel.setText(getResources().getString(R.string.dataToUplaodColumnHeaders));
            	
            	this.lv = getListView();
            	
            	path = Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.exported_data_folder);            
            	
            	Button btn =(Button) findViewById(R.id.btnUpload);
                btn.setOnClickListener(new OnClickListener() {
    			    @Override
    			    public void onClick(View v) {
    			    	pd = ProgressDialog.show(UploadActivity.this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.uploadingDataToServerMessage));
    			    	CheckBox upload;
    			    	CheckBox overwrite;
    			    	for (int i=0;i<adapter.getCount();i++){
    			    		LinearLayout ll = (LinearLayout)lv.getChildAt(i);
    			    		upload = (CheckBox)ll.getChildAt(1);
    			    		overwrite = (CheckBox)ll.getChildAt(2);
    			    		if (upload.isChecked()){
    			    			(new SendData()).execute(adapter.getItem(i).toString(),overwrite.isChecked());
    		    				filesCount++;	
    			    		}
    			    	}
    			    	if (filesCount==0){
    			    		pd.dismiss();
    			    	}
    			    	/*
    			    	pd = ProgressDialog.show(UploadActivity.this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.uploadingDataToServerMessage));
    			    	
    		        	SparseBooleanArray checkedItems = lv.getCheckedItemPositions();
    			    	for (int i=0;i<lv.getChildCount();i++){
    			    		if (checkedItems.get(i)){
    			    			try {
    			    				(new SendData()).execute(lv.getItemAtPosition(i).toString());
    			    				filesCount++;
    							} catch (Exception e) {
    								e.printStackTrace();
    							}
    			    		}
    			    	}
    			    	if (filesCount==0){
    			    		pd.dismiss();
    			    	}*/
    			    }
    		    });
        	} else {
        		AlertMessage.createPositiveDialog(UploadActivity.this, true, null,
						getResources().getString(R.string.noInternetTitle), 
						getResources().getString(R.string.noInternetMessage),
							getResources().getString(R.string.okay),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									UploadActivity.this.finish();
								}
							},
							null).show();
        	}
        	
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
		
		File dataFilesFolder = new File(path);
		File[] dataFiles = dataFilesFolder.listFiles();
		int filesNo = dataFiles.length;
		filesList = new String[filesNo];
		this.selections = new Boolean[filesNo];
		for (int i=0;i<filesNo;i++) {
			File inFile = dataFiles[i];
	        filesList[i] = inFile.getName();
	        this.selections[i] = false;
		}
		if (filesNo==0){
			this.activityLabel.setText(getResources().getString(R.string.noDataToUpload)+" "+getResources().getString(R.string.exported_data_folder));
		}
		//int layout = (backgroundColor!=Color.WHITE)?R.layout.selectableitem_white:R.layout.selectableitem_black;
		int layout = (backgroundColor!=Color.WHITE)?R.layout.upload_list_item_white:R.layout.upload_list_item_black;
		//this.adapter = new ArrayAdapter<String>(this,layout,filesList);
		this.adapter = new ArrayAdapter<String>(this, layout, R.id.lblFileName, filesList);
		this.setListAdapter(this.adapter);
    }
    
    /*@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i(getResources().getString(R.string.app_name),TAG+":onListItemClick");
	}*/
    
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
		UploadActivity.this.finish();
	}
	
    private void changeBackgroundColor(int backgroundColor){
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
		this.activityLabel.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		this.columnLabel.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
    }
    
    /*public void postData(String sendData) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(getResources().getString(R.string.serverAddress));
        
        try {
            StringEntity se = new StringEntity(sendData);
            httppost.setEntity(se);
            HttpResponse response = httpclient.execute(httppost);
            Log.e("response","=="+response.getEntity().getContent().toString());
        } 
        catch (ClientProtocolException e) 
        {
            Log.e("ClientProtocolException","==");     
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            Log.e("IOException","==");
            e.printStackTrace();
        }
    }*/
    
    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
        	if (line.contains("<value>")&&line.contains("</value>")){
        		line = line.substring(line.indexOf("<value>")+7,line.indexOf("</value>"));
        	} else if (line.contains("<code>")&&line.contains("<code>")){
        		line = line.substring(line.indexOf("<code>")+6,line.indexOf("</code>"));        		
        	}
        	sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();        
        return ret;
    }
    
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    private class SendData extends AsyncTask {
    	 
        /**
         * Let's make the http request and return the result as a String.
         */
        protected String doInBackground(Object... args) {
            try {            
				return ServerInterface.sendDataFiles(UploadActivity.getStringFromFile(Environment.getExternalStorageDirectory().toString()+String.valueOf(getResources().getString(R.string.exported_data_folder)+"/"+args[0])),(Boolean)args[1]);
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
        }
     
        /**
         * Parse the String result, and create a new array adapter for the list
         * view.
         */
        protected void onPostExecute(Object objResult) {
        	Log.e("onPostExecute","=="+objResult);
        	filesCount--;

            if(objResult != null && objResult instanceof String) {
                String result = (String) objResult;

                String[] responseList;
     
                StringTokenizer tk = new StringTokenizer(result, ",");
     
                responseList = new String[tk.countTokens()];
     
                int i = 0;
                while(tk.hasMoreTokens()) {
                    responseList[i++] = tk.nextToken();
                }
            }
            if (filesCount==0){
            	pd.dismiss();
    			AlertMessage.createPositiveDialog(UploadActivity.this, true, null,
    					getResources().getString(R.string.uploadToServerSuccessfulTitle), 
    					getResources().getString(R.string.uploadToServerSuccessfulMessage),
    						getResources().getString(R.string.okay),
    			    		new DialogInterface.OnClickListener() {
    							@Override
    							public void onClick(DialogInterface dialog, int which) {
    								
    							}
    						},
    						null).show();	
            }            	
        }
     
    }
}