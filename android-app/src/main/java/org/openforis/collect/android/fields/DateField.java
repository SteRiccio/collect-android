package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.messages.ToastMessage;

import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DateField extends InputField {
	
	private List<String> values;
	
	public DateField(Context context, int id, String labelText, String initialText, String hintText,
			boolean isMultiple, boolean isRequired) {
		super(context, id, isMultiple, isRequired);
		
		this.values = new ArrayList<String>();
		DateField.this.values.add(DateField.this.currentInstanceNo, "");
		
		this.label.setText(labelText);
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(DateField.this.getContext(), DateField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.txtBox = new EditText(context);
		this.setHint(hintText);
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		this.txtBox.addTextChangedListener(this);
		
		this.addView(this.scrollLeft);
		this.addView(this.label);
		this.addView(this.txtBox);
		this.addView(this.scrollRight);			
	}
	
	@Override
	public void scrollLeft(){
    	if (DateField.this.currentInstanceNo>0){
    		DateField.this.values.set(DateField.this.currentInstanceNo, DateField.this.txtBox.getText().toString());	        		
    		DateField.this.currentInstanceNo--;
    		DateField.this.txtBox.setText(DateField.this.values.get(DateField.this.currentInstanceNo));    		
    	}
	}
	
	@Override
	public void scrollRight(){
    	if (DateField.this.values.size()==(DateField.this.currentInstanceNo+1)){
    		DateField.this.values.add(DateField.this.currentInstanceNo+1, "");
    	}
    	DateField.this.values.set(DateField.this.currentInstanceNo, DateField.this.txtBox.getText().toString());
    	DateField.this.currentInstanceNo++;
		if (DateField.this.values.size()>=(DateField.this.currentInstanceNo+1)){
			DateField.this.txtBox.setText(DateField.this.values.get(DateField.this.currentInstanceNo));
		}
	}
	
	public String getValue(int index){
		return DateField.this.values.get(index);
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		DateField.this.values.add(currentInstanceNo, s.toString());
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
