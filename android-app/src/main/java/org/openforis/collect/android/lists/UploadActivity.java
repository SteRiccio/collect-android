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
import org.openforis.collect.android.screens.FormScreen;

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
	
	private UploadActivity mainActivity = null;
	
	private ProgressDialog pd;
	
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
    			    	//SparseBooleanArray checkedItems = lv.getCheckedItemPositions();
    			    	pd = ProgressDialog.show(UploadActivity.this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.uploadingDataToServerMessage));
    			    	(new SendData()).execute();
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
        	
        	mainActivity = this;
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
    
    /*protected String addParamsToUrl(String url){
        if(!url.endsWith("?"))
            url += "?";

        List<NameValuePair> params = new LinkedList<NameValuePair>();

        if (lat != 0.0 && lon != 0.0){
            params.add(new BasicNameValuePair("lat", String.valueOf(lat)));
            params.add(new BasicNameValuePair("lon", String.valueOf(lon)));
        }

        if (address != null && address.getPostalCode() != null)
            params.add(new BasicNameValuePair("postalCode", address.getPostalCode()));
        if (address != null && address.getCountryCode() != null)
            params.add(new BasicNameValuePair("country",address.getCountryCode()));

        params.add(new BasicNameValuePair("user", agent.uniqueId));

        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;
        return url;
    }*/
    
    private class SendData extends AsyncTask {
    	 
        /**
         * Let's make the http request and return the result as a String.
         */
        protected String doInBackground(Object... args) {        	
        	Log.e("iloscPARAMETROW","=="+args.length);
        	Log.e("iloscDZIECI","=="+lv.getChildCount());
        	SparseBooleanArray checkedItems = lv.getCheckedItemPositions();
	    	for (int i=0;i<lv.getChildCount();i++){
	    		if (checkedItems.get(i)){
	    			try {
	    				Log.e("file",lv.getItemAtPosition(i).toString()+"=="+checkedItems.get(i));
					} catch (Exception e) {
						e.printStackTrace();
					}
	    		}
	    	}
            return ServerInterface.sendDataFiles();
        }
     
        /**
         * Parse the String result, and create a new array adapter for the list
         * view.
         */
        protected void onPostExecute(/*void result*/Object objResult) {
        	Log.e("zakonczono","egzekucje"+objResult);
            /*wywolujaceActivity.removeDialog(MainActivity.PLEASE_WAIT_DIALOG);
            Toast.makeText(wywolujaceActivity, "Obliczono!", Toast.LENGTH_SHORT).show();*/
            // check to make sure we're dealing with a string
            if(objResult != null && objResult instanceof String) {
                String result = (String) objResult;
                // this is used to hold the string array, after tokenizing
                String[] responseList;
     
                // we'll use a string tokenizer, with "," (comma) as the delimiter
                StringTokenizer tk = new StringTokenizer(result, ",");
     
                // now we know how long the string array is
                responseList = new String[tk.countTokens()];
     
                // let's build the string array
                int i = 0;
                while(tk.hasMoreTokens()) {
                    responseList[i++] = tk.nextToken();
                }
     
                // now we'll supply the data structure needed by this ListActivity
                //ArrayAdapter<String> newAdapter = new ArrayAdapter<String>(mainActivity, R.layout.list, responseList);
                //mainActivity.setListAdapter(newAdapter);
            }
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