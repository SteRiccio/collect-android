package org.openforis.collect.android.fields;

import java.util.List;

import org.openforis.collect.android.messages.ToastMessage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DateField extends InputField {
	
	private List<String> values;
	
	public DateField(Context context, String labelText, String initialText, String hintText,
			boolean isMultiple) {
		super(context, isMultiple);
		
		this.label = new TextView(context);
		this.label.setMaxLines(1);
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
		
		this.addView(this.scrollLeft);
		this.addView(this.label);
		this.addView(this.txtBox);
		this.addView(this.scrollRight);
	}
	
	@Override
	public void scrollLeft(){
    	if (DateField.this.currentInstanceNo>1){
    		DateField.this.values.set(DateField.this.currentInstanceNo-1, DateField.this.txtBox.getText().toString());	        		
    		DateField.this.txtBox.setText(DateField.this.values.get(DateField.this.currentInstanceNo-2));
    		DateField.this.currentInstanceNo--;
    	}
	}
	
	@Override
	public void scrollRight(){
    	if (DateField.this.values.size()==DateField.this.currentInstanceNo){
    		DateField.this.values.add(DateField.this.currentInstanceNo, "");	        		
    	}
    	DateField.this.values.set(DateField.this.currentInstanceNo-1, DateField.this.txtBox.getText().toString());        			        		
		if (DateField.this.values.size()>DateField.this.currentInstanceNo)
			DateField.this.txtBox.setText(DateField.this.values.get(DateField.this.currentInstanceNo));
		DateField.this.currentInstanceNo++;
	}
}
