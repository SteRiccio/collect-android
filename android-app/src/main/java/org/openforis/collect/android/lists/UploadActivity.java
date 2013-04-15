package org.openforis.collect.android.lists;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.BaseListActivity;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.RunnableHandler;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class UploadActivity extends BaseListActivity{
	
	private static final String TAG = "UploadActivity";

	private TextView activityLabel;
	
	private ArrayAdapter<String> adapter;
	
	private Boolean[] selections;
	
	private String[] filesList;
	
	private ListView lv; 
	
	private String path;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        setContentView(R.layout.uploadactivity);
        try{
        	if (isNetworkAvailable()){
        		this.activityLabel = (TextView)findViewById(R.id.lblList);
            	this.activityLabel.setText(getResources().getString(R.string.dataToUpload));
            	
            	this.lv = getListView();
            	
            	path = Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.exported_data_folder);
            	
            	Button btn =(Button) findViewById(R.id.btnUpload);
                btn.setOnClickListener(new OnClickListener() {
    			    @Override
    			    public void onClick(View v) {
    			    	SparseBooleanArray checkedItems = lv.getCheckedItemPositions();
    			    	for (int i=0;i<lv.getChildCount();i++){
    			    		if (checkedItems.get(i)){
    			    			try {
    			    				//postData(getStringFromFile(path+"/"+filesList[i]));
    							} catch (Exception e) {
    								e.printStackTrace();
    							}
    			    		}
    			    	}			    	
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
		
		int layout = (backgroundColor!=Color.WHITE)?R.layout.selectableitem_white:R.layout.selectableitem_black;
		this.adapter = new ArrayAdapter<String>(this,layout,filesList);
		this.setListAdapter(this.adapter);
    }
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i(getResources().getString(R.string.app_name),TAG+":onListItemClick");
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
		UploadActivity.this.finish();
	}
	
    private void changeBackgroundColor(int backgroundColor){
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
		this.activityLabel.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
    }
    
    public void postData(String sendData) {
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
    }
    
    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
        	sb.append(line).append("\n");
        }
        //Log.e("koniec","=="+sb.toString().substring(sb.length()-20,sb.length()-1));
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
}