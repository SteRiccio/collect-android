package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;
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
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class RangeField extends InputField {
	
	private List<String> values;
	
	public RangeField(Context context, NodeDefinition nodeDef) {
		super(context, nodeDef);
		
		RangeField.this.values = new ArrayList<String>();
		RangeField.this.values.add(RangeField.this.currentInstanceNo, "");

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
				    		Log.i(getResources().getString(R.string.app_name), "Setting range field is: " + valueForNum);
				    		RangeField.this.makeReal();			    		
				        }
				    	else {
				    		Log.i(getResources().getString(R.string.app_name), "Setting range field is: " + valueForNum);
				    		txtBox.setInputType(InputType.TYPE_NULL);
//				    		RangeField.this.setKeyboardType(null);
				    	}
			    	}
		    	}
		    }
	    });
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
	}
	
	@Override
	public int getInstancesNo(){
		return this.values.size();
	}
}
