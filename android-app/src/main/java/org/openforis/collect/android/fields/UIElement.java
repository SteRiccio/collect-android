package org.openforis.collect.android.fields;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NodeLabel.Type;
import org.openforis.idm.model.Entity;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class UIElement extends TableLayout{
	
	protected LinearLayout container;
	
	protected TextView label;
	
	protected int currentInstanceNo;
	
	protected int elemId;	
	
	public NodeDefinition nodeDefinition;
	
	protected FormScreen form;
	
	private boolean isRequired;

	
	public UIElement(Context context, NodeDefinition nodeDef){
		super(context);
		
		this.elemId = nodeDef.getId();
		this.nodeDefinition = nodeDef;
		this.form = (FormScreen)context;
		
		this.isRequired = false;
		try{
			if (!this.form.getFormScreenId().equals("")){
				Entity parentEntity = (Entity)this.findParentEntity(this.form.getFormScreenId());
				this.isRequired = parentEntity.isRequired(nodeDef.getName());				
			}	
		} catch (Exception e){

		}

		this.label = new TextView(context);
		this.label.setMaxLines(1);
		this.label.setTextColor(Color.BLACK);
		String labelText = nodeDef.getLabel(Type.INSTANCE, null);
		if (labelText==null){
			if (nodeDef.getLabels().size()>0)
				labelText = nodeDef.getLabels().get(0).getText();
		}
		this.label.setText((this.isRequired)?getResources().getString(R.string.requiredFieldMarker)+labelText:getResources().getString(R.string.notRequiredFieldMarker)+labelText);
		if (!(nodeDef instanceof EntityDefinition)&& !(nodeDef.isMultiple()))
			this.addView(this.label);
		
		
		
		this.container = new LinearLayout(context);
		//this.container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, getResources().getInteger(R.integer.field_height)));
		
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
		if (path!=null){
			Entity parentEntity = ApplicationManager.currentRecord.getRootEntity();
			String[] entityPath = path.split(getResources().getString(R.string.valuesSeparator2));
			try{
				for (int m=1;m<entityPath.length;m++){
					
					String[] instancePath = entityPath[m].split(getResources().getString(R.string.valuesSeparator1));
					
					int id = Integer.valueOf(instancePath[0]);
					int instanceNo = Integer.valueOf(instancePath[1]);

					if (parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo)!=null)
						parentEntity = (Entity) parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
				}
			} catch (Exception e){

			}
			return parentEntity;
		}
		return null;
	}
}
