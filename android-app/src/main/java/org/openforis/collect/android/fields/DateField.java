package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openforis.collect.android.R;
import org.openforis.collect.android.dialogs.DateSetDialog;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Date;
import org.openforis.idm.model.DateAttribute;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.Node;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class DateField extends InputField {
	
	private List<String> values;
	
	public DateField(Context context, NodeDefinition nodeDef) {
		super(context, nodeDef);
		
		this.values = new ArrayList<String>();
		DateField.this.values.add(DateField.this.currentInstanceNo, "");

		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(DateField.this.getContext(), DateField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.txtBox = new EditText(context);
//		this.setHint(hintText);
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		this.txtBox.addTextChangedListener(this);
		
		//this.addView(this.label);
		this.addView(this.txtBox);
	
		// When text box in DateField got focus
		this.txtBox.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	//Get current settings about software keyboard for text fields
		    	if(hasFocus){
			    	if(this.getClass().toString().contains("DateField")){
				    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
				    	Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));
				    	//Switch on or off Software keyboard depend of settings
				    	if(valueForNum){
				    		DateField.this.makeReal();			    		
				        }
				    	else {
				    		txtBox.setInputType(InputType.TYPE_NULL);
//				    		DateField.this.setKeyboardType(null);
				    	}
				    	//Generate random id for text box
				    	final Random myRandom = new Random();
				    	DateField.this.txtBox.setId(myRandom.nextInt());
				    	//Show Date picker
				    	showDatePickerDialog(DateField.this.elemId);
			    	}
		    	}
		    }
	    });
	}

	private void showDatePickerDialog(int id) {
		Intent datePickerIntent = new Intent(DateField.this.getContext(), DateSetDialog.class);
    	datePickerIntent.putExtra("datefield_id", id);
    	datePickerIntent.putExtra("dateFieldPath", DateField.this.form.getFormScreenId());
    	datePickerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	super.getContext().startActivity(datePickerIntent);	
	}
	
	public void setValue(Integer position, String value, String path, boolean isTextChanged)
	{
		if (!isTextChanged)
			this.txtBox.setText(value);

		String day = "";
		String month = "";
		String year = "";
		int firstSeparatorIndex = value.indexOf(getResources().getString(R.string.dateSeparator));
		int secondSeparatorIndex = value.lastIndexOf(getResources().getString(R.string.dateSeparator));
		if (firstSeparatorIndex!=-1){
			if (secondSeparatorIndex!=-1){
				month = value.substring(0,firstSeparatorIndex);
				if (secondSeparatorIndex>(firstSeparatorIndex+1)){
					day = value.substring(firstSeparatorIndex+1,secondSeparatorIndex);	
				}				
				if (secondSeparatorIndex+1<value.length())
					year = value.substring(secondSeparatorIndex+1);
			}
		}
		Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
		if (node!=null){
			DateAttribute dateAtr = (DateAttribute)node;
			if (month.equals("") && day.equals("") && year.equals("")){
				dateAtr.setValue(new Date(null,null,null));
			} else if (month.equals("") && day.equals("")){
				dateAtr.setValue(new Date(Integer.valueOf(year),null,null));		    							
			} else if (month.equals("") && year.equals("")){
				dateAtr.setValue(new Date(null,null,Integer.valueOf(day)));	
			} else if (day.equals("") && year.equals("")){
				dateAtr.setValue(new Date(null,Integer.valueOf(month),null));	
			} else if (month.equals("")){
				dateAtr.setValue(new Date(Integer.valueOf(year),null,Integer.valueOf(day)));			    							
			} else if (day.equals("")){
				dateAtr.setValue(new Date(Integer.valueOf(year),Integer.valueOf(month),null));	
			} else if (year.equals("")){
				dateAtr.setValue(new Date(null,Integer.valueOf(month),Integer.valueOf(day)));	
			} else {
				dateAtr.setValue(new Date(Integer.valueOf(year),Integer.valueOf(month),Integer.valueOf(day)));
			}
		} else {
			if (month.equals("") && day.equals("") && year.equals("")){
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Date(null,null,null), position);
			} else if (month.equals("") && day.equals("")){
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Date(Integer.valueOf(year),null,null), position);		    							
			} else if (month.equals("") && year.equals("")){
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Date(null,null,Integer.valueOf(day)), position);	
			} else if (day.equals("") && year.equals("")){
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Date(null,Integer.valueOf(month),null), position);	
			} else if (month.equals("")){
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Date(Integer.valueOf(year),null,Integer.valueOf(day)), position);			    							
			} else if (day.equals("")){
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Date(Integer.valueOf(year),Integer.valueOf(month),null), position);	
			} else if (year.equals("")){
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Date(null,Integer.valueOf(month),Integer.valueOf(day)), position);
			} else {
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Date(Integer.valueOf(year),Integer.valueOf(month),Integer.valueOf(day)), position);
			}	
		}
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
