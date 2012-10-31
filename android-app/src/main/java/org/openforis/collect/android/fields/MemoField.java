package org.openforis.collect.android.fields;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.messages.ToastMessage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MemoField extends InputField {
	
	private List<String> values;
	
	public MemoField(Context context, String labelText, String initialText, String hintText,
			boolean isMultiple) {
		super(context, isMultiple);
		
		this.label = new TextView(context);
		this.label.setMaxLines(1);
		this.label.setText(labelText);
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(MemoField.this.getContext(), MemoField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.txtBox = new EditText(context);
		this.setHint(hintText);
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		this.txtBox.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
	        @Override
	        public void onFocusChange(View v, boolean hasFocus) {
	            if (hasFocus) {
	            	final EditText input = new EditText(MemoField.this.getContext());
	            	input.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
	            	input.setText(txtBox.getText());
	            	new AlertDialog.Builder(MemoField.this.getContext())	            	
	                .setTitle(getResources().getString(R.string.editingMemoField)+" "+MemoField.this.getLabelText())
	                .setView(input)
	                .setPositiveButton(getResources().getString(R.string.okay), new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                        String value = input.getText().toString();
	                        txtBox.setText(value);
	                    }
	                }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                        //do nothing.
	                    }
	                }).show();
	            }
	        }

	    });
		
		this.addView(this.scrollLeft);
		this.addView(this.label);
		this.addView(this.txtBox);
		this.addView(this.scrollRight);
	}
	
	@Override
	public void scrollLeft(){
    	if (MemoField.this.currentInstanceNo>1){
    		MemoField.this.values.set(MemoField.this.currentInstanceNo-1, MemoField.this.txtBox.getText().toString());	        		
    		MemoField.this.txtBox.setText(MemoField.this.values.get(MemoField.this.currentInstanceNo-2));
    		MemoField.this.currentInstanceNo--;
    	}
	}
	
	@Override
	public void scrollRight(){
    	if (MemoField.this.values.size()==MemoField.this.currentInstanceNo){
    		MemoField.this.values.add(MemoField.this.currentInstanceNo, "");	        		
    	}
    	MemoField.this.values.set(MemoField.this.currentInstanceNo-1, MemoField.this.txtBox.getText().toString());        			        		
		if (MemoField.this.values.size()>MemoField.this.currentInstanceNo)
			MemoField.this.txtBox.setText(MemoField.this.values.get(MemoField.this.currentInstanceNo));
		MemoField.this.currentInstanceNo++;
	}
}
