package org.openforis.collect.android.fields;

import java.util.List;

import org.openforis.collect.android.messages.ToastMessage;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TimeField extends InputField {
	
	private List<String> values;
	
	public TimeField(Context context, String labelText, String initialText, String hintText,
			boolean isMultiple) {
		super(context, isMultiple);
		
		this.label = new TextView(context);
		this.label.setMaxLines(1);
		this.label.setText(labelText);
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(TimeField.this.getContext(), TimeField.this.getLabelText(), Toast.LENGTH_LONG);
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
	
	/*@Override
	protected void scrollLeft(){
    	Log.e("SCROLL","LEFTTimeField");
    	Log.e("currINstancenO","=="+TimeField.this.currentInstanceNo);
    	if (TimeField.this.currentInstanceNo>1){
    		TimeField.this.values.set(TimeField.this.currentInstanceNo-1, TimeField.this.txtBox.getText().toString());	        		
    		TimeField.this.txtBox.setText(TimeField.this.values.get(TimeField.this.currentInstanceNo-2));
    		TimeField.this.currentInstanceNo--;
    	}
    	Log.e("currentInstanceNO","=="+TimeField.this.currentInstanceNo);
    	for (int i=0;i<TimeField.this.values.size();i++){
    		Log.e("values"+i,"=="+TimeField.this.values.get(i));
    	}
	}
	
	@Override
	protected void scrollRight(){
    	Log.e("currINstancenO","=="+TimeField.this.currentInstanceNo);
    	if (TimeField.this.values.size()==TimeField.this.currentInstanceNo){
    		TimeField.this.values.add(TimeField.this.currentInstanceNo, "added");	        		
    	}
    	TimeField.this.values.set(TimeField.this.currentInstanceNo-1, TimeField.this.txtBox.getText().toString());        			        		
		if (TimeField.this.values.size()>TimeField.this.currentInstanceNo)
			TimeField.this.txtBox.setText(TimeField.this.values.get(TimeField.this.currentInstanceNo));
		TimeField.this.currentInstanceNo++;
    	Log.e("currentInstanceNO","=="+TimeField.this.currentInstanceNo);
    	for (int i=0;i<TimeField.this.values.size();i++){
    		Log.e("values"+i,"=="+TimeField.this.values.get(i));
    	}
	}*/
}
