package org.openforis.collect.android.fields;

import java.util.List;

import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.model.AttributeChange;
import org.openforis.collect.model.NodeChange;
import org.openforis.collect.model.NodeChangeSet;
import org.openforis.collect.model.validation.ValidationMessageBuilder;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.validation.ValidationResult;
import org.openforis.idm.metamodel.validation.ValidationResults;
import org.openforis.idm.model.Attribute;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.util.Log;
import android.widget.EditText;

public class InputField extends Field implements TextWatcher {
	
	public EditText txtBox;
	
	public InputField(Context context, NodeDefinition nodeDef) {
		super(context, nodeDef);
		this.txtBox = new EditText(context);
		this.txtBox.addTextChangedListener(this);
	}
	
	public void setKeyboardType(KeyListener keyListener){
		this.txtBox.setKeyListener(keyListener);
	}
	
	public String getHint()
	{
		return this.txtBox.getHint().toString();
	}
	
	public void setHint(String value)
	{
		this.txtBox.setHint(value);
	}
	
	public void setAlignment(int alignment){
		this.txtBox.setGravity(alignment);
	}
	
	public void makeReal()
	{
		this.txtBox.setKeyListener(new DigitsKeyListener(true,true));		
	}
	
	public void makeInteger()
	{
		this.txtBox.setKeyListener(new DigitsKeyListener(true,false));
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}
	
	public void addTextChangedListener(TextWatcher textWatcher) {
		this.txtBox.addTextChangedListener(textWatcher);
	}
	
	
	/**
	 * It validates value into the field
	 * @param nodeChangeSet
	 */
	public void validateField(NodeChangeSet nodeChangeSet){
    	List<NodeChange<?>> nodeChangesList = nodeChangeSet.getChanges();
    	Log.d("Validation starts. Size of NodeChangeList","== " + nodeChangesList.size());
    	for (NodeChange<?> nodeChange : nodeChangesList){
    			//HERE WE CHECK DOES IT HAVE ANY ERRORS or WARNINGS
    			if (nodeChange instanceof AttributeChange) {
    				ValidationResults results = ((AttributeChange)nodeChange).getValidationResults();
    				Log.e("VALIDATION FOR FIELD", "Errors: " + results.getErrors().size() + " : " + results.getErrors().toString());
    				Log.d("VALIDATION FOR FIELD", "Warnings: "  + results.getWarnings().size() + " : " + results.getWarnings().toString()); 			
    				//Make background color red or yellow if there is any errors/warnings 				
    				String validationMsg = "";
    				if (results.getErrors().size() > 0){
    					setBackgroundColor(Color.RED);
    					for (ValidationResult error : results.getErrors()){
    						validationMsg += ValidationMessageBuilder.createInstance().getValidationMessage((Attribute<?, ?>)nodeChange.getNode(), error) + " : ";
    					}
    					Log.d("Validation message is: ", validationMsg);
    					//Show dialog 
    					if (this instanceof TimeField || this instanceof DateField){
    						//Just change background for first time
    					}else{
							AlertDialog alertDialog = getValidationMessageAlert("Error!", validationMsg);
	    					alertDialog.show();       
    					} 						
    				}
    				else if (results.getWarnings().size() > 0){
    					setBackgroundColor(Color.YELLOW);
    					for (ValidationResult warning : results.getWarnings()){
    						validationMsg += ValidationMessageBuilder.createInstance().getValidationMessage((Attribute<?, ?>)nodeChange.getNode(), warning) + " : ";
    					}
    					Log.d("Validation message is: ", validationMsg);  
    					//Show dialog 
    					if (this instanceof TimeField || this instanceof DateField){
    						//Just change background for first time
    					}else{
							AlertDialog alertDialog = getValidationMessageAlert("Warning!", validationMsg);
	    					alertDialog.show();       
    					}      						       					
    				}
    				else
    					setBackgroundColor(Color.TRANSPARENT); 
    			}		
    		}    				
	}
	
	/**
	 * It shows alert dialog when validation returns error or warning
	 * @param strTitle alert title, could be "Error" or "Warning"
	 * @param validationMsg the message from ValidationResults
	 * @return
	 */
	public AlertDialog getValidationMessageAlert(String strTitle, String validationMsg){
		AlertDialog alertDialog = new AlertDialog.Builder(this.getContext()).create();
		alertDialog.setTitle(strTitle);
		alertDialog.setMessage(validationMsg);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			 //Do nothing, just close dialog
			 }
		 });
		    		
		return alertDialog;
	}
}
