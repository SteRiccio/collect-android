package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.CodeListItem;
import org.openforis.idm.metamodel.CodeListLevel;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Code;
import org.openforis.idm.model.CodeAttribute;
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
	
	
	private List<ArrayAdapter<String>> aaList;
	private List<Spinner> spinnerList;
	private Spinner currentSpinner;

	ArrayList<String> options;
	ArrayList<String> codes;
	private ArrayList<String> currentCodes;
	
	private boolean searchable;
	private boolean hierarchical;
	private ArrayList<String> selectedCodesList;
	private int currentHierarchyLevel;
	
	private static FormScreen form;
	
	private boolean selectedForTheFirstTime;
	
	private CodeAttributeDefinition codeAttrDef;
	
	public CodeField(Context context, NodeDefinition nodeDef, 
			ArrayList<String> codes, ArrayList<String> options, 
			String selectedItem) {
		super(context, nodeDef);

		this.codeAttrDef = (CodeAttributeDefinition)this.nodeDefinition;
		
		this.searchable = true;
		this.hierarchical = false;//(this.codeAttrDef.getList().getHierarchy().size()>0);
		this.aaList = new ArrayList<ArrayAdapter<String>>();
		this.spinnerList = new ArrayList<Spinner>();
		this.selectedCodesList = new ArrayList<String>();
		
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
		
		
		if (codeAttrDef.isAllowUnlisted()){
			this.txtBox = new EditText(context);
			this.txtBox.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));
			this.txtBox.addTextChangedListener(this);
			//this.txtBox.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
			this.addView(this.txtBox);
		} else {
			if (!this.hierarchical){
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
				    	//Log.e("onItemSelected",CodeField.form.getFormScreenId()+"=="+CodeField.this.currentInstanceNo+"("+CodeField.form.currInstanceNo+")");
				    	if (CodeField.this.nodeDefinition.isMultiple()){
				    		CodeField.this.setValue(CodeField.form.currInstanceNo, CodeField.this.codes.get(CodeField.this.spinner.getSelectedItemPosition()),CodeField.form.getFormScreenId(),true);	
				    	} else {
				    		CodeField.this.setValue(0, CodeField.this.codes.get(CodeField.this.spinner.getSelectedItemPosition()),CodeField.form.getFormScreenId(),true);
				    	}			    	
				    	
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
			} else {
				Log.e("isHierarchical","==");
				int hierarchyLevelsNo = this.codeAttrDef.getList().getHierarchy().size();
				Log.e("iloscPoziomow","=="+hierarchyLevelsNo);
				for (int hierarchyLevel=0;hierarchyLevel<hierarchyLevelsNo;hierarchyLevel++){
					currentHierarchyLevel = hierarchyLevel;
					this.selectedCodesList.add("");
					
					currentSpinner = new Spinner(context);
					currentSpinner.setPrompt(this.codeAttrDef.getList().getHierarchy().get(hierarchyLevel).getName());
					this.spinnerList.add(currentSpinner);
					
					ArrayList<String> currentOptions = new ArrayList<String>();
					currentCodes = new ArrayList<String>();
					currentOptions.add("");
					currentCodes.add("");
    				List<CodeListItem> codeListItemsList = codeAttrDef.getList().getItems(hierarchyLevel);
    				for (CodeListItem codeListItem : codeListItemsList){
    					currentCodes.add(codeListItem.getCode());
    					currentOptions.add(CodeField.getLabelForCodeListItem(codeListItem));
    				}
					ArrayAdapter<String> currentAA = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, currentOptions);
					currentAA.setDropDownViewResource(R.layout.codelistitem);
					this.aaList.add(currentAA);

					currentSpinner.setAdapter(currentAA);					
					currentSpinner.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));
					if (hierarchyLevel<hierarchyLevelsNo-1){
						currentSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
						    @Override
						    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
						    	if (CodeField.this.nodeDefinition.isMultiple()){
						    		//CodeField.this.setValue(CodeField.form.currInstanceNo, CodeField.this.currentCodes.get(CodeField.this.currentSpinner.getSelectedItemPosition()),CodeField.form.getFormScreenId(),true);	
						    	} else {
						    		//CodeField.this.setValue(0, CodeField.this.currentCodes.get(CodeField.this.currentSpinner.getSelectedItemPosition()),CodeField.form.getFormScreenId(),true);
						    		//CodeField.this.selectedCodesList.set(CodeField.this.currentHierarchyLevel, CodeField.this.currentCodes.get(position));
						    	}
						    	Log.e("setCode"+CodeField.this.currentHierarchyLevel,"=="+ CodeField.this.currentCodes.get(position));
						    	CodeField.this.selectedCodesList.set(CodeField.this.currentHierarchyLevel, CodeField.this.currentCodes.get(position));
						    	if (position!=0){
						    		Log.e("DOPUSZCZANIE",selectedItemView.getParent().getClass()+"KOLEJNEJ LISTY"+position);
						    		Spinner spinner = (Spinner)selectedItemView.getParent();
						    		for (int i=0;i<CodeField.this.spinnerList.size();i++){
						    			if (spinner.equals(CodeField.this.spinnerList.get(i))){
						    				spinner = CodeField.this.spinnerList.get(i+1);
						    				Log.e("spinnerNR","=="+(i+1));
						    				break;
						    			}
						    		}
						    		if (spinner!=null)
						    			spinner.setEnabled(true);
						    	}
						    }

						    @Override
						    public void onNothingSelected(AdapterView<?> parentView) {
						    	
						    }

						});
					} else {
						currentSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
						    @Override
						    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
						    	Log.e("setCodeLAST"+CodeField.this.currentHierarchyLevel,"=="+ CodeField.this.currentCodes.get(position));
						    	CodeField.this.selectedCodesList.set(CodeField.this.currentHierarchyLevel, CodeField.this.currentCodes.get(position));
						    	/*if (CodeField.this.nodeDefinition.isMultiple()){
						    		CodeField.this.setValue(CodeField.form.currInstanceNo, CodeField.this.currentCodes.get(CodeField.this.currentSpinner.getSelectedItemPosition()),CodeField.form.getFormScreenId(),true);	
						    	} else {
						    		CodeField.this.setValue(0, CodeField.this.currentCodes.get(CodeField.this.currentSpinner.getSelectedItemPosition()),CodeField.form.getFormScreenId(),true);
						    	}*/
						    }

						    @Override
						    public void onNothingSelected(AdapterView<?> parentView) {
						    	
						    }

						});
					}					
					
					boolean isFound = false;
					int position = 0;
					if (selectedItem!=null){
						while (!isFound&&position<currentCodes.size()){
							if (currentCodes.get(position).equals(selectedItem)){
								isFound = true;
							}
							position++;
						}	
					}
					if (isFound)
						currentSpinner.setSelection(position-1);
					else
						currentSpinner.setSelection(0);

					if (hierarchyLevel>0){
						//currentSpinner.setClickable(false);
						//currentSpinner.getSelectedView().setEnabled(false);
						currentSpinner.setEnabled(false);
					}
					this.addView(currentSpinner);				
				}
			}				
		}
		
		List<CodeListItem> codeListItemsList = codeAttrDef.getList().getItems();
		for (CodeListItem codeListItem : codeListItemsList){
			codes.add(codeListItem.getCode());

			options.add(getLabelForCodeListItem(codeListItem));
			/*if (codeListItem.getLabel(null)==null){
				options.add(codeListItem.getLabel("en"));
			} else {
				options.add(codeListItem.getLabel(null));	    						
			}*/
			
		}
	}
	
	public void setValue(int position, String code, String path, boolean isSelectionChanged)
	{
		if (!this.codeAttrDef.isAllowUnlisted()){
			if (!this.hierarchical){
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
			} else {//setting value of hierarchical list
				
			}
		} else {
			if (!isSelectionChanged)
				this.txtBox.setText(code);
		}

		Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
		if (node!=null){
			CodeAttribute codeAtr = (CodeAttribute)node;
			codeAtr.setValue(new Code(code));
		} else {
			EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new Code(code), position);	
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
}
