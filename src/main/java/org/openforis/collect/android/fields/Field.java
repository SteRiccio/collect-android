package org.openforis.collect.android.fields;

import org.openforis.collect.android.R;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public abstract class Field extends UiElement{
	
	protected TextView label;
	public ImageView scrollLeft;
	public ImageView scrollRight;
	
	public boolean isMultiple;
	public boolean hasMultipleParent;
	
	public Field(Context context, boolean isMultiple) {
		super(context);
		
		this.label = new TextView(context);
		this.label.setMaxLines(1);
		
		this.scrollLeft = new ImageView(context);			
		this.scrollRight = new ImageView(context);				
		if (isMultiple){
			this.scrollLeft.setImageResource(R.drawable.arrow_left);
			this.scrollLeft.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
		        	
		        }
		    });
			this.scrollRight.setImageResource(R.drawable.arrow_right);
			this.scrollRight.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
		        	
		        }
		    });					
		}
		else{
			this.scrollLeft.setVisibility(View.GONE);
			this.scrollRight.setVisibility(View.GONE);
		}
	}

	public String getLabelText(){
		return this.label.getText().toString();
	}
	
	public void setLabelText(String label){
		this.label.setText(label);
	}
}
