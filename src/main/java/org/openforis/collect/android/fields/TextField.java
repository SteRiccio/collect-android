package org.openforis.collect.android.fields;

import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.misc.SwipeDetector;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class TextField extends InputField {
	
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	
	public TextField(Context context, String labelText, String initialText, String hintText,
			boolean isMultiple) {
		super(context, isMultiple);
		
		this.label.setText(labelText);
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
		
		this.container.addView(this.scrollLeft);
		this.container.addView(this.label);
		this.container.addView(this.txtBox);
		this.container.addView(this.scrollRight);
		this.addView(this.container);
	}
}
