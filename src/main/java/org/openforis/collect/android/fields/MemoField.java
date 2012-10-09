package org.openforis.collect.android.fields;

import org.openforis.collect.android.R;
import org.openforis.collect.android.messages.ToastMessage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MemoField extends InputField {
	
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
	                        // Do nothing.
	                    }
	                }).show();
	            	/*final EditText input = new EditText(MemoField.this.getContext());
	            	AlertMessage.createPositiveNegativeDialog(MemoField.this.getContext(), false, getResources().getDrawable(R.drawable.warningsign),
	        				getResources().getString(R.string.exitAppTitle), getResources().getString(R.string.exitAppMessage),
	        				getResources().getString(R.string.yes), getResources().getString(R.string.no),
	        	    		new DialogInterface.OnClickListener() {
	        					@Override
	        					public void onClick(DialogInterface dialog, int which) {
	        						String value = input.getText().toString(); 
	        						Log.e("TEXT","=="+value);
	        						txtBox.setText(value);
	        					}
	        				},
	        	    		new DialogInterface.OnClickListener() {
	        					@Override
	        					public void onClick(DialogInterface dialog, int which) {
	        						
	        					}
	        				},
	        				input).show();*/
	            }
	        }

	    });
		
		this.addView(this.scrollLeft);
		this.addView(this.label);
		this.addView(this.txtBox);
		this.addView(this.scrollRight);
	}
}
