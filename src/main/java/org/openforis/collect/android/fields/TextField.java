package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.messages.ToastMessage;

import android.content.Context;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class TextField extends InputField {

	protected List<String> values;
	
	public TextField(Context context, int id, String labelText, String initialText, String hintText,
			boolean isMultiple) {
		super(context, id, isMultiple);
		
		this.values = new ArrayList<String>();
		TextField.this.values.add(TextField.this.currentInstanceNo, "");
		
		this.label.setText(labelText);
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(TextField.this.getContext(), TextField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.setHint(hintText);		
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));
		this.txtBox.addTextChangedListener(this);
		
		this.container.addView(this.scrollLeft);
		this.container.addView(this.label);
		this.container.addView(this.txtBox);
		this.container.addView(this.scrollRight);
		this.addView(this.container);
		
		this.scrollLeft.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	TextField.this.scrollLeft();
	        }
	    });
		
		this.scrollRight.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	TextField.this.scrollRight();
	        }
	    });
	}
	
	@Override
	public void scrollLeft(){
    	if (TextField.this.currentInstanceNo>0){
    		TextField.this.values.set(TextField.this.currentInstanceNo, TextField.this.txtBox.getText().toString());	        		
    		TextField.this.currentInstanceNo--;
    		TextField.this.txtBox.setText(TextField.this.values.get(TextField.this.currentInstanceNo));    		
    	}
	}
	
	@Override
	public void scrollRight(){
    	if (TextField.this.values.size()==(TextField.this.currentInstanceNo+1)){
    		TextField.this.values.add(TextField.this.currentInstanceNo+1, "");
    	}
    	TextField.this.values.set(TextField.this.currentInstanceNo, TextField.this.txtBox.getText().toString());
    	TextField.this.currentInstanceNo++;
		if (TextField.this.values.size()>=(TextField.this.currentInstanceNo+1)){
			TextField.this.txtBox.setText(TextField.this.values.get(TextField.this.currentInstanceNo));
		}
	}
	
	public String getValue(int index){
		return TextField.this.values.get(index);
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		TextField.this.values.set(TextField.this.currentInstanceNo, s.toString());
	}
}
