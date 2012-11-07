package org.openforis.collect.android.fields;

import org.openforis.collect.android.R;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class UIElement extends LinearLayout{
	
	protected LinearLayout container;
	
	protected ImageView scrollLeft;
	protected ImageView scrollRight;
	
	protected int currentInstanceNo;
	
	protected int elemId;	
	
	public UIElement(Context context, int id, boolean hasScrollingArrows){
		super(context);
		
		this.elemId = id;
		
		this.container = new LinearLayout(context);
		this.container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, getResources().getInteger(R.integer.field_height)));
		
		this.currentInstanceNo = 0;
		
		this.scrollLeft = new ImageView(context);			
		this.scrollRight = new ImageView(context);			
		
		if (hasScrollingArrows){
			this.scrollLeft.setImageResource(R.drawable.arrow_left);
			this.scrollLeft.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
		        	UIElement.this.scrollLeft();
		        }
		    });
			this.scrollRight.setImageResource(R.drawable.arrow_right);
			this.scrollRight.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
		        	UIElement.this.scrollRight();
		        }
		    });
			
			LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 0.6);
			params.gravity = Gravity.CENTER;
			
			this.scrollLeft.setLayoutParams(params);
			this.scrollRight.setLayoutParams(params);			
		}
		else{
			this.scrollLeft.setVisibility(View.GONE);
			this.scrollRight.setVisibility(View.GONE);
		}
	}
	
	public void scrollLeft(){
		
	}
	
	public void scrollRight(){
		
	}
	
	public int getElementId(){
		return this.elemId;
	}
	
}
