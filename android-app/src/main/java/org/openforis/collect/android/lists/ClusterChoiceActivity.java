package org.openforis.collect.android.lists;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.android.tabs.TabManager;
import org.openforis.collect.model.CollectRecord;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ClusterChoiceActivity extends ListActivity{
	
	private static final String TAG = "ClusterChoiceActivity";

	private List<CollectRecord> recordsList;
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
        	ProgressDialog pd = ProgressDialog.show(ClusterChoiceActivity.this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.loading), true, false);
            //populating available cluster list - all files in constants.dataSavingPath folder
			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
			jdbcDao.getConnection();
			
			long startTime = System.currentTimeMillis();
			recordsList = TabManager.getRecordManager().loadSummaries(TabManager.getSurvey(), "cluster");
			Log.e("loadingSummaries","=="+(System.currentTimeMillis()-startTime)/1000);
			clusterList = new String[recordsList.size()];
			for (int i=0;i<recordsList.size();i++){
				clusterList[i] = recordsList.get(i).getId()+" "+recordsList.get(i).getCreatedBy().getName()
						+" "+recordsList.get(i).getCreationDate().toLocaleString();
			}			
			JdbcDaoSupport.close();
			
            this.adapter = new ArrayAdapter<String>(this, R.layout.localclusterrow, R.id.plotlabel, clusterList);
    		this.setListAdapter(this.adapter);
    		this.isFirstClick = false;
    		this.firstClickPosition = -1;
    		this.firstClickTime = 0;
    		pd.dismiss();
    		if (recordsList.size()==0){
    			//no data saved in database
    			setResult(getResources().getInteger(R.integer.clusterChoiceFailed), new Intent());
    			ClusterChoiceActivity.this.finish();
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
    
    protected void onResume(){
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
    }
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i(getResources().getString(R.string.app_name),TAG+":onListItemClick");
		Intent resultHolder = new Intent();
		resultHolder.putExtra("clusterId", this.recordsList.get(position).getId());
		setResult(getResources().getInteger(R.integer.clusterChoiceSuccessful),resultHolder);
		ClusterChoiceActivity.this.finish();
		/*
		if (!this.isFirstClick){
			this.isFirstClick = true;
			this.firstClickPosition = position;
			this.firstClickTime = System.currentTimeMillis();
		} else {
			if ((position==this.firstClickPosition)&&(System.currentTimeMillis()-this.firstClickTime<getResources().getInteger(R.integer.timeDifference4DoubleClick))){//double click on the item
				Intent resultHolder = new Intent();
				resultHolder.putExtra("clusterId", this.recordsList.get(position).getId());
				setResult(getResources().getInteger(R.integer.clusterChoice),resultHolder);
				ClusterChoiceActivity.this.finish();
			}
		}*/
	}
}