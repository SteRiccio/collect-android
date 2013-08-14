package org.openforis.collect.android.lists;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.BaseListActivity;
import org.openforis.collect.android.management.DataManager;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.idm.metamodel.EntityDefinition;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RecordChoiceActivity extends BaseListActivity implements OnItemLongClickListener{
	
	private static final String TAG = "RecordChoiceActivity";

	private TextView activityLabel;
	
	private List<CollectRecord> recordsList;
	private ArrayAdapter<String> adapter;
	
	private EntityDefinition rootEntityDef;
	
	private String[] clusterList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.clusterchoiceactivity);
        try{
        	ApplicationManager.recordSelectionActivity = this;
        	
        	this.activityLabel = (TextView)findViewById(R.id.lblList);
        	this.activityLabel.setText(getResources().getString(R.string.clusterChoiceListLabel));
        	
        	this.getListView().setLongClickable(true);
        	this.getListView().setOnItemLongClickListener(this); 
        	Log.e("rootEntity",ApplicationManager.getSurvey().getSchema().getRootEntityDefinition(ApplicationManager.currRootEntityId).getName()+"=="+ApplicationManager.currRootEntityId);
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
		
		refreshRecordsList();
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

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			long id) {
		if ((recordsList.size()!=0) && (position<recordsList.size())){
			final int number = position;
			AlertMessage.createPositiveNegativeDialog(RecordChoiceActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
					getResources().getString(R.string.deleteRecordTitle), getResources().getString(R.string.deleteRecord),
					getResources().getString(R.string.yes), getResources().getString(R.string.no),
		    		new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {							
							ApplicationManager.dataManager.deleteRecord(number);
							refreshRecordsList();
						}
					},
		    		new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					},
					null).show();
		}		
		return false;
	}
	
	public void refreshRecordsList(){
		 final Handler handler = new Handler(){
			    @Override
			    public void handleMessage(Message msg) {
			    	int layout = (backgroundColor!=Color.WHITE)?R.layout.localclusterrow_white:R.layout.localclusterrow_black;
					RecordChoiceActivity.this.adapter = new ArrayAdapter<String>(RecordChoiceActivity.this, layout, R.id.plotlabel, clusterList);
					RecordChoiceActivity.this.setListAdapter(RecordChoiceActivity.this.adapter);
			        }
			    };
		  new Thread(new Runnable() {
			    public void run() {
			    	RecordChoiceActivity.this.rootEntityDef = ApplicationManager.getSurvey().getSchema().getRootEntityDefinition(getIntent().getIntExtra(getResources().getString(R.string.rootEntityId),1));
					
					CollectSurvey collectSurvey = (CollectSurvey)ApplicationManager.getSurvey();	        	
			    	DataManager dataManager = new DataManager(collectSurvey,RecordChoiceActivity.this.rootEntityDef.getName(),ApplicationManager.getLoggedInUser());
			    	RecordChoiceActivity.this.recordsList = dataManager.loadSummaries();
			    	if (RecordChoiceActivity.this.recordsList.size()==0){
			    		Intent resultHolder = new Intent();
						resultHolder.putExtra(getResources().getString(R.string.recordId), -1);	
						setResult(getResources().getInteger(R.integer.clusterChoiceSuccessful),resultHolder);
						RecordChoiceActivity.this.finish();	
			    	}
					
					if (RecordChoiceActivity.this.recordsList.size()==0){
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
					if (RecordChoiceActivity.this.recordsList.size()==0){		
						clusterList[0]=getResources().getString(R.string.addNewRecord)+" "+ApplicationManager.getLabel(RecordChoiceActivity.this.rootEntityDef);;
					} else {
						clusterList[recordsList.size()]="";
						clusterList[recordsList.size()+1]=getResources().getString(R.string.addNewRecord)+" "+ApplicationManager.getLabel(RecordChoiceActivity.this.rootEntityDef);;
					}
					
					 Message msg = Message.obtain();
			            msg.what = 1;
					handler.sendMessage(msg);
			    }
			  }).start();
		  
		 
		  	
		
	}
}