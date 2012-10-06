package org.openforis.collect.android.fields;

import android.content.Context;
import android.widget.LinearLayout;

public class UiElement extends LinearLayout{
	
	protected LinearLayout container;
	
	public UiElement(Context context){
		super(context);
		this.container = new LinearLayout(context);
	}
	
}
