package org.openforis.collect.android.fields;

import java.util.List;
import java.util.Map;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
import org.openforis.idm.metamodel.validation.ValidationResults;
import org.openforis.idm.metamodel.validation.Validator;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.IntegerAttribute;
import org.openforis.idm.model.IntegerValue;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.NumberAttribute;
import org.openforis.idm.model.RealAttribute;
import org.openforis.idm.model.RealValue;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class NumberField extends InputField {
	
	private List<String> values;
	private NumberAttributeDefinition numberNodeDef;
	private String type;
	private Entity parentEntity;
	
	public NumberField(Context context, NodeDefinition nodeDef) {
		super(context, nodeDef);

		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(NumberField.this.getContext(), NumberField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.txtBox = new EditText(context);
		//this.setHint(hintText);
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		this.numberNodeDef = (NumberAttributeDefinition)nodeDef;
		this.type = numberNodeDef.getType().toString();
		if (!this.numberNodeDef.isMultiple()){
			this.parentEntity =  NumberField.this.form.parentEntitySingleAttribute;
		}
		else{
			this.parentEntity =  NumberField.this.form.parentEntityMultipleAttribute;
		}
		
		
		this.addView(this.txtBox);	
		
		// When NumberField got focus
		this.txtBox.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @SuppressWarnings("unchecked")
			@Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	//Get current settings about software keyboard for text fields
		    	if(hasFocus){
			    	if(this.getClass().toString().contains("NumberField")){
				    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
				    	Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));
				    	//Switch on or off Software keyboard depend of settings
				    	if(valueForNum){
				    		Log.i(getResources().getString(R.string.app_name), "Setting numeric field is: " + valueForNum);
				    		if (NumberField.this.type.toLowerCase().equals("integer")){
				    			NumberField.this.makeInteger();
				    		} else{
				    			NumberField.this.makeReal();
				    		}	    		
				        }
				    	else {
				    		Log.i(getResources().getString(R.string.app_name), "Setting numeric field is: " + valueForNum);
				    		NumberField.this.txtBox.setInputType(InputType.TYPE_NULL);
				    	}
			    	}
		    	}else{
		    		Log.i("NUMBER FIELD info", "Number field lost focus");		    		
		    		Log.i("VALIDATION FOR NUMBER FIELD", "Attribute defenition: " + NumberField.this.numberNodeDef.toString());
		    		@SuppressWarnings("rawtypes")
					NumberAttribute attribute;
		    		if (NumberField.this.type.toLowerCase().equals("integer")){
		    			Log.i("VALIDATION FOR NUMBER FIELD", "Integer Attribute");
		    			attribute = new IntegerAttribute((NumberAttributeDefinition) NumberField.this.numberNodeDef);
		    		} else{
		    			Log.i("VALIDATION FOR NUMBER FIELD", "Real Attribute");
		    			attribute = new RealAttribute((NumberAttributeDefinition) NumberField.this.numberNodeDef);
		    		}
		    		if(parentEntity == null)
		    			Log.i("VALIDATION FOR NUMBER FIELD", "Parent entity is: NULL");
		    		Log.i("VALIDATION FOR NUMBER FIELD", "Parent entity is: " + parentEntity.getName());
		    		//Log.i("VALIDATION FOR NUMBER FIELD", "Currect record is: " + ApplicationManager.currentRecord);
		    		
		    		//GETTING VALUE (Karol code)
		    		String loadedValue = "";
		    		if (((NumberAttributeDefinition) NumberField.this.numberNodeDef).isInteger()){
					    IntegerValue intValue = (IntegerValue)parentEntity.getValue(NumberField.this.numberNodeDef.getName(), NumberField.this.currentInstanceNo);
					    if (intValue!=null){
					        loadedValue = intValue.getValue().toString();    
					    	attribute.setValue(intValue);
					    }
					} else{
					        RealValue realValue = (RealValue)parentEntity.getValue(NumberField.this.numberNodeDef.getName(), NumberField.this.currentInstanceNo);
					        if (realValue!=null){
					            loadedValue = realValue.getValue().toString();
					            attribute.setValue(realValue);    
					        }
					}  
		    		Log.i("VALIDATION FOR NUMBER FIELD", "Value from txtbox is: " + loadedValue);
		    		Log.i("VALIDATION FOR NUMBER FIELD", "Value from attribute is: " + attribute.getValue());
		    		Log.i("VALIDATION FOR NUMBER FIELD", "Record of attribute is: " + attribute.getRecord());
		    		//Log.i("VALIDATION FOR NUMBER FIELD", "Currect record is: " + ApplicationManager.currentRecord);
//					Validator validator = new Validator();
//		    		ValidationResults results = validator.validate(attribute);
//		    		Log.i("VALIDATION FOR NUMBER FIELD", "Errors: " + results.getErrors().size() + " : " + results.getErrors().toString());
//		    		Log.i("VALIDATION FOR NUMBER FIELD", "Warnings: "  + results.getWarnings().size() + " : " + results.getWarnings().toString());
//		    		Log.i("VALIDATION FOR NUMBER FIELD", "Fails: "  + results.getFailed().size() + " : " +  results.getFailed().toString());
//		    		Log.i("VALIDATION FOR NUMBER FIELD", "Number of ERRORS from Current Record: " + ApplicationManager.currentRecord.getErrors());
		    	}
		    }
	    });
		
		//Check for every given character is it number or not
		this.txtBox.addTextChangedListener(new TextWatcher(){
		   
			public void afterTextChanged(Editable s) {
				if (s.length() > 0){
					if(!isNumeric(s.toString())){
						Log.i("NUMBER FIELD", "Value: " + s + " is NOT numeric.");
						String strReplace = s.subSequence(0, s.length()-1).toString();
						NumberField.this.txtBox.setText(strReplace);
						NumberField.this.txtBox.setSelection(strReplace.length());
					}else{
						Log.i("NUMBER FIELD", "Value: " + s + " is numeric.");
					}
				}
			}
			public void beforeTextChanged(CharSequence s, int start,  int count, int after) {}				 
			public void onTextChanged(CharSequence s, int start, int before, int count) {}	
			
		});
	}
	
	
	
	public void setValue(int position, String value, String path, boolean isTextChanged)
	{		
		try{
			Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
			if (node!=null){
				if ((value!=null) && (!value.equals("")) && (!value.equals("null"))){
					if (((NumberAttributeDefinition) this.nodeDefinition).isInteger()){
						IntegerAttribute intAttr = (IntegerAttribute)node;
						intAttr.setValue(new IntegerValue(Integer.valueOf(value), null));
					} else {
						RealAttribute intAttr = (RealAttribute)node;
						intAttr.setValue(new RealValue(Double.valueOf(value), null));
					}
				}
			} else {
				if ((value!=null) && (!value.equals("")) && (!value.equals("null"))){
					if (((NumberAttributeDefinition) this.nodeDefinition).isInteger()){
						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), Integer.valueOf(value), position);	
					} else {
						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), Double.valueOf(value), position);
					}	
				}			
			}
			
			if (!isTextChanged)
				this.txtBox.setText(value);
		} catch (Exception e){
			
		}		
	}
	
	public String getType(){
		return this.type;
	}
	
	//Check is given value a number
	private Boolean isNumeric(String strValue){
		Boolean result = false;
		if (this.type.toLowerCase().equals("integer")){
			try{
				Integer.parseInt(strValue);
				result = true;
			} catch(NumberFormatException e){
				result = false;
			}
		}
		else if (this.type.toLowerCase().equals("real")){
			try{
				Double.parseDouble(strValue);
				result = true;
			} catch(NumberFormatException e){
				result = false;
			}
		}
		else {
			result = false;
		}	
		return result;
	}
}
