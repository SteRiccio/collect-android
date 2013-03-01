package org.openforis.collect.android.fields;

import org.openforis.idm.metamodel.NodeDefinition;

import android.content.Context;

public abstract class Field extends UIElement {
	
	public boolean isMultiple;
	public boolean hasMultipleParent;
	
	public Field(Context context, NodeDefinition nodeDef /*int id, boolean isMultiple, boolean required*/) {
		super(context, nodeDef);
	}

	public String getLabelText(){
		return this.label.getText().toString();
	}
	
	public void setLabelTextColor(int color){
		this.label.setTextColor(color);
	}
}
