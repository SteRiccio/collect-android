package org.openforis.collect.android.fields;

import java.util.List;

import org.openforis.collect.android.messages.ToastMessage;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TaxonField extends InputField {
	
	private List<String> values;
	
	public TaxonField(Context context, String labelText, String initialText, String hintText,
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
		this.txtBox = new EditText(context);
		this.setHint(hintText);
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		
		this.addView(this.scrollLeft);
		this.addView(this.label);
		this.addView(this.txtBox);
		this.addView(this.scrollRight);
	}
	
	/*@Override
	protected void scrollLeft(){
    	Log.e("SCROLL","LEFTTaxonField");
    	Log.e("currINstancenO","=="+TaxonField.this.currentInstanceNo);
    	if (TaxonField.this.currentInstanceNo>1){
    		TaxonField.this.values.set(TaxonField.this.currentInstanceNo-1, TaxonField.this.txtBox.getText().toString());	        		
    		TaxonField.this.txtBox.setText(TaxonField.this.values.get(TaxonField.this.currentInstanceNo-2));
    		TaxonField.this.currentInstanceNo--;
    	}
    	Log.e("currentInstanceNO","=="+TaxonField.this.currentInstanceNo);
    	for (int i=0;i<TaxonField.this.values.size();i++){
    		Log.e("values"+i,"=="+TaxonField.this.values.get(i));
    	}
	}
	
	@Override
	protected void scrollRight(){
    	Log.e("currINstancenO","=="+TaxonField.this.currentInstanceNo);
    	if (TaxonField.this.values.size()==TaxonField.this.currentInstanceNo){
    		TaxonField.this.values.add(TaxonField.this.currentInstanceNo, "added");	        		
    	}
    	TaxonField.this.values.set(TaxonField.this.currentInstanceNo-1, TaxonField.this.txtBox.getText().toString());        			        		
		if (TaxonField.this.values.size()>TaxonField.this.currentInstanceNo)
			TaxonField.this.txtBox.setText(TaxonField.this.values.get(TaxonField.this.currentInstanceNo));
		TaxonField.this.currentInstanceNo++;
    	Log.e("currentInstanceNO","=="+TaxonField.this.currentInstanceNo);
    	for (int i=0;i<TaxonField.this.values.size();i++){
    		Log.e("values"+i,"=="+TaxonField.this.values.get(i));
    	}
	}*/
}
