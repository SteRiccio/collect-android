package org.openforis.collect.android.fields;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.idm.metamodel.BooleanAttributeDefinition;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.DateAttributeDefinition;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
import org.openforis.idm.metamodel.RangeAttributeDefinition;
import org.openforis.idm.metamodel.TaxonAttributeDefinition;
import org.openforis.idm.metamodel.TextAttributeDefinition;
import org.openforis.idm.metamodel.TimeAttributeDefinition;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;

public class Separator extends UIElement {
	
	private Button separator;
	
	private EntityDefinition entity;
	
	public Separator(Context context, int id, boolean hasScrollingArrows, EntityDefinition entityDef) {
		super(context, id, hasScrollingArrows);
		
		this.separator = new Button(context);
		
		LayoutParams params = new LayoutParams(0, getResources().getInteger(R.integer.separator_height), (float) 5.0);
		params.gravity = Gravity.CENTER;
		
		this.separator.setLayoutParams(params);
		
		this.container.addView(separator);
		this.container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, getResources().getInteger(R.integer.field_height)));
		
		this.addView(this.container);
		
		this.entity = entityDef;
	}
	
	public void setSeparatorColor(int color){
		this.separator.setBackgroundColor(color);
	}
	
	public EntityDefinition getEntityDefinition(){
		return this.entity;
	}
	
	public int getInstancesNo(){
		NodeDefinition nodeDefn = Separator.this.entity.getChildDefinitions().get(0);
		UIElement uiField = ApplicationManager.getUIElement(nodeDefn.getId());
		if (nodeDefn.getClass().equals(NumberAttributeDefinition.class)){
			NumberField field = (NumberField)uiField;
			return field.getInstancesNo();
		} else if (nodeDefn.getClass().equals(CodeAttributeDefinition.class)){
			CodeField field = (CodeField)uiField;
			return field.getInstancesNo();
		} else if (nodeDefn.getClass().equals(BooleanAttributeDefinition.class)){
			BooleanField field = (BooleanField)uiField;
			return field.getInstancesNo();
		} else if (nodeDefn.getClass().equals(TextAttributeDefinition.class)){
			TextAttributeDefinition textAttrField = (TextAttributeDefinition) nodeDefn;			
			Object fieldType = textAttrField.getType();
			if (fieldType!=null){
				if(fieldType.toString().equals(getResources().getString(R.string.text_type_long))){//memo
					MemoField field = (MemoField)uiField;
					return field.getInstancesNo();
				} else {//short
					TextField field = (TextField)uiField;
					return field.getInstancesNo();
				}
			} else{//no type of text field specified
				TextField field = (TextField)uiField;
				return field.getInstancesNo();
			}			
		} else if (nodeDefn.getClass().equals(DateAttributeDefinition.class)){
			DateField field = (DateField)uiField;
			return field.getInstancesNo();
		} else if (nodeDefn.getClass().equals(TimeAttributeDefinition.class)){
			TimeField field = (TimeField)uiField;
			return field.getInstancesNo();
		} else if (nodeDefn.getClass().equals(RangeAttributeDefinition.class)){
			RangeField field = (RangeField)uiField;
			return field.getInstancesNo();
		} else if (nodeDefn.getClass().equals(TaxonAttributeDefinition.class)){
			TaxonField field = (TaxonField)uiField;
			return field.getInstancesNo();
		} else return -1;
	}
	
	public void fixInstanceNo(int value){
		this.currentInstanceNo = value;
	}
}
