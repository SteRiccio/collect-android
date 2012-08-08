package org.openforis.collect.android.fields;

import org.openforis.collect.android.messages.ToastMessage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class BooleanField extends Field {
	private CheckBox chckBox;
	
	public BooleanField(Context context, String labelText, boolean isChecked) {
		super(context);
		
		this.label = new TextView(context);
		this.label.setMaxLines(1);
		this.label.setText(labelText);
		this.label.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, (float) 8.0));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(BooleanField.this.getContext(), BooleanField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.chckBox = new CheckBox(context);
		this.chckBox.setChecked(isChecked);
		this.chckBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2.0));
		this.addView(this.label);
		this.addView(this.chckBox);
	}

	public boolean getValue()
	{
		return this.chckBox.isChecked();
	}
	
	public void setValue(boolean isChecked)
	{
		this.chckBox.setChecked(isChecked);
	}
}
