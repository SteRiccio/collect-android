package org.openforis.collect.android.fields;

import org.openforis.collect.android.messages.ToastMessage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class TextField extends InputField {
	
	public TextField(Context context, String labelText, String initialText) {
		super(context);
		
		this.label = new TextView(context);
		this.label.setMaxLines(1);
		this.label.setText(labelText);
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(TextField.this.getContext(), TextField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.setValue(initialText);
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		this.addView(this.label);
		this.addView(this.txtBox);
	}
	
	public String getValue()
	{
		return this.txtBox.getText().toString();
	}
	
	public void setValue(String value)
	{
		this.txtBox.setText(value);
	}
}
