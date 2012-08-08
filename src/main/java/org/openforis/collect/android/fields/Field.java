package org.openforis.collect.android.fields;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class Field extends LinearLayout {
	
	protected TextView label;
	
	public Field(Context context) {
		super(context);
	}

	public String getLabelText(){
		return this.label.getText().toString();
	}
	
	public void setLabelText(String label){
		this.label.setText(label);
	}
}
