package org.openforis.collect.android.fields;

import org.openforis.collect.android.messages.ToastMessage;

import android.content.Context;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CoordinateField extends InputField {
	private EditText txtLatitude;
	private EditText txtLongitude;
	
	public CoordinateField(Context context, String labelText, String initialText, String hintText,
			boolean isMultiple) {		
		super(context, isMultiple);

		this.label = new TextView(context);
		this.label.setMaxLines(1);
		this.label.setText(labelText);
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(CoordinateField.this.getContext(), CoordinateField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.label.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	    			        	
	        }
	    });
		
		this.addView(this.scrollLeft);
		this.addView(this.label);
		
		this.txtLatitude = new EditText(context);
		this.txtLatitude.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		this.addView(txtLatitude);

		this.txtLongitude = new EditText(context);
		this.txtLongitude.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		this.addView(txtLongitude);
		this.addView(this.scrollRight);
	}
	
	@Override
	public String getValue()
	{
		return this.txtLatitude.getText().toString()+","+this.txtLongitude.getText().toString();
	}
	
	public void setValue(String latitude, String longitude)
	{
		this.txtLatitude.setText(latitude);
		this.txtLongitude.setText(longitude);
	}
	
	public void makeNumericKeyboard(){
		DigitsKeyListener dkl = new DigitsKeyListener(true,true);
		txtLatitude.setKeyListener(dkl);
		txtLongitude.setKeyListener(dkl);
	}
}
