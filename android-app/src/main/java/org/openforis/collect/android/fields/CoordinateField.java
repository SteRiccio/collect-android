package org.openforis.collect.android.fields;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;

import android.content.Context;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class CoordinateField extends InputField {
	
	private EditText txtLatitude;
	private EditText txtLongitude;
	

	private List<ArrayList<String>> values;
	
	public CoordinateField(Context context, int id, String labelText,
			String initialTextLat, String initialTextLon,
			String hintTextLat, String hintTextLon,
			boolean isMultiple, boolean isRequired) {		
		super(context, id, isMultiple, isRequired);

		this.values = new ArrayList<ArrayList<String>>();
		ArrayList<String> initialValue = new ArrayList<String>();
		initialValue.add(initialTextLat);
		initialValue.add(initialTextLon);
		this.values.add(initialValue);
		
		this.label.setMaxLines(1);
		this.label.setText(labelText);
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(CoordinateField.this.getContext(), CoordinateField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.label.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	    			        	
	        }
	    });
		
		//this.addView(this.scrollLeft);
		this.addView(this.label);
		
		this.txtLatitude = new EditText(context);
		this.txtLatitude.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		this.txtLatitude.setText(initialTextLat);
		this.txtLatitude.setHint(hintTextLat);
		this.txtLatitude.addTextChangedListener(this);
		this.addView(txtLatitude);

		this.txtLatitude.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Log.i(getResources().getString(R.string.app_name), "Lattitude field got focus");				
			}
		});
//		this.setKeyboardType(new DigitsKeyListener(true,true));
		
		this.txtLongitude = new EditText(context);
		this.txtLongitude.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		this.txtLongitude.setText(initialTextLon);
		this.txtLongitude.setHint(hintTextLon);
		this.txtLongitude.addTextChangedListener(this);
		this.addView(txtLongitude);
		//this.addView(this.scrollRight);
		
		this.txtLongitude.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Log.i(getResources().getString(R.string.app_name), "Longitude field got focus");
			}
		});		
		// When NumberField got focus
		this.txtBox.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	//Get current settings about software keyboard for text fields
		    	if(this.getClass().toString().contains("CoordinateField")){
			    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
			    	Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumeric));
			    	//Switch on or off Software keyboard depend of settings
			    	if(valueForNum){
			    		CoordinateField.this.makeReal();		    		
			        }
			    	else {
			    		CoordinateField.this.setKeyboardType(null);
			    	}
		    	}
		    }
	    });			
	}
	
	//@Override
	public String getValue()
	{
		return this.txtLatitude.getText().toString()+","+this.txtLongitude.getText().toString();
	}
	
	public void setValue(String latitude, String longitude)
	{
		this.txtLatitude.setText(latitude);
		this.txtLongitude.setText(longitude);
	}
	
	@Override
	public void setKeyboardType(KeyListener keyListener){
		this.txtLatitude.setKeyListener(keyListener);
		this.txtLongitude.setKeyListener(keyListener);
	}
	
	@Override
	public void setAlignment(int alignment){
		this.txtLatitude.setGravity(alignment);
		this.txtLongitude.setGravity(alignment);
	}
	
	@Override
	public void scrollLeft(){
    	if (CoordinateField.this.currentInstanceNo>0){
    		ArrayList<String> tempValue = new ArrayList<String>();
    		tempValue.add(CoordinateField.this.txtLatitude.getText().toString());
    		tempValue.add(CoordinateField.this.txtLongitude.getText().toString());
    		CoordinateField.this.values.set(CoordinateField.this.currentInstanceNo, tempValue);
    		CoordinateField.this.currentInstanceNo--;
    		CoordinateField.this.txtLatitude.setText(CoordinateField.this.values.get(CoordinateField.this.currentInstanceNo-1).get(0));
    		CoordinateField.this.txtLatitude.setText(CoordinateField.this.values.get(CoordinateField.this.currentInstanceNo-1).get(1));
    		CoordinateField.this.currentInstanceNo--;
    	}
	}
	
	@Override
	public void scrollRight(){
    	if (CoordinateField.this.values.size()==(CoordinateField.this.currentInstanceNo+1)){
    		ArrayList<String> tempValue = new ArrayList<String>();
    		tempValue.add("");
    		tempValue.add("");
    		CoordinateField.this.values.add(CoordinateField.this.currentInstanceNo+1, tempValue);	        		
    	}
    	ArrayList<String> tempValue = new ArrayList<String>();
		tempValue.add(CoordinateField.this.txtLatitude.getText().toString());
		tempValue.add(CoordinateField.this.txtLongitude.getText().toString());
		CoordinateField.this.values.set(CoordinateField.this.currentInstanceNo, tempValue);   
		CoordinateField.this.currentInstanceNo++;
		if (CoordinateField.this.values.size()>=(CoordinateField.this.currentInstanceNo+1)){
			CoordinateField.this.txtLatitude.setText(CoordinateField.this.values.get(CoordinateField.this.currentInstanceNo).get(0));
			CoordinateField.this.txtLatitude.setText(CoordinateField.this.values.get(CoordinateField.this.currentInstanceNo).get(1));
		}
	}
	
	public ArrayList<String> getValue(int index){
		return CoordinateField.this.values.get(index);
	}
	
	@Override
	public void afterTextChanged(Editable s) {
    	ArrayList<String> tempValue = new ArrayList<String>();
		tempValue.add(CoordinateField.this.txtLatitude.getText().toString());
		tempValue.add(CoordinateField.this.txtLongitude.getText().toString());
		CoordinateField.this.values.set(CoordinateField.this.currentInstanceNo, tempValue);   
	}
	
	@Override
	public int getInstancesNo(){
		return this.values.size();
	}
	
	public void resetValues(){
		this.values = new ArrayList<ArrayList<String>>();

	}
	
	public void addValue(ArrayList<String> value){
		this.values.add(value);
		this.currentInstanceNo++;
	}
	
	public List<ArrayList<String>> getValues(){
		return this.values;
	}
}
