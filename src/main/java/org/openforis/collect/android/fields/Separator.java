package org.openforis.collect.android.fields;

import org.openforis.collect.android.R;
import org.openforis.collect.android.misc.SwipeDetector;

import android.content.Context;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Separator extends UiElement {
	
	private Button separator;
	
	 private GestureDetector gestureDetector;
	 private View.OnTouchListener gestureListener;
	
	public Separator(Context context, boolean hasScrollingArrows) {
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
	}
	
	public void setSeparatorColor(int color){
		this.separator.setBackgroundColor(color);
	}
	
	/*public void setSeparatorLayout(LayoutParams params){
		this.separator.setLayoutParams(params);
	}*/
	
	
}
