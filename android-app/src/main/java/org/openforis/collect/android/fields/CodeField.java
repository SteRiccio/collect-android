package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.data.FieldValue;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.screens.FormScreen;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


public class CodeField extends Field {
	
	private ArrayAdapter<String> aa;
	private Spinner spinner;

	ArrayList<String> options;
	ArrayList<String> codes;
	
	boolean searchable;
	
	private List<Integer> values;
	
	private static FormScreen form;
	
	public CodeField(Context context, int id, String labelText, String promptText, 
			ArrayList<String> codes, ArrayList<String> options, 
			String selectedItem, boolean isSearchable,
			boolean isMultiple, boolean isRequired, FieldValue fieldValue) {
		super(context, id, isMultiple, isRequired);
		this.searchable = isSearchable;

		CodeField.form = (FormScreen)context;
		
		this.label.setText(labelText);
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(CodeField.this.getContext(), CodeField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		if (this.searchable){
			this.label.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
				}});
		}
		
		this.spinner = new Spinner(context);
		this.spinner.setPrompt(promptText);
		
		this.codes = codes;
		this.options = options;

		this.aa = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, this.options);
		this.aa.setDropDownViewResource(R.layout.codelistitem);

		this.spinner.setAdapter(aa);
		this.spinner.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));
		this.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	//CodeField.this.values.set(CodeField.this.currentInstanceNo, CodeField.this.spinner.getSelectedItemPosition());
		    	ArrayList<String> valueToAdd = new ArrayList<String>();
		    	valueToAdd.add(CodeField.this.codes.get((CodeField.this.spinner.getSelectedItemPosition())));

		    	//CodeField.this.setValue(CodeField.this.currentInstanceNo, CodeField.this.codes.get(CodeField.this.spinner.getSelectedItemPosition()));
		    	CodeField.this.setValue(CodeField.form.currInstanceNo, CodeField.this.codes.get(CodeField.this.spinner.getSelectedItemPosition()));
				FormScreen.currentFieldValue = CodeField.this.value;
				FormScreen.currentFieldValue.setValue(CodeField.form.currInstanceNo, valueToAdd);
				if (CodeField.form.currentNode!=null){
					CodeField.form.currentNode.addFieldValue(FormScreen.currentFieldValue);
				}
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    	
		    }

		});
		
		boolean isFound = false;
		int position = 0;
		if (selectedItem!=null){
			while (!isFound&&position<this.codes.size()){
				if (this.codes.get(position).equals(selectedItem)){
					isFound = true;
				}
				position++;
			}	
		}		
		if (isFound)
			this.spinner.setSelection(position-1);
		else
			this.spinner.setSelection(0);
		
		this.values = new ArrayList<Integer>();
		this.values.add(this.spinner.getSelectedItemPosition());
		//this.addView(this.scrollLeft);
		this.addView(this.label);
		this.addView(this.spinner);
		//this.addView(this.scrollRight);
		
		this.value = fieldValue;
	}
	
	/*public String getValue()
	{
		return String.valueOf(this.codes.get((int)this.spinner.getSelectedItemId()));
	}
	
	public void setValue(String code)
	{
		boolean isFound = false;
		int position = 0;
		while (!isFound&&position<this.codes.size()){
			if (this.codes.get(position).equals(code)){
				isFound = true;
			}
			position++;
		}
		if (isFound){
			this.spinner.setSelection(position-1);
		}			
		else{
			this.spinner.setSelection(0);
		}			
	}
	
	public void setValue(int position)
	{
		this.spinner.setSelection(position);
	}
	
	public void setEmptyValue()
	{
		this.spinner.setSelection(0);
	}*/
	
	public String getValue(int index){
		//return TextField.this.values.get(index);
		return CodeField.this.value.getValue(index).get(0);
	}
	
	/*public void setValue(int position, int positionOnList)
	{
		this.spinner.setSelection(positionOnList);
		ArrayList<String> valueToAdd = new ArrayList<String>();	
		valueToAdd.add(String.valueOf(positionOnList));
		CodeField.this.value.setValue(position, valueToAdd);
	}*/
	public void setValue(int position, String code)
	{
		Log.e("setVALUE"+this.getElementId(),position+"=="+code);
		ArrayList<String> valueToAdd = new ArrayList<String>();	
		boolean isFound = false;
		int counter = 0;
		while (!isFound&&counter<this.codes.size()){
			if (this.codes.get(counter).equals(code)){
				isFound = true;
			}
			counter++;
		}
		if (isFound){
			this.spinner.setSelection(counter-1);
			valueToAdd.add(code);
		}			
		else{
			this.spinner.setSelection(0);
			valueToAdd.add("null");
		}
		CodeField.this.value.setValue(position, valueToAdd);		
	}
	
	/*@Override
	public void scrollLeft(){
		Log.e("scrollLEFT","=="+this.currentInstanceNo);
    	if (CodeField.this.currentInstanceNo>0){
    		CodeField.this.values.set(CodeField.this.currentInstanceNo, CodeField.this.spinner.getSelectedItemPosition());
    		CodeField.this.currentInstanceNo--;
    		CodeField.this.spinner.setSelection(CodeField.this.values.get(CodeField.this.currentInstanceNo));    		
    	}
	}
	
	@Override
	public void scrollRight(){
		Log.e("scrollRIGHT","=="+this.currentInstanceNo);
    	if (CodeField.this.values.size()==(CodeField.this.currentInstanceNo+1)){
    		CodeField.this.values.add(CodeField.this.currentInstanceNo+1, 0);
    	}
    	CodeField.this.values.set(CodeField.this.currentInstanceNo, CodeField.this.spinner.getSelectedItemPosition());
    	CodeField.this.currentInstanceNo++;
		if (CodeField.this.values.size()>=(CodeField.this.currentInstanceNo+1)){
			CodeField.this.spinner.setSelection(CodeField.this.values.get(CodeField.this.currentInstanceNo));
		}		
	}*/
	
	/*public String getValue(int index){
		//return CodeField.this.values.get(index);
		if (this.codes.size()>0)
			return this.codes.get(index);
		else
			return null;
	}*/
	
	@Override
	public int getInstancesNo(){
		return this.values.size();
	}
	
	public void resetValues(){
		this.values = new ArrayList<Integer>();
	}
	
	public void addValue(String valueCode){
		boolean isFound = false;
		int position = 0;
		while (!isFound&&position<this.codes.size()){
			if (this.codes.get(position).equals(valueCode)){
				isFound = true;
			}
			position++;
		}
		if (isFound){
			this.values.add(position-1);
			this.currentInstanceNo++;
		}
	}
	
	public List<Integer> getValues(){
		return this.values;
	}
}
