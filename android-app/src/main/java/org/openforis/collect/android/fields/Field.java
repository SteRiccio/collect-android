package org.openforis.collect.android.fields;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class Field extends UIElement {
	
	protected TextView label;
	
	public boolean isMultiple;
	public boolean hasMultipleParent;
	public boolean isRequired;
	
	public Field(Context context, int id, boolean isMultiple, boolean required) {
		super(context, id, isMultiple);
		
		this.label = new TextView(context);
		this.label.setMaxLines(1);
		this.label.setTextColor(Color.BLACK);
		
		this.isRequired = required;
	}

	public String getLabelText(){
		return this.label.getText().toString();
	}
	
	public void setLabelText(String label){
		this.label.setText(label);
	}
	
	public void setLabelTextColor(int color){
		this.label.setTextColor(color);
	}
}
