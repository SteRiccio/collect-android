package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.messages.ToastMessage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RangeField extends InputField {
	
	private List<String> values;
	
	public RangeField(Context context, int id, String labelText, String initialText, String hintText,
			boolean isMultiple) {
		super(context, id, isMultiple);
		
		RangeField.this.values = new ArrayList<String>();
		RangeField.this.values.add(RangeField.this.currentInstanceNo, "");
		
		this.label = new TextView(context);
		this.label.setMaxLines(1);
		this.label.setText(labelText);
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(RangeField.this.getContext(), RangeField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.txtBox = new EditText(context);
		this.setHint(hintText);
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));		
		
		this.addView(this.scrollLeft);
		this.addView(this.label);
		this.addView(this.txtBox);
		this.addView(this.scrollRight);
	}
	
	@Override
	public void scrollLeft(){
    	if (RangeField.this.currentInstanceNo>0){
    		RangeField.this.values.set(RangeField.this.currentInstanceNo, RangeField.this.txtBox.getText().toString());	        		
    		RangeField.this.currentInstanceNo--;
    		RangeField.this.txtBox.setText(RangeField.this.values.get(RangeField.this.currentInstanceNo));    		
    	}
	}
	
	@Override
	public void scrollRight(){
    	if (RangeField.this.values.size()==(RangeField.this.currentInstanceNo+1)){
    		RangeField.this.values.add(RangeField.this.currentInstanceNo+1, "");
    	}
    	RangeField.this.values.set(RangeField.this.currentInstanceNo, RangeField.this.txtBox.getText().toString());
    	RangeField.this.currentInstanceNo++;
		if (RangeField.this.values.size()>=(RangeField.this.currentInstanceNo+1)){
			RangeField.this.txtBox.setText(RangeField.this.values.get(RangeField.this.currentInstanceNo));
		}
	}
	
	/* 
	 * getValue and saving field value after state changed i.e. user selected from menu or typed sth
	 * TO BE IMPLEMENTED */
}
