package org.openforis.collect.android.fields;

import org.openforis.collect.android.data.FieldValue;
import org.openforis.idm.metamodel.NodeDefinition;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

public abstract class Field extends UIElement {
	
	protected TextView label;
	
	public boolean isMultiple;
	public boolean hasMultipleParent;
	public boolean isRequired;
	
	public FieldValue value;
	
	public Field(Context context, NodeDefinition nodeDef /*int id, boolean isMultiple, boolean required*/) {
		super(context, nodeDef);
		
		this.label = new TextView(context);
		this.label.setMaxLines(1);
		this.label.setTextColor(Color.BLACK);
		
		//this.isRequired = required;
	}

	public String getLabelText(){
		return this.label.getText().toString();
	}
	
	public void setLabelText(String label){
		if (this.isRequired){
			this.label.setText(label+"*");	
		} else {
			this.label.setText(label);	
		}		
	}
	
	public void setLabelTextColor(int color){
		this.label.setTextColor(color);
	}
}
