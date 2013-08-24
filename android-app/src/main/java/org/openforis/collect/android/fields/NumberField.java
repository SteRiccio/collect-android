package org.openforis.collect.android.fields;

import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.ValidationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.collect.model.NodeChangeSet;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
import org.openforis.idm.metamodel.validation.ValidationResults;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.IntegerAttribute;
import org.openforis.idm.model.IntegerValue;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.RealAttribute;
import org.openforis.idm.model.RealValue;
import org.openforis.idm.model.TextAttribute;
import org.openforis.idm.model.TextValue;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class NumberField extends InputField {
	
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
			@Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	//Get current settings about software keyboard for text fields
		    	if(hasFocus){
			    	if(this.getClass().toString().contains("NumberField")){
				    	//Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
				    	//Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));
				    	boolean valueForNum = false;				   
				    	if (ApplicationManager.appPreferences!=null){
				    		valueForNum = ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnNumericField), false);
				    	}
				    	//Switch on or off Software keyboard depend of settings
				    	if(valueForNum){
				    		if (NumberField.this.type.toLowerCase().equals("integer")){
				    			NumberField.this.makeInteger();
				    		} else{
				    			NumberField.this.makeReal();
				    		}	    		
				        }
				    	else {
				    		NumberField.this.txtBox.setInputType(InputType.TYPE_NULL);
				    	}
			    	}
		    	}else{
		    		//NumberField.this.validateResult();
		    	}
		    }
	    });
		
		//Check for every given character is it number or not
		//and remove all non-digit characters
		this.txtBox.addTextChangedListener(new TextWatcher(){
		   
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
						NumberField.this.txtBox.setText(strReplace);
						NumberField.this.txtBox.setSelection(start);
					}
				}
			}	
		});
	}
/*	
	private void validateResult(){
		String value = NumberField.this.txtBox.getText().toString();
		if ((value!=null) && (!value.equals("")) && (!value.equals("null"))){
			Node<? extends NodeDefinition> node = NumberField.this.findParentEntity(form.getFormScreenId()).get(NumberField.this.nodeDefinition.getName(), form.currInstanceNo);
			ValidationResults results = ValidationManager.validateField(node);
			if(results.getErrors().size() > 0 || results.getFailed().size() > 0){
				NumberField.this.txtBox.setBackgroundColor(Color.RED);
			}else if (results.getWarnings().size() > 0){
				NumberField.this.txtBox.setBackgroundColor(Color.YELLOW);
			}else{
				NumberField.this.txtBox.setBackgroundColor(Color.TRANSPARENT);
			}
		}
	}
*/	
	public void setValue(int position, String value, String path, boolean isTextChanged)
	{		
		try{
			if (!isTextChanged)
				this.txtBox.setText(value);
			//Validate and add/update attribute
			Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
			NodeChangeSet nodeChangeSet = null;
			Entity parentEntity = this.findParentEntity(path);
			if (node!=null){
				if ((value!=null) && (!value.equals("")) && (!value.equals("null"))){
					if (((NumberAttributeDefinition) this.nodeDefinition).isInteger()){
//						IntegerAttribute intAttr = (IntegerAttribute)node;
//						intAttr.setValue(new IntegerValue(Integer.valueOf(value), null));
						Log.d("Number(int) field with Id: ",node.getDefinition().getId() + " is updating. Node name is: " + node.getName() + " Node ID is: " + node.getInternalId());
						nodeChangeSet = ServiceFactory.getRecordManager().updateAttribute((IntegerAttribute)node, new IntegerValue(Integer.valueOf(value), null));					
					} else {
//						RealAttribute intAttr = (RealAttribute)node;
//						intAttr.setValue(new RealValue(Double.valueOf(value), null));
						Log.d("Number(real) field with Id: ",node.getDefinition().getId() + " is updating. Node name is: " + node.getName() + " Node ID is: " + node.getInternalId());
						nodeChangeSet = ServiceFactory.getRecordManager().updateAttribute((RealAttribute)node, new RealValue(Double.valueOf(value), null));						
					}
				}
			} else {
				if ((value!=null) && (!value.equals("")) && (!value.equals("null"))){
					if (((NumberAttributeDefinition) this.nodeDefinition).isInteger()){
//						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), Integer.valueOf(value), position);	
						Log.d("Number(int) field","is adding attribute.");
						nodeChangeSet = ServiceFactory.getRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new IntegerValue(Integer.valueOf(value), null), null, null);			
					} else {
//						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), Double.valueOf(value), position);
						Log.d("Number(real) field","is adding attribute.");
						nodeChangeSet = ServiceFactory.getRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new RealValue(Double.valueOf(value), null), null, null);
					}
				}			
			}
//			ApplicationManager.updateUIElementsWithValidationResults(nodeChangeSet);
			validateField(nodeChangeSet);

		} catch (Exception e){
			Log.e("Number value got exception", "Value is: " + value);
			//e.printStackTrace();
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
