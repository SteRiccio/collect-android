package org.openforis.collect.android.fields;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.misc.SwipeDetector;
import org.openforis.collect.android.tabs.TabManager;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;

import android.content.Context;
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
    	if (Separator.this.currentInstanceNo>0){
    		List<NodeDefinition> childrenList = Separator.this.entity.getChildDefinitions();
    		for (int i=0;i<childrenList.size();i++){
    			NodeDefinition nodeDefn = childrenList.get(i);
    			UIElement field = TabManager.getUIElement(nodeDefn.getId());
    			field.scrollLeft();
    		}
    		Separator.this.currentInstanceNo--;
    	}
	}
	
	@Override
	public void scrollRight(){
    	List<NodeDefinition> childrenList = Separator.this.entity.getChildDefinitions();
		for (int i=0;i<childrenList.size();i++){
			NodeDefinition nodeDefn = childrenList.get(i);
			UIElement field = TabManager.getUIElement(nodeDefn.getId());
			field.scrollRight();		
		}
		Separator.this.currentInstanceNo++;
	}
}
