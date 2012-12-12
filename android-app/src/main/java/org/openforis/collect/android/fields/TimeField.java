package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openforis.collect.android.R;
import org.openforis.collect.android.dialogs.DateSetDialog;
import org.openforis.collect.android.dialogs.TimeSetDialog;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.tabs.TabManager;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TimeField extends InputField implements TextWatcher {
	
	private List<String> values;
	
	public TimeField(Context context, int id, String labelText, String initialText, String hintText,
			boolean isMultiple, boolean isRequired) {
		super(context, id, isMultiple, isRequired);
		
		this.values = new ArrayList<String>();
		TimeField.this.values.add(TimeField.this.currentInstanceNo, "");
		
		this.label.setText(labelText);
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(TimeField.this.getContext(), TimeField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.txtBox = new EditText(context);
		this.setHint(hintText);
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		this.txtBox.addTextChangedListener(this);
		
		this.addView(this.scrollLeft);
		this.addView(this.label);
		this.addView(this.txtBox);
		this.addView(this.scrollRight);		
		
		// When TimeField got focus
		this.txtBox.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	//Get current settings about software keyboard for text fields
		    	if(hasFocus){
			    	if(this.getClass().toString().contains("TimeField")){
				    	Map<String, ?> settings = TabManager.sharedPreferences.getAll();
				    	Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumeric));
				    	//Switch on or off Software keyboard depend of settings
				    	if(valueForNum){
				    		Log.i(getResources().getString(R.string.app_name), "Setting time field is: " + valueForNum);
				    		TimeField.this.makeReal();			    		
				        }
				    	else {
				    		Log.i(getResources().getString(R.string.app_name), "Setting time field is: " + valueForNum);
				    		TimeField.this.setKeyboardType(null);
				    	}
				    	//Generate random id for text box
				    	final Random myRandom = new Random();
				    	TimeField.this.txtBox.setId(myRandom.nextInt());
				    	//Show Time picker
				    	showTimePickerDialog(TimeField.this.elemId);				    	
			    	}
		    	}
		    }
	    });			
	}
	
	private void showTimePickerDialog(int id) {  	
//		Log.i(getResources().getString(R.string.app_name), "Id from date field was: " + id);
		Intent timePickerIntent = new Intent(TimeField.this.getContext(), TimeSetDialog.class);
		timePickerIntent.putExtra("timefield_id", id);
		timePickerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	super.getContext().startActivity(timePickerIntent);	
	}	
	
	@Override
	public void scrollLeft(){
    	if (TimeField.this.currentInstanceNo>0){
    		TimeField.this.values.set(TimeField.this.currentInstanceNo, TimeField.this.txtBox.getText().toString());	        		
    		TimeField.this.currentInstanceNo--;
    		TimeField.this.txtBox.setText(TimeField.this.values.get(TimeField.this.currentInstanceNo));    		
    	}
	}
	
	@Override
	public void scrollRight(){
    	if (TimeField.this.values.size()==(TimeField.this.currentInstanceNo+1)){
    		TimeField.this.values.add(TimeField.this.currentInstanceNo+1, "");
    	}
    	TimeField.this.values.set(TimeField.this.currentInstanceNo, TimeField.this.txtBox.getText().toString());
    	TimeField.this.currentInstanceNo++;
		if (TimeField.this.values.size()>=(TimeField.this.currentInstanceNo+1)){
			TimeField.this.txtBox.setText(TimeField.this.values.get(TimeField.this.currentInstanceNo));
		}
	}

	public String getValue(int index){
		return TimeField.this.values.get(index);
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		TimeField.this.values.add(currentInstanceNo, s.toString());
	}
	
	@Override
	public int getInstancesNo(){
		return this.values.size();
	}
	
	public void resetValues(){
		this.values = new ArrayList<String>();
	}
	
	public void addValue(String value){
		this.values.add(value);
		this.currentInstanceNo++;
	}
	
	public List<String> getValues(){
		return this.values;
	}
}
