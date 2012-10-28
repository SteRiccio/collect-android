package org.openforis.collect.android.lists;

import org.openforis.collect.android.R;
import org.openforis.collect.android.misc.RunnableHandler;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ClusterChoiceActivity extends ListActivity{
	
	private static final String TAG = "ClusterChoiceActivity";

	private String[] clusterList;
	private ArrayAdapter<String> adapter;
	
	private boolean isFirstClick;
	private int firstClickPosition;
	private long firstClickTime;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        setContentView(R.layout.clusterchoiceactivity);
        try{
            //populating available cluster list - all files in constants.dataSavingPath folder
            clusterList = new String[11];
            for (int i=0;i<11;i++){
            	clusterList[i] = "$"+i+" PLOT";//fileList[i];
            }
            clusterList[clusterList.length-1] = "CREATE EMPTY FORM";
            this.adapter = new ArrayAdapter<String>(this, R.layout.localclusterrow, R.id.plotlabel, clusterList);
    		this.setListAdapter(this.adapter);
    		this.isFirstClick = false;
    		this.firstClickPosition = -1;
    		this.firstClickTime = 0;
        } catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onCreate",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
        }
    }
    
    protected void onResume(){
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
    }
    
	/*@Override
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
		try{
    		AlertMessage.createPositiveNegativeDialog(ClusterChoiceActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
    				getResources().getString(R.string.exitAppTitle), getResources().getString(R.string.exitAppMessage),
    				getResources().getString(R.string.yes), getResources().getString(R.string.no),
    	    		new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						setResult(1);
    						ClusterChoiceActivity.this.finish();
    					}
    				},
    	    		new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						
    					}
    				}).show();
    	}catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onBackPressed",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
    	}
	}*/
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i(getResources().getString(R.string.app_name),TAG+":onListItemClick");
		if (!this.isFirstClick){
			this.isFirstClick = true;
			this.firstClickPosition = position;
			this.firstClickTime = System.currentTimeMillis();
		} else {
			if ((position==this.firstClickPosition)&&(System.currentTimeMillis()-this.firstClickTime<getResources().getInteger(R.integer.timeDifference4DoubleClick))){//double click on the item
				Log.e("DOUBLE","CLICK");
				finish();
			}
		}
		
	}
}