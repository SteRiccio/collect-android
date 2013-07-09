package org.openforis.collect.android.fields;

import java.util.Random;

import org.openforis.collect.android.R;
import org.openforis.collect.android.dialogs.TimeSetDialog;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.ValidationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.validation.ValidationResults;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.Time;
import org.openforis.idm.model.TimeAttribute;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class TimeField extends InputField implements TextWatcher {
	
	public TimeField(Context context, NodeDefinition nodeDef) {
		super(context, nodeDef);
		
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(TimeField.this.getContext(), TimeField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.txtBox = new EditText(context);
		//this.setHint(hintText);
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		this.txtBox.addTextChangedListener(this);
		
		//this.addView(this.scrollLeft);
		//this.addView(this.label);
		this.addView(this.txtBox);
		//this.addView(this.scrollRight);		
		
		// When TimeField got focus
		this.txtBox.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	//Get current settings about software keyboard for text fields
		    	if(hasFocus){
			    	if(this.getClass().toString().contains("TimeField")){
				    	//Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
				    	//Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));
				    	boolean valueForNum = ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnNumericField), false);
				    	//Switch on or off Software keyboard depend of settings
				    	if(valueForNum){
				    		TimeField.this.makeReal();			    		
				        }
				    	else {
				    		txtBox.setInputType(InputType.TYPE_NULL);
//				    		TimeField.this.setKeyboardType(null);
				    	}
				    	//Generate random id for text box
				    	final Random myRandom = new Random();
				    	TimeField.this.txtBox.setId(myRandom.nextInt());
				    	//Show Time picker
				    	showTimePickerDialog(TimeField.this.elemDefId);				    	
			    	}
		    	}
		    }
	    });
	}
	
	private void validateResult(Node<? extends NodeDefinition> node){
		ValidationResults results = ValidationManager.validateField(node);
		if(results.getErrors().size() > 0 || results.getFailed().size() > 0){
			TimeField.this.txtBox.setBackgroundColor(Color.RED);
		}else if (results.getWarnings().size() > 0){
			TimeField.this.txtBox.setBackgroundColor(Color.YELLOW);
		}else{
			TimeField.this.txtBox.setBackgroundColor(Color.TRANSPARENT);
		}			    				
	}
	
	private void showTimePickerDialog(int id) {  	
//		Log.i(getResources().getString(R.string.app_name), "Id from date field was: " + id);
		Intent timePickerIntent = new Intent(TimeField.this.getContext(), TimeSetDialog.class);
		timePickerIntent.putExtra("timefield_id", id);
		timePickerIntent.putExtra("timeFieldPath", TimeField.this.form.getFormScreenId());
		timePickerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	super.getContext().startActivity(timePickerIntent);	
	}	
	
	/*@Override
	public void scrollLeft(){
    	if (TimeField.this.currentInstanceNo>0){
    		TimeField.this.values.set(TimeField.this.currentInstanceNo, TimeField.this.txtBox.getText().toString());	        		
    		TimeField.this.currentInstanceNo--;
    		TimeField.this.txtBox.setText(TimeField.this.values.get(TimeField.this.currentInstanceNo));    		
    	}
	}
	
	@Override
	public void scrollRight(){
    	if (TimeField.this.values.size()==(TimeField.this.currentInstanceNo+1)){
    		TimeField.this.values.add(TimeField.this.currentInstanceNo+1, "");
    	}
    	TimeField.this.values.set(TimeField.this.currentInstanceNo, TimeField.this.txtBox.getText().toString());
    	TimeField.this.currentInstanceNo++;
		if (TimeField.this.values.size()>=(TimeField.this.currentInstanceNo+1)){
			TimeField.this.txtBox.setText(TimeField.this.values.get(TimeField.this.currentInstanceNo));
		}
	}*/

	/*public String getValue(int index){
		return TimeField.this.values.get(index);
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		TimeField.this.values.add(currentInstanceNo, s.toString());
	}*/
	
	/*/public String getValue(int index){
		return TimeField.this.value.getValue(index).get(0);
	}*/
	
	/*public void setValue(int position, String value, String path, boolean isTextChanged)
	{
		if (!isTextChanged && !value.equals("null:null"))
			this.txtBox.setText(value);
		ArrayList<String> valueToAdd = new ArrayList<String>();
		valueToAdd.add(value);
		
		try{
			EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), Time.parseTime(value), position);
			//Log.e("time sparsowany","=="+value);
		} catch (Exception e){
			//Log.e("CZAS NIE SPARSOWANy","=="+value);
		}
	}*/
	
	public void setValue(Integer position, String value, String path, boolean isTextChanged)
	{
		if (!isTextChanged)
			this.txtBox.setText(value);
		
		String hour = "";
		String minute = "";
		int separatorIndex = value.indexOf(getResources().getString(R.string.timeSeparator));
		if (separatorIndex!=-1){
			hour = value.substring(0,separatorIndex);
			if (separatorIndex+1<value.length())
				minute = value.substring(separatorIndex+1);
		}
		Log.e("TIMEsetValue","value=="+value);
		Log.e("TIMEsetValue","hour=="+hour+"minute"+minute);
		Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
		
		if (node!=null){
			TimeAttribute timeAttr = (TimeAttribute)node;
			if (hour.equals("") && minute.equals("")){
				timeAttr.setValue(new Time(null,null));
			} else if (hour.equals("")){
				timeAttr.setValue(new Time(null,Integer.valueOf(minute)));
			} else if (minute.equals("")){
				timeAttr.setValue(new Time(Integer.valueOf(hour),null));
			} else {
				timeAttr.setValue(new Time(Integer.valueOf(hour),Integer.valueOf(minute)));
			}
			//Validate results
			//this.validateResult(node);
		} else {
			if (hour.equals("") && minute.equals("")){
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Time(null,null), position);
			} else if (hour.equals("")){
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Time(null,Integer.valueOf(minute)), position);
			} else if (minute.equals("")){
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Time(Integer.valueOf(hour),null), position);
			} else {
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Time(Integer.valueOf(hour),Integer.valueOf(minute)), position);
			}			
		}
	}
	
	/*@Override
	public void afterTextChanged(Editable s) {
		Log.e("afterTextChanged","time"+s.toString());
		this.setValue(0, s.toString(), TimeField.this.form.getFormScreenId(),true);
	}*/
}
