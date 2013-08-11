package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.collect.model.NodeChangeSet;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.RangeAttributeDefinition;
import org.openforis.idm.metamodel.Unit;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.IntegerRange;
import org.openforis.idm.model.IntegerRangeAttribute;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.RealRange;
import org.openforis.idm.model.RealRangeAttribute;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TableLayout.LayoutParams;

public class RangeField extends InputField {
	
	ArrayList<String> options;
	ArrayList<String> codes;
	
	private ArrayAdapter<String> aa;
	private Spinner spinner;
	
	private Unit unit;
	private List<Unit> unitsList;
	
	public RangeField(Context context, NodeDefinition nodeDef) {
		super(context, nodeDef);

		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(RangeField.this.getContext(), RangeField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.txtBox = new EditText(context);
		//this.setHint(hintText);
		this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));		
		
		//this.addView(this.label);
		this.addView(this.txtBox);
		
		// When RangeField got focus
		this.txtBox.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	//Get current settings about software keyboard for text fields
		    	if(hasFocus){
			    	if(this.getClass().toString().contains("RangeField")){
				    	//Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
				    	//Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));
			    		boolean valueForNum = false;				   
				    	if (ApplicationManager.appPreferences!=null){
				    		valueForNum = ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnNumericField), false);
				    	}
				    	//Switch on or off Software keyboard depend of settings
				    	if(valueForNum){
				    		RangeField.this.makeReal();			    		
				        }
				    	else {
				    		txtBox.setInputType(InputType.TYPE_NULL);
				    	}
			    	}
		    	}
		    }
	    });
		
		//Check for every given character is it number or not
		this.txtBox.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {
				/*if (s.length() > 0){
					char symbol = s.charAt(s.length()-1);
					if (!validateCharacter(symbol)){
						String strReplace = s.subSequence(0, s.length()-1).toString(); 
						RangeField.this.txtBox.setText(strReplace);
						RangeField.this.txtBox.setSelection(strReplace.length());
					}	
				}*/			
			}
			public void beforeTextChanged(CharSequence s, int start,  int count, int after) {}				 
			//public void onTextChanged(CharSequence s, int start, int before, int count) {}	
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 0){
					if (before==0){
						char symbol = s.charAt(start);
						if (!validateCharacter(symbol)){
							String strReplace = "";
							if (before==0){//inputting characters
								strReplace = s.toString().substring(0, start+count-1);
								strReplace += s.toString().substring(start+count);
							} else {//deleting characters
								//do nothing - number with deleted digit is still a number
							}
							RangeField.this.txtBox.setText(strReplace);
							RangeField.this.txtBox.setSelection(start);
						}	
					}					
				}
			}
		});	
	
		RangeAttributeDefinition rangeAttr = (RangeAttributeDefinition) this.nodeDefinition;
		this.unitsList = rangeAttr.getUnits();
		if (unitsList.size()>0){
			this.codes = new ArrayList<String>();
			this.codes.add("null");
			this.options = new ArrayList<String>();
			this.options.add("");
			int defaultUnitPosition = 0;
			for (int i=0;i<unitsList.size();i++){
				Unit unit = unitsList.get(i);
				this.codes.add(unit.getName());
				this.options.add(unit.getName());
				if (rangeAttr.getDefaultUnit().equals(unit)){
					defaultUnitPosition = i+1;
				}
			}
			this.spinner = new Spinner(context);
			this.spinner.setPrompt(this.label.getText());
			this.aa = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, this.options);
			this.aa.setDropDownViewResource(R.layout.codelistitem);
			this.spinner.setAdapter(aa);
			//this.spinner.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));
			this.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			    @Override
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
			    	RangeField.this.unit = RangeField.this.unitsList.get(position-1);
			    }

			    @Override
			    public void onNothingSelected(AdapterView<?> parentView) {
			    	
			    }

			});
			this.spinner.setSelection(defaultUnitPosition);
			this.addView(this.spinner);
		}		
	}
	
	//Check is given symbol number or "." (if type is not "integer")
	private Boolean validateCharacter(char symbol){
		Boolean result = false;
		if (Character.isDigit(symbol) || symbol == '.' || symbol == '-'){
			result = true;
		}
		else{
			result = false;
		}
		return result;
	}
	
	public void setValue(Integer position, String value, String path, boolean isTextChanged)
	{
		if (!isTextChanged)
			this.txtBox.setText(value);

		String valueFrom = "";
		String valueTo = "";
		int separatorIndex = value.indexOf(getResources().getString(R.string.rangeSeparator));
		if (separatorIndex!=-1){
			valueFrom = value.substring(0,separatorIndex);
			if (separatorIndex+1<value.length())
				valueTo = value.substring(separatorIndex+1);
		} else {
			valueFrom = value;
		}
		try{
			Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
			NodeChangeSet nodeChangeSet = null;
			Entity parentEntity = this.findParentEntity(path);
			if (node!=null){
				if (((RangeAttributeDefinition) this.nodeDefinition).isReal()){
//					RealRangeAttribute rangeAtr = (RealRangeAttribute)node;
					if (valueFrom.equals("") && valueTo.equals("")){
//						rangeAtr.setValue(new RealRange(null,null,null));
						nodeChangeSet = ServiceFactory.getRecordManager().updateAttribute((RealRangeAttribute)node, new RealRange(null,null,this.unit));
					} else if (valueFrom.equals("")){
//						rangeAtr.setValue(new RealRange(null,Double.valueOf(valueTo),null));
						nodeChangeSet = ServiceFactory.getRecordManager().updateAttribute((RealRangeAttribute)node, new RealRange(null,Double.valueOf(valueTo),this.unit));
					} else if (valueTo.equals("")){
//						rangeAtr.setValue(new RealRange(Double.valueOf(valueFrom),null,null));
						nodeChangeSet = ServiceFactory.getRecordManager().updateAttribute((RealRangeAttribute)node, new RealRange(Double.valueOf(valueFrom),null,this.unit));
					} else {
//						rangeAtr.setValue(new RealRange(Double.valueOf(valueFrom),Double.valueOf(valueTo),null));
						nodeChangeSet = ServiceFactory.getRecordManager().updateAttribute((RealRangeAttribute)node, new RealRange(Double.valueOf(valueFrom),Double.valueOf(valueTo),this.unit));
					}			
				} else {
//					IntegerRangeAttribute rangeAtr = (IntegerRangeAttribute)node;
					if (valueFrom.equals("") && valueTo.equals("")){
//						rangeAtr.setValue(new IntegerRange(null,null,null));
						nodeChangeSet = ServiceFactory.getRecordManager().updateAttribute((IntegerRangeAttribute)node, new IntegerRange(null,null,null));
					} else if (valueFrom.equals("")){
//						rangeAtr.setValue(new IntegerRange(null,Integer.valueOf(valueTo),null));
						nodeChangeSet = ServiceFactory.getRecordManager().updateAttribute((IntegerRangeAttribute)node, new IntegerRange(null,Integer.valueOf(valueTo),this.unit));
						
					} else if (valueTo.equals("")){
//						rangeAtr.setValue(new IntegerRange(Integer.valueOf(valueFrom),null,null));
						nodeChangeSet = ServiceFactory.getRecordManager().updateAttribute((IntegerRangeAttribute)node, new IntegerRange(Integer.valueOf(valueFrom),null,this.unit));
					} else {
//						rangeAtr.setValue(new IntegerRange(Integer.valueOf(valueFrom),Integer.valueOf(valueTo),null));
						nodeChangeSet = ServiceFactory.getRecordManager().updateAttribute((IntegerRangeAttribute)node, new IntegerRange(Integer.valueOf(valueFrom),Integer.valueOf(valueTo),this.unit));
					}	
				}			
			} else {
				if (((RangeAttributeDefinition) this.nodeDefinition).isReal()){
					if (valueFrom.equals("") && valueTo.equals("")){
//						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new RealRange(null,null,null), position);
						nodeChangeSet = ServiceFactory.getRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new RealRange(null,null,this.unit), null, null);
					} else if (valueFrom.equals("")){
//						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new RealRange(null,Double.valueOf(valueTo),null), position);
						nodeChangeSet = ServiceFactory.getRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new RealRange(null,Double.valueOf(valueTo),this.unit), null, null);
					} else if (valueTo.equals("")){
//						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new RealRange(Double.valueOf(valueFrom),null,null), position);
						nodeChangeSet = ServiceFactory.getRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new RealRange(Double.valueOf(valueFrom),null,this.unit), null, null);
					} else {
//						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new RealRange(Double.valueOf(valueFrom),Double.valueOf(valueTo),null), position);
						nodeChangeSet = ServiceFactory.getRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new RealRange(Double.valueOf(valueFrom),Double.valueOf(valueTo),this.unit), null, null);
					}		
				} else {
					if (valueFrom.equals("") && valueTo.equals("")){
//						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new IntegerRange(null,null,null), position);
						nodeChangeSet = ServiceFactory.getRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new IntegerRange(null,null,this.unit), null, null);
					} else if (valueFrom.equals("")){
//						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new IntegerRange(null,Integer.valueOf(valueTo),null), position);
						nodeChangeSet = ServiceFactory.getRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new IntegerRange(null,Integer.valueOf(valueTo),this.unit), null, null);
					} else if (valueTo.equals("")){
//						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new IntegerRange(Integer.valueOf(valueFrom),null,null), position);
						nodeChangeSet = ServiceFactory.getRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new IntegerRange(Integer.valueOf(valueFrom),null,this.unit), null, null);
					} else {
//						EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new IntegerRange(Integer.valueOf(valueFrom),Integer.valueOf(valueTo),null), position);
						nodeChangeSet = ServiceFactory.getRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new IntegerRange(Integer.valueOf(valueFrom),Integer.valueOf(valueTo),this.unit), null, null);
					}	
				}
			}
//			ApplicationManager.updateUIElementsWithValidationResults(nodeChangeSet);
			validateField(nodeChangeSet);
		}catch (Exception e){
			Log.e("RangeField", "ERROR when try to set value" + e.getMessage());
			e.printStackTrace();
		}
	}
}
