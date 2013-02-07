package org.openforis.collect.android.fields;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openforis.collect.android.R;
import org.openforis.collect.android.data.FieldValue;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NodeLabel.Type;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class CoordinateField extends InputField {
	
	private EditText txtLatitude;
	private EditText txtLongitude;
	
	private static FormScreen form;

	private List<ArrayList<String>> values;
	
	public CoordinateField(Context context, NodeDefinition nodeDef, FieldValue fieldValue) {		
		super(context, nodeDef);

		CoordinateField.form = (FormScreen)context;
		
		this.values = new ArrayList<ArrayList<String>>();
		ArrayList<String> initialValue = new ArrayList<String>();
		//initialValue.add(initialTextLat);
		//initialValue.add(initialTextLon);
		initialValue.add("");
		initialValue.add("");
		this.values.add(initialValue);
		
		this.label.setMaxLines(1);
		this.label.setText(nodeDef.getLabel(Type.INSTANCE, null));
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

		this.addView(this.label);
		
		this.txtLatitude = new EditText(context);
		this.txtLatitude.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		//this.txtLatitude.setText(initialTextLat);
		//this.txtLatitude.setHint(hintTextLat);
		this.txtLatitude.addTextChangedListener(this);
		this.addView(txtLatitude);

		this.txtLatitude.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Log.i(getResources().getString(R.string.app_name), "Lattitude field got focus");					    	
		    	//Get current settings about software keyboard for numeric fields
		    	if(hasFocus){
		    		FormScreen.currentFieldValue = CoordinateField.this.value;
			    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
			    	Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));
			    	Log.i(getResources().getString(R.string.app_name), "Setting latitude field is: " + valueForNum);
			    	//Switch on or off Software keyboard depend of settings
			    	if(valueForNum){	
			    		txtLatitude.setKeyListener(new DigitsKeyListener(true,true));
			        }
			    	else {
			    		txtLatitude.setInputType(InputType.TYPE_NULL);
//			    		CoordinateField.this.setKeyboardType(null);
			    	}

		    	}		    	
			}
		});
		
		this.txtLongitude = new EditText(context);
		this.txtLongitude.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		//this.txtLongitude.setText(initialTextLon);
		//this.txtLongitude.setHint(hintTextLon);
		this.txtLongitude.addTextChangedListener(this);
		this.addView(txtLongitude);
		
		this.txtLongitude.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Log.i(getResources().getString(R.string.app_name), "Longitude field got focus");
		    	FormScreen.currentFieldValue = CoordinateField.this.value;
		    	//Get current settings about software keyboard for numeric fields
		    	if(hasFocus){
			    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
			    	Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));
			    	Log.i(getResources().getString(R.string.app_name), "Setting longitude field is: " + valueForNum);
			    	//Switch on or off Software keyboard depend of settings
			    	if(valueForNum){	
			    		txtLongitude.setKeyListener(new DigitsKeyListener(true,true));
			        }
			    	else {
			    		txtLongitude.setInputType(InputType.TYPE_NULL);
//			    		CoordinateField.this.setKeyboardType(null);
			    	}

		    	}		    	
			}
		});		
		
		this.value = fieldValue;
	}

	public List<String> getValue(int index){
		return CoordinateField.this.value.getValue(index);
	}
	
	public void setValue(int position, String latitude, String longitude)
	{
		this.txtLatitude.setText(latitude);
		this.txtLongitude.setText(longitude);
		ArrayList<String> valueToAdd = new ArrayList<String>();
		valueToAdd.add(latitude);
		valueToAdd.add(longitude);
		CoordinateField.this.value.setValue(position, valueToAdd);
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
	public void afterTextChanged(Editable s) {
    	ArrayList<String> tempValue = new ArrayList<String>();
		tempValue.add(CoordinateField.this.txtLatitude.getText().toString());
		tempValue.add(CoordinateField.this.txtLongitude.getText().toString());
		CoordinateField.this.value.setValue(CoordinateField.form.currInstanceNo, tempValue);
		FormScreen.currentFieldValue = CoordinateField.this.value;
		FormScreen.currentFieldValue.setValue(CoordinateField.form.currInstanceNo, tempValue);
		if (CoordinateField.form.currentNode!=null){
			CoordinateField.form.currentNode.addFieldValue(FormScreen.currentFieldValue);
		}
		//CoordinateField.this.values.set(CoordinateField.this.currentInstanceNo, tempValue);   
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
	
	@Override
	public void addTextChangedListener(TextWatcher textWatcher) {
		this.txtLatitude.addTextChangedListener(textWatcher);
		this.txtLongitude.addTextChangedListener(textWatcher);
	}
}
