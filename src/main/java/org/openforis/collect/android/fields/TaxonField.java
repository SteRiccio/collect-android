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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TaxonField extends Field {
	
	public EditText txtBox;
	private ArrayAdapter<String> aa;
	private Spinner spinner;

	ArrayList<String> options;
	ArrayList<String> codes;
	
	boolean searchable;
	
	private List<ArrayList<String>> values;
	
	public TaxonField(Context context, String labelText, String initialText, String hintText, String promptText, 
			ArrayList<String> codes, ArrayList<String> options, 
			String selectedItem, boolean isSearchable,
			boolean isMultiple) {
		super(context, isMultiple);
		
		this.label = new TextView(context);
		this.label.setMaxLines(1);
		this.label.setText(labelText);
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(TaxonField.this.getContext(), TaxonField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		if (this.searchable){
			this.label.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
				}});
		}
		
		this.txtBox = new EditText(context);
		this.setHint(hintText);
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		
		this.searchable = isSearchable;
		
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
		
		this.values = new ArrayList<ArrayList<String>>();
		ArrayList<String> initialValue = new ArrayList<String>();
		initialValue.add(String.valueOf(this.spinner.getSelectedItemPosition()));
		initialValue.add(initialText);
		this.values.add(initialValue);
		
		
		this.addView(this.scrollLeft);
		this.addView(this.label);
		this.addView(this.txtBox);
		this.addView(this.scrollRight);
	}
	
	@Override
	public void scrollLeft(){
    	if (TaxonField.this.currentInstanceNo>1){
    		ArrayList<String> tempValue = new ArrayList<String>();
    		tempValue.add(String.valueOf(TaxonField.this.spinner.getSelectedItemPosition()));
    		tempValue.add(TaxonField.this.txtBox.getText().toString());
    		TaxonField.this.values.set(TaxonField.this.currentInstanceNo-1, tempValue);
    		TaxonField.this.txtBox.setText(TaxonField.this.values.get(TaxonField.this.currentInstanceNo-2).get(1).toString());
			TaxonField.this.spinner.setSelection(Integer.valueOf(TaxonField.this.values.get(TaxonField.this.currentInstanceNo-2).get(0)));
    		TaxonField.this.currentInstanceNo--;
    	}
	}
	
	@Override
	public void scrollRight(){
    	if (TaxonField.this.values.size()==TaxonField.this.currentInstanceNo){
    		ArrayList<String> tempValue = new ArrayList<String>();
    		tempValue.add("0");
    		tempValue.add("");
    		this.values.add(tempValue);
    		TaxonField.this.values.add(TaxonField.this.currentInstanceNo, tempValue);	        		
    	}
		ArrayList<String> tempValue = new ArrayList<String>();
		tempValue.add(String.valueOf(TaxonField.this.spinner.getSelectedItemPosition()));
		tempValue.add(TaxonField.this.txtBox.getText().toString());
    	TaxonField.this.values.set(TaxonField.this.currentInstanceNo-1, tempValue);        			        		
		if (TaxonField.this.values.size()>TaxonField.this.currentInstanceNo){
			TaxonField.this.txtBox.setText(TaxonField.this.values.get(TaxonField.this.currentInstanceNo).get(1).toString());
			TaxonField.this.spinner.setSelection(Integer.valueOf(TaxonField.this.values.get(TaxonField.this.currentInstanceNo).get(0)));
		}			
		TaxonField.this.currentInstanceNo++;
	}
	
	public String getHint()
	{
		return this.txtBox.getHint().toString();
	}
	
	public void setHint(String value)
	{
		this.txtBox.setHint(value);
	}
}
