package org.openforis.collect.android.fields;

import org.openforis.collect.android.messages.ToastMessage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class BooleanField extends Field {
	
	private TextView label1;
	private TextView label2;
	private CheckBox chckBox1;
	private CheckBox chckBox2;
	
	public BooleanField(Context context, String labelText, boolean isChecked1, boolean isChecked2,
			String label1Text, String label2Text,
			boolean isMultiple, boolean isAffirmativeOnly) {
		super(context, isMultiple);
		
		this.label = new TextView(context);
		this.label.setMaxLines(1);
		this.label.setText(labelText);
		this.label.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(BooleanField.this.getContext(), BooleanField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.chckBox1 = new CheckBox(context);
		this.chckBox1.setChecked(isChecked1);
		this.chckBox1.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		this.label1 = new TextView(context);
		this.label1.setText(label1Text);
		this.label1.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		
		this.chckBox2 = new CheckBox(context);
		this.chckBox2.setChecked(isChecked2);
		this.chckBox2.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		this.label2 = new TextView(context);
		this.label2.setText(label2Text);
		this.label2.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		
		if (isAffirmativeOnly){
			this.chckBox2.setVisibility(View.GONE);
			this.label2.setVisibility(View.GONE);
			this.label1.setVisibility(View.GONE);
		}
		
		this.addView(this.scrollLeft);
		this.addView(this.label);
		this.addView(this.chckBox1);
		this.addView(this.label1);
		this.addView(this.chckBox2);
		this.addView(this.label2);
		this.addView(this.scrollRight);
	}

	public boolean getValue1()
	{
		return this.chckBox1.isChecked();
	}
	
	public void setValue1(boolean isChecked)
	{
		this.chckBox1.setChecked(isChecked);
	}
	
	public boolean getValue2()
	{
		return this.chckBox2.isChecked();
	}
	
	public void setValue2(boolean isChecked)
	{
		this.chckBox2.setChecked(isChecked);
	}
}
