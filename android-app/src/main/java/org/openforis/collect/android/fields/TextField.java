package org.openforis.collect.android.fields;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.collect.model.NodeChangeSet;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.TextAttribute;
import org.openforis.idm.model.TextValue;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.QwertyKeyListener;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class TextField extends InputField {

	public TextField(Context context, NodeDefinition nodeDef) {
		super(context, nodeDef);
		
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		//this.label.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(TextField.this.getContext(), TextField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.txtBox = new EditText(context);
		//this.setHint(hintText);
		//this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));
		//this.txtBox.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,  getResources().getInteger(R.integer.input_field_height)));
		this.txtBox.addTextChangedListener(this);
		this.txtBox.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
		
		//this.container.addView(this.label);
		//this.container.addView(this.txtBox);
		//this.container.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,  LayoutParams.WRAP_CONTENT));
		//this.addView(this.container);
		this.addView(this.txtBox);
		
		// When TextField gets focus
		this.txtBox.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	// Get current settings about software keyboard for text fields
		    	if(hasFocus){
			    	if(this.getClass().toString().contains("TextField")){
				    	//Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
				    	//Boolean valueForText = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnTextField));			    
				    	boolean valueForText = false;				   
				    	if (ApplicationManager.appPreferences!=null){
				    		valueForText = ApplicationManager.appPreferences.getBoolean(getResources().getString(R.string.showSoftKeyboardOnTextField), false);
				    	}
				    	// Switch on or off Software keyboard depend of settings
				    	if(valueForText){
				    		TextField.this.setKeyboardType(new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false));
				        }
				    	else {
				    		txtBox.setInputType(InputType.TYPE_NULL);
				    	}				    	
			    	}
		    	} else {
		    		//Log.e("FOCUS","LOST TEXT");
		    	}
		    }
	    });
	}
	
	
	public void setValue(Integer position, String value, String path, boolean isTextChanged)
	{
		if (!isTextChanged)
			this.txtBox.setText(value);
		
		/*Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
		if (node!=null){
			TextAttribute textAtr = (TextAttribute)node;
			textAtr.setValue(new TextValue(value));
		} else {
			EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), value, position);	
		}*/		
		Entity parentEntity = this.findParentEntity(path);
		Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
		NodeChangeSet nodeChangeSet = null;
		if (node!=null){
			//Log.e("Text field with Id: ",node.getDefinition().getId() + " is updating. Node name is: " + node.getName() + " Node ID is: " + node.getInternalId());
			nodeChangeSet = ServiceFactory.getRecordManager().updateAttribute((TextAttribute)node, new TextValue(value));
//			ApplicationManager.updateUIElementsWithValidationResults(nodeChangeSet);
		} else {
			//Log.e("Text field","is adding attribute. Node is NULL ");
			nodeChangeSet = ServiceFactory.getRecordManager().addAttribute(parentEntity, this.nodeDefinition.getName(), new TextValue(value), null, null);
		}
//		ApplicationManager.updateUIElementsWithValidationResults(nodeChangeSet);
		validateField(nodeChangeSet);
	}
}
