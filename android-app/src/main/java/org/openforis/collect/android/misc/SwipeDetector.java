package org.openforis.collect.android.misc;

import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class SwipeDetector extends SimpleOnGestureListener {
	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
        	Log.e("onFLING","==============================================");
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            // right to left swipe
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
               // Toast.makeText(SelectFilterActivity.this, "Left Swipe", Toast.LENGTH_SHORT).show();
            	Log.e("LEFT","SWIPE");
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                //Toast.makeText(SelectFilterActivity.this, "Right Swipe", Toast.LENGTH_SHORT).show();
            	Log.e("RIGHT","SWIPE");
            }
        } catch (Exception e) {
            // nothing
        }
        return false;
    }

}