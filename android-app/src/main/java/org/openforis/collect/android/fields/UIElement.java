package org.openforis.collect.android.fields;

import org.openforis.collect.android.R;
import org.openforis.idm.metamodel.NodeDefinition;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class UIElement extends LinearLayout{
	
	protected LinearLayout container;
	
	protected int currentInstanceNo;
	
	protected int elemId;	
	
	protected NodeDefinition nodeDefinition;
	
	public UIElement(Context context, NodeDefinition nodeDef/*, int id, boolean hasScrollingArrows*/){
		super(context);
		
		this.elemId = nodeDef.getId();
		this.nodeDefinition = nodeDef;
		
		this.container = new LinearLayout(context);
		this.container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, getResources().getInteger(R.integer.field_height)));
		
		this.currentInstanceNo = 0;
	}
	
	public int getElementId(){
		return this.elemId;
	}
	
	public int getInstancesNo(){
		return -1;
	}
	
	public void setCurrentInstanceNo(int value){
		this.currentInstanceNo = value;
	}
}
