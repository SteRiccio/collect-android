package org.openforis.collect.android.fields;

import java.util.Map;
import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.RangeAttributeDefinition;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.IntegerRange;
import org.openforis.idm.model.IntegerRangeAttribute;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.RealRange;
import org.openforis.idm.model.RealRangeAttribute;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextWatcher;

public class RangeField extends InputField {
	
	public RangeField(Context context, NodeDefinition nodeDef) {
		super(context, nodeDef);

		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(RangeField.this.getContext(), RangeField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.txtBox = new EditText(context);
		//this.setHint(hintText);
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));		
		
		//this.addView(this.label);
		this.addView(this.txtBox);
		
		// When RangeField got focus
		this.txtBox.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	//Get current settings about software keyboard for text fields
		    	if(hasFocus){
			    	if(this.getClass().toString().contains("RangeField")){
				    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
				    	Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));
				    	//Switch on or off Software keyboard depend of settings
				    	if(valueForNum){
				    		RangeField.this.makeReal();			    		
				        }
				    	else {
				    		txtBox.setInputType(InputType.TYPE_NULL);
				    	}
			    	}
		    	}
//		    	else{
//		    		String strValue = RangeField.this.txtBox.getText().toString();
//		    		if(strValue.contains("-")){
//		    			String[] rangeValues = strValue.split(getResources().getString(R.string.rangeSeparator));
//		    			Log.i("RANGE NUMBER", "Value is: " + rangeValues[0] + " - " + rangeValues[1]);
//		    			if(!isNumeric(rangeValues[0])){
//		    				Log.i("RANGE FIELD", "Value 'From': " + rangeValues[0] + " is NOT numeric.");
//		    			}
//		    			if(!isNumeric(rangeValues[1])){
//		    				Log.i("RANGE FIELD", "Value 'To': " + rangeValues[1] + " is NOT numeric.");
//		    			}		    			
//		    		}else{
//		    			Log.i("RANGE FIELD", "Value does not contains separator '-'");
//		    		}
//		    	}
		    }
	    });
		
		//Check for every given character is it number or not
		this.txtBox.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {
				if (s.length() > 0){
					char symbol = s.charAt(s.length()-1);
					Log.i("RANGE FIELD", "Check character: " + symbol);
					if (validateCharacter(symbol))
						Log.i("RANGE FIELD", "Result is: TRUE");
					else{ 
						Log.i("RANGE FIELD", "Result is: FALSE");
						String strReplace = s.subSequence(0, s.length()-1).toString(); 
						RangeField.this.txtBox.setText(strReplace);
						RangeField.this.txtBox.setSelection(strReplace.length());
					}	
				}			
			}
			public void beforeTextChanged(CharSequence s, int start,  int count, int after) {}				 
			public void onTextChanged(CharSequence s, int start, int before, int count) {}	
			
		});		
	}
	
	//Check is given symbol number or "." (if type is not "integer")
	private Boolean validateCharacter(char symbol){
		Boolean result = false;
		if (Character.isDigit(symbol) || symbol == '.' || symbol == '-'){
			result = true;
		}
		else{
			result = false;
		}
		return result;
	}
	
	public void setValue(Integer position, String value, String path, boolean isTextChanged)
	{
		if (!isTextChanged)
			this.txtBox.setText(value);
		
		String valueFrom = "";
		String valueTo = "";
		int separatorIndex = value.indexOf(getResources().getString(R.string.rangeSeparator));
		if (separatorIndex!=-1){
			valueFrom = value.substring(0,separatorIndex);
			if (separatorIndex+1<value.length())
				valueTo = value.substring(separatorIndex+1);
		}
		try{
			Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
			if (node!=null){
				if (((RangeAttributeDefinition) this.nodeDefinition).isReal()){
					RealRangeAttribute rangeAtr = (RealRangeAttribute)node;
					if (valueFrom.equals("") && valueTo.equals("")){
						rangeAtr.setValue(new RealRange(null,null,null));
					} else if (valueFrom.equals("")){
						rangeAtr.setValue(new RealRange(null,Double.valueOf(valueTo),null));
					} else if (valueTo.equals("")){
						rangeAtr.setValue(new RealRange(Double.valueOf(valueFrom),null,null));
					} else {
						rangeAtr.setValue(new RealRange(Double.valueOf(valueFrom),Double.valueOf(valueTo),null));
					}				
				} else {
					IntegerRangeAttribute rangeAtr = (IntegerRangeAttribute)node;
					if (valueFrom.equals("") && valueTo.equals("")){
						rangeAtr.setValue(new IntegerRange(null,null,null));
					} else if (valueFrom.equals("")){
						rangeAtr.setValue(new IntegerRange(null,Integer.valueOf(valueTo),null));
					} else if (valueTo.equals("")){
						rangeAtr.setValue(new IntegerRange(Integer.valueOf(valueFrom),null,null));
					} else {
						rangeAtr.setValue(new IntegerRange(Integer.valueOf(valueFrom),Integer.valueOf(valueTo),null));
					}	
				}			
			} else {
				if (((RangeAttributeDefinition) this.nodeDefinition).isReal()){
					if (valueFrom.equals("") && valueTo.equals("")){
						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new RealRange(null,null,null), position);
					} else if (valueFrom.equals("")){
						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new RealRange(null,Double.valueOf(valueTo),null), position);
					} else if (valueTo.equals("")){
						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new RealRange(Double.valueOf(valueFrom),null,null), position);
					} else {
						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new RealRange(Double.valueOf(valueFrom),Double.valueOf(valueTo),null), position);
					}		
				} else {
					if (valueFrom.equals("") && valueTo.equals("")){
						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new IntegerRange(null,null,null), position);
					} else if (valueFrom.equals("")){
						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new IntegerRange(null,Integer.valueOf(valueTo),null), position);
					} else if (valueTo.equals("")){
						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new IntegerRange(Integer.valueOf(valueFrom),null,null), position);
					} else {
						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new IntegerRange(Integer.valueOf(valueFrom),Integer.valueOf(valueTo),null), position);
					}	
				}				
			}
		}catch (Exception e){
			Log.e("RangeField", "ERROR when try to set value" + e.getMessage());
		}
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
}
