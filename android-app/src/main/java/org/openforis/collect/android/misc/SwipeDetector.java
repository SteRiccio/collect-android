package org.openforis.collect.android.misc;

import org.openforis.collect.android.fields.UIElement;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class SwipeDetector extends SimpleOnGestureListener {
	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    
    private UIElement uiElement;

    public SwipeDetector(UIElement uiEl){
    	this.uiElement = uiEl;
    }
    
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            /*if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            //right to left swipe
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
               	this.uiElement.scrollRight();
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            	this.uiElement.scrollLeft();
            }*/
        } catch (Exception e) {
          
        }
        return false;
    }

}