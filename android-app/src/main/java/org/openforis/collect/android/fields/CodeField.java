package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.messages.ToastMessage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class CodeField extends Field {
	
	private ArrayAdapter<String> aa;
	private Spinner spinner;

	ArrayList<String> options;
	ArrayList<String> codes;
	
	boolean searchable;
	
	private List<Integer> values;
	
	public CodeField(Context context, String labelText, String promptText, 
			ArrayList<String> codes, ArrayList<String> options, 
			String selectedItem, boolean isSearchable,
			boolean isMultiple) {
		super(context, isMultiple);
		this.searchable = isSearchable;		
		
		this.label = new TextView(context);
		this.label.setMaxLines(1);
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
		/*this.options = new ArrayList<String>();
		this.options.add("OPTION!");*/
		this.aa = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, this.options);

		this.aa.setDropDownViewResource(R.layout.codelistitem);

		this.spinner.setAdapter(aa);
		this.spinner.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));
		this.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	
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
		
		this.addView(this.scrollLeft);
		this.addView(this.label);
		this.addView(this.spinner);
		this.addView(this.scrollRight);
	}
	
	public String getValue()
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
	}
	
	@Override
	public void scrollLeft(){
    	if (CodeField.this.currentInstanceNo>1){
    		CodeField.this.values.set(CodeField.this.currentInstanceNo-1, CodeField.this.spinner.getSelectedItemPosition());
    		CodeField.this.spinner.setSelection(CodeField.this.values.get(CodeField.this.currentInstanceNo-2));
    		CodeField.this.currentInstanceNo--;
    	}
	}
	
	@Override
	public void scrollRight(){
    	if (CodeField.this.values.size()==CodeField.this.currentInstanceNo){
    		CodeField.this.values.add(0);    		
    	}
    	CodeField.this.values.set(CodeField.this.currentInstanceNo-1, CodeField.this.spinner.getSelectedItemPosition());        		
		if (CodeField.this.values.size()>CodeField.this.currentInstanceNo){
			CodeField.this.spinner.setSelection(CodeField.this.values.get(CodeField.this.currentInstanceNo));
		}
		CodeField.this.currentInstanceNo++;
	}
}
