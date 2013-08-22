package org.openforis.collect.android.fields;


import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.collect.model.NodeChangeSet;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.SpatialReferenceSystem;
import org.openforis.idm.model.Coordinate;
import org.openforis.idm.model.CoordinateAttribute;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.Node;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CoordinateField extends InputField implements OnClickListener {
	
	private EditText txtLatitude;
	private EditText txtLongitude;
	private Button btnGetCoordinates;
	
	private TextView coordLabel;
	
	private static FormScreen form;
	
	ArrayList<String> options;
	ArrayList<String> codes;
	
	private ArrayAdapter<String> aa;
	private Spinner spinner;
	
	public SpatialReferenceSystem srs;
	private List<SpatialReferenceSystem> srsList;
	
	public CoordinateField(Context context, NodeDefinition nodeDef) {		
		super(context, nodeDef);

		CoordinateField.form = (FormScreen)context;
		
		//this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
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
		//this.txtLongitude.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
		//this.txtLongitude.setText(initialTextLon);
		//this.txtLongitude.setHint("LONGITUDEx");
		this.txtLongitude.addTextChangedListener(this);
		//this.addView(txtLongitude);
		
		this.txtLongitude.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
		    	//Get current settings about software keyboard for numeric fields
		    	if(hasFocus){
			    	//Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
			    	//Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));
			    	boolean valueForNum = ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnNumericField), false);
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
		//this.txtLatitude.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,3f));
		//this.txtLatitude.setText(initialTextLat);
		//this.txtLatitude.setHint("LATITUDEy");
		this.txtLatitude.addTextChangedListener(this);
		//this.addView(txtLatitude);

		this.txtLatitude.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {					    	
		    	//Get current settings about software keyboard for numeric fields
		    	if(hasFocus){
			    	//Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
			    	//Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));
		    		boolean valueForNum = false;				   
			    	if (ApplicationManager.appPreferences!=null){
			    		valueForNum = ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnNumericField), false);
			    	}
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
		
		//Check if value is numeric
		this.txtLatitude.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start,  int count, int after) {}				 
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 0){
					if(!isNumeric(s.toString())){
						String strReplace = "";
						if (before==0){//inputting characters
							strReplace = s.toString().substring(0, start+count-1);
							strReplace += s.toString().substring(start+count);
						} else {//deleting characters
							//do nothing - number with deleted digit is still a number
						}
						CoordinateField.this.txtLatitude.setText(strReplace);
						CoordinateField.this.txtLatitude.setSelection(start);
					}
				}
			}
		});
		this.txtLongitude.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start,  int count, int after) {}				 
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 0){
					if(!isNumeric(s.toString())){
						String strReplace = "";
						if (before==0){//inputting characters
							strReplace = s.toString().substring(0, start+count-1);
							strReplace += s.toString().substring(start+count);
						} else {//deleting characters
							//do nothing - number with deleted digit is still a number
						}
						CoordinateField.this.txtLongitude.setText(strReplace);
						CoordinateField.this.txtLongitude.setSelection(start);
					}
				}
			}
		});	

		this.addView(this.txtLongitude);
		this.addView(this.txtLatitude);
		
		this.srsList = ApplicationManager.getSurvey().getSpatialReferenceSystems();
		if (this.srsList.size()>0){
			this.coordLabel = new TextView(context);
			this.coordLabel.setText(getResources().getString(R.string.spatialReferenceSystemLabel));

			this.codes = new ArrayList<String>();
			this.codes.add("null");
			this.options = new ArrayList<String>();
			this.options.add("");
			for (int i=0;i<this.srsList.size();i++){
				SpatialReferenceSystem srs = this.srsList.get(i);
				this.codes.add(srs.getLabel(ApplicationManager.selectedLanguage));
				this.options.add(srs.getLabel(ApplicationManager.selectedLanguage));
			}
			this.spinner = new Spinner(context);
			this.spinner.setPrompt(this.label.getText());
			this.aa = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, this.options);
			this.aa.setDropDownViewResource(R.layout.codelistitem);
			this.spinner.setAdapter(aa);
			//this.spinner.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));
			this.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			    @Override
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
			    	if (position>0)
			    		CoordinateField.this.srs = CoordinateField.this.srsList.get(position-1);
			    	else
			    		CoordinateField.this.srs = null;

					String srsId = null;
					if (CoordinateField.this.srs!=null){						
						srsId = CoordinateField.this.srs.getId();
					}
			    	if (CoordinateField.this.nodeDefinition.isMultiple()){
			    		CoordinateField.this.setValue(CoordinateField.form.currInstanceNo, CoordinateField.this.txtLongitude.getText().toString(), CoordinateField.this.txtLatitude.getText().toString(), srsId, CoordinateField.form.getFormScreenId(),false);	
			    	} else {
			    		CoordinateField.this.setValue(0, CoordinateField.this.txtLongitude.getText().toString(), CoordinateField.this.txtLatitude.getText().toString(), srsId, CoordinateField.form.getFormScreenId(),false);
			    	}
			    }

			    @Override
			    public void onNothingSelected(AdapterView<?> parentView) {
			    	
			    }

			});
			this.spinner.setSelection(0);
			
			this.addView(this.coordLabel);
			this.addView(this.spinner);
		}	
		
		this.btnGetCoordinates = new Button(context);
		this.btnGetCoordinates.setText(getResources().getString(R.string.internalGpsButton));
		this.btnGetCoordinates.setOnClickListener(this);  
		this.addView(this.btnGetCoordinates);
	}
	
	public void setValue(Integer position, String lon, String lat, String srsId, String path, boolean isTextChanged)
	{
		if (!isTextChanged){
			this.txtLongitude.setText(lon);
			this.txtLatitude.setText(lat);
		}

		Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
		NodeChangeSet nodeChangeSet = null;
		Entity parentEntity = this.findParentEntity(path);

		if (node!=null){
//			CoordinateAttribute coordAtr = (CoordinateAttribute)node;
			Log.e("Coordinate field with Id: ",node.getDefinition().getId() + " is updating. Node name is: " + node.getName() + " Node ID is: " + node.getInternalId());
			if ((lat.equals("")&&lon.equals(""))){
//				coordAtr.setValue(new Coordinate(null, null, null));	
				nodeChangeSet = ServiceFactory.getRecordManager().updateAttribute((CoordinateAttribute)node, new Coordinate(null, null, srsId));
			} else if (lat.equals("")){
//				coordAtr.setValue(new Coordinate(Double.valueOf(lon), null, null));
				nodeChangeSet = ServiceFactory.getRecordManager().updateAttribute((CoordinateAttribute)node, new Coordinate(Double.valueOf(lon), null, srsId));
			} else if (lon.equals("")){
//				coordAtr.setValue(new Coordinate(null, Double.valueOf(lat), null));
				nodeChangeSet = ServiceFactory.getRecordManager().updateAttribute((CoordinateAttribute)node, new Coordinate(null,  Double.valueOf(lat), srsId));
			} else {
//				coordAtr.setValue(new Coordinate(Double.valueOf(lon), Double.valueOf(lat), null));
				nodeChangeSet = ServiceFactory.getRecordManager().updateAttribute((CoordinateAttribute)node, new Coordinate(Double.valueOf(lon),  Double.valueOf(lat), srsId));
			}
		} else {
			Log.e("Coordinate field","is adding attribute.");
			if ((lat.equals("")&&lon.equals(""))){
//				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Coordinate(null, null, null), position);
				nodeChangeSet = ServiceFactory.getRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Coordinate(null, null, srsId), null, null);
			} else if (lat.equals("")){
//				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Coordinate(Double.valueOf(lon), null, null), position);
				nodeChangeSet = ServiceFactory.getRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Coordinate(Double.valueOf(lon), null, srsId), null, null);
			} else if (lon.equals("")){
//				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Coordinate(null, Double.valueOf(lat), null), position);
				nodeChangeSet = ServiceFactory.getRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Coordinate(null, Double.valueOf(lat), srsId), null, null);
			} else {
//				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Coordinate(Double.valueOf(lon), Double.valueOf(lat), null), position);
				nodeChangeSet = ServiceFactory.getRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new Coordinate(Double.valueOf(lon), Double.valueOf(lat), srsId), null, null);
			}	
		}
//		ApplicationManager.updateUIElementsWithValidationResults(nodeChangeSet);
		validateField(nodeChangeSet);
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
		String srsId = null;
		if (CoordinateField.this.srs!=null){						
			srsId = CoordinateField.this.srs.getId();
		}
		this.setValue(0, CoordinateField.this.txtLongitude.getText().toString(), CoordinateField.this.txtLatitude.getText().toString(), srsId, CoordinateField.form.getFormScreenId(),true);
	}
	
	@Override
	public void addTextChangedListener(TextWatcher textWatcher) {
		
	}
	
	//Check is given value a number
	private Boolean isNumeric(String strValue){
		Boolean result = false;
		try{
			Double.parseDouble(strValue);
			result = true;
		} catch(NumberFormatException e){
			result = false;
		}
		return result;
	}
	
	@Override
	public void onClick(View arg0) {
		CoordinateField.form.currentCoordinateField = this;
		CoordinateField.form.startInternalGps(this);
	}
	
	public void setCoordinateLabelTextColor(int color){
		if (this.coordLabel!=null)
			this.coordLabel.setTextColor(color);
	}
}