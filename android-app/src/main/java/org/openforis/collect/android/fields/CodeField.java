package org.openforis.collect.android.fields;

import java.util.ArrayList;

import org.openforis.collect.android.R;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Code;
import org.openforis.idm.model.CodeAttribute;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.Node;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


public class CodeField extends Field {
	
	private ArrayAdapter<String> aa;
	private Spinner spinner;

	ArrayList<String> options;
	ArrayList<String> codes;
	
	boolean searchable;
	
	private static FormScreen form;
	
	private boolean selectedForTheFirstTime;
	
	public CodeField(Context context, NodeDefinition nodeDef, 
			ArrayList<String> codes, ArrayList<String> options, 
			String selectedItem) {
		super(context, nodeDef);
		this.searchable = true;

		CodeField.form = (FormScreen)context;
		
		this.selectedForTheFirstTime = true;
		
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(CodeField.this.getContext(), CodeField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		if (this.searchable){
			this.label.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
				}});
		}
		
		this.spinner = new Spinner(context);
		this.spinner.setPrompt(nodeDef.getName());
		
		this.codes = codes;
		this.options = options;

		this.aa = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, this.options);
		this.aa.setDropDownViewResource(R.layout.codelistitem);

		this.spinner.setAdapter(aa);
		this.spinner.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));
		this.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	ArrayList<String> valueToAdd = new ArrayList<String>();
		    	valueToAdd.add(CodeField.this.codes.get((CodeField.this.spinner.getSelectedItemPosition())));

		    	CodeField.this.setValue(0/*CodeField.form.currInstanceNo*/, CodeField.this.codes.get(CodeField.this.spinner.getSelectedItemPosition()),CodeField.form.getFormScreenId(),true);
		    	if (!CodeField.this.selectedForTheFirstTime){
					
		    	} else {
		    		CodeField.this.selectedForTheFirstTime = false;
		    	}
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    	
		    }

		});
		
		boolean isFound = false;
		int position = 0;
		if (selectedItem!=null){
			while (!isFound&&position<this.codes.size()){
				if (this.codes.get(position).equals(selectedItem)){
					isFound = true;
				}
				position++;
			}	
		}		
		if (isFound)
			this.spinner.setSelection(position-1);
		else
			this.spinner.setSelection(0);

		this.addView(this.spinner);
	}
	
	public void setValue(int position, String code, String path, boolean isSelectionChanged)
	{
		boolean isFound = false;
		int counter = 0;
		while (!isFound&&counter<this.codes.size()){
			if (this.codes.get(counter).equals(code)){
				isFound = true;
			}
			counter++;
		}
		if (isFound){
			if (!isSelectionChanged)
				this.spinner.setSelection(counter-1);
		}
		else{
			if (!isSelectionChanged)
				this.spinner.setSelection(0);
		}
		
		Entity parentEntity = this.findParentEntity(path);
		Node<? extends NodeDefinition> node = parentEntity.get(this.nodeDefinition.getName(), position);
		if (node!=null){
			CodeAttribute codeAtr = (CodeAttribute)node;
			codeAtr.setValue(new Code(code));
		} else {
			EntityBuilder.addValue(parentEntity, this.nodeDefinition.getName(), new Code(code), position);	
		}
	}
}
