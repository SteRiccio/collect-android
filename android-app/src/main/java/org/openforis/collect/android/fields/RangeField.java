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
import org.openforis.idm.model.RealRange;

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
	
	/*public String getValue(int index){
		return RangeField.this.value.getValue(index).get(0);
	}*/
	
	public void setValue(int position, String value, String path, boolean isTextChanged)
	{
		if (!isTextChanged)
			this.txtBox.setText(value);
		ArrayList<String> valueToAdd = new ArrayList<String>();
		valueToAdd.add(value);
		String[] rangeArray = value.split(getResources().getString(R.string.rangeSeparator));
		try{
			if (rangeArray.length>0){
				if (((RangeAttributeDefinition) this.nodeDefinition).isReal()){
					RealRange rangeValue = null;
					if (rangeArray.length==1){
						rangeValue = new RealRange(Double.valueOf(rangeArray[0]), null);					
					} else {
						rangeValue = new RealRange(Double.valueOf(rangeArray[0]),Double.valueOf(rangeArray[1]), null);	
					}
					
					EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), rangeValue, position);
				} else {
					IntegerRange rangeValue = null;
					if (rangeArray.length==1){
						rangeValue = new IntegerRange(Integer.valueOf(rangeArray[0]), null);					
					} else {
						rangeValue = new IntegerRange(Integer.valueOf(rangeArray[0]),Integer.valueOf(rangeArray[1]), null);	
					}
					
					EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), rangeValue, position);
				}	
			}	
		} catch (Exception e){
			
		}	
	}
	
	@Override
	public int getInstancesNo(){
		return this.values.size();
	}
}
