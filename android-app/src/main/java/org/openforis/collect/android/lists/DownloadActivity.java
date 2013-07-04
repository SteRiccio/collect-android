package org.openforis.collect.android.lists;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.DataManager;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.android.misc.ServerInterface;
import org.openforis.collect.model.CollectSurvey;

import android.app.Activity;
import android.app.Dialog;
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
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadActivity extends Activity{
	
	private static final String TAG = "DownloadActivity";

	private TextView activityLabel;
	private TextView columnLabel;
	
	//private ArrayAdapter<String> adapter;
	
	
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
        	if (isNetworkAvailable()){
        		      		
            	this.activityLabel.setText(getResources().getString(R.string.dataToDownload));
            	
            	
            	this.columnLabel.setText(getResources().getString(R.string.dataToDownlaodColumnHeaders));
            	
            	this.lv = (ListView)findViewById(R.id.file_list);
            	
            	path = Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.exported_data_folder);            
            	
            	Button btn =(Button) findViewById(R.id.btnUpload);
            	btn.setText(getResources().getString(R.string.downloadFromServerButton));
            	btn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	//showProgress(dwnload_file_path);
                    	pd = ProgressDialog.show(DownloadActivity.this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.downloadingDataToServerMessage));
                    	for (int i=0;i<dataFilesList.size();i++){
                    		if (DownloadActivity.this.adapter.checkList.get(i)[0]){
                    			final int number = i;
                    					
                                new Thread(new Runnable() {
                                    public void run() {
                                         downloadFile(adapter.getItem(number).getName());
                                    }
                                  }).start();
                                filesCount++;
                    		}
                    	}
                    	if (filesCount==0){
    			    		pd.dismiss();
    			    	}
    			    	/*CheckBox upload;
    			    	//CheckBox overwrite;
    			    	for (int i=0;i<adapter.getCount();i++){
    			    		LinearLayout ll = (LinearLayout)lv.getChildAt(i);
    			    		Log.e("ll==null",i+"=="+(ll==null));
    			    		upload = (CheckBox)ll.getChildAt(1);
    			    		//overwrite = (CheckBox)ll.getChildAt(2);
    			    		final int number = i;
    			    		if (upload.isChecked()){
    			    			showProgress(dwnload_file_path);                             
                                new Thread(new Runnable() {
                                    public void run() {
                                         downloadFile(adapter.getItem(number).toString());
                                    }
                                  }).start();
    			    			//(new SendData()).execute(adapter.getItem(i).toString(),overwrite.isChecked());
    		    				filesCount++;	
    			    		}
    			    	}*/
    			    	/*if (filesCount==0){
    			    		dialog.dismiss();
    			    	}*/
                        /* showProgress(dwnload_file_path);                             
                            new Thread(new Runnable() {
                                public void run() {
                                     downloadFile(filesList);
                                }
                              }).start();*/
                    }
                });              
        	} else {
        		AlertMessage.createPositiveDialog(DownloadActivity.this, true, null,
						getResources().getString(R.string.noInternetTitle), 
						getResources().getString(R.string.noInternetMessage),
							getResources().getString(R.string.okay),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									DownloadActivity.this.finish();
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
		
		/*File dataFilesFolder = new File(path);
		File[] dataFiles = dataFilesFolder.listFiles();
		int filesNo = dataFiles.length;
		filesList = new String[filesNo];
		this.selections = new Boolean[filesNo];*/
		/*List<String> serverFiles = ServerInterface.getFilesList();
		int filesNo = serverFiles.size();
		filesList = new String[filesNo];
		this.selections = new Boolean[filesNo];
		for (int i=0;i<filesNo;i++) {
	        filesList[i] = serverFiles.get(i);
	        this.selections[i] = false;
		}*/
		/*for (int i=0;i<filesNo;i++) {
			File inFile = dataFiles[i];
	        filesList[i] = inFile.getName();
	        this.selections[i] = false;
		}*/
		/*if (filesNo==0){
			this.activityLabel.setText(getResources().getString(R.string.noDataToUpload));
		}*/
		//int layout = (backgroundColor!=Color.WHITE)?R.layout.selectableitem_white:R.layout.selectableitem_black;
		//int layout = (backgroundColor!=Color.WHITE)?R.layout.download_list_item_white:R.layout.download_list_item_black;
		//this.adapter = new ArrayAdapter<String>(this,layout,filesList);
		dataFilesList = new ArrayList<DataFile>();
		List<String> serverFiles = ServerInterface.getFilesList();
		int filesNo = serverFiles.size();
		for (int i=0;i<filesNo;i++) {
	        //filesList[i] = serverFiles.get(i);
	        dataFilesList.add(new DataFile(serverFiles.get(i),"xml_icon"));
		}
		if (filesNo==0){
			this.activityLabel.setText(getResources().getString(R.string.noDataToDownload));
		}
		int layout = (backgroundColor!=Color.WHITE)?R.layout.download_list_item_white:R.layout.download_list_item_black;
		this.adapter = new FileListAdapter(this, layout, dataFilesList, "download");
		lv.setAdapter(this.adapter);
		//this.adapter = new ArrayAdapter<String>(this, layout, R.id.lblFileName, filesList);
		//this.setListAdapter(this.adapter);
		
		this.filesCount = 0;
    }
    
    /*public List clientServerFileList(){
    	Log.e("clientServerFileList","===");
        URL url;
        List serverDir = null;
        List serverFiles = null;
        try {
            url = new URL("http://ar5.arbonaut.com/webforest/public/test/");           
            ApacheURLLister lister = new ApacheURLLister();   
            serverFiles = lister.listFiles(url);
            serverDir = lister.listAll(url);
        } 
        catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR ON GETTING FILE","Error is " +e);
        }
        Log.e("serverFilesList","=="+serverFiles);
        Log.e("serverList","=="+serverDir);
        return serverDir;
    } */
    
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
		DownloadActivity.this.finish();
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
            	String survey_id = ApplicationManager.appPreferences.getString(getResources().getString(R.string.surveyId), "99");
            	String username = ApplicationManager.appPreferences.getString(getResources().getString(R.string.username), "collect");
				return ServerInterface.sendDataFiles(DownloadActivity.getStringFromFile(Environment.getExternalStorageDirectory().toString()+String.valueOf(getResources().getString(R.string.exported_data_folder)+"/"+args[0])), survey_id, username,(Boolean)args[1]);
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
    			AlertMessage.createPositiveDialog(DownloadActivity.this, true, null,
    					getResources().getString(R.string.downloadToDeviceSuccessfulTitle), 
    					getResources().getString(R.string.downloadToDeviceSuccessfulMessage),
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
    
    void downloadFile(String fileName){
        
        try {        	
            URL url = new URL(dwnload_file_path+fileName);
            Log.e("file","=="+dwnload_file_path+fileName);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
 
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
 
            //connect
            urlConnection.connect();
 
            //set the path where we want to save the file          
            File SDCardRoot = new File(Environment.getExternalStorageDirectory()+getResources().getString(R.string.imported_data_folder));
            //create a new file, to save the downloaded file
            File file = new File(SDCardRoot,fileName);
  
            FileOutputStream fileOutput = new FileOutputStream(file);
 
            //Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();
 
            //this is the total size of the file which we are downloading
            totalSize = urlConnection.getContentLength();
 
            /*runOnUiThread(new Runnable() {
                public void run() {
                    pb.setMax(totalSize);
                }              
            });*/
             
            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
 
            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                // update the progressbar //
                /*runOnUiThread(new Runnable() {
                    public void run() {
                        pb.setProgress(downloadedSize);
                        float per = ((float)downloadedSize/totalSize) * 100;
                        cur_val.setText("Downloaded " + downloadedSize + "KB / " + totalSize + "KB (" + (int)per + "%)" );
                    }
                });*/
            }
            //close the output stream when complete //
            fileOutput.close();
            /*runOnUiThread(new Runnable() {
                public void run() {
                    //dialog.dismiss(); // if you want close it..
                }
            });*/
            DataManager dataManager = new DataManager((CollectSurvey) ApplicationManager.getSurvey(),ApplicationManager.getSurvey().getSchema().getRootEntityDefinition(ApplicationManager.currRootEntityId).getName(),ApplicationManager.getLoggedInUser());
            Log.e("fileNAMEtoLoad","=="+fileName);
            dataManager.loadRecordFromXml(fileName);
            filesCount--;
            if (filesCount==0){
            	pd.dismiss();
    			/*AlertMessage.createPositiveDialog(DownloadActivity.this, true, null,
    					getResources().getString(R.string.downloadToDeviceSuccessfulTitle), 
    					getResources().getString(R.string.downloadToDeviceSuccessfulMessage),
    						getResources().getString(R.string.okay),
    			    		new DialogInterface.OnClickListener() {
    							@Override
    							public void onClick(DialogInterface dialog, int which) {
    								
    							}
    						},
    						null).show();	*/
            }            	
        } catch (final MalformedURLException e) {
            showError("Error : MalformedURLException " + e);       
            e.printStackTrace();
        } catch (final IOException e) {
            showError("Error : IOException " + e);         
            e.printStackTrace();
        }
        catch (final Exception e) {
            showError("Error : Please check your internet connection " + e);
        }      
    }
     
    void showError(final String err){
        runOnUiThread(new Runnable() {
            public void run() {
                //Toast.makeText(DownloadActivity.this, err, Toast.LENGTH_LONG).show();
            	Log.e("showError","=="+err);
                //dialog.dismiss();
            }
        });
    }
     
    void showProgress(String file_path){
        dialog = new Dialog(DownloadActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.myprogressdialog);
        dialog.setTitle("Download Progress");
 
        TextView text = (TextView) dialog.findViewById(R.id.tv1);
        text.setText("Downloading file from ... " + file_path);
        cur_val = (TextView) dialog.findViewById(R.id.cur_pg_tv);
        cur_val.setText("Starting download...");
        dialog.show();
         
        pb = (ProgressBar)dialog.findViewById(R.id.progress_bar);
        pb.setProgress(0);
        pb.setProgressDrawable(getResources().getDrawable(R.drawable.green_progress)); 
    }
}