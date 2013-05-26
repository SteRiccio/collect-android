package org.openforis.collect.android.fields;

import java.util.ArrayList;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.CodeListItem;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Code;
import org.openforis.idm.model.CodeAttribute;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.Node;

import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
//import android.util.Log;
//import android.util.Log;


public class CodeField extends InputField {
	
	private ArrayAdapter<String> aa;
	private Spinner spinner;
	
	
	/*private List<ArrayAdapter<String>> aaList;
	private List<Spinner> spinnerList;
	private Spinner currentSpinner;*/

	ArrayList<String> options;
	ArrayList<String> codes;
	//private ArrayList<String> currentCodes;
	
	private boolean searchable;
	private boolean hierarchical;
	/*private ArrayList<String> selectedCodesList;
	private int currentHierarchyLevel;*/
	
	private static FormScreen form;
	
//	private boolean selectedForTheFirstTime;
	
	private CodeAttributeDefinition codeAttrDef;
	
	private ArrayList<Integer> childrenIds;
	
	public CodeField(Context context, NodeDefinition nodeDef, 
			ArrayList<String> codes, ArrayList<String> options, 
			String selectedItem) {
		super(context, nodeDef);

		this.codeAttrDef = (CodeAttributeDefinition)this.nodeDefinition;
		
		this.childrenIds = new ArrayList<Integer>();
		
		this.searchable = true;
		this.hierarchical = (this.codeAttrDef.getList().getHierarchy().size()>0);
		/*this.aaList = new ArrayList<ArrayAdapter<String>>();
		this.spinnerList = new ArrayList<Spinner>();
		this.selectedCodesList = new ArrayList<String>();*/
		
		CodeField.form = (FormScreen)context;
		
		//this.selectedForTheFirstTime = true;
		
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
			//this.txtBox.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
			this.addView(this.txtBox);
		} else {
			if (!this.hierarchical){
				this.spinner = new Spinner(context);
				this.spinner.setPrompt(this.label.getText());
				
				this.codes = codes;
				this.options = options;

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
			} else {
				if (this.codeAttrDef.getParentCodeAttributeDefinition()!=null){
					this.spinner = new Spinner(context);
					this.spinner.setPrompt(this.label.getText());
					
					//this.codes = codes;
					//this.options = options;
					this.codes = new ArrayList<String>();
					this.codes.add("");
					this.options = new ArrayList<String>();
					this.options.add("");
					
					CodeField parentCodeField = (CodeField)ApplicationManager.getUIElement(this.codeAttrDef.getParentCodeAttributeDefinition().getId());
					if (parentCodeField!=null){
						int selectedPositionInParent = parentCodeField.spinner.getSelectedItemPosition();
						if (selectedPositionInParent>0){
							selectedPositionInParent--;
							for (int i=0;i<this.codeAttrDef.getList().getItems(this.codeAttrDef.getCodeListLevel()-1).get(selectedPositionInParent).getChildItems().size();i++){
								this.codes.add(this.codeAttrDef.getList().getItems(this.codeAttrDef.getCodeListLevel()-1).get(selectedPositionInParent).getChildItems().get(i).getCode().toString());
								this.options.add(this.codeAttrDef.getList().getItems(this.codeAttrDef.getCodeListLevel()-1).get(selectedPositionInParent).getChildItems().get(i).getLabels().get(0).getText());
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
					if (isFound){
						this.spinner.setSelection(position-1);
					}						
					else{
						this.spinner.setSelection(0);						
					}

					if (this.aa.getCount()==1){
						this.spinner.setEnabled(false);
					} else {
						this.spinner.setEnabled(true);
					}
					this.addView(this.spinner);
				}			
				else {
					this.spinner = new Spinner(context);
					this.spinner.setPrompt(this.label.getText());
					
					this.codes = codes;
					this.options = options;

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
					    			currentChild.codes.add("");
					    			currentChild.options = new ArrayList<String>();
					    			currentChild.options.add("");
					    			
									currentChild.aa.clear();
					    			currentChild.aa.add("");
					    			
					    			int selectedPositionInParent = spinner.getSelectedItemPosition();
									if (selectedPositionInParent>0){
										selectedPositionInParent--;

										for (int j=0;j<CodeField.this.codeAttrDef.getList().getItems(CodeField.this.codeAttrDef.getCodeListLevel()-1).get(selectedPositionInParent).getChildItems().size();j++){									
											currentChild.codes.add(CodeField.this.codeAttrDef.getList().getItems(CodeField.this.codeAttrDef.getCodeListLevel()-1).get(selectedPositionInParent).getChildItems().get(j).getCode().toString());
											currentChild.options.add(CodeField.this.codeAttrDef.getList().getItems(CodeField.this.codeAttrDef.getCodeListLevel()-1).get(selectedPositionInParent).getChildItems().get(j).getLabels().get(0).getText());
											currentChild.aa.add(CodeField.this.codeAttrDef.getList().getItems(CodeField.this.codeAttrDef.getCodeListLevel()-1).get(selectedPositionInParent).getChildItems().get(j).getLabels().get(0).getText());
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
					if (isFound){
						this.spinner.setSelection(position-1);
					}
					else{
						this.spinner.setSelection(0);
					}



					
					this.addView(this.spinner);
				}
				//int hierarchyLevelsNo = this.codeAttrDef.getList().getHierarchy().size();
				//Log.e("iloscPoziomow","=="+hierarchyLevelsNo);
				/*for (int hierarchyLevel=0;hierarchyLevel<hierarchyLevelsNo;hierarchyLevel++){
					Log.e("poziom"+hierarchyLevel,"=="+this.codeAttrDef.getList().getHierarchy().get(hierarchyLevel).getName());					
				}*/
				/*if (this.codeAttrDef.getParentCodeAttributeDefinition()!=null){
					Log.e("parentCODEnode","=="+this.codeAttrDef.getParentCodeAttributeDefinition().getName());
				}			
				else {
					Log.e("parentCODEnode","==ROOT");
				}
				Log.e("codeListLevel","=="+this.codeAttrDef.getCodeListLevel());
				Log.e("codeListLevelIndex","=="+this.codeAttrDef.getListLevelIndex());
				Log.e("codeListHierarchyName","=="+this.codeAttrDef.getList().getHierarchy().get(1).getName());
				for (int i=0;i<this.codeAttrDef.getList().getItems(this.codeAttrDef.getCodeListLevel()).size();i++){
					Log.e("element"+i,"=="+this.codeAttrDef.getList().getItems(this.codeAttrDef.getCodeListLevel()).get(i).getLabels().get(0).getText());					
				}
				if (this.codeAttrDef.getCodeListLevel()>0)
					for (int i=0;i<this.codeAttrDef.getList().getItems(this.codeAttrDef.getCodeListLevel()-1).get(0).getChildItems().size();i++){
						Log.e("element"+i,"=="+this.codeAttrDef.getList().getItems(this.codeAttrDef.getCodeListLevel()-1).get(0).getChildItems().get(i).getLabels().get(0).getText());					
					}*/
			}				
		}
		
		/*List<CodeListItem> codeListItemsList = codeAttrDef.getList().getItems();
		for (CodeListItem codeListItem : codeListItemsList){
			codes.add(codeListItem.getCode());

			options.add(getLabelForCodeListItem(codeListItem));			
		}*/
	}
	
	public void setValue(int position, String code, String path, boolean isSelectionChanged)
	{
		if (!this.codeAttrDef.isAllowUnlisted()){
			//if (!this.hierarchical){
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
			/*} else {//setting value of hierarchical list
				
			}*/
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
}
