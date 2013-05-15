package org.openforis.collect.android.lists;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.BaseListActivity;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.model.CollectSurvey;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FormChoiceActivity extends BaseListActivity {
	
	private static final String TAG = "FormChoiceActivity";

	private TextView activityLabel;
	
	private List<CollectSurvey> surveysList;
	private ArrayAdapter<String> adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        setContentView(R.layout.clusterchoiceactivity);
        try{
        	ApplicationManager.formSelectionActivity = this;
        	
        	this.activityLabel = (TextView)findViewById(R.id.lblList);
        	this.activityLabel.setText(getResources().getString(R.string.formChoiceListLabel));           
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
		String selectedFormDefinitionFile = ApplicationManager.appPreferences.getString(getResources().getString(R.string.formDefinitionPath), getResources().getString(R.string.defaultFormDefinitionPath));
		this.surveysList = ApplicationManager.surveyManager.getAll();
		String[] formsList;
		if (this.surveysList.size()==0){
			formsList = new String[1];
		} else {
			formsList = new String[surveysList.size()+2];
		}
		for (int i=0;i<surveysList.size();i++){
			CollectSurvey survey = surveysList.get(i);
			formsList[i] = survey.getName();
		}
		if (this.surveysList.size()==0){			
			formsList[0]=getResources().getString(R.string.addNewSurvey)+selectedFormDefinitionFile;
		} else {
			formsList[surveysList.size()]="";
			formsList[surveysList.size()+1]=getResources().getString(R.string.addNewSurvey)+selectedFormDefinitionFile;
		}
		
		int layout = (backgroundColor!=Color.WHITE)?R.layout.localclusterrow_white:R.layout.localclusterrow_black;
        this.adapter = new ArrayAdapter<String>(this, layout, R.id.plotlabel, formsList);
		this.setListAdapter(this.adapter);
    }
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i(getResources().getString(R.string.app_name),TAG+":onListItemClick");
		if (this.surveysList.size()==0){
			Intent resultHolder = new Intent();
			resultHolder.putExtra(getResources().getString(R.string.formId), -1);	
			setResult(getResources().getInteger(R.integer.formDefinitionChoiceSuccessful),resultHolder);
			FormChoiceActivity.this.finish();	
		} else {
			if (position!=this.surveysList.size()){
				Intent resultHolder = new Intent();
				if (position<this.surveysList.size()){
					ApplicationManager.setSurvey(this.surveysList.get(position));
					resultHolder.putExtra(getResources().getString(R.string.formId), this.surveysList.get(position).getId());	
				} else {					
					resultHolder.putExtra(getResources().getString(R.string.formId), -1);					
				}			
				setResult(getResources().getInteger(R.integer.formDefinitionChoiceSuccessful),resultHolder);
				FormChoiceActivity.this.finish();	
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
		AlertMessage.createPositiveNegativeDialog(FormChoiceActivity.this, false, getResources().getDrawable(R.drawable.warningsign),
				getResources().getString(R.string.exitAppTitle), getResources().getString(R.string.exitAppMessage),
				getResources().getString(R.string.yes), getResources().getString(R.string.no),
	    		new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setResult(getResources().getInteger(R.integer.backButtonPressed), new Intent());
						FormChoiceActivity.this.finish();
					}
				},
	    		new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				},
				null).show();
	}
	
    private void changeBackgroundColor(int backgroundColor){
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
		this.activityLabel.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
    }
}