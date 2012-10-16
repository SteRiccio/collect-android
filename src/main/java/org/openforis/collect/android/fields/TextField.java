package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.messages.ToastMessage;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class TextField extends InputField {
	
	//private GestureDetector gestureDetector;
	// View.OnTouchListener gestureListener;

	protected List<String> values;
	
	public TextField(Context context, String labelText, String initialText, String hintText,
			boolean isMultiple) {
		super(context, isMultiple);
		
		this.values = new ArrayList<String>();
		this.values.add("");
		
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
	protected void scrollLeft(){
    	Log.e("SCROLL","LEFTtextfield");
    	Log.e("currINstancenO","=="+TextField.this.currentInstanceNo);
    	if (TextField.this.currentInstanceNo>1){
    		TextField.this.values.set(TextField.this.currentInstanceNo-1, TextField.this.txtBox.getText().toString());	        		
    		TextField.this.txtBox.setText(TextField.this.values.get(TextField.this.currentInstanceNo-2));
    		TextField.this.currentInstanceNo--;
    	}
    	Log.e("currentInstanceNO","=="+TextField.this.currentInstanceNo);
    	for (int i=0;i<TextField.this.values.size();i++){
    		Log.e("values"+i,"=="+TextField.this.values.get(i));
    	}
	}
	
	@Override
	protected void scrollRight(){
    	Log.e("currINstancenO","=="+TextField.this.currentInstanceNo);
    	if (TextField.this.values.size()==TextField.this.currentInstanceNo){
    		TextField.this.values.add(TextField.this.currentInstanceNo, "added");	        		
    	}
    	TextField.this.values.set(TextField.this.currentInstanceNo-1, TextField.this.txtBox.getText().toString());        			        		
		if (TextField.this.values.size()>TextField.this.currentInstanceNo)
			TextField.this.txtBox.setText(TextField.this.values.get(TextField.this.currentInstanceNo));
		TextField.this.currentInstanceNo++;
    	Log.e("currentInstanceNO","=="+TextField.this.currentInstanceNo);
    	for (int i=0;i<TextField.this.values.size();i++){
    		Log.e("values"+i,"=="+TextField.this.values.get(i));
    	}
	}
}
