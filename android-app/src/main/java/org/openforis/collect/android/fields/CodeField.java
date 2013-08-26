package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.CodeListManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.misc.ItemsStorage;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.collect.android.service.ServiceFactory;
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
//import org.openforis.collect.manager.CodeListManager;


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
				
				ItemsStorage foundItemsStorage = ApplicationManager.getStoredItems(this.nodeDefinition.getId(),0);			
				List<CodeListItem> parentItems = null;
				if (foundItemsStorage!=null){
					parentItems = foundItemsStorage.items;
				}
				if (parentItems!=null){
					//parentItems = ApplicationManager.storedItemsList.getItems(currentChild.getId(),positionToLoadItemsFrom).items;							
				} else {
					CodeListManager codeListManager = ServiceFactory.getCodeListManager();
//					org.openforis.collect.android.database.CodeListItemDao codeListItemDao = new org.openforis.collect.android.database.CodeListItemDao();
//					org.openforis.collect.android.management.CodeListManager codeListManager = new org.openforis.collect.android.management.CodeListManager();
//					codeListManager.setCodeListItemDao(codeListItemDao);
					parentItems = codeListManager.loadValidItems(this.parentEntity, this.codeAttrDef);
					ItemsStorage storage = new ItemsStorage();
					storage.setDefinitionId(this.codeAttrDef.getId());
					storage.setItems(parentItems);
					storage.addSelectedPositionInParent(0);
					ApplicationManager.storedItemsList.add(storage);				
				}
				for (int j=0;j<parentItems.size();j++){
					CodeListItem item = parentItems.get(j);
					this.codes.add(item.getCode().toString());
					this.options.add(item.getLabel(ApplicationManager.selectedLanguage)/*item.getLabels().get(0).getText()*/);
					//currentChild.aa.add(item.getLabel(ApplicationManager.selectedLanguage)/*item.getLabels().get(0).getText()*/);
				}

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
					
					CodeField parentCodeField = (CodeField)ApplicationManager.getUIElement(this.codeAttrDef.getParentCodeAttributeDefinition().getId());
					if (parentCodeField!=null){
						int selectedPositionInParent = parentCodeField.spinner.getSelectedItemPosition();
						if (selectedPositionInParent>0){
							selectedPositionInParent--;
							ItemsStorage foundItemsStorage = ApplicationManager.getStoredItems(this.codeAttrDef.getId(),selectedPositionInParent);			
							List<CodeListItem> parentItems = null;
							if (foundItemsStorage!=null){
								parentItems = foundItemsStorage.items;
							}
							if (parentItems!=null){
								//parentItems = ApplicationManager.storedItemsList.getItems(currentChild.getId(),positionToLoadItemsFrom).items;							
							} else {
								CodeListManager codeListManager = ServiceFactory.getCodeListManager();
								parentItems = codeListManager.loadValidItems(this.parentEntity, this.codeAttrDef);
								ItemsStorage storage = new ItemsStorage();
								storage.setDefinitionId(this.codeAttrDef.getId());
								storage.setItems(parentItems);
								storage.addSelectedPositionInParent(selectedPositionInParent);
								ApplicationManager.storedItemsList.add(storage);
							}
							for (int j=0;j<parentItems.size();j++){
								CodeListItem item = parentItems.get(j);
								this.codes.add(item.getCode().toString());
								this.options.add(item.getLabel(ApplicationManager.selectedLanguage));
								//currentChild.aa.add(item.getLabel(ApplicationManager.selectedLanguage)/*item.getLabels().get(0).getText()*/);
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
					    	Log.e("CodeField2",CodeField.this.nodeDefinition.getName()+"=="+CodeField.this.codes.get(CodeField.this.spinner.getSelectedItemPosition()));
					    	Log.e("CodeField2","iloscDzieci"+CodeField.this.childrenIds.size());
					    	updateChildren(CodeField.this);
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
					
					ItemsStorage foundItemsStorage = ApplicationManager.getStoredItems(this.codeAttrDef.getId(),0);			
					List<CodeListItem> parentItems = null;
					if (foundItemsStorage!=null){
						parentItems = foundItemsStorage.items;
					}
					if (parentItems!=null){
						//parentItems = ApplicationManager.storedItemsList.getItems(currentChild.getId(),positionToLoadItemsFrom).items;							
					} else {
						CodeListManager codeListManager = ServiceFactory.getCodeListManager();
						Log.e("Test from CodeField", "CodeListManager is: " + codeListManager.toString());
						parentItems = codeListManager.loadValidItems(this.parentEntity, this.codeAttrDef);
						ItemsStorage storage = new ItemsStorage();
						storage.setDefinitionId(this.codeAttrDef.getId());
						storage.setItems(parentItems);
						storage.addSelectedPositionInParent(0);
						ApplicationManager.storedItemsList.add(storage);
					}
					for (int j=0;j<parentItems.size();j++){
						CodeListItem item = parentItems.get(j);
						this.codes.add(item.getCode().toString());
						this.options.add(item.getLabel(ApplicationManager.selectedLanguage)/*item.getLabels().get(0).getText()*/);
						//currentChild.aa.add(item.getLabel(ApplicationManager.selectedLanguage)/*item.getLabels().get(0).getText()*/);
					}
					
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
					    	Log.e("CodeField1",CodeField.this.nodeDefinition.getName()+"=="+CodeField.this.codes.get(CodeField.this.spinner.getSelectedItemPosition()));
					    	Log.e("CodeField1","iloscDzieci"+CodeField.this.childrenIds.size());
					    	updateChildren(CodeField.this);
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
		Log.e("setValueINSIDE",this.codeAttrDef.getName()+"=="+code+isSelectionChanged);
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

	private void updateChildren(CodeField codeField){
//		Log.e("childCodeField","=="+codeField.getLabelText());
		if (!codeField.childrenIds.isEmpty()){
    		for (int i=0;i<codeField.childrenIds.size();i++){
    			//Log.e("childID","=="+codeField.childrenIds.get(i));
    			CodeField currentChild = (CodeField)ApplicationManager.getUIElement(codeField.childrenIds.get(i));
    			currentChild.codes = new ArrayList<String>();
    			currentChild.codes.add("null");
    			currentChild.options = new ArrayList<String>();
    			currentChild.options.add("");
    			
				currentChild.aa.clear();
    			currentChild.aa.add("");
    			int selectedPositionInParent = spinner.getSelectedItemPosition();
				if (selectedPositionInParent>0){
					selectedPositionInParent--;
					ItemsStorage foundItemsStorage = ApplicationManager.getStoredItems(currentChild.nodeDefinition.getId(),selectedPositionInParent);			
					List<CodeListItem> parentItems = null;
					if (foundItemsStorage!=null){
						parentItems = foundItemsStorage.items;
					}
					if (parentItems!=null){
						//parentItems = ApplicationManager.storedItemsList.getItems(currentChild.getId(),positionToLoadItemsFrom).items;							
					} else {
						CodeListManager codeListManager = ServiceFactory.getCodeListManager();
						parentItems = codeListManager.loadValidItems(currentChild.parentEntity, currentChild.codeAttrDef);
						ItemsStorage storage = new ItemsStorage();
						storage.setDefinitionId(currentChild.codeAttrDef.getId());
						storage.setItems(parentItems);
						storage.addSelectedPositionInParent(selectedPositionInParent);
						ApplicationManager.storedItemsList.add(storage);
					}
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
				//Log.e("currentChild.childrenSize",currentChild.getLabelText()+"=="+currentChild.childrenIds.size());
				updateChildren(currentChild);
    		}					    		
    	}
	}

}

