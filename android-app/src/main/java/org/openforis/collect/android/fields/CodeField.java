package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.collect.manager.CodeListManager;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.CodeListItem;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Code;
import org.openforis.idm.model.CodeAttribute;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.Node;

import android.content.Context;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class CodeField extends InputField {
	
	private ArrayAdapter<String> aa;
	private Spinner spinner;

	ArrayList<String> options;
	ArrayList<String> codes;
	
	private boolean searchable;
	private boolean hierarchical;
	
	private static FormScreen form;
	
	private CodeAttributeDefinition codeAttrDef;
	
	private ArrayList<Integer> childrenIds;
	
	private Entity parentEntity;
	
	public CodeField(Context context, NodeDefinition nodeDef, 
			ArrayList<String> codes, ArrayList<String> options, 
			String selectedItem, String path) {
		super(context, nodeDef);

		this.parentEntity = this.findParentEntity(path);
		
		this.codeAttrDef = (CodeAttributeDefinition)this.nodeDefinition;
		
		this.childrenIds = new ArrayList<Integer>();
		
		this.searchable = true;
		this.hierarchical = (this.codeAttrDef.getList().getHierarchy().size()>0);
		
		CodeField.form = (FormScreen)context;
		
		this.codes = new ArrayList<String>();
		this.codes.add("null");
		this.options = new ArrayList<String>();
		this.options.add("");
		CodeListManager codeListManager = ServiceFactory.getCodeListManager();
		List<CodeListItem> parentItems = codeListManager.loadValidItems(this.parentEntity, this.codeAttrDef);
		Log.e("parentItems.size1",this.codeAttrDef.getName()+"=="+parentItems.size());
		for (int i=0;i<parentItems.size();i++){
			CodeListItem item = parentItems.get(i);
			this.codes.add(item.getCode());
			this.options.add(item.getLabel(ApplicationManager.selectedLanguage)/*item.getLabels().get(0).getText()*/);
		}
		
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
		
		if (codeAttrDef.isAllowUnlisted()){
			this.txtBox = new EditText(context);
			this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));
			this.txtBox.addTextChangedListener(this);
			this.addView(this.txtBox);
		} else {//dropdown menu
			if (!this.hierarchical){
				this.spinner = new Spinner(context);
				this.spinner.setPrompt(this.label.getText());
				
				/*this.codes = codes;
				this.options = options;*/

				/*CodeListManager codeListManager = ServiceFactory.getCodeListManager();
				List<CodeListItem> parentItems = codeListManager.loadValidItems(this.parentEntity, this.codeAttrDef);
				for (int i=0;i<parentItems.size();i++){
					CodeListItem item = parentItems.get(i);
					this.codes.add(item.getCode());
					this.options.add(item.getLabel(ApplicationManager.selectedLanguage));
				}*/

				this.aa = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, this.options);
				this.aa.setDropDownViewResource(R.layout.codelistitem);

				this.spinner.setAdapter(aa);
				this.spinner.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));
				this.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				    @Override
				    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				    	
				    	if (CodeField.this.nodeDefinition.isMultiple()){
				    		CodeField.this.setValue(CodeField.form.currInstanceNo, CodeField.this.codes.get(CodeField.this.spinner.getSelectedItemPosition()),CodeField.form.getFormScreenId(),true);	
				    	} else {
				    		CodeField.this.setValue(0, CodeField.this.codes.get(CodeField.this.spinner.getSelectedItemPosition()),CodeField.form.getFormScreenId(),true);
				    	}
				    }

				    @Override
				    public void onNothingSelected(AdapterView<?> parentView) {
				    	
				    }

				});
			} else {//hierarchical list
				if (this.codeAttrDef.getParentCodeAttributeDefinition()!=null){
					this.spinner = new Spinner(context);
					this.spinner.setPrompt(this.label.getText());
					
					/*this.codes = new ArrayList<String>();
					this.codes.add("null");
					this.options = new ArrayList<String>();
					this.options.add("");*/
					
					CodeField parentCodeField = (CodeField)ApplicationManager.getUIElement(this.codeAttrDef.getParentCodeAttributeDefinition().getId());
					if (parentCodeField!=null){
						int selectedPositionInParent = parentCodeField.spinner.getSelectedItemPosition();
						if (selectedPositionInParent>0){
							selectedPositionInParent--;
							/*CodeListManager */codeListManager = ServiceFactory.getCodeListManager();
							/*List<CodeListItem> */parentItems = codeListManager.loadValidItems(this.parentEntity, this.codeAttrDef);
							for (int i=0;i<parentItems.size();i++){
								CodeListItem item = parentItems.get(i);
								this.codes.add(item.getCode());
								this.options.add(item.getLabel(ApplicationManager.selectedLanguage)/*item.getLabels().get(0).getText()*/);
							}
						}							
					}
					
					parentCodeField.addChildId(this.codeAttrDef.getId());
					
					this.aa = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, this.options);
					this.aa.setDropDownViewResource(R.layout.codelistitem);

					this.spinner.setAdapter(aa);
					this.spinner.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));
					this.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
					    @Override
					    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					    	if (CodeField.this.nodeDefinition.isMultiple()){
					    		CodeField.this.setValue(CodeField.form.currInstanceNo, CodeField.this.codes.get(CodeField.this.spinner.getSelectedItemPosition()),CodeField.form.getFormScreenId(),true);	
					    	} else {
					    		CodeField.this.setValue(0, CodeField.this.codes.get(CodeField.this.spinner.getSelectedItemPosition()),CodeField.form.getFormScreenId(),true);
					    	}
					    }

					    @Override
					    public void onNothingSelected(AdapterView<?> parentView) {
					    	
					    }

					});

					if (this.aa.getCount()==1){
						this.spinner.setEnabled(false);
					} else {
						this.spinner.setEnabled(true);
					}
				}			
				else {
					this.spinner = new Spinner(context);
					this.spinner.setPrompt(this.label.getText());
					
					/*this.codes = codes;
					this.options = options;*/

					this.aa = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, this.options);
					this.aa.setDropDownViewResource(R.layout.codelistitem);

					this.spinner.setAdapter(aa);
					this.spinner.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));
					this.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
					    @Override
					    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					    	if (CodeField.this.nodeDefinition.isMultiple()){
					    		CodeField.this.setValue(CodeField.form.currInstanceNo, CodeField.this.codes.get(CodeField.this.spinner.getSelectedItemPosition()),CodeField.form.getFormScreenId(),true);	
					    	} else {
					    		CodeField.this.setValue(0, CodeField.this.codes.get(CodeField.this.spinner.getSelectedItemPosition()),CodeField.form.getFormScreenId(),true);
					    	}
					    	if (!CodeField.this.childrenIds.isEmpty()){
					    		for (int i=0;i<CodeField.this.childrenIds.size();i++){
					    			CodeField currentChild = (CodeField)ApplicationManager.getUIElement(CodeField.this.childrenIds.get(i));
					    			currentChild.codes = new ArrayList<String>();
					    			currentChild.codes.add("null");
					    			currentChild.options = new ArrayList<String>();
					    			currentChild.options.add("");
					    			
									currentChild.aa.clear();
					    			currentChild.aa.add("");
					    			
					    			int selectedPositionInParent = spinner.getSelectedItemPosition();
									if (selectedPositionInParent>0){
										selectedPositionInParent--;
										CodeListManager codeListManager = ServiceFactory.getCodeListManager();
										List<CodeListItem> parentItems = codeListManager.loadValidItems(currentChild.parentEntity, currentChild.codeAttrDef);
										for (int j=0;j<parentItems.size();j++){
											CodeListItem item = parentItems.get(j);
											currentChild.codes.add(item.getCode().toString());
											currentChild.options.add(item.getLabel(ApplicationManager.selectedLanguage)/*item.getLabels().get(0).getText()*/);
											currentChild.aa.add(item.getLabel(ApplicationManager.selectedLanguage)/*item.getLabels().get(0).getText()*/);
										}
									}
									if (currentChild.aa.getCount()==1){
						    			currentChild.spinner.setEnabled(false);
									} else {
										currentChild.spinner.setEnabled(true);
									}									
					    		}					    		
					    	}
					    }

					    @Override
					    public void onNothingSelected(AdapterView<?> parentView) {
					    	
					    }

					});					
				}
			}//end of hierarchical list
			setSpinnerSelection(selectedItem);
			this.addView(this.spinner);
		}//end of dropdown menu
	}
	
	public void setValue(int position, String code, String path, boolean isSelectionChanged)
	{
		if (!this.codeAttrDef.isAllowUnlisted()){
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
		} else {
			if (!isSelectionChanged)
				this.txtBox.setText(code);
		}
		
		if (code!=null && code!="null"){
			Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
			if (node!=null){
				CodeAttribute codeAtr = (CodeAttribute)node;
				codeAtr.setValue(new Code(code));
			} else {
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Code(code), position);	
			}	
		}		
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		this.setValue(0, s.toString(), CodeField.form.getFormScreenId(),true);
	}
	
	public static String getLabelForCodeListItem(CodeListItem codeListItem){
		String label = codeListItem.getLabel(ApplicationManager.selectedLanguage);
		if (label==null){
			if (codeListItem.getLabels().size()>0){
				label = codeListItem.getLabels().get(0).getText();	
			} else {
				label = "";
			}			
		}
		return label;
	}
	
	private void addChildId(int childCodeListId){
		this.childrenIds.add(childCodeListId);
	}
	
	private void setSpinnerSelection(String selectedItem){
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
	}
}
