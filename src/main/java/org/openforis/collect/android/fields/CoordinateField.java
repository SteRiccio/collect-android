package org.openforis.collect.android.fields;


import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.messages.ToastMessage;

import android.content.Context;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CoordinateField extends InputField {
	
	private EditText txtLatitude;
	private EditText txtLongitude;
	

	private List<ArrayList<String>> values;
	
	public CoordinateField(Context context, String labelText,
			String initialTextLat, String initialTextLon,
			String hintTextLat, String hintTextLon,
			boolean isMultiple) {		
		super(context, isMultiple);

		this.values = new ArrayList<ArrayList<String>>();
		ArrayList<String> initialValue = new ArrayList<String>();
		initialValue.add(initialTextLat);
		initialValue.add(initialTextLon);
		this.values.add(initialValue);
		
		this.label = new TextView(context);
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
		
		this.addView(this.scrollLeft);
		this.addView(this.label);
		
		this.txtLatitude = new EditText(context);
		this.txtLatitude.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		this.txtLatitude.setText(initialTextLat);
		this.txtLatitude.setHint(hintTextLat);
		this.addView(txtLatitude);

		this.setKeyboardType(new DigitsKeyListener(true,true));
		
		this.txtLongitude = new EditText(context);
		this.txtLongitude.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		this.txtLongitude.setText(initialTextLon);
		this.txtLongitude.setHint(hintTextLon);
		this.addView(txtLongitude);
		this.addView(this.scrollRight);
	}
	
	@Override
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
    	if (CoordinateField.this.currentInstanceNo>1){
    		ArrayList<String> tempValue = new ArrayList<String>();
    		tempValue.add(CoordinateField.this.txtLatitude.getText().toString());
    		tempValue.add(CoordinateField.this.txtLongitude.getText().toString());
    		CoordinateField.this.values.set(CoordinateField.this.currentInstanceNo-1, tempValue);
    		CoordinateField.this.txtLatitude.setText(CoordinateField.this.values.get(CoordinateField.this.currentInstanceNo-2).get(0));
    		CoordinateField.this.txtLatitude.setText(CoordinateField.this.values.get(CoordinateField.this.currentInstanceNo-2).get(1));
    		CoordinateField.this.currentInstanceNo--;
    	}
	}
	
	@Override
	public void scrollRight(){
    	if (CoordinateField.this.values.size()==CoordinateField.this.currentInstanceNo){
    		ArrayList<String> tempValue = new ArrayList<String>();
    		tempValue.add("");
    		tempValue.add("");
    		CoordinateField.this.values.add(CoordinateField.this.currentInstanceNo, tempValue);	        		
    	}
    	ArrayList<String> tempValue = new ArrayList<String>();
		tempValue.add(CoordinateField.this.txtLatitude.getText().toString());
		tempValue.add(CoordinateField.this.txtLongitude.getText().toString());
		CoordinateField.this.values.set(CoordinateField.this.currentInstanceNo-1, tempValue);        			        		
		if (CoordinateField.this.values.size()>CoordinateField.this.currentInstanceNo){
			CoordinateField.this.txtLatitude.setText(CoordinateField.this.values.get(CoordinateField.this.currentInstanceNo).get(0));
			CoordinateField.this.txtLatitude.setText(CoordinateField.this.values.get(CoordinateField.this.currentInstanceNo).get(1));
		}
		CoordinateField.this.currentInstanceNo++;
	}
}
