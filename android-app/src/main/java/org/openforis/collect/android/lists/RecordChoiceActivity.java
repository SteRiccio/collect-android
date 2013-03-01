package org.openforis.collect.android.lists;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.BaseListActivity;
import org.openforis.collect.android.management.DataManager;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeLabel.Type;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RecordChoiceActivity extends BaseListActivity{
	
	private static final String TAG = "RecordChoiceActivity";

	private TextView activityLabel;
	
	private List<CollectRecord> recordsList;
	private ArrayAdapter<String> adapter;
	
	private EntityDefinition rootEntityDef;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.clusterchoiceactivity);
        try{
        	this.activityLabel = (TextView)findViewById(R.id.lblList);
        	this.activityLabel.setText(getResources().getString(R.string.clusterChoiceListLabel));
        	
        	/*ProgressDialog pd = ProgressDialog.show(ClusterChoiceActivity.this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.loading), true, false);
    		pd.dismiss();*/
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
		
		this.rootEntityDef = ApplicationManager.getSurvey().getSchema().getRootEntityDefinition(getIntent().getIntExtra(getResources().getString(R.string.rootEntityId),1));
		
		CollectSurvey collectSurvey = (CollectSurvey)ApplicationManager.getSurvey();	        	
    	DataManager dataManager = new DataManager(collectSurvey,this.rootEntityDef.getName(),ApplicationManager.getLoggedInUser());
    	this.recordsList = dataManager.loadSummaries();
		String[] clusterList;
		if (this.recordsList.size()==0){
			clusterList = new String[1];
		} else {
			clusterList = new String[recordsList.size()+2];
		}
		for (int i=0;i<recordsList.size();i++){
			CollectRecord record = recordsList.get(i);
			clusterList[i] = record.getId()+" "+record.getCreatedBy().getName()
					+"\n"+record.getCreationDate();
			if (record.getModifiedDate()!=null){
				clusterList[i] += "\n"+record.getModifiedDate();
			}
		}
		if (this.recordsList.size()==0){			
			clusterList[0]=getResources().getString(R.string.addNewRecord)+" "+this.rootEntityDef.getLabel(Type.INSTANCE, null);
		} else {
			clusterList[recordsList.size()]="";
			clusterList[recordsList.size()+1]=getResources().getString(R.string.addNewRecord)+" "+this.rootEntityDef.getLabel(Type.INSTANCE, null);
		}
		
		int layout = (backgroundColor!=Color.WHITE)?R.layout.localclusterrow_white:R.layout.localclusterrow_black;
        this.adapter = new ArrayAdapter<String>(this, layout, R.id.plotlabel, clusterList);
		this.setListAdapter(this.adapter);
    }
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i(getResources().getString(R.string.app_name),TAG+":onListItemClick");
		if (this.recordsList.size()==0){
			Intent resultHolder = new Intent();
			resultHolder.putExtra(getResources().getString(R.string.recordId), -1);	
			setResult(getResources().getInteger(R.integer.clusterChoiceSuccessful),resultHolder);
			RecordChoiceActivity.this.finish();		
		} else {
			if (position!=recordsList.size()){
				Intent resultHolder = new Intent();
				if (position<recordsList.size()){
					resultHolder.putExtra(getResources().getString(R.string.recordId), this.recordsList.get(position).getId());	
				} else {
					resultHolder.putExtra(getResources().getString(R.string.recordId), -1);	
				}			
				setResult(getResources().getInteger(R.integer.clusterChoiceSuccessful),resultHolder);
				RecordChoiceActivity.this.finish();	
			}
		}		
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
		setResult(getResources().getInteger(R.integer.backButtonPressed), new Intent());
		RecordChoiceActivity.this.finish();
	}
	
    private void changeBackgroundColor(int backgroundColor){
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
		this.activityLabel.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
    }
}