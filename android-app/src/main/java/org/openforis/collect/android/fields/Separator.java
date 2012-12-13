package org.openforis.collect.android.fields;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.misc.SwipeDetector;
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
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Separator extends UIElement {
	
	private Button separator;

	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	
	private EntityDefinition entity;
	
	public Separator(Context context, int id, boolean hasScrollingArrows, EntityDefinition entityDef) {
		super(context, id, hasScrollingArrows);
		
		this.separator = new Button(context);
		
		LayoutParams params = new LayoutParams(0, getResources().getInteger(R.integer.separator_height), (float) 5.0);
		params.gravity = Gravity.CENTER;
		
		this.separator.setLayoutParams(params/*new LayoutParams(0, getResources().getInteger(R.integer.separator_height), (float) 5)*/);
		
		//this.scrollLeft = new ImageView(context);			
		//this.scrollRight = new ImageView(context);			
		
		/*this.addView(this.scrollLeft);
		this.addView(separator);
		this.addView(this.scrollRight);*/
		
		this.container.addView(scrollLeft);
		this.container.addView(separator);
		this.container.addView(scrollRight);
		this.container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, getResources().getInteger(R.integer.field_height)));
		
		this.addView(this.container);
		
		this.entity = entityDef;

		//swipe detection
    	gestureDetector = new GestureDetector(new SwipeDetector(this));
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        this.separator.setOnTouchListener(gestureListener);
	}
	
	public void setSeparatorColor(int color){
		this.separator.setBackgroundColor(color);
	}
	
	@Override
	public void scrollLeft(){
		Log.e("scrollLEFT","=="+this.currentInstanceNo);
    	if (Separator.this.currentInstanceNo>0){
    		List<NodeDefinition> childrenList = Separator.this.entity.getChildDefinitions();
    		for (int i=0;i<childrenList.size();i++){
    			NodeDefinition nodeDefn = childrenList.get(i);
    			UIElement field = ApplicationManager.getUIElement(nodeDefn.getId());
    			field.scrollLeft();
    		}
    		Separator.this.currentInstanceNo--;
    	}
    	Log.e("scrolledLEFT","=="+this.currentInstanceNo);
	}
	
	@Override
	public void scrollRight(){
		Log.e("scrollRIGHT","=="+this.currentInstanceNo);
    	List<NodeDefinition> childrenList = Separator.this.entity.getChildDefinitions();
		for (int i=0;i<childrenList.size();i++){
			NodeDefinition nodeDefn = childrenList.get(i);
			UIElement field = ApplicationManager.getUIElement(nodeDefn.getId());
			field.scrollRight();		
		}
		Separator.this.currentInstanceNo++;
		Log.e("scrolledRIGHT","=="+this.currentInstanceNo);
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
		Log.e("FIXING","=="+value);
		this.currentInstanceNo = value;
	}
}
