package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.data.FieldValue;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.screens.FormScreen;

import android.content.Context;
import android.graphics.Color;
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
	
	private static FormScreen form;
	
	public BooleanField(Context context, FormScreen form, int id, String labelText, boolean isChecked1, boolean isChecked2,
			String label1Text, String label2Text,
			boolean isMultiple, boolean isAffirmativeOnly, boolean isRequired, FieldValue fieldValue) {
		super(context, id, isMultiple, isRequired);

		this.values = new ArrayList<ArrayList<Boolean>>();
		ArrayList<Boolean> initialValue = new ArrayList<Boolean>();
		initialValue.add(isChecked1);
		initialValue.add(isChecked2);
		this.values.add(initialValue);
		
		BooleanField.form = form;
		
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
				chckBox2.setChecked(!chckBox1.isChecked());
				ArrayList<String> value = new ArrayList<String>();
				value.add(String.valueOf(chckBox1.isChecked()));
				value.add(String.valueOf(!chckBox1.isChecked()));
				BooleanField.this.setValue(BooleanField.form.currInstanceNo, chckBox1.isChecked(), !chckBox1.isChecked());
				FormScreen.currentFieldValue = BooleanField.this.value;
				FormScreen.currentFieldValue.setValue(BooleanField.form.currInstanceNo, value);
				if (BooleanField.form.currentNode!=null){
					BooleanField.form.currentNode.addFieldValue(FormScreen.currentFieldValue);	
				}
  			}
	    });		
		this.label1 = new TextView(context);
		this.label1.setText(label1Text);
		this.label1.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		this.label1.setTextColor(Color.BLACK);
		
		this.chckBox2 = new CheckBox(context);
		this.chckBox2.setChecked(isChecked2);
		this.chckBox2.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		this.chckBox2.setOnClickListener(new OnClickListener() {
	          @Override
	          public void onClick(View v) {
	        	  	chckBox1.setChecked(!chckBox2.isChecked());		
					ArrayList<String> value = new ArrayList<String>();
					value.add(String.valueOf(!chckBox2.isChecked()));
					value.add(String.valueOf(chckBox2.isChecked()));
					BooleanField.this.setValue(BooleanField.form.currInstanceNo, !chckBox2.isChecked(), chckBox2.isChecked());
					FormScreen.currentFieldValue = BooleanField.this.value;
					FormScreen.currentFieldValue.setValue(BooleanField.form.currInstanceNo, value);
					if (BooleanField.form.currentNode!=null){
						BooleanField.form.currentNode.addFieldValue(FormScreen.currentFieldValue);
					}
	          }
	    });
		this.label2 = new TextView(context);
		this.label2.setText(label2Text);
		this.label2.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		this.label2.setTextColor(Color.BLACK);
		
		if (isAffirmativeOnly){
			this.chckBox2.setVisibility(View.GONE);
			this.label2.setVisibility(View.GONE);
			this.label1.setVisibility(View.GONE);
		}
		
		this.value = fieldValue;
		
		//this.addView(this.scrollLeft);
		this.addView(this.label);
		this.addView(this.chckBox1);
		this.addView(this.label1);
		this.addView(this.chckBox2);
		this.addView(this.label2);
		//this.addView(this.scrollRight);
	}

	/*public boolean getValue1()
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
	}*/
	
	public String getValue(int index, int tickedNo){
		//return TextField.this.values.get(index);
		return BooleanField.this.value.getValue(index).get(tickedNo);
	}
	
	public void setValue(int position, Boolean value1, Boolean value2)
	{
		ArrayList<String> valueToAdd = new ArrayList<String>();
		if (value1!=null){
			this.chckBox1.setChecked(value1);
			valueToAdd.add(String.valueOf(value1));	
		} else {
			this.chckBox1.setChecked(false);
			valueToAdd.add("");
		}
		
		if (value2!=null){
			this.chckBox2.setChecked(value2);
			valueToAdd.add(String.valueOf(value2));	
		} else {
			this.chckBox2.setChecked(false);
			valueToAdd.add("");
		}
		BooleanField.this.value.setValue(position, valueToAdd);
	}
	
	/*@Override
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
	}*/
	
	/*public List<Boolean> getValue(int index){
		return BooleanField.this.values.get(index);
	}*/
	
	@Override
	public int getInstancesNo(){
		return this.values.size();
	}
	
	public void resetValues(){
		this.values = new ArrayList<ArrayList<Boolean>>();
	}
	
	public void addValue(ArrayList<Boolean> value){
		this.values.add(value);
		this.currentInstanceNo++;
	}
	
	public List<ArrayList<Boolean>> getValues(){
		return this.values;
	}
	
	public void addOnClickListener(OnClickListener onClickListener1, OnClickListener onClickListener2) {
		this.chckBox1.setOnClickListener(onClickListener1);
		this.chckBox2.setOnClickListener(onClickListener2);
	}
}
