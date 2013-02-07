package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openforis.collect.android.R;
import org.openforis.collect.android.data.FieldValue;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NodeLabel.Type;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.method.QwertyKeyListener;
import android.text.method.TextKeyListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class MemoField extends InputField {
	
	private List<String> values;
	
	public MemoField(Context context, NodeDefinition nodeDef, FieldValue fieldValue) {
		super(context, nodeDef);
		
		MemoField.this.values = new ArrayList<String>();
		MemoField.this.values.add(MemoField.this.currentInstanceNo, "");
		
		this.label.setText(nodeDef.getLabel(Type.INSTANCE, null));
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(MemoField.this.getContext(), MemoField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.txtBox = new EditText(context);
		//this.setHint(hintText);
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		this.txtBox.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
	        @Override
	        public void onFocusChange(View v, boolean hasFocus) {
	            if (hasFocus) {
	            	FormScreen.currentFieldValue = MemoField.this.value;
	            	
			    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
			    	Boolean valueForText = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnTextField));
	            	
	            	//Create dialog for Memo
	            	final EditText input = new EditText(MemoField.this.getContext());
	            	input.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
	            	input.setText(txtBox.getText());
	            	
            	
	            	AlertDialog dialog = new AlertDialog.Builder(MemoField.this.getContext())	            	
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

			    	if(valueForText){
			    		input.setKeyListener(new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false));
			    		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			        }
			    	else {
			    		input.setKeyListener(null);
			    		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			    	}
	            }
	        }
	    });
		
		this.value = fieldValue;

		this.addView(this.label);
		this.addView(this.txtBox);
	}
	
	public String getValue(int index){
		return MemoField.this.value.getValue(index).get(0);
	}
	
	public void setValue(int position, String value)
	{
		this.txtBox.setText(value);
		ArrayList<String> valueToAdd = new ArrayList<String>();
		valueToAdd.add(value);
		MemoField.this.value.setValue(position, valueToAdd);
	}
	
	@Override
	public int getInstancesNo(){
		return this.values.size();
	}
	
	public void resetValues(){
		this.values = new ArrayList<String>();
	}
	
	public void addValue(String value){
		this.values.add(value);
		this.currentInstanceNo++;
	}
	
	public List<String> getValues(){
		return this.values;
	}
}
