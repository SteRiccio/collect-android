package org.openforis.collect.android.fields;

import java.util.Map;
import java.util.Random;

import org.openforis.collect.android.R;
import org.openforis.collect.android.dialogs.TimeSetDialog;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.IntegerRange;
import org.openforis.idm.model.IntegerRangeAttribute;
import org.openforis.idm.model.Node;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.text.TextWatcher;
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
				    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
				    	Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));
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
				    	showTimePickerDialog(TimeField.this.elemId);				    	
			    	}
		    	}
		    }
	    });
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

		Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
		if (node!=null){
			IntegerRangeAttribute rangeAtr = (IntegerRangeAttribute)node;
			if (hour.equals("") && minute.equals("")){
				rangeAtr.setValue(new IntegerRange(null,null,null));
			} else if (hour.equals("")){
				rangeAtr.setValue(new IntegerRange(null,Integer.valueOf(minute),null));
			} else if (minute.equals("")){
				rangeAtr.setValue(new IntegerRange(Integer.valueOf(hour),null,null));
			} else {
				rangeAtr.setValue(new IntegerRange(Integer.valueOf(hour),Integer.valueOf(minute),null));
			}
		} else {
			if (hour.equals("") && minute.equals("")){
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new IntegerRange(null,null,null), position);
			} else if (hour.equals("")){
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new IntegerRange(null,Integer.valueOf(minute),null), position);
			} else if (minute.equals("")){
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new IntegerRange(Integer.valueOf(hour),null,null), position);
			} else {
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new IntegerRange(Integer.valueOf(hour),Integer.valueOf(minute),null), position);
			}			
		}
	}
}
