package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openforis.collect.android.R;
import org.openforis.collect.android.data.FieldValue;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.screens.FormScreen;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class NumberField extends InputField {
	
	private List<String> values;

	private String type;
	
	public NumberField(Context context, int id, String labelText, String initialText, String hintText,
			String numberType, boolean isMultiple, boolean isRequired, FieldValue fieldValue) {
		super(context, id, isMultiple, isRequired);
		
		this.values = new ArrayList<String>();
		NumberField.this.values.add(NumberField.this.currentInstanceNo, "");

		this.label.setText(labelText);
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(NumberField.this.getContext(), NumberField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.txtBox = new EditText(context);
		this.setHint(hintText);
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		
		this.type = numberType;
//		if (this.type.toLowerCase().equals("integer")){
//			this.makeInteger();
//		} else{
//			this.makeReal();
//		}
		
		this.value = fieldValue;
		//this.addView(this.scrollLeft);
		this.addView(this.label);
		this.addView(this.txtBox);
		//this.addView(this.scrollRight);		
		
		// When NumberField got focus
		this.txtBox.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	//Get current settings about software keyboard for text fields
		    	if(hasFocus){
		    		FormScreen.currentFieldValue = NumberField.this.value;
		    		//Log.e("FOCUS ON",NumberField.this.value.getValue(0).get(0)+"=="+FormScreen.currentFieldValue.getId());
			    	if(this.getClass().toString().contains("NumberField")){
				    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
				    	Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));
//				    	Log.i(getResources().getString(R.string.app_name), "Boolean is: " + valueForNum);
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
//				    		NumberField.this.setKeyboardType(null);
				    	}
				    	//Log.e("CHANGING","CURRENT FIELDVALUE"+NumberField.this.getElementId());
				    	//FormScreen.currentFieldValue = NumberField.this.value;
			    	}
		    	}
		    }
	    });		
	}

	/*@Override
	public void scrollLeft(){
    	if (NumberField.this.currentInstanceNo>0){
    		NumberField.this.values.set(NumberField.this.currentInstanceNo, NumberField.this.txtBox.getText().toString());	        		
    		NumberField.this.currentInstanceNo--;
    		NumberField.this.txtBox.setText(NumberField.this.values.get(NumberField.this.currentInstanceNo));    		
    	}
	}
	
	@Override
	public void scrollRight(){
    	if (NumberField.this.values.size()==(NumberField.this.currentInstanceNo+1)){
    		NumberField.this.values.add(NumberField.this.currentInstanceNo+1, "");
    	}
    	NumberField.this.values.set(NumberField.this.currentInstanceNo, NumberField.this.txtBox.getText().toString());
    	NumberField.this.currentInstanceNo++;
		if (NumberField.this.values.size()>=(NumberField.this.currentInstanceNo+1)){
			NumberField.this.txtBox.setText(NumberField.this.values.get(NumberField.this.currentInstanceNo));
		}
	}*/
	
	/*public String getValue(int index){
		return NumberField.this.values.get(index);
	}*/
	
	/*@Override
	public void afterTextChanged(Editable s) {
		NumberField.this.values.add(currentInstanceNo, s.toString());
	}*/
	public String getValue(int index){
		//return TextField.this.values.get(index);
		return NumberField.this.value.getValue(index).get(0);
	}
	
	public void setValue(int position, String value)
	{
		this.txtBox.setText(value);
		ArrayList<String> valueToAdd = new ArrayList<String>();
		valueToAdd.add(value);
		NumberField.this.value.setValue(position, valueToAdd);
	}
	
	public String getType(){
		return this.type;
	}
	
	@Override
	public int getInstancesNo(){
		return this.values.size();
	}
	
	public void resetValues(){
		this.values = new ArrayList<String>();
	}
	
	public void addValue(String value){
		this.values.add(value);
		this.currentInstanceNo++;
	}
	
	public List<String> getValues(){
		return this.values;
	}
}
