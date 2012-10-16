package org.openforis.collect.android.fields;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.misc.SwipeDetector;
import org.openforis.collect.android.tabs.TabManager;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;

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
	
	public Separator(Context context, boolean hasScrollingArrows, EntityDefinition entityDef) {
		super(context, hasScrollingArrows);
		
		this.separator = new Button(context);
		
		LayoutParams params = new LayoutParams(0, getResources().getInteger(R.integer.separator_height), (float) 5.0);
		params.gravity = Gravity.CENTER;
		
		this.separator.setLayoutParams(params/*new LayoutParams(0, getResources().getInteger(R.integer.separator_height), (float) 5)*/);
		
		//this.scrollLeft = new ImageView(context);			
		//this.scrollRight = new ImageView(context);			
		
			
		/*this.addView(this.scrollLeft);
		this.addView(separator);
		this.addView(this.scrollRight);*/
		
		//swipe detection
    	gestureDetector = new GestureDetector(new SwipeDetector(context));
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        this.separator.setOnTouchListener(gestureListener);
		
		this.container.addView(scrollLeft);
		this.container.addView(separator);
		this.container.addView(scrollRight);
		this.container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, getResources().getInteger(R.integer.field_height)));
		
		this.addView(this.container);
		
		this.entity = entityDef;
	}
	
	public void setSeparatorColor(int color){
		this.separator.setBackgroundColor(color);
	}
	
	/*public void setSeparatorLayout(LayoutParams params){
		this.separator.setLayoutParams(params);
	}*/
	
	@Override
	protected void scrollLeft(){
    	Log.e("SCROLL","LEFTSeparator");
    	Log.e("currINstancenO","=="+Separator.this.currentInstanceNo);
    	if (Separator.this.currentInstanceNo>1){    		
    		//Separator.this.values.set(Separator.this.currentInstanceNo-1, Separator.this.txtBox.getText().toString());
    		//Separator.this.txtBox.setText(Separator.this.values.get(Separator.this.currentInstanceNo-2));
    		List<NodeDefinition> childrenList = Separator.this.entity.getChildDefinitions();
    		for (int i=0;i<childrenList.size();i++){
    			NodeDefinition nodeDefn = childrenList.get(i);
    			
    			UIElement textField = TabManager.getUIElement(nodeDefn);
    			Log.e("textField"+nodeDefn.getName(),"=="+textField.currentInstanceNo);
    			textField.scrollLeft();
    		}
    		Separator.this.currentInstanceNo--;
    	}
    	Log.e("currentInstanceNO","=="+Separator.this.currentInstanceNo);
    	/*for (int i=0;i<Separator.this.values.size();i++){
    		Log.e("values"+i,"=="+Separator.this.values.get(i));
    	}*/
	}
	
	@Override
	protected void scrollRight(){
		Log.e("SCROLL","RIGHTSeparator");
    	Log.e("currINstancenO","=="+Separator.this.currentInstanceNo);
    	/*if (Separator.this.values.size()==Separator.this.currentInstanceNo){
    		Separator.this.values.add(Separator.this.currentInstanceNo, "added");	        		
    	}
    	Separator.this.values.set(Separator.this.currentInstanceNo-1, Separator.this.txtBox.getText().toString());        			        		
		if (Separator.this.values.size()>Separator.this.currentInstanceNo)
			Separator.this.txtBox.setText(Separator.this.values.get(Separator.this.currentInstanceNo));
		*/
		Separator.this.currentInstanceNo++;
    	Log.e("currentInstanceNO","=="+Separator.this.currentInstanceNo);
    	/*for (int i=0;i<Separator.this.values.size();i++){
    		Log.e("values"+i,"=="+Separator.this.values.get(i));
    	}*/
	}
}
