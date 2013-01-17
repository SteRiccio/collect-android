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
import android.widget.AdapterView.OnItemClickListener;
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
	
	private boolean selectedForTheFirstTime;
	
	public CodeField(Context context, int id, String labelText, String promptText, 
			ArrayList<String> codes, ArrayList<String> options, 
			String selectedItem, boolean isSearchable,
			boolean isMultiple, boolean isRequired, FieldValue fieldValue) {
		super(context, id, isMultiple, isRequired);
		this.searchable = isSearchable;

		CodeField.form = (FormScreen)context;
		
		this.selectedForTheFirstTime = true;
		
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
		    	ArrayList<String> valueToAdd = new ArrayList<String>();
		    	valueToAdd.add(CodeField.this.codes.get((CodeField.this.spinner.getSelectedItemPosition())));

		    	FieldValue previousFocusedFieldValue = FormScreen.currentFieldValue;
		    	CodeField.this.setValue(CodeField.form.currInstanceNo, CodeField.this.codes.get(CodeField.this.spinner.getSelectedItemPosition()));
		    	if (!CodeField.this.selectedForTheFirstTime){
		    		FormScreen.currentFieldValue = CodeField.this.value;
					FormScreen.currentFieldValue.setValue(CodeField.form.currInstanceNo, valueToAdd);					
		    	} else {
		    		CodeField.this.selectedForTheFirstTime = false;
		    	}
				if (CodeField.form.currentNode!=null&&FormScreen.currentFieldValue!=null){
					CodeField.form.currentNode.addFieldValue(FormScreen.currentFieldValue);
				}
				FormScreen.currentFieldValue = previousFocusedFieldValue;
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    	
		    }

		});


		/*this.spinner.setOnItemClickListener(new OnItemClickListener() {			
		    public void onClick(View v) {
		            Log.e("SPINNER"+CodeField.this.getElementId(), "onClick");
			    	ArrayList<String> valueToAdd = new ArrayList<String>();
			    	valueToAdd.add(CodeField.this.codes.get((CodeField.this.spinner.getSelectedItemPosition())));

			    	CodeField.this.setValue(CodeField.form.currInstanceNo, CodeField.this.codes.get(CodeField.this.spinner.getSelectedItemPosition()));
					FormScreen.currentFieldValue = CodeField.this.value;
					FormScreen.currentFieldValue.setValue(CodeField.form.currInstanceNo, valueToAdd);
		        }

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
		        Log.e("SPINNER"+CodeField.this.getElementId(), "onItemClick");
		    	ArrayList<String> valueToAdd = new ArrayList<String>();
		    	valueToAdd.add(CodeField.this.codes.get((CodeField.this.spinner.getSelectedItemPosition())));

		    	CodeField.this.setValue(CodeField.form.currInstanceNo, CodeField.this.codes.get(CodeField.this.spinner.getSelectedItemPosition()));
				FormScreen.currentFieldValue = CodeField.this.value;
				FormScreen.currentFieldValue.setValue(CodeField.form.currInstanceNo, valueToAdd);
				if (CodeField.form.currentNode!=null&&FormScreen.currentFieldValue!=null){
					CodeField.form.currentNode.addFieldValue(FormScreen.currentFieldValue);
				}
			}
		});*/
		
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

		this.addView(this.label);
		this.addView(this.spinner);
		
		this.value = fieldValue;
	}
	
	public String getValue(int index){
		return CodeField.this.value.getValue(index).get(0);
	}
	
	public void setValue(int position, String code)
	{
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
