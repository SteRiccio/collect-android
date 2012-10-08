package org.openforis.collect.android.misc;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class SwipeDetector extends SimpleOnGestureListener {
	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private Context ctx;

    public SwipeDetector(Context context){
    	this.ctx = context;
    }
    
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
        	Log.e("onFling","======");
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            // right to left swipe
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            	Log.e("SWIPE LEFT",""+velocityX);
            	Log.e("SWIPE LEFT",""+velocityY);
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
               	Log.e("SWIPE RIGHT",""+velocityX);
               	Log.e("SWIPE RIGHT",""+velocityY);
            }
        } catch (Exception e) {
          
        }
        return false;
    }

}