package org.openforis.collect.android.fields;

import org.openforis.collect.android.R;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class Separator extends UiElement {
	
	private View separator;
	private ImageView scrollLeft;
	private ImageView scrollRight;
	
	public Separator(Context context, boolean hasScrollingArrows) {
		super(context);
		
		this.separator = new View(context);
		
		LayoutParams params = new LayoutParams(0, getResources().getInteger(R.integer.separator_height), (float) 5.0);
		params.gravity = Gravity.CENTER;
		
		this.separator.setLayoutParams(params/*new LayoutParams(0, getResources().getInteger(R.integer.separator_height), (float) 5)*/);
		
		this.scrollLeft = new ImageView(context);			
		this.scrollRight = new ImageView(context);			
		if (hasScrollingArrows){
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
			
			params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 0.4);
			params.gravity = Gravity.CENTER;
			
			this.scrollLeft.setLayoutParams(params);
			this.scrollRight.setLayoutParams(params);			
		}
		else{
			this.scrollLeft.setVisibility(View.GONE);
			this.scrollRight.setVisibility(View.GONE);
		}
			
		/*this.addView(this.scrollLeft);
		this.addView(separator);
		this.addView(this.scrollRight);*/
		this.container.addView(scrollLeft);
		this.container.addView(separator);
		this.container.addView(scrollRight);
		this.container.setLayoutParams(params);
		
		this.addView(this.container);
	}
	
	public void setSeparatorColor(int color){
		this.separator.setBackgroundColor(color);
	}
	
	/*public void setSeparatorLayout(LayoutParams params){
		this.separator.setLayoutParams(params);
	}*/
	
	
}
