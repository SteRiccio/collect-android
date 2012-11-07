package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;

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
	
	private List<ArrayList<Boolean>> values;
	
	public BooleanField(Context context, int id, String labelText, boolean isChecked1, boolean isChecked2,
			String label1Text, String label2Text,
			boolean isMultiple, boolean isAffirmativeOnly) {
		super(context, id, isMultiple);

		this.values = new ArrayList<ArrayList<Boolean>>();
		ArrayList<Boolean> initialValue = new ArrayList<Boolean>();
		initialValue.add(isChecked1);
		initialValue.add(isChecked2);
		this.values.add(initialValue);
		
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
		this.chckBox1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<Boolean> tempValue = new ArrayList<Boolean>();
				tempValue.add(chckBox1.isChecked());
				tempValue.add(chckBox2.isChecked());
				values.set(currentInstanceNo, tempValue);
  			}
	    });
		this.label1 = new TextView(context);
		this.label1.setText(label1Text);
		this.label1.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		
		this.chckBox2 = new CheckBox(context);
		this.chckBox2.setChecked(isChecked2);
		this.chckBox2.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		this.chckBox2.setOnClickListener(new OnClickListener() {
	          @Override
	          public void onClick(View v) { 
					ArrayList<Boolean> tempValue = new ArrayList<Boolean>();
					tempValue.add(chckBox1.isChecked());
					tempValue.add(chckBox2.isChecked());
					values.set(currentInstanceNo, tempValue);
	          }
	    });
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
	
	@Override
	public void scrollLeft(){
    	if (BooleanField.this.currentInstanceNo>0){
    		ArrayList<Boolean> tempValue = new ArrayList<Boolean>();
    		tempValue.add(BooleanField.this.chckBox1.isChecked());
    		tempValue.add(BooleanField.this.chckBox2.isChecked());
    		BooleanField.this.values.set(BooleanField.this.currentInstanceNo, tempValue);
    		BooleanField.this.chckBox1.setChecked(BooleanField.this.values.get(BooleanField.this.currentInstanceNo-1).get(0));
    		BooleanField.this.chckBox1.setChecked(BooleanField.this.values.get(BooleanField.this.currentInstanceNo-1).get(1));
    		BooleanField.this.currentInstanceNo--;
    	}
	}
	
	@Override
	public void scrollRight(){
    	if (BooleanField.this.values.size()==(BooleanField.this.currentInstanceNo+1)){    		
    		ArrayList<Boolean> tempValue = new ArrayList<Boolean>();
    		tempValue.add(false);
    		tempValue.add(false);
    		BooleanField.this.values.add(BooleanField.this.currentInstanceNo+1, tempValue);
    	}
    	ArrayList<Boolean> tempValue = new ArrayList<Boolean>();
		tempValue.add(BooleanField.this.chckBox1.isChecked());
		tempValue.add(BooleanField.this.chckBox2.isChecked());
    	BooleanField.this.values.set(BooleanField.this.currentInstanceNo, tempValue);
    	BooleanField.this.currentInstanceNo++;
		if (BooleanField.this.values.size()>=(BooleanField.this.currentInstanceNo+1)){
			BooleanField.this.chckBox1.setChecked(BooleanField.this.values.get(BooleanField.this.currentInstanceNo).get(0));
    		BooleanField.this.chckBox1.setChecked(BooleanField.this.values.get(BooleanField.this.currentInstanceNo).get(1));
		}
	}
	
	public List<Boolean> getValue(int index){
		return BooleanField.this.values.get(index);
	}
}
