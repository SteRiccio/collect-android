package org.openforis.collect.android.fields;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Entity;

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
	
	public Entity findParentEntity(String path){
		Entity parentEntity = ApplicationManager.currentRecord.getRootEntity();
		String screenPath = path;
		String[] entityPath = screenPath.split(getResources().getString(R.string.valuesSeparator2));
		try{
			for (int m=2;m<entityPath.length;m++){
				String[] instancePath = entityPath[m].split(getResources().getString(R.string.valuesSeparator1));
				
				int id = Integer.valueOf(instancePath[0]);
				int instanceNo = Integer.valueOf(instancePath[1]);
				parentEntity = (Entity) parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
			}			
		} catch (ClassCastException e){
			
		}
		return parentEntity;
		//Log.e("VALUEsetTO",parentEntity.getName()+"==="+this.nodeDefinition.getName());
		//Log.e("VALUEset","P"+value+"==="+position);
	}
}
