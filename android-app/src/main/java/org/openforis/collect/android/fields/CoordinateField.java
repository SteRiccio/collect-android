package org.openforis.collect.android.fields;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Coordinate;
import org.openforis.idm.model.CoordinateAttribute;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.Node;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class CoordinateField extends InputField {
	
	private EditText txtLatitude;
	private EditText txtLongitude;
	
	private static FormScreen form;

	private List<ArrayList<String>> values;
	
	public CoordinateField(Context context, NodeDefinition nodeDef) {		
		super(context, nodeDef);

		CoordinateField.form = (FormScreen)context;
		
		this.values = new ArrayList<ArrayList<String>>();
		ArrayList<String> initialValue = new ArrayList<String>();
		//initialValue.add(initialTextLat);
		//initialValue.add(initialTextLon);
		initialValue.add("");
		initialValue.add("");
		this.values.add(initialValue);
		
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
		
		this.txtLongitude = new EditText(context);
		this.txtLongitude.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		//this.txtLongitude.setText(initialTextLon);
		//this.txtLongitude.setHint("LONGITUDEx");
		this.txtLongitude.addTextChangedListener(this);
		this.addView(txtLongitude);
		
		this.txtLongitude.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
		    	//Get current settings about software keyboard for numeric fields
		    	if(hasFocus){
			    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
			    	Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));
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

		this.txtLatitude = new EditText(context);
		this.txtLatitude.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		//this.txtLatitude.setText(initialTextLat);
		//this.txtLatitude.setHint("LATITUDEy");
		this.txtLatitude.addTextChangedListener(this);
		this.addView(txtLatitude);

		this.txtLatitude.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {					    	
		    	//Get current settings about software keyboard for numeric fields
		    	if(hasFocus){
			    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
			    	Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));
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
		
		
	}

	/*public List<String> getValue(int index){
		return CoordinateField.this.value.getValue(index);
	}*/
	
	/*public void setValue(int position, String latitude, String longitude, String path, boolean isTextChanged)
	{
		if (!isTextChanged){
			this.txtLatitude.setText(latitude);
			this.txtLongitude.setText(longitude);	
		}		
		ArrayList<String> valueToAdd = new ArrayList<String>();
		valueToAdd.add(latitude);
		valueToAdd.add(longitude);		
		
		try{
			//Log.e("coordinateAddedVALUE",latitude+"=="+longitude);
			EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Coordinate(Double.valueOf(latitude),Double.valueOf(longitude),null), position);	
		} catch (Exception e) {
			//Log.e("EXCEPTION","WHILE ADDING COORD VALUE");
		}		
	}*/
	
	public void setValue(Integer position, String lon, String lat, String path, boolean isTextChanged)
	{
		if (!isTextChanged){
			this.txtLongitude.setText(lon);
			this.txtLatitude.setText(lat);
		}

		Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
		if (node!=null){
			CoordinateAttribute coordAtr = (CoordinateAttribute)node;
			if ((lat.equals("")&&lon.equals(""))){
				coordAtr.setValue(new Coordinate(null, null, null));	
			} else if (lat.equals("")){
				coordAtr.setValue(new Coordinate(Double.valueOf(lon), null, null));
			} else if (lon.equals("")){
				coordAtr.setValue(new Coordinate(null, Double.valueOf(lat), null));
			} else {
				coordAtr.setValue(new Coordinate(Double.valueOf(lon), Double.valueOf(lat), null));
			}			
		} else {
			if ((lat.equals("")&&lon.equals(""))){
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Coordinate(null, null, null), position);
			} else if (lat.equals("")){
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Coordinate(Double.valueOf(lon), null, null), position);
			} else if (lon.equals("")){
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Coordinate(null, Double.valueOf(lat), null), position);
			} else {
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Coordinate(Double.valueOf(lon), Double.valueOf(lat), null), position);
			}
				
		}
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
		this.setValue(0, CoordinateField.this.txtLongitude.getText().toString(), CoordinateField.this.txtLatitude.getText().toString(), CoordinateField.form.getFormScreenId(),true);
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
