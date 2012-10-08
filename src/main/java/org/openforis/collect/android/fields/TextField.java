package org.openforis.collect.android.fields;

import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.misc.SwipeDetector;

import android.content.Context;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class TextField extends InputField {
	
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	
	public TextField(Context context, String labelText, String initialText, String hintText,
			boolean isMultiple) {
		super(context, isMultiple);
		
		this.label.setText(labelText);
		//this.label.setGravity(Gravity.CENTER);
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(TextField.this.getContext(), TextField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.setHint(hintText);
		
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));
		this.txtBox.setGravity(Gravity.CENTER);
		
		/*this.addView(this.scrollLeft);
		this.addView(this.label);
		this.addView(this.txtBox);
		this.addView(this.scrollRight);*/
		
		/*LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 0.6);
		params.gravity = Gravity.CENTER;
		
		this.scrollLeft.setLayoutParams(params);
		this.scrollRight.setLayoutParams(params);*/
		
		this.container.addView(this.scrollLeft);
		this.container.addView(this.label);
		this.container.addView(this.txtBox);
		this.container.addView(this.scrollRight);
		//this.container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, getResources().getInteger(R.integer.field_height)));
//		this.container.getChildAt(0).setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 0.4));
//		this.container.getChildAt(1).setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
//		this.container.getChildAt(2).setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 11));
//		this.container.getChildAt(3).setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 0.4));
		this.addView(this.container);
	}
}
