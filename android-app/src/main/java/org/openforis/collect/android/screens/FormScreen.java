package org.openforis.collect.android.screens;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.data.DataTreeNode;
import org.openforis.collect.android.data.FieldValue;
import org.openforis.collect.android.fields.BooleanField;
import org.openforis.collect.android.fields.CodeField;
import org.openforis.collect.android.fields.CoordinateField;
import org.openforis.collect.android.fields.DateField;
import org.openforis.collect.android.fields.Field;
import org.openforis.collect.android.fields.InputField;
import org.openforis.collect.android.fields.MemoField;
import org.openforis.collect.android.fields.NumberField;
import org.openforis.collect.android.fields.RangeField;
import org.openforis.collect.android.fields.SummaryList;
import org.openforis.collect.android.fields.SummaryTable;
import org.openforis.collect.android.fields.TaxonField;
import org.openforis.collect.android.fields.TextField;
import org.openforis.collect.android.fields.TimeField;
import org.openforis.collect.android.fields.UIElement;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.BaseActivity;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.idm.metamodel.AttributeDefinition;
import org.openforis.idm.metamodel.BooleanAttributeDefinition;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.CodeListItem;
import org.openforis.idm.metamodel.CoordinateAttributeDefinition;
import org.openforis.idm.metamodel.DateAttributeDefinition;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NodeLabel.Type;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
import org.openforis.idm.metamodel.RangeAttributeDefinition;
import org.openforis.idm.metamodel.TaxonAttributeDefinition;
import org.openforis.idm.metamodel.TextAttributeDefinition;
import org.openforis.idm.metamodel.TimeAttributeDefinition;
import org.openforis.idm.model.BooleanValue;
import org.openforis.idm.model.Coordinate;
import org.openforis.idm.model.Date;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.NumberValue;
import org.openforis.idm.model.TextValue;
import org.openforis.idm.model.Time;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class FormScreen extends BaseActivity implements OnClickListener, TextWatcher{
	
	private static final String TAG = "FormScreen";

	private ScrollView sv;			
    private LinearLayout ll;

	private ArrayList<String> detailsLists;
	
	private int intentType;
	
	private int idmlId;
	public int currInstanceNo;
	private int numberOfInstances;
	private String parentFormScreenId;
	
	private Intent startingIntent;

	private String breadcrumb;

	public DataTreeNode currentNode;
	public static FieldValue currentFieldValue;
	private FieldValue currentMultipleFieldValue;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        	Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    		
            //breadcrumb of the screen
    		startingIntent = getIntent();
    		this.breadcrumb = startingIntent.getStringExtra(getResources().getString(R.string.breadcrumb));
    		this.intentType = startingIntent.getIntExtra(getResources().getString(R.string.intentType),-1);
    		this.idmlId = startingIntent.getIntExtra(getResources().getString(R.string.idmlId),-1);
    		this.currInstanceNo = startingIntent.getIntExtra(getResources().getString(R.string.instanceNo),-1);
    		this.numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances),-1);
    		this.parentFormScreenId = startingIntent.getStringExtra(getResources().getString(R.string.parentFormScreenId));;
    		
    		//ApplicationManager.valuesTree.printTree(ApplicationManager.valuesTree.getChild("0,0"));
    		if ((this.intentType==getResources().getInteger(R.integer.multipleEntityIntent))&&(!this.parentFormScreenId.equals(""))){
    			this.currentNode = ApplicationManager.valuesTree.getChild(getFormScreenId());
    			if (this.currentNode==null){
    				this.currentNode = new DataTreeNode(this.idmlId, this.currInstanceNo, this.parentFormScreenId, ApplicationManager.valuesTree.getChild(this.parentFormScreenId) , new ArrayList<FieldValue>());
            		ApplicationManager.valuesTree.addChild(getFormScreenId(), this.currentNode);
    			}
        		this.currentMultipleFieldValue = null;
    		} else if ((this.intentType==getResources().getInteger(R.integer.singleEntityIntent))&&(!this.parentFormScreenId.equals(""))){
    			this.currentNode = ApplicationManager.valuesTree.getChild(getFormScreenId());
    			if (this.currentNode==null){
    				this.currentNode = new DataTreeNode(this.idmlId, this.currInstanceNo, this.parentFormScreenId, ApplicationManager.valuesTree.getChild(this.parentFormScreenId) , new ArrayList<FieldValue>());
            		ApplicationManager.valuesTree.addChild(getFormScreenId(), this.currentNode);
    			}
        		this.currentMultipleFieldValue = null;
    		} else if (!this.parentFormScreenId.equals("")){//current screen isn't entity
    			this.currentNode=null;
    			ArrayList<String> tableColHeaders = new ArrayList<String>();
        		tableColHeaders.add("Value");
        		ArrayList<List<String>> tableRowLists = new ArrayList<List<String>>();
    			this.currentMultipleFieldValue = new FieldValue(this.idmlId, getFormScreenId(), tableRowLists);    			
    		}    		
    		ApplicationManager.formScreensMap.put(getFormScreenId(), this);
        } catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onCreate",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
		}
	}
	
    @Override
	public void onResume()
	{
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
		try{
			String loadedValue = "";
			if (ApplicationManager.fieldValueToPass!=null){
				for (int i=0;i<ApplicationManager.fieldValueToPass.size();i++){
					Log.e("savedFieldVALUE"+i,"=="+ApplicationManager.fieldValueToPass.getValue(i));					
				}
				this.currentNode.addFieldValue(ApplicationManager.fieldValueToPass);
				Log.i("FormScreen onResume","fieldValueToPass is: " + ApplicationManager.fieldValueToPass);
				ApplicationManager.fieldValueToPass = null;	
			}
			else{
				Log.i("FormScreen onResume","fieldValueToPass is NULL");
			}

    		ArrayList<String> tableColHeaders = new ArrayList<String>();
    		tableColHeaders.add("Value");
    		
    		this.sv = new ScrollView(this);
    		this.ll = new LinearLayout(this);
    		this.ll.setOrientation(android.widget.LinearLayout.VERTICAL);
    		this.sv.addView(ll);

    		TextView breadcrumb = new TextView(this);
    		breadcrumb.setText(this.breadcrumb);
    		breadcrumb.setTextSize(getResources().getInteger(R.integer.breadcrumbFontSize));
    		this.ll.addView(breadcrumb);
    		
    		int fieldsNo = startingIntent.getExtras().size()-1;
    		for (int i=0;i<fieldsNo;i++){
    			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+i, -1));
    			if ((nodeDef instanceof EntityDefinition)/*&&(nodeDef.isMultiple())*/){    				
    				if (nodeDef.isMultiple()){
    					
    				} else {
    					
    				}
    				EntityDefinition entityDef = (EntityDefinition)nodeDef;    				
    				int entityCardinality = 1;
					ArrayList<List<String>> keysLists = new ArrayList<List<String>>();
    				ArrayList<List<String>> detailsLists = new ArrayList<List<String>>();
    				for (int j=0;j<entityCardinality;j++){
        				ArrayList<String> keysList = new ArrayList<String>();
        				List<NodeDefinition> fieldsList = entityDef.getChildDefinitions();  
        				List<AttributeDefinition> attrDefsList = entityDef.getKeyAttributeDefinitions();
        				//Log.e("#entityKeysNo","=="+attrDefsList.size());
        				if (getFormScreenId()!=null){
        					//Log.e("getFormScreenId()","=="+getFormScreenId());
        					for (AttributeDefinition attrDef : attrDefsList){
            					//Log.e("keyAttr"+attrDef.getName(),attrDef.getId()+"=="+attrDef.getName());
            					DataTreeNode currTreeNode = ApplicationManager.valuesTree.getChild(getFormScreenId()+getResources().getString(R.string.valuesSeparator2)+entityDef.getId()+getResources().getString(R.string.valuesSeparator1)+j);
                				//Log.e("currFormScreenId","=="+getFormScreenId()+getResources().getString(R.string.valuesSeparator2)+entityDef.getId()+getResources().getString(R.string.valuesSeparator1)+j);
                				/*if (currTreeNode!=null)
                					Log.e("currTreeeNode","=="+currTreeNode.getFieldsNo());*/
            					if (currTreeNode!=null)
            						keysList.add(attrDef.getName()+getResources().getString(R.string.valuesEqualTo)+currTreeNode.getFieldValue(attrDef.getId()).getValue(0).get(0));
            					else 
            						keysList.add(attrDef.getName()+"");
            				}
            				keysLists.add(keysList);
            				
            				ArrayList<String> detailsList = new ArrayList<String>();
            				  				
            				for (NodeDefinition childDef : fieldsList){
            					if (childDef instanceof EntityDefinition){
            						detailsList.add(childDef.getName()+getResources().getString(R.string.entityMarker));
            					} else {
            						DataTreeNode currTreeNode = ApplicationManager.valuesTree.getChild(getFormScreenId()+getResources().getString(R.string.valuesSeparator2)+entityDef.getId()+getResources().getString(R.string.valuesSeparator1)+j);
            						//Log.e("child",childDef.getId()+"=="+childDef.getName());
                					if (currTreeNode!=null){
                						FieldValue tempFieldValue = currTreeNode.getFieldValue(childDef.getId());
                						if (tempFieldValue!=null){
                							detailsList.add(/*childDef.getName()+getResources().getString(R.string.valuesEqualTo)+*/tempFieldValue.getValue(0).get(0));
                						} else {
                							detailsList.add(""/*childDef.getName()+"EMPTY"*/);
                						}
                					}
                					else 
                						detailsList.add(""/*childDef.getName()+"EMPTY"*/);
            						
            					}
            					
            				}
            				detailsLists.add(detailsList);
        				}  				
    				}
    				String label = entityDef.getLabel(Type.INSTANCE, null);
    				if (label==null){
    					if (entityDef.getLabels().size()>0)
    						label = entityDef.getLabels().get(0).getText();
    				}
    				SummaryList summaryListView = new SummaryList(this, entityDef, calcNoOfCharsFitInOneLine(),
    						label,keysLists,detailsLists,
    						this);
    				summaryListView.setOnClickListener(this);
    				summaryListView.setId(nodeDef.getId());
    				this.ll.addView(summaryListView);
    			} else if (nodeDef instanceof TextAttributeDefinition){
    				loadedValue = "";
    				if (ApplicationManager.currentRecord!=null){
    					//Log.e("breadcrumb","=="+this.getFormScreenId());
    					Entity rootEntity = ApplicationManager.currentRecord.getRootEntity();
    					String[] path = this.getFormScreenId().split(";");
    					Entity currentEntity = rootEntity;
    					for (int p=2;p<path.length;p++){
    						String[] elementId = path[p].split(",");
    						int elementIdmlId = Integer.valueOf(elementId[0]);
    						int elementInstanceNo = Integer.valueOf(elementId[1]);
    						//Log.e("getEntityparams",ApplicationManager.getSurvey().getSchema().getDefinitionById(elementIdmlId).getName()+"=="+elementInstanceNo);
    						if (currentEntity!=null)
    							currentEntity = (Entity) currentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(elementIdmlId).getName(), elementInstanceNo);
    						//Log.e("p=="+p,"currentEntity==Null"+(currentEntity==null));
    						/*if (currentEntity!=null)
    							Log.e("currentEntity"+path[p],currentEntity.getName()+"=="+currentEntity.getValue(nodeDef.getName(), this.currInstanceNo));*/
    					}
    					if (currentEntity!=null){
    						TextValue textValue = (TextValue)currentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
        					if (textValue!=null){
        						loadedValue = textValue.getValue();
        						//Log.e(nodeDef.getName()+"value",this.currInstanceNo+"=="+textValue.getValue());
        					}   	
    					}    					 						
    				}
    				if (((TextAttributeDefinition) nodeDef).getType().toString().toLowerCase().equals("short")){
    					if (!nodeDef.isMultiple()){
            				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
//            				Log.e("ILOSC INSTANCJI","=="+numberOfInstances);
            				FieldValue tempFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
            				if (numberOfInstances!=-1){
            					ArrayList<String> instanceValues = new ArrayList<String>();
                				//Log.e("numberOFinstances","=="+numberOfInstances);
                				//for (int k=0;k<numberOfInstances;k++){
            					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+0);
            					tempFieldValue.addValue(instanceValues);
                				//Log.e("dodanowartosc","=="+instanceValues.get(1));
                				//}	
            				}
            				else {
            					ArrayList<String> instanceValues = new ArrayList<String>();
            					instanceValues.add(loadedValue);
            					tempFieldValue.addValue(instanceValues);
            				}
            				
            				TextField textField= new TextField(this, nodeDef, tempFieldValue);
            				textField.setOnClickListener(this);
            				textField.setId(nodeDef.getId());
            				//textField.txtBox.addTextChangedListener(this);
            				textField.setValue(0, tempFieldValue.getValue(0).get(0));
            				FormScreen.currentFieldValue = this.currentNode.getFieldValue(nodeDef.getId());
            				//Log.e("FormScreen.currentFieldValue==null",nodeDef.getId()+"=="+(FormScreen.currentFieldValue==null));
            				//ApplicationManager.valuesTree.printTree();
            				if (FormScreen.currentFieldValue==null){
            					ArrayList<String> initialValue = new ArrayList<String>();
            					initialValue.add(textField.getValue(0));
            					FormScreen.currentFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
            					FormScreen.currentFieldValue.addValue(initialValue);
            					this.currentNode.addFieldValue(FormScreen.currentFieldValue);        		
            				} else {
            					//Log.e("wczytanaWARTOSC","=="+FormScreen.currentFieldValue.getValue(0).get(0));
            					textField.setValue(0, FormScreen.currentFieldValue.getValue(0).get(0));       
            				}
            				textField.addTextChangedListener(new TextWatcher(){
            			        public void afterTextChanged(Editable s) {
            			            //Log.e("s","=="+s.toString());
            			            ArrayList<String> value = new ArrayList<String>();
            			            value.add(s.toString());
            			            FormScreen.currentFieldValue.setValue(0, value);
            			            FormScreen.this.currentNode.addFieldValue(FormScreen.currentFieldValue);  
            			            //Log.e("changed",FormScreen.currentFieldValue.getId()+"ValueOFsingleField=="+FormScreen.currentFieldValue.getValue(0).get(0));
            			        }
            			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            			        public void onTextChanged(CharSequence s, int start, int before, int count){}
            			    });
            				this.ll.addView(textField);
            				//this.currentNode.addFieldValue(tempFieldValue);
            	    		//Log.e("iloscPOLzWARTOSCIA","=="+this.currentNode.getFieldsNo());       				
        				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){    					
            				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
            				this.currentMultipleFieldValue = new FieldValue(nodeDef.getId(), getFormScreenId(),null);
            				ArrayList<String> instanceValues = new ArrayList<String>();
            				//Log.e("numberOFinstances","=="+numberOfInstances);
            				for (int k=0;k<numberOfInstances;k++){
            					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+k);
            					this.currentMultipleFieldValue.addValue(instanceValues);
            					//Log.e("dodanowartosc","=="+instanceValues.get(1));
            				}

            				TextField textField= new TextField(this, nodeDef, this.currentMultipleFieldValue);
            				textField.setOnClickListener(this);
            				textField.setId(nodeDef.getId());
            				textField.txtBox.addTextChangedListener(this);
            				//Log.e("TEXTvalues.size",this.currInstanceNo+"=="+this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
            				if (this.currentMultipleFieldValue.size()>this.currInstanceNo){
            					textField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));	
            				}			
            				this.ll.addView(textField);
        				} else {
        					FieldValue fieldValueToBeRestored = this.currentNode.getFieldValue(nodeDef.getId());
        					//Log.e("fieldValueToBeRestored!=null",this.currentNode.getNodeValues().size()+"=="+(fieldValueToBeRestored!=null));
        					if (fieldValueToBeRestored!=null){
        						//Log.e("POWROT","z MULTIPLE FIELD");
        						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, fieldValueToBeRestored.getValues(), this);
            					summaryTableView.setOnClickListener(this);
                				summaryTableView.setId(nodeDef.getId());
                				this.ll.addView(summaryTableView);
        					} else{
        						//Log.e("PIERWSZE","OTWARCIE");
        			    		ArrayList<List<String>> tableValuesLists = new ArrayList<List<String>>();
        			    		ArrayList<String> valueRow1 = new ArrayList<String>();
        			    		//row1.add("1");
        			    		valueRow1.add("");
        			    		tableValuesLists.add(valueRow1);
        						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, tableValuesLists, this);
            					summaryTableView.setOnClickListener(this);
                				summaryTableView.setId(nodeDef.getId());
                				this.ll.addView(summaryTableView);
        					}			
        				}
    				} else {//memo field
    					loadedValue = "";
        				if (ApplicationManager.currentRecord!=null){
        					//Log.e("breadcrumb","=="+this.getFormScreenId());
        					Entity rootEntity = ApplicationManager.currentRecord.getRootEntity();
        					String[] path = this.getFormScreenId().split(";");
        					Entity currentEntity = rootEntity;
        					for (int p=2;p<path.length;p++){
        						String[] elementId = path[p].split(",");
        						int elementIdmlId = Integer.valueOf(elementId[0]);
        						int elementInstanceNo = Integer.valueOf(elementId[1]);
        						//Log.e("getEntityparams",ApplicationManager.getSurvey().getSchema().getDefinitionById(elementIdmlId).getName()+"=="+elementInstanceNo);
        						if (currentEntity!=null)
        							currentEntity = (Entity) currentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(elementIdmlId).getName(), elementInstanceNo);
        						//Log.e("p=="+p,"currentEntity==Null"+(currentEntity==null));
        						/*if (currentEntity!=null)
        							Log.e("currentEntity"+path[p],currentEntity.getName()+"=="+currentEntity.getValue(nodeDef.getName(), this.currInstanceNo));*/
        					}
        					if (currentEntity!=null){
        						TextValue textValue = (TextValue)currentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
            					if (textValue!=null){
            						loadedValue = textValue.getValue();
            						//Log.e(nodeDef.getName()+"value",this.currInstanceNo+"=="+textValue.getValue());
            					}   	
        					}    					 						
        				}
    					if (!nodeDef.isMultiple()){
            				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
//            				Log.e("ILOSC INSTANCJI","=="+numberOfInstances);
            				FieldValue tempFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
            				if (numberOfInstances!=-1){
            					ArrayList<String> instanceValues = new ArrayList<String>();
                				//Log.e("numberOFinstances","=="+numberOfInstances);
                				//for (int k=0;k<numberOfInstances;k++){
            					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+0);
            					tempFieldValue.addValue(instanceValues);
                				//Log.e("dodanowartosc","=="+instanceValues.get(1));
                				//}	
            				}
            				else {
            					ArrayList<String> instanceValues = new ArrayList<String>();
            					instanceValues.add(loadedValue);
            					tempFieldValue.addValue(instanceValues);
            				}
            				
            				MemoField memoField= new MemoField(this, nodeDef, tempFieldValue);
            				memoField.setOnClickListener(this);
            				memoField.setId(nodeDef.getId());
            				//textField.txtBox.addTextChangedListener(this);
            				memoField.setValue(0, tempFieldValue.getValue(0).get(0));
            				FormScreen.currentFieldValue = this.currentNode.getFieldValue(nodeDef.getId());
            				//Log.e("FormScreen.currentFieldValue==null",nodeDef.getId()+"=="+(FormScreen.currentFieldValue==null));
            				if (FormScreen.currentFieldValue==null){
            					ArrayList<String> initialValue = new ArrayList<String>();
            					initialValue.add(memoField.getValue(0));
            					FormScreen.currentFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
            					FormScreen.currentFieldValue.addValue(initialValue);
            					this.currentNode.addFieldValue(FormScreen.currentFieldValue);        		
            				} else {
            					//Log.e("wczytanaWARTOSC","=="+FormScreen.currentFieldValue.getValue(0).get(0));
            					memoField.setValue(0, FormScreen.currentFieldValue.getValue(0).get(0));       
            				}
            				memoField.addTextChangedListener(new TextWatcher(){
            			        public void afterTextChanged(Editable s) {
            			            //Log.e("s","=="+s.toString());
            			            ArrayList<String> value = new ArrayList<String>();
            			            value.add(s.toString());
            			            FormScreen.currentFieldValue.setValue(0, value);
            			            FormScreen.this.currentNode.addFieldValue(FormScreen.currentFieldValue);  
            			            //Log.e("changed",FormScreen.currentFieldValue.getId()+"ValueOFsingleField=="+FormScreen.currentFieldValue.getValue(0).get(0));
            			        }
            			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            			        public void onTextChanged(CharSequence s, int start, int before, int count){}
            			    });
            				this.ll.addView(memoField);
            				//this.currentNode.addFieldValue(tempFieldValue);
            	    		//Log.e("iloscPOLzWARTOSCIA","=="+this.currentNode.getFieldsNo());       				
        				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){    					
            				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
            				this.currentMultipleFieldValue = new FieldValue(nodeDef.getId(), getFormScreenId(),null);
            				ArrayList<String> instanceValues = new ArrayList<String>();
            				//Log.e("numberOFinstances","=="+numberOfInstances);
            				for (int k=0;k<numberOfInstances;k++){
            					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+k);
            					this.currentMultipleFieldValue.addValue(instanceValues);
            					//Log.e("dodanowartosc","=="+instanceValues.get(1));
            				}

            				MemoField memoField= new MemoField(this, nodeDef, this.currentMultipleFieldValue);
            				memoField.setOnClickListener(this);
            				memoField.setId(nodeDef.getId());
            				memoField.txtBox.addTextChangedListener(this);
            				//Log.e("TEXTvalues.size",this.currInstanceNo+"=="+this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
            				if (this.currentMultipleFieldValue.size()>this.currInstanceNo){
            					memoField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));	
            				}			
            				this.ll.addView(memoField);
        				} else {
        					FieldValue fieldValueToBeRestored = this.currentNode.getFieldValue(nodeDef.getId());
        					//Log.e("fieldValueToBeRestored!=null",this.currentNode.getNodeValues().size()+"=="+(fieldValueToBeRestored!=null));
        					if (fieldValueToBeRestored!=null){
        						//Log.e("POWROT","z MULTIPLE FIELD");
        						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, fieldValueToBeRestored.getValues(), this);
            					summaryTableView.setOnClickListener(this);
                				summaryTableView.setId(nodeDef.getId());
                				this.ll.addView(summaryTableView);
        					} else{
        						//Log.e("PIERWSZE","OTWARCIE");
        			    		ArrayList<List<String>> tableValuesLists = new ArrayList<List<String>>();
        			    		ArrayList<String> valueRow1 = new ArrayList<String>();
        			    		//row1.add("1");
        			    		valueRow1.add("");
        			    		tableValuesLists.add(valueRow1);
        						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, tableValuesLists, this);
            					summaryTableView.setOnClickListener(this);
                				summaryTableView.setId(nodeDef.getId());
                				this.ll.addView(summaryTableView);
        					}			
        				}
    				}    				
    			} else if (nodeDef instanceof NumberAttributeDefinition){
    				loadedValue = "";
    				if (ApplicationManager.currentRecord!=null){
    					//Log.e("breadcrumb","=="+this.getFormScreenId());
    					Entity rootEntity = ApplicationManager.currentRecord.getRootEntity();
    					String[] path = this.getFormScreenId().split(";");
    					Entity currentEntity = rootEntity;
    					for (int p=2;p<path.length;p++){
    						String[] elementId = path[p].split(",");
    						int elementIdmlId = Integer.valueOf(elementId[0]);
    						int elementInstanceNo = Integer.valueOf(elementId[1]);
    						//Log.e("getEntityparams",ApplicationManager.getSurvey().getSchema().getDefinitionById(elementIdmlId).getName()+"=="+elementInstanceNo);
    						if (currentEntity!=null)
    							currentEntity = (Entity) currentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(elementIdmlId).getName(), elementInstanceNo);
    						//Log.e("p=="+p,"currentEntity==Null"+(currentEntity==null));
    						/*if (currentEntity!=null)
    							Log.e("currentEntity"+path[p],currentEntity.getName()+"=="+currentEntity.getValue(nodeDef.getName(), this.currInstanceNo));*/
    					}
    					if (currentEntity!=null){    						
    						NumberValue numberValue = (NumberValue)currentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
        					if (numberValue!=null){
        						if (((NumberAttributeDefinition) nodeDef).isInteger()){
        							loadedValue = String.valueOf(numberValue.getValue().intValue());	
        						} else {
        							loadedValue = String.valueOf(numberValue.getValue().doubleValue());
        						}
        					}   	
    					} 					 						
    				}
    				if (!nodeDef.isMultiple()){
        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
//        				Log.e("ILOSC INSTANCJI","=="+numberOfInstances);
        				FieldValue tempFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
        				if (numberOfInstances!=-1){
        					ArrayList<String> instanceValues = new ArrayList<String>();
            				//Log.e("numberOFinstances","=="+numberOfInstances);
            				//for (int k=0;k<numberOfInstances;k++){
        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+0);
        					tempFieldValue.addValue(instanceValues);
            				//Log.e("dodanowartosc","=="+instanceValues.get(1));
            				//}	
        				}
        				else {
        					ArrayList<String> instanceValues = new ArrayList<String>();
        					instanceValues.add(loadedValue);
        					tempFieldValue.addValue(instanceValues);
        				}
        				
        				NumberField numberField= new NumberField(this, nodeDef, tempFieldValue);
        				numberField.setOnClickListener(this);
        				numberField.setId(nodeDef.getId());
        				//textField.txtBox.addTextChangedListener(this);
        				numberField.setValue(0, tempFieldValue.getValue(0).get(0));
        				FormScreen.currentFieldValue = this.currentNode.getFieldValue(nodeDef.getId());
        				//Log.e("FormScreen.currentFieldValue==null",nodeDef.getId()+"=="+(FormScreen.currentFieldValue==null));
        				if (FormScreen.currentFieldValue==null){
        					ArrayList<String> initialValue = new ArrayList<String>();
        					initialValue.add(numberField.getValue(0));
        					FormScreen.currentFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
        					FormScreen.currentFieldValue.addValue(initialValue);
        					this.currentNode.addFieldValue(FormScreen.currentFieldValue);        		
        				} else {
        					//Log.e("wczytanaWARTOSC","=="+FormScreen.currentFieldValue.getValue(0).get(0));
        					numberField.setValue(0, FormScreen.currentFieldValue.getValue(0).get(0));       
        				}
        				numberField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {
        			            //Log.e("s","=="+s.toString());
        			            ArrayList<String> value = new ArrayList<String>();
        			            value.add(s.toString());
        			            FormScreen.currentFieldValue.setValue(0, value);
        			            FormScreen.this.currentNode.addFieldValue(FormScreen.currentFieldValue);  
        			            //Log.e("changed",FormScreen.currentFieldValue.getId()+"ValueOFsingleField=="+FormScreen.currentFieldValue.getValue(0).get(0));
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				this.ll.addView(numberField);
        				//this.currentNode.addFieldValue(tempFieldValue);
        	    		//Log.e("iloscPOLzWARTOSCIA","=="+this.currentNode.getFieldsNo());       				
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){    					
        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
        				this.currentMultipleFieldValue = new FieldValue(nodeDef.getId(), getFormScreenId(),null);
        				ArrayList<String> instanceValues = new ArrayList<String>();
        				//Log.e("numberOFinstances","=="+numberOfInstances);
        				for (int k=0;k<numberOfInstances;k++){
        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+k);
        					this.currentMultipleFieldValue.addValue(instanceValues);
        					//Log.e("dodanowartosc","=="+instanceValues.get(1));
        				}

        				NumberField numberField= new NumberField(this, nodeDef, this.currentMultipleFieldValue);
        				numberField.setOnClickListener(this);
        				numberField.setId(nodeDef.getId());
        				numberField.txtBox.addTextChangedListener(this);
        				//Log.e("TEXTvalues.size",this.currInstanceNo+"=="+this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
        				if (this.currentMultipleFieldValue.size()>this.currInstanceNo){
        					numberField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));	
        				}			
        				this.ll.addView(numberField);
    				} else {
    					FieldValue fieldValueToBeRestored = this.currentNode.getFieldValue(nodeDef.getId());
    					//Log.e("fieldValueToBeRestored!=null",this.currentNode.getNodeValues().size()+"=="+(fieldValueToBeRestored!=null));
    					if (fieldValueToBeRestored!=null){
    						//Log.e("POWROT","z MULTIPLE FIELD");
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, fieldValueToBeRestored.getValues(), this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
    					} else{
    						//Log.e("PIERWSZE","OTWARCIE");
    			    		ArrayList<List<String>> tableValuesLists = new ArrayList<List<String>>();
    			    		ArrayList<String> valueRow1 = new ArrayList<String>();
    			    		//row1.add("1");
    			    		valueRow1.add("");
    			    		tableValuesLists.add(valueRow1);
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, tableValuesLists, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
    					}			
    				}	
    			} else if (nodeDef instanceof CodeAttributeDefinition){
    				CodeAttributeDefinition codeAttrDef = (CodeAttributeDefinition)nodeDef;
    				ArrayList<String> options = new ArrayList<String>();
    				ArrayList<String> codes = new ArrayList<String>();
    				options.add("");
    				codes.add("null");
    				List<CodeListItem> codeListItemsList = codeAttrDef.getList().getItems();
    				for (CodeListItem codeListItem : codeListItemsList){
    					codes.add(codeListItem.getCode());
    					options.add(codeListItem.getLabel(null));
    				}
    				
    				if (!nodeDef.isMultiple()){
        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
        				//Log.e("ILOSC INSTANCJI","=="+numberOfInstances);
        				FieldValue tempFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
        				if (numberOfInstances!=-1){
        					ArrayList<String> instanceValues = new ArrayList<String>();
            				//Log.e("numberOFinstances","=="+numberOfInstances);
            				//for (int k=0;k<numberOfInstances;k++){
        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+0);
        					tempFieldValue.addValue(instanceValues);
            				//Log.e("dodanowartosc","=="+instanceValues.get(0));
            				//}	
        				}
        				else {
        					ArrayList<String> instanceValues = new ArrayList<String>();
        					instanceValues.add("null");
        					tempFieldValue.addValue(instanceValues);
        				}
        				
        				CodeField codeField= new CodeField(this, nodeDef, codes, options, null, tempFieldValue);
        				codeField.setOnClickListener(this);
        				codeField.setId(nodeDef.getId());
        				//textField.txtBox.addTextChangedListener(this);
        				codeField.setValue(0, tempFieldValue.getValue(0).get(0));
        				FormScreen.currentFieldValue = this.currentNode.getFieldValue(nodeDef.getId());
        				//if (FormScreen.currentFieldValue!=null)
        				//	Log.e("getValue",codeField.getValue(0)+"=="+FormScreen.currentFieldValue.getValue(0).get(0));
        				//Log.e("FormScreen.currentFieldValue==null",nodeDef.getId()+"=="+(FormScreen.currentFieldValue==null));
        				if (FormScreen.currentFieldValue==null){
        					ArrayList<String> initialValue = new ArrayList<String>();
        					initialValue.add(codeField.getValue(0));
        					FormScreen.currentFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
        					FormScreen.currentFieldValue.addValue(initialValue);
        					this.currentNode.addFieldValue(FormScreen.currentFieldValue);        		
        				} else {
        					//Log.e("wczytanaWARTOSC",FormScreen.currentFieldValue.getId()+"=="+FormScreen.currentFieldValue.getValue(0).get(0));
        					codeField.setValue(0, FormScreen.currentFieldValue.getValue(0).get(0));    
        				}
        				//ApplicationManager.uiElementsMap.put(codeField.getId(), codeField);
        				this.ll.addView(codeField);
        				//this.currentNode.addFieldValue(tempFieldValue);
        	    		//Log.e("iloscPOLzWARTOSCIA","=="+this.currentNode.getFieldsNo());       				
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){    					
        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
        				this.currentMultipleFieldValue = new FieldValue(nodeDef.getId(), getFormScreenId(),null);
        				
        				ArrayList<String> instanceValues = new ArrayList<String>();
        				//Log.e("numberOFinstances","=="+numberOfInstances);
        				for (int k=0;k<numberOfInstances;k++){
        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+k);
        					this.currentMultipleFieldValue.addValue(instanceValues);
        					//Log.e("dodanowartosc","=="+instanceValues.get(0));
        				}

        				CodeField codeField= new CodeField(this, nodeDef, codes, options, null, this.currentMultipleFieldValue);
        				codeField.setOnClickListener(this);
        				codeField.setId(nodeDef.getId());
        				//booleanField.txtBox.addTextChangedListener(this);
        				//add onclick listener for multiple field
        				//Log.e("CODEvalues.size", this.currInstanceNo+"=="+this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));

        				if (this.currentMultipleFieldValue.size()>this.currInstanceNo){        					
    						codeField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));	    						
        				}
        				//ApplicationManager.uiElementsMap.put(codeField.getId(), codeField);
        				this.ll.addView(codeField);
    				} else {
    					FieldValue fieldValueToBeRestored = this.currentNode.getFieldValue(nodeDef.getId());    					
    					//Log.e("fieldValueToBeRestored!=null",this.currentNode.getNodeValues().size()+"=="+(fieldValueToBeRestored!=null));
    					if (fieldValueToBeRestored!=null){
    						//Log.e("POWROT","z MULTIPLE FIELD");    						
    						ArrayList<List<String>> tableCodeLabelsLists = new ArrayList<List<String>>();    			    		
    			    		//Log.e("ILOSC","wartosciDOwczytania=="+fieldValueToBeRestored.getValues().size());
    			    		for (List<String> rowValue : fieldValueToBeRestored.getValues()){
    			    			 //Log.e("rowValueToBeRestored",rowValue.size()+"=="+rowValue.get(0));
    			    			 ArrayList<String> codeLabelsRow1 = new ArrayList<String>();
    			    			 for (int d=0;d<rowValue.size();d++){    			    				
        			    			 codeLabelsRow1.add(rowValue.get(d));	 
    			    			 }
    			    			 tableCodeLabelsLists.add(codeLabelsRow1);
    			    		}            				
    			    		
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, tableCodeLabelsLists, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
    					} else{
    						//Log.e("PIERWSZE","OTWARCIE");
    			    		ArrayList<List<String>> tableValuesLists = new ArrayList<List<String>>();
    			    		ArrayList<String> valueRow1 = new ArrayList<String>();
    			    		//row1.add("1");
    			    		valueRow1.add("null");
    			    		tableValuesLists.add(valueRow1);
    			    		ArrayList<List<String>> tableCodeLabelsLists = new ArrayList<List<String>>();
    			    		ArrayList<String> codeLabelsRow1 = new ArrayList<String>();
    			    		CodeListItem codeListItem = codeAttrDef.getList().getItem(tableValuesLists.get(0).get(0));    			    		
    			    		if (codeListItem!=null){
    			    			codeLabelsRow1.add(codeAttrDef.getList().getItem(tableValuesLists.get(0).get(0)).getCode());	
    			    		} else {
    			    			codeLabelsRow1.add("");
    			    		}
    			    		tableCodeLabelsLists.add(codeLabelsRow1);
    			    		
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, tableCodeLabelsLists, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
    					}			
    				}
    			} else if (nodeDef instanceof BooleanAttributeDefinition){
    				loadedValue = "";
    				if (ApplicationManager.currentRecord!=null){
    					//Log.e("breadcrumb","=="+this.getFormScreenId());
    					Entity rootEntity = ApplicationManager.currentRecord.getRootEntity();
    					String[] path = this.getFormScreenId().split(";");
    					Entity currentEntity = rootEntity;
    					for (int p=2;p<path.length;p++){
    						String[] elementId = path[p].split(",");
    						int elementIdmlId = Integer.valueOf(elementId[0]);
    						int elementInstanceNo = Integer.valueOf(elementId[1]);
    						//Log.e("getEntityparams",ApplicationManager.getSurvey().getSchema().getDefinitionById(elementIdmlId).getName()+"=="+elementInstanceNo);
    						if (currentEntity!=null)
    							currentEntity = (Entity) currentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(elementIdmlId).getName(), elementInstanceNo);
    						//Log.e("p=="+p,"currentEntity==Null"+(currentEntity==null));
    						/*if (currentEntity!=null)
    							Log.e("currentEntity"+path[p],currentEntity.getName()+"=="+currentEntity.getValue(nodeDef.getName(), this.currInstanceNo));*/
    					}
    					if (currentEntity!=null){    						
    						BooleanValue booleanValue = (BooleanValue)currentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
        					if (booleanValue!=null){
        							loadedValue = String.valueOf(booleanValue.getValue());
        					}   	
    					} 					 						
    				}
    				if (!nodeDef.isMultiple()){
        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
//        				Log.e("ILOSC INSTANCJI","=="+numberOfInstances);
        				FieldValue tempFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
        				if (numberOfInstances!=-1){
        					ArrayList<String> instanceValues = new ArrayList<String>();
            				//Log.e("numberOFinstances","=="+numberOfInstances);
            				//for (int k=0;k<numberOfInstances;k++){
        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+0);
        					tempFieldValue.addValue(instanceValues);
            				//Log.e("dodanowartosc","=="+instanceValues.get(1));
            				//}	
        				}
        				else {
        					ArrayList<String> instanceValues = new ArrayList<String>();
        					instanceValues.add(loadedValue);
        					String otherValue = "";
        					instanceValues.add(otherValue);
        					tempFieldValue.addValue(instanceValues);
        				}
        				
        				BooleanField booleanField= new BooleanField(this, nodeDef, 
        						false, false, 
        						getResources().getString(R.string.yes), 
        						getResources().getString(R.string.no), tempFieldValue);
        				booleanField.setOnClickListener(this);
        				booleanField.setId(nodeDef.getId());
        				//textField.txtBox.addTextChangedListener(this);
        				booleanField.setValue(0, Boolean.valueOf(tempFieldValue.getValue(0).get(0)), Boolean.valueOf(tempFieldValue.getValue(0).get(1)));
        				FormScreen.currentFieldValue = this.currentNode.getFieldValue(nodeDef.getId());
        				//Log.e("FormScreen.currentFieldValue==null",nodeDef.getId()+"=="+(FormScreen.currentFieldValue==null));
        				if (FormScreen.currentFieldValue==null){
        					ArrayList<String> initialValue = new ArrayList<String>();
        					initialValue.add(booleanField.getValue(0,0));
        					initialValue.add(booleanField.getValue(0,1));
        					FormScreen.currentFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
        					FormScreen.currentFieldValue.addValue(initialValue);
        					this.currentNode.addFieldValue(FormScreen.currentFieldValue);        		
        				} else {
        					//Log.e("wczytanaWARTOSC",FormScreen.currentFieldValue.getId()+"=="+FormScreen.currentFieldValue.getValue(0).get(0));
        					booleanField.setValue(0, Boolean.valueOf(FormScreen.currentFieldValue.getValue(0).get(0)), Boolean.valueOf(FormScreen.currentFieldValue.getValue(0).get(1)));    
        				}
        				/*booleanField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {
        			            //Log.e("s","=="+s.toString());
        			            ArrayList<String> value = new ArrayList<String>();
        			            value.add(s.toString());
        			            FormScreen.currentFieldValue.setValue(0, value);
        			            FormScreen.this.currentNode.addFieldValue(FormScreen.currentFieldValue);  
        			            //Log.e("changed",FormScreen.currentFieldValue.getId()+"ValueOFsingleField=="+FormScreen.currentFieldValue.getValue(0).get(0));
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });*/
        				/*booleanField.addOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Log.e("CHANGING","CURRENT FIELDVALUE"+BooleanField.this.getElementId());
						    	FormScreen.currentFieldValue = BooleanField.this.value;
								CheckBox checkBox1 = (CheckBox)v;
								ArrayList<String> value = new ArrayList<String>();
								value.add(String.valueOf(checkBox1.isChecked()));
								value.add(String.valueOf(!checkBox1.isChecked()));								
								FormScreen.currentFieldValue.setValue(0, value);
        			            FormScreen.this.currentNode.addFieldValue(FormScreen.currentFieldValue);
        			            Log.e("CLICKED1","=="+checkBox1.isChecked());
				  			}
					    }, new OnClickListener() {
							@Override
							public void onClick(View v) {
								CheckBox checkBox2 = (CheckBox)v;
								ArrayList<String> value = new ArrayList<String>();
								value.add(String.valueOf(!checkBox2.isChecked()));
								value.add(String.valueOf(checkBox2.isChecked()));								
								FormScreen.currentFieldValue.setValue(0, value);
        			            FormScreen.this.currentNode.addFieldValue(FormScreen.currentFieldValue);
        			            Log.e("CLICKED2","=="+checkBox2.isChecked());
				  			}
					    });*/
        				this.ll.addView(booleanField);
        				//this.currentNode.addFieldValue(tempFieldValue);
        	    		//Log.e("iloscPOLzWARTOSCIA","=="+this.currentNode.getFieldsNo());       				
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){    					
        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
        				this.currentMultipleFieldValue = new FieldValue(nodeDef.getId(), getFormScreenId(),null);
        				ArrayList<String> instanceValues = new ArrayList<String>();
        				//Log.e("numberOFinstances","=="+numberOfInstances);
        				for (int k=0;k<numberOfInstances;k++){
        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+k);
        					this.currentMultipleFieldValue.addValue(instanceValues);
        					//Log.e("dodanowartosc","=="+instanceValues.get(1));
        				}

        				BooleanField booleanField = new BooleanField(this, nodeDef, 
        						false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no),
        						this.currentMultipleFieldValue);
        				booleanField.setOnClickListener(this);
        				booleanField.setId(nodeDef.getId());
        				//booleanField.txtBox.addTextChangedListener(this);
        				//add onclick listener for multiple field
        				//Log.e("TEXTvalues.size",this.currInstanceNo+"=="+this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
        				if (this.currentMultipleFieldValue.size()>this.currInstanceNo){        					
        					if (!Boolean.valueOf(this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0))&&! Boolean.valueOf(this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(1))){
        						booleanField.setValue(this.currInstanceNo,null,null);	
        					} else {
        						booleanField.setValue(this.currInstanceNo, Boolean.valueOf(this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0)),Boolean.valueOf(this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(1)));	
        					}
        					
        				}			
        				this.ll.addView(booleanField);
    				} else {
    					FieldValue fieldValueToBeRestored = this.currentNode.getFieldValue(nodeDef.getId());
    					//Log.e("fieldValueToBeRestored!=null",this.currentNode.getNodeValues().size()+"=="+(fieldValueToBeRestored!=null));
    					if (fieldValueToBeRestored!=null){
    						//Log.e("POWROT","z MULTIPLE FIELD");
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, fieldValueToBeRestored.getValues(), this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
    					} else{
    						//Log.e("PIERWSZE","OTWARCIE");
    			    		ArrayList<List<String>> tableValuesLists = new ArrayList<List<String>>();
    			    		ArrayList<String> valueRow1 = new ArrayList<String>();
    			    		//row1.add("1");
    			    		valueRow1.add("");
    			    		valueRow1.add("");
    			    		tableValuesLists.add(valueRow1);
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, tableValuesLists, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
    					}			
    				}
    			} else if (nodeDef instanceof CoordinateAttributeDefinition){
    				String latitude = "";
    				String longitude = "";
    				if (ApplicationManager.currentRecord!=null){
    					//Log.e("breadcrumb","=="+this.getFormScreenId());
    					Entity rootEntity = ApplicationManager.currentRecord.getRootEntity();
    					String[] path = this.getFormScreenId().split(";");
    					Entity currentEntity = rootEntity;
    					for (int p=2;p<path.length;p++){
    						String[] elementId = path[p].split(",");
    						int elementIdmlId = Integer.valueOf(elementId[0]);
    						int elementInstanceNo = Integer.valueOf(elementId[1]);
    						//Log.e("getEntityparams",ApplicationManager.getSurvey().getSchema().getDefinitionById(elementIdmlId).getName()+"=="+elementInstanceNo);
    						if (currentEntity!=null)
    							currentEntity = (Entity) currentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(elementIdmlId).getName(), elementInstanceNo);
    						//Log.e("p=="+p,"currentEntity==Null"+(currentEntity==null));
    						/*if (currentEntity!=null)
    							Log.e("currentEntity"+path[p],currentEntity.getName()+"=="+currentEntity.getValue(nodeDef.getName(), this.currInstanceNo));*/
    					}
    					if (currentEntity!=null){    						
    						Coordinate coordinateValue = (Coordinate)currentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
        					if (coordinateValue!=null){
        							latitude = String.valueOf(coordinateValue.getY());
        							longitude = String.valueOf(coordinateValue.getX());
        					}   	
    					} 					 						
    				}
    				if (!nodeDef.isMultiple()){
        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
//        				Log.e("ILOSC INSTANCJI","=="+numberOfInstances);
        				FieldValue tempFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
        				if (numberOfInstances!=-1){
        					ArrayList<String> instanceValues = new ArrayList<String>();
            				//Log.e("numberOFinstances","=="+numberOfInstances);
            				//for (int k=0;k<numberOfInstances;k++){
        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+0);
        					tempFieldValue.addValue(instanceValues);
            				//Log.e("dodanowartosc","=="+instanceValues.get(1));
            				//}	
        				}
        				else {
        					ArrayList<String> instanceValues = new ArrayList<String>();
        					instanceValues.add(longitude);
        					instanceValues.add(latitude);
        					tempFieldValue.addValue(instanceValues);
        				}
        				
        				CoordinateField coordinateField= new CoordinateField(this, nodeDef, tempFieldValue);
        				coordinateField.setOnClickListener(this);
        				coordinateField.setId(nodeDef.getId());
        				//textField.txtBox.addTextChangedListener(this);
        				//coordinateField.setValue(0, tempFieldValue.getValue(0).get(0), tempFieldValue.getValue(0).get(1));
        				FormScreen.currentFieldValue = this.currentNode.getFieldValue(nodeDef.getId());
        				//Log.e("FormScreen.currentFieldValue==null",nodeDef.getId()+"=="+(FormScreen.currentFieldValue==null));
        				if (FormScreen.currentFieldValue==null){
        					ArrayList<String> initialValue = new ArrayList<String>();
        					initialValue.add(coordinateField.getValue(0).get(0));
        					initialValue.add(coordinateField.getValue(0).get(1));
        					FormScreen.currentFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
        					FormScreen.currentFieldValue.addValue(initialValue);
        					this.currentNode.addFieldValue(FormScreen.currentFieldValue);        		
        				} else {
        					//Log.e("wczytanaWARTOSC",FormScreen.currentFieldValue.getValue(0).get(0)+"=="+FormScreen.currentFieldValue.getValue(0).get(1));
        					coordinateField.setValue(0, FormScreen.currentFieldValue.getValue(0).get(0), FormScreen.currentFieldValue.getValue(0).get(1));
        				}
        				/*coordinateField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {
        			            //Log.e("s","=="+s.toString());
        			            ArrayList<String> value = new ArrayList<String>();
        			            value.add(s.toString());
        			            FormScreen.currentFieldValue.setValue(0, value);
        			            FormScreen.this.currentNode.addFieldValue(FormScreen.currentFieldValue);  
        			            //Log.e("changed",FormScreen.currentFieldValue.getId()+"ValueOFsingleField=="+FormScreen.currentFieldValue.getValue(0).get(0));
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });*/
        				this.ll.addView(coordinateField);
        				//this.currentNode.addFieldValue(tempFieldValue);
        	    		//Log.e("iloscPOLzWARTOSCIA","=="+this.currentNode.getFieldsNo());       				
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){    					
        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
        				this.currentMultipleFieldValue = new FieldValue(nodeDef.getId(), getFormScreenId(),null);
        				ArrayList<String> instanceValues = new ArrayList<String>();
        				//Log.e("numberOFinstances","=="+numberOfInstances);
        				for (int k=0;k<numberOfInstances;k++){
        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+k);
        					this.currentMultipleFieldValue.addValue(instanceValues);
        					//Log.e("dodanowartosc","=="+instanceValues.get(1));
        				}

        				CoordinateField coordinateField= new CoordinateField(this, nodeDef, this.currentMultipleFieldValue);
        				coordinateField.setOnClickListener(this);
        				coordinateField.setId(nodeDef.getId());
        				//coordinateField.txtBox.addTextChangedListener(this);
        				//Log.e("TEXTvalues.size",this.currInstanceNo+"=="+this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
        				if (this.currentMultipleFieldValue.size()>this.currInstanceNo){
        					coordinateField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0), this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(1));	
        				}
        				this.ll.addView(coordinateField);
    				} else {
    					FieldValue fieldValueToBeRestored = this.currentNode.getFieldValue(nodeDef.getId());
    					//Log.e("fieldValueToBeRestored!=null",this.currentNode.getNodeValues().size()+"=="+(fieldValueToBeRestored!=null));
    					if (fieldValueToBeRestored!=null){
    						//Log.e("POWROT","z MULTIPLE FIELD");
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, fieldValueToBeRestored.getValues(), this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
    					} else{
    						//Log.e("PIERWSZE","OTWARCIE");
    			    		ArrayList<List<String>> tableValuesLists = new ArrayList<List<String>>();
    			    		ArrayList<String> valueRow1 = new ArrayList<String>();
    			    		//row1.add("1");
    			    		valueRow1.add("");
    			    		valueRow1.add("");
    			    		tableValuesLists.add(valueRow1);
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, tableValuesLists, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
    					}			
    				}	
    			} else if (nodeDef instanceof DateAttributeDefinition){
    				loadedValue = "";
    				if (ApplicationManager.currentRecord!=null){
    					//Log.e("breadcrumb","=="+this.getFormScreenId());
    					Entity rootEntity = ApplicationManager.currentRecord.getRootEntity();
    					String[] path = this.getFormScreenId().split(";");
    					Entity currentEntity = rootEntity;
    					for (int p=2;p<path.length;p++){
    						String[] elementId = path[p].split(",");
    						int elementIdmlId = Integer.valueOf(elementId[0]);
    						int elementInstanceNo = Integer.valueOf(elementId[1]);
    						//Log.e("getEntityparams",ApplicationManager.getSurvey().getSchema().getDefinitionById(elementIdmlId).getName()+"=="+elementInstanceNo);
    						if (currentEntity!=null)
    							currentEntity = (Entity) currentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(elementIdmlId).getName(), elementInstanceNo);
    						//Log.e("p=="+p,"currentEntity==Null"+(currentEntity==null));
    						/*if (currentEntity!=null)
    							Log.e("currentEntity"+path[p],currentEntity.getName()+"=="+currentEntity.getValue(nodeDef.getName(), this.currInstanceNo));*/
    					}
    					if (currentEntity!=null){    						
    						Date dateValue = (Date)currentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
        					if (dateValue!=null){
        							loadedValue = dateValue.getMonth()+"/"+dateValue.getDay()+"/"+dateValue.getYear();
        					}   	
    					} 					 						
    				}
    				if (!nodeDef.isMultiple()){
        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
//        				Log.e("ILOSC INSTANCJI","=="+numberOfInstances);
        				FieldValue tempFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
        				if (numberOfInstances!=-1){
        					ArrayList<String> instanceValues = new ArrayList<String>();
            				//Log.e("numberOFinstances","=="+numberOfInstances);
            				//for (int k=0;k<numberOfInstances;k++){
        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+0);
        					tempFieldValue.addValue(instanceValues);
            				//Log.e("dodanowartosc","=="+instanceValues.get(1));
            				//}	
        				}
        				else {
        					ArrayList<String> instanceValues = new ArrayList<String>();
        					instanceValues.add(loadedValue);
        					tempFieldValue.addValue(instanceValues);
        				}
        				
        				DateField dateField = new DateField(this, nodeDef, tempFieldValue);
        				dateField.setOnClickListener(this);
        				dateField.setId(nodeDef.getId());
        				//textField.txtBox.addTextChangedListener(this);
        				dateField.setValue(0, tempFieldValue.getValue(0).get(0));
        				FormScreen.currentFieldValue = this.currentNode.getFieldValue(nodeDef.getId());
        				//Log.e("FormScreen.currentFieldValue==null",nodeDef.getId()+"=="+(FormScreen.currentFieldValue==null));
        				if (FormScreen.currentFieldValue==null){
        					ArrayList<String> initialValue = new ArrayList<String>();
        					initialValue.add(dateField.getValue(0));
        					FormScreen.currentFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
        					FormScreen.currentFieldValue.addValue(initialValue);
        					this.currentNode.addFieldValue(FormScreen.currentFieldValue);        		
        				} else {
        					//Log.e("wczytanaWARTOSC","=="+FormScreen.currentFieldValue.getValue(0).get(0));
        					dateField.setValue(0, FormScreen.currentFieldValue.getValue(0).get(0));       
        				}
        				dateField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {
        			            //Log.e("s","=="+s.toString());
        			            ArrayList<String> value = new ArrayList<String>();
        			            value.add(s.toString());
        			            FormScreen.currentFieldValue.setValue(0, value);
        			            FormScreen.this.currentNode.addFieldValue(FormScreen.currentFieldValue);  
        			            //Log.e("changed",FormScreen.currentFieldValue.getId()+"ValueOFsingleField=="+FormScreen.currentFieldValue.getValue(0).get(0));
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.uiElementsMap.put(dateField.getId(), dateField);
        				this.ll.addView(dateField);
        				//this.currentNode.addFieldValue(tempFieldValue);
        	    		//Log.e("iloscPOLzWARTOSCIA","=="+this.currentNode.getFieldsNo());       				
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){    					
        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
        				this.currentMultipleFieldValue = new FieldValue(nodeDef.getId(), getFormScreenId(),null);
        				ArrayList<String> instanceValues = new ArrayList<String>();
        				//Log.e("numberOFinstances","=="+numberOfInstances);
        				for (int k=0;k<numberOfInstances;k++){
        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+k);
        					this.currentMultipleFieldValue.addValue(instanceValues);
        					//Log.e("dodanowartosc","=="+instanceValues.get(1));
        				}

        				DateField dateField= new DateField(this, nodeDef, this.currentMultipleFieldValue);
        				dateField.setOnClickListener(this);
        				dateField.setId(nodeDef.getId());
        				dateField.txtBox.addTextChangedListener(this);
        				//Log.e("TEXTvalues.size",this.currInstanceNo+"=="+this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
        				if (this.currentMultipleFieldValue.size()>this.currInstanceNo){
        					dateField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));	
        				}
        				ApplicationManager.uiElementsMap.put(dateField.getId(), dateField);
        				this.ll.addView(dateField);
    				} else {
    					FieldValue fieldValueToBeRestored = this.currentNode.getFieldValue(nodeDef.getId());
    					//Log.e("fieldValueToBeRestored!=null",this.currentNode.getNodeValues().size()+"=="+(fieldValueToBeRestored!=null));
    					if (fieldValueToBeRestored!=null){
    						//Log.e("POWROT","z MULTIPLE FIELD");
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, fieldValueToBeRestored.getValues(), this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
    					} else{
    						//Log.e("PIERWSZE","OTWARCIE");
    			    		ArrayList<List<String>> tableValuesLists = new ArrayList<List<String>>();
    			    		ArrayList<String> valueRow1 = new ArrayList<String>();
    			    		//row1.add("1");
    			    		valueRow1.add("");
    			    		tableValuesLists.add(valueRow1);
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, tableValuesLists, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
    					}			
    				}	
    			} else if (nodeDef instanceof RangeAttributeDefinition){
    				/*if (!nodeDef.isMultiple()||(this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent))){
    					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
        				RangeField rangeField= new RangeField(this, nodeDef.getId(), nodeDef.getLabel(Type.INSTANCE, null), null, null, rangeAttrDef.isMultiple(), false);
        				rangeField.setOnClickListener(this);
        				rangeField.setId(nodeDef.getId());
        				this.ll.addView(rangeField);
    				} else {
    					SummaryTable summaryTableView = new SummaryTable(this, nodeDef.getId(), nodeDef.getLabel(Type.INSTANCE, null), tableColHeaders, tableRowLists, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}*/
    				if (!nodeDef.isMultiple()){
        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
//        				Log.e("ILOSC INSTANCJI","=="+numberOfInstances);
        				FieldValue tempFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
        				if (numberOfInstances!=-1){
        					ArrayList<String> instanceValues = new ArrayList<String>();
            				//Log.e("numberOFinstances","=="+numberOfInstances);
            				//for (int k=0;k<numberOfInstances;k++){
        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+0);
        					tempFieldValue.addValue(instanceValues);
            				//Log.e("dodanowartosc","=="+instanceValues.get(1));
            				//}	
        				}
        				else {
        					ArrayList<String> instanceValues = new ArrayList<String>();
        					instanceValues.add("");
        					tempFieldValue.addValue(instanceValues);
        				}
        				
        				RangeField rangeField= new RangeField(this, nodeDef, tempFieldValue);
        				rangeField.setOnClickListener(this);
        				rangeField.setId(nodeDef.getId());
        				//textField.txtBox.addTextChangedListener(this);
        				rangeField.setValue(0, tempFieldValue.getValue(0).get(0));
        				FormScreen.currentFieldValue = this.currentNode.getFieldValue(nodeDef.getId());
        				//Log.e("FormScreen.currentFieldValue==null",nodeDef.getId()+"=="+(FormScreen.currentFieldValue==null));
        				if (FormScreen.currentFieldValue==null){
        					ArrayList<String> initialValue = new ArrayList<String>();
        					initialValue.add(rangeField.getValue(0));
        					FormScreen.currentFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
        					FormScreen.currentFieldValue.addValue(initialValue);
        					this.currentNode.addFieldValue(FormScreen.currentFieldValue);        		
        				} else {
        					//Log.e("wczytanaWARTOSC","=="+FormScreen.currentFieldValue.getValue(0).get(0));
        					rangeField.setValue(0, FormScreen.currentFieldValue.getValue(0).get(0));       
        				}
        				rangeField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {
        			            //Log.e("s","=="+s.toString());
        			            ArrayList<String> value = new ArrayList<String>();
        			            value.add(s.toString());
        			            FormScreen.currentFieldValue.setValue(0, value);
        			            FormScreen.this.currentNode.addFieldValue(FormScreen.currentFieldValue);  
        			            //Log.e("changed",FormScreen.currentFieldValue.getId()+"ValueOFsingleField=="+FormScreen.currentFieldValue.getValue(0).get(0));
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				this.ll.addView(rangeField);
        				//this.currentNode.addFieldValue(tempFieldValue);
        	    		//Log.e("iloscPOLzWARTOSCIA","=="+this.currentNode.getFieldsNo());       				
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){    					
        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
        				this.currentMultipleFieldValue = new FieldValue(nodeDef.getId(), getFormScreenId(),null);
        				ArrayList<String> instanceValues = new ArrayList<String>();
        				//Log.e("numberOFinstances","=="+numberOfInstances);
        				for (int k=0;k<numberOfInstances;k++){
        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+k);
        					this.currentMultipleFieldValue.addValue(instanceValues);
        					//Log.e("dodanowartosc","=="+instanceValues.get(1));
        				}

        				RangeField rangeField= new RangeField(this, nodeDef, this.currentMultipleFieldValue);
        				rangeField.setOnClickListener(this);
        				rangeField.setId(nodeDef.getId());
        				rangeField.txtBox.addTextChangedListener(this);
        				//Log.e("TEXTvalues.size",this.currInstanceNo+"=="+this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
        				if (this.currentMultipleFieldValue.size()>this.currInstanceNo){
        					rangeField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));	
        				}			
        				this.ll.addView(rangeField);
    				} else {
    					FieldValue fieldValueToBeRestored = this.currentNode.getFieldValue(nodeDef.getId());
    					//Log.e("fieldValueToBeRestored!=null",this.currentNode.getNodeValues().size()+"=="+(fieldValueToBeRestored!=null));
    					if (fieldValueToBeRestored!=null){
    						//Log.e("POWROT","z MULTIPLE FIELD");
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, fieldValueToBeRestored.getValues(), this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
    					} else{
    			    		ArrayList<List<String>> tableValuesLists = new ArrayList<List<String>>();
    			    		ArrayList<String> valueRow1 = new ArrayList<String>();
    			    		//row1.add("1");
    			    		valueRow1.add("");
    			    		tableValuesLists.add(valueRow1);
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, tableValuesLists, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
    					}			
    				}
    			} else if (nodeDef instanceof TaxonAttributeDefinition){
    				/*if (!nodeDef.isMultiple()||(this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent))){
    					TaxonAttributeDefinition taxonAttrDef = (TaxonAttributeDefinition)nodeDef;
        				TaxonField taxonField= new TaxonField(this, nodeDef.getId(), nodeDef.getLabel(Type.INSTANCE, null), null, null, null, row3, row3, null, false, taxonAttrDef.isMultiple(), false);
        				taxonField.setOnClickListener(this);
        				taxonField.setId(nodeDef.getId());
        				this.ll.addView(taxonField);
    				} else {
    					SummaryTable summaryTableView = new SummaryTable(this, nodeDef.getId(), nodeDef.getLabel(Type.INSTANCE, null), tableColHeaders, tableRowLists, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}*/
    				ArrayList<String> taxonOptions = new ArrayList<String>();
    				ArrayList<String> taxonCodes = new ArrayList<String>();
    				taxonOptions.add("");
    				taxonCodes.add("");
    				
    				if (!nodeDef.isMultiple()){
        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
//        				Log.e("ILOSC INSTANCJI","=="+numberOfInstances);
        				Log.i("TAXON_FIELD","Not multiple");
        				FieldValue tempFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
        				if (numberOfInstances!=-1){
        					ArrayList<String> instanceValues = new ArrayList<String>();
            				//Log.e("numberOFinstances","=="+numberOfInstances);
        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+0);
        					tempFieldValue.addValue(instanceValues);
        				}
        				else {
        					ArrayList<String> instanceValues = new ArrayList<String>();
        					instanceValues.add("");
        					instanceValues.add("");
        					instanceValues.add("");
        					instanceValues.add("");
        					instanceValues.add("");
        					tempFieldValue.addValue(instanceValues);
        				}
        				Log.i("TAXON_FIELD","TempFieldValue is: " + tempFieldValue.getValues().toString());
        				
        				TaxonField taxonField = new TaxonField(this, nodeDef, taxonCodes, taxonOptions, null, tempFieldValue);
        				taxonField.setOnClickListener(this);
        				taxonField.setId(nodeDef.getId());
        				Log.i("TAXON_FIELD","Setting TaxonField value by tempFieldValue");
        				taxonField.setValue(0, tempFieldValue.getValue(0).get(0), tempFieldValue.getValue(0).get(1), tempFieldValue.getValue(0).get(2),tempFieldValue.getValue(0).get(3), tempFieldValue.getValue(0).get(4));

        				FormScreen.currentFieldValue = this.currentNode.getFieldValue(nodeDef.getId());
        				if (FormScreen.currentFieldValue==null){
        					ArrayList<String> initialValue = new ArrayList<String>();
        					initialValue.add(taxonField.getValue(0).get(0));
        					initialValue.add(taxonField.getValue(0).get(1));
        					initialValue.add(taxonField.getValue(0).get(2));
        					initialValue.add(taxonField.getValue(0).get(3));
        					initialValue.add(taxonField.getValue(0).get(4));
        					
        					FormScreen.currentFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
        					FormScreen.currentFieldValue.addValue(initialValue);
        					this.currentNode.addFieldValue(FormScreen.currentFieldValue);        		
        				} else {
        					//Log.e("wczytanaWARTOSC",FormScreen.currentFieldValue.getId()+"=="+FormScreen.currentFieldValue.getValue(0).get(0));
        					Log.i("TAXON_FIELD","Setting TaxonField value by FormScreen.currentFieldValue");
        					Log.i("TAXON_FIELD","FormScreen.currentFieldValue is: " + FormScreen.currentFieldValue.getValue(0).toString());
        					taxonField.setValue(0, FormScreen.currentFieldValue.getValue(0).get(0), FormScreen.currentFieldValue.getValue(0).get(1), FormScreen.currentFieldValue.getValue(0).get(2), FormScreen.currentFieldValue.getValue(0).get(3), FormScreen.currentFieldValue.getValue(0).get(4));    
        				}
        				ApplicationManager.uiElementsMap.put(taxonField.getId(), taxonField);
        				this.ll.addView(taxonField);       				
    				}
    				else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){    					
    					Log.i("TAXON_FIELD","Is multiple. Intent type is multipleAttributeIntent");
    					int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
        				this.currentMultipleFieldValue = new FieldValue(nodeDef.getId(), getFormScreenId(),null);
        				ArrayList<String> instanceValues = new ArrayList<String>();
        				for (int k=0;k<numberOfInstances;k++){
        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+k);
        					this.currentMultipleFieldValue.addValue(instanceValues);
        				}

        				TaxonField taxonField = new TaxonField(this, nodeDef, taxonCodes, taxonOptions, null, this.currentMultipleFieldValue);
        				taxonField.setOnClickListener(this);
        				taxonField.setId(nodeDef.getId());
        				Log.i("TAXON_FIELD","currentMultipleFieldValue size is: " + this.currentMultipleFieldValue.size());
        				Log.i("TAXON_FIELD","this.currInstanceNo is: " + this.currInstanceNo);
        				if (this.currentMultipleFieldValue.size()>this.currInstanceNo){        					
//        					taxonField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));	    						
        					Log.i("TAXON_FIELD","Set TaxonField value by this.currentMultipleFieldValue: " + this.currentMultipleFieldValue.getValue(this.currInstanceNo).toString());
        					taxonField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0), this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(1), this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(2), this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(3), this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(4));
        				}
        				ApplicationManager.uiElementsMap.put(taxonField.getId(), taxonField);
        				this.ll.addView(taxonField);
    				}
    				else {
    					Log.i("TAXON_FIELD","Is multiple. Intent type is NOT multipleAttributeIntent");
    					FieldValue fieldValueToBeRestored = this.currentNode.getFieldValue(nodeDef.getId());    					
    					if (fieldValueToBeRestored!=null){
    						//Log.e("POWROT","z MULTIPLE FIELD");
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, fieldValueToBeRestored.getValues(), this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
    					} else{
    						//Log.e("PIERWSZE","OTWARCIE");
    			    		ArrayList<List<String>> tableValuesLists = new ArrayList<List<String>>();
    			    		ArrayList<String> valueRow1 = new ArrayList<String>();
    			    		//row1.add("1");
    			    		valueRow1.add("");
    			    		valueRow1.add("");
    			    		valueRow1.add("");
    			    		valueRow1.add("");
    			    		valueRow1.add("");
    			    		tableValuesLists.add(valueRow1);
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, tableValuesLists, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);			
    					} 
    				}
    			} else if (nodeDef instanceof TimeAttributeDefinition){
    				loadedValue = "";
    				if (ApplicationManager.currentRecord!=null){
    					//Log.e("breadcrumb","=="+this.getFormScreenId());
    					Entity rootEntity = ApplicationManager.currentRecord.getRootEntity();
    					String[] path = this.getFormScreenId().split(";");
    					Entity currentEntity = rootEntity;
    					for (int p=2;p<path.length;p++){
    						String[] elementId = path[p].split(",");
    						int elementIdmlId = Integer.valueOf(elementId[0]);
    						int elementInstanceNo = Integer.valueOf(elementId[1]);
    						//Log.e("getEntityparams",ApplicationManager.getSurvey().getSchema().getDefinitionById(elementIdmlId).getName()+"=="+elementInstanceNo);
    						if (currentEntity!=null)
    							currentEntity = (Entity) currentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(elementIdmlId).getName(), elementInstanceNo);
    						//Log.e("p=="+p,"currentEntity==Null"+(currentEntity==null));
    						/*if (currentEntity!=null)
    							Log.e("currentEntity"+path[p],currentEntity.getName()+"=="+currentEntity.getValue(nodeDef.getName(), this.currInstanceNo));*/
    					}
    					if (currentEntity!=null){    						
    						Time timeValue = (Time)currentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
        					if (timeValue!=null){
        							loadedValue = timeValue.getHour()+":"+timeValue.getMinute();
        					}   	
    					} 					 						
    				}
    				if (!nodeDef.isMultiple()){
        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
//        				Log.e("ILOSC INSTANCJI","=="+numberOfInstances);
        				FieldValue tempFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
        				if (numberOfInstances!=-1){
        					ArrayList<String> instanceValues = new ArrayList<String>();
            				//Log.e("numberOFinstances","=="+numberOfInstances);
            				//for (int k=0;k<numberOfInstances;k++){
        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+0);
        					tempFieldValue.addValue(instanceValues);
            				//Log.e("dodanowartosc","=="+instanceValues.get(1));
            				//}
        				}
        				else {
        					ArrayList<String> instanceValues = new ArrayList<String>();
        					instanceValues.add(loadedValue);
        					tempFieldValue.addValue(instanceValues);
        				}
        				
        				TimeField timeField = new TimeField(this, nodeDef, tempFieldValue);
        				timeField.setOnClickListener(this);
        				timeField.setId(nodeDef.getId());
        				//textField.txtBox.addTextChangedListener(this);
        				timeField.setValue(0, tempFieldValue.getValue(0).get(0));
        				FormScreen.currentFieldValue = this.currentNode.getFieldValue(nodeDef.getId());
        				//Log.e("FormScreen.currentFieldValue==null",nodeDef.getId()+"=="+(FormScreen.currentFieldValue==null));
        				if (FormScreen.currentFieldValue==null){
        					ArrayList<String> initialValue = new ArrayList<String>();
        					initialValue.add(timeField.getValue(0));
        					FormScreen.currentFieldValue = new FieldValue(nodeDef.getId(),getFormScreenId(),null);
        					FormScreen.currentFieldValue.addValue(initialValue);
        					this.currentNode.addFieldValue(FormScreen.currentFieldValue);        		
        				} else {
        					//Log.e("wczytanaWARTOSC","=="+FormScreen.currentFieldValue.getValue(0).get(0));
        					timeField.setValue(0, FormScreen.currentFieldValue.getValue(0).get(0));       
        				}
        				timeField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {
        			            //Log.e("s","=="+s.toString());
        			            ArrayList<String> value = new ArrayList<String>();
        			            value.add(s.toString());
        			            FormScreen.currentFieldValue.setValue(0, value);
        			            FormScreen.this.currentNode.addFieldValue(FormScreen.currentFieldValue);  
        			            //Log.e("changed",FormScreen.currentFieldValue.getId()+"ValueOFsingleField=="+FormScreen.currentFieldValue.getValue(0).get(0));
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.uiElementsMap.put(timeField.getId(), timeField);
        				this.ll.addView(timeField);
        				//this.currentNode.addFieldValue(tempFieldValue);
        	    		//Log.e("iloscPOLzWARTOSCIA","=="+this.currentNode.getFieldsNo());       				
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){    					
        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
        				this.currentMultipleFieldValue = new FieldValue(nodeDef.getId(), getFormScreenId(),null);
        				ArrayList<String> instanceValues = new ArrayList<String>();
        				//Log.e("numberOFinstances","=="+numberOfInstances);
        				for (int k=0;k<numberOfInstances;k++){
        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+k);
        					this.currentMultipleFieldValue.addValue(instanceValues);
        					//Log.e("dodanowartosc","=="+instanceValues.get(1));
        				}

        				TimeField timeField= new TimeField(this, nodeDef, this.currentMultipleFieldValue);
        				timeField.setOnClickListener(this);
        				timeField.setId(nodeDef.getId());
        				timeField.txtBox.addTextChangedListener(this);
        				//Log.e("TEXTvalues.size",this.currInstanceNo+"=="+this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
        				if (this.currentMultipleFieldValue.size()>this.currInstanceNo){
        					timeField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));	
        				}
        				ApplicationManager.uiElementsMap.put(timeField.getId(), timeField);
        				this.ll.addView(timeField);
    				} else {
    					FieldValue fieldValueToBeRestored = this.currentNode.getFieldValue(nodeDef.getId());
    					//Log.e("fieldValueToBeRestored!=null",this.currentNode.getNodeValues().size()+"=="+(fieldValueToBeRestored!=null));
    					if (fieldValueToBeRestored!=null){
    						//Log.e("POWROT","z MULTIPLE FIELD");
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, fieldValueToBeRestored.getValues(), this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
    					} else{
    						//Log.e("PIERWSZE","OTWARCIE");
    			    		ArrayList<List<String>> tableValuesLists = new ArrayList<List<String>>();
    			    		ArrayList<String> valueRow1 = new ArrayList<String>();
    			    		//row1.add("1");
    			    		valueRow1.add("");
    			    		tableValuesLists.add(valueRow1);
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, tableValuesLists, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
    					}			
    				}
    			}
    		}
			if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
				//Log.e("multiple","ATTRIBUTE");
				this.ll.addView(arrangeButtonsInLine(new Button(this),getResources().getString(R.string.previousInstanceButton),new Button(this),getResources().getString(R.string.nextInstanceButton),this));
			} else if (this.intentType==getResources().getInteger(R.integer.multipleEntityIntent)){ 
				//Log.e("multiple","ENTITY");
				//this.ll.addView(arrangeButtonsInLine(new Button(this),getResources().getString(R.string.previousInstanceButton),new Button(this),getResources().getString(R.string.nextInstanceButton),this));
			}
    		setContentView(this.sv);
			
			int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);		
    		changeBackgroundColor(backgroundColor);    		
		} catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onResume",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
		}
	}
    
    @Override
    public void onPause(){    
		Log.i(getResources().getString(R.string.app_name),TAG+":onPause");
		if (this.currentMultipleFieldValue!=null){
			/*Log.e("multipleFieldVALUESno","=="+this.currentMultipleFieldValue.size());
			for (int i=0;i<this.currentMultipleFieldValue.size();i++){
				Log.e("multipleFieldVALUE"+i,"=="+this.currentMultipleFieldValue.getValue(i));
			}*/	
			ApplicationManager.fieldValueToPass = this.currentMultipleFieldValue;
		}
		else{
			Log.i("FormScreen onPause","currentMultipleFieldValue is NULL");
		}
		//Log.e("onPauseID",this.idmlId+"|"+this.currInstanceNo+"=="+getFormScreenId());
		super.onPause();
    }
	
    /*private String getLabel(CodeAttributeDefinition codeAttrDef, String code){
    	String label = "etykieta";
		//ArrayList<String> options = new ArrayList<String>();
		//ArrayList<String> codes = new ArrayList<String>();
		//options.add("");
		//codes.add("null");
		List<CodeListItem> codeListItemsList = codeAttrDef.getList().getItems();
		for (CodeListItem codeListItem : codeListItemsList){
			if (codeListItem.getCode().equals(code)){
				label = codeListItem.getLabel(null);
				break;
			}
		}
    	return label;
    }
    
    private String getCode(CodeAttributeDefinition codeAttrDef, String label){
    	String code = "kod";
		//ArrayList<String> options = new ArrayList<String>();
		//ArrayList<String> codes = new ArrayList<String>();
		//options.add("");
		//codes.add("null");
		List<CodeListItem> codeListItemsList = codeAttrDef.getList().getItems();
		for (CodeListItem codeListItem : codeListItemsList){
			if (codeListItem.getLabel(null).equals(label)){
				code = codeListItem.getCode();
				break;
			}
		}
    	return code;
    }*/
    
	/*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
	
	@Override
	public void onBackPressed() { 
		this.getParent().onBackPressed();
	}*/
	
	private int calcNoOfCharsFitInOneLine(){
		DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
    	//Log.e("ilosc znakow","=="+metrics.widthPixels/14);
    	return metrics.widthPixels/10;    	
	}

	@Override
	public void onClick(View arg0) {
//		Log.e("clickedVIEW",arg0.getClass()+"=="+arg0.getId());
		if (arg0 instanceof SummaryList){
			//Log.e("summary","list");
			SummaryList temp = (SummaryList)arg0;
			//Log.e("nazwa","=="+temp.getTitle());
		} else if (arg0 instanceof Button){
			Button btn = (Button)arg0;
			if (btn.getId()==getResources().getInteger(R.integer.leftButtonMultipleAttribute)){
				if (this.currInstanceNo>0){
					View fieldView = (View)this.ll.getChildAt(1);
					String currValue = "";
					if (fieldView instanceof TextField){
						TextField tempTextField = (TextField)fieldView;
						currValue = tempTextField.getValue(this.currInstanceNo);
					} else if (fieldView instanceof MemoField){
						MemoField tempMemoField = (MemoField)fieldView;
						currValue = tempMemoField.getValue(this.currInstanceNo);
					} else if (fieldView instanceof NumberField){
						NumberField tempNumberField = (NumberField)fieldView;
						currValue = tempNumberField.getValue(this.currInstanceNo);
					} else if (fieldView instanceof BooleanField){
						BooleanField tempBooleanField = (BooleanField)fieldView;
						currValue = tempBooleanField.getValue(this.currInstanceNo, 0);
					} else if (fieldView instanceof DateField){
						DateField tempDateField = (DateField)fieldView;
						currValue = tempDateField.getValue(this.currInstanceNo);
					} else if (fieldView instanceof TimeField){
						TimeField tempTimeField = (TimeField)fieldView;
						currValue = tempTimeField.getValue(this.currInstanceNo);
					} else if (fieldView instanceof CoordinateField){
						CoordinateField tempCoordinateField = (CoordinateField)fieldView;
						List<String> currCoordinateValue = tempCoordinateField.getValue(this.currInstanceNo);
						this.currentMultipleFieldValue.getValue(this.currInstanceNo).set(0, currCoordinateValue.get(0));
						this.currentMultipleFieldValue.getValue(this.currInstanceNo).set(1, currCoordinateValue.get(1));
					}else if (fieldView instanceof TaxonField){
						TaxonField tempTaxonField = (TaxonField)fieldView;
						List<String> curTaxonValue = tempTaxonField.getValue(this.currInstanceNo);
						this.currentMultipleFieldValue.getValue(this.currInstanceNo).set(0, curTaxonValue.get(0));
						this.currentMultipleFieldValue.getValue(this.currInstanceNo).set(1, curTaxonValue.get(1));
						this.currentMultipleFieldValue.getValue(this.currInstanceNo).set(2, curTaxonValue.get(2));
						this.currentMultipleFieldValue.getValue(this.currInstanceNo).set(3, curTaxonValue.get(3));
						this.currentMultipleFieldValue.getValue(this.currInstanceNo).set(4, curTaxonValue.get(4));
					} else if (fieldView instanceof CodeField){
						CodeField tempCodeField = (CodeField)fieldView;
						currValue = tempCodeField.getValue(this.currInstanceNo);
					}
					if (!((fieldView instanceof CoordinateField)||(fieldView instanceof TaxonField))){
						Log.e("setting current VALUE",this.currInstanceNo+"=="+currValue);
						this.currentMultipleFieldValue.getValue(this.currInstanceNo).set(0, currValue);	
					}					
					this.currInstanceNo--;
					if (fieldView instanceof TextField){
						TextField tempTextField = (TextField)fieldView;
						tempTextField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
					} else if (fieldView instanceof NumberField){
						MemoField tempMemoField = (MemoField)fieldView;
						tempMemoField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
					} else if (fieldView instanceof NumberField){
						NumberField tempNumberField = (NumberField)fieldView;
						tempNumberField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
					} else if (fieldView instanceof BooleanField){
						BooleanField tempBooleanField = (BooleanField)fieldView;
						tempBooleanField.setValue(this.currInstanceNo, Boolean.valueOf(this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0)),!Boolean.valueOf(this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0)));
					} else if (fieldView instanceof DateField){
						DateField tempDateField = (DateField)fieldView;
						tempDateField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
					} else if (fieldView instanceof TimeField){
						TimeField tempTimeField = (TimeField)fieldView;
						tempTimeField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
					} else if (fieldView instanceof CoordinateField){
						CoordinateField tempCoordinateField = (CoordinateField)fieldView;
						tempCoordinateField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0),this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(1));
					} else if (fieldView instanceof TaxonField){
						TaxonField tempTaxonField = (TaxonField)fieldView;
						tempTaxonField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0),this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(1), this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(2),this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(3),this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(4));
					} else if (fieldView instanceof CodeField){
						CodeField tempCodeField = (CodeField)fieldView;
						tempCodeField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
					}
				}
			} else if (btn.getId()==getResources().getInteger(R.integer.rightButtonMultipleAttribute)){
				View fieldView = (View)this.ll.getChildAt(1);
				String currValue = "";
				if (fieldView instanceof TextField){
					TextField tempTextField = (TextField)fieldView;
					currValue = tempTextField.getValue(this.currInstanceNo);
				} else if (fieldView instanceof MemoField){
					MemoField tempMemoField = (MemoField)fieldView;
					currValue = tempMemoField.getValue(this.currInstanceNo);
				} else if (fieldView instanceof NumberField){
					NumberField tempNumberField = (NumberField)fieldView;
					currValue = tempNumberField.getValue(this.currInstanceNo);
				} else if (fieldView instanceof BooleanField){
					BooleanField tempBooleanField = (BooleanField)fieldView;
					currValue = tempBooleanField.getValue(this.currInstanceNo, 0);
				} else if (fieldView instanceof DateField){
					DateField tempDateField = (DateField)fieldView;
					currValue = tempDateField.getValue(this.currInstanceNo);
				} else if (fieldView instanceof TimeField){
					TimeField tempTimeField = (TimeField)fieldView;
					currValue = tempTimeField.getValue(this.currInstanceNo);
				} else if (fieldView instanceof CoordinateField){
					CoordinateField tempCoordinateField = (CoordinateField)fieldView;
					List<String> currCoordinateValue = tempCoordinateField.getValue(this.currInstanceNo);
					this.currentMultipleFieldValue.getValue(this.currInstanceNo).set(0, currCoordinateValue.get(0));
					this.currentMultipleFieldValue.getValue(this.currInstanceNo).set(1, currCoordinateValue.get(1));
				} else if (fieldView instanceof TaxonField){
					TaxonField tempTaxonField = (TaxonField)fieldView;
					List<String> curTaxonValue = tempTaxonField.getValue(this.currInstanceNo);
					this.currentMultipleFieldValue.getValue(this.currInstanceNo).set(0, curTaxonValue.get(0));
					this.currentMultipleFieldValue.getValue(this.currInstanceNo).set(1, curTaxonValue.get(1));
					this.currentMultipleFieldValue.getValue(this.currInstanceNo).set(2, curTaxonValue.get(2));
					this.currentMultipleFieldValue.getValue(this.currInstanceNo).set(3, curTaxonValue.get(3));
					this.currentMultipleFieldValue.getValue(this.currInstanceNo).set(4, curTaxonValue.get(4));
				} else if (fieldView instanceof CodeField){
					CodeField tempCodeField = (CodeField)fieldView;
					currValue = tempCodeField.getValue(this.currInstanceNo);
				}
				if (!((fieldView instanceof CoordinateField)||(fieldView instanceof TaxonField))){
					this.currentMultipleFieldValue.getValue(this.currInstanceNo).set(0, currValue);	
				}
				this.currInstanceNo++;
				if (this.currInstanceNo<this.numberOfInstances){
					if (fieldView instanceof TextField){
						TextField tempTextField = (TextField)fieldView;
						tempTextField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
					} else if (fieldView instanceof MemoField){
						MemoField tempMemoField = (MemoField)fieldView;
						tempMemoField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
					} else if (fieldView instanceof NumberField){
						NumberField tempNumberField = (NumberField)fieldView;
						tempNumberField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
					} else if (fieldView instanceof BooleanField){
						BooleanField tempBooleanField = (BooleanField)fieldView;
						tempBooleanField.setValue(this.currInstanceNo, Boolean.valueOf(this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0)),!Boolean.valueOf(this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0)));						
					} else if (fieldView instanceof DateField){
						DateField tempDateField = (DateField)fieldView;
						tempDateField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
					} else if (fieldView instanceof TimeField){
						TimeField tempTimeField = (TimeField)fieldView;
						tempTimeField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
					} else if (fieldView instanceof CoordinateField){
						CoordinateField tempCoordinateField = (CoordinateField)fieldView;
						tempCoordinateField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0), this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(1));
					} else if (fieldView instanceof TaxonField){
						TaxonField tempTaxonField = (TaxonField)fieldView;
						tempTaxonField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0),this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(1), this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(2),this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(3),this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(4));
					} else if (fieldView instanceof CodeField){
						CodeField tempCodeField = (CodeField)fieldView;
						tempCodeField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
					}
				} else {//new instance
					ArrayList<String> newValue = new ArrayList<String>();
					//newValue.add(""+(this.currInstanceNo+1));
					if (fieldView instanceof InputField){
						newValue.add("");
					}/* else if (fieldView instanceof BooleanField) {
						newValue.add("");
					}*/ else if (fieldView instanceof BooleanField) {
						newValue.add("");
						newValue.add("");
					} else if (fieldView instanceof CodeField){
						newValue.add("null");
					}
					this.currentMultipleFieldValue.addValue(this.currInstanceNo,newValue);
					if (fieldView instanceof TextField){
						TextField tempTextField = (TextField)fieldView;
						tempTextField.setValue(this.currInstanceNo, "");
					} else if (fieldView instanceof MemoField){
						MemoField tempMemoField = (MemoField)fieldView;
						tempMemoField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
					} else if (fieldView instanceof NumberField){
						NumberField tempNumberField = (NumberField)fieldView;
						tempNumberField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
					} else if (fieldView instanceof BooleanField){
						BooleanField tempBooleanField = (BooleanField)fieldView;
						tempBooleanField.setValue(this.currInstanceNo, null/*Boolean.valueOf(this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0))*/,null/*Boolean.valueOf(this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0))*/);
					} else if (fieldView instanceof DateField){
						DateField tempDateField = (DateField)fieldView;
						tempDateField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
					} else if (fieldView instanceof TimeField){
						TimeField tempTimeField = (TimeField)fieldView;
						tempTimeField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
					} else if (fieldView instanceof CoordinateField){
						CoordinateField tempCoordinateField = (CoordinateField)fieldView;
						tempCoordinateField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0), this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(1));
					} else if (fieldView instanceof TaxonField){
						TaxonField tempTaxonField = (TaxonField)fieldView;
						tempTaxonField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0), this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(1), this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(2), this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(3), this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(4));
					} else if (fieldView instanceof CodeField){
						CodeField tempCodeField = (CodeField)fieldView;
						tempCodeField.setValue(this.currInstanceNo, this.currentMultipleFieldValue.getValue(this.currInstanceNo).get(0));
					}
					this.numberOfInstances++;
				}				
			}
		} else if (arg0 instanceof TextView){
			//Log.e("summary","list row");
			TextView tv = (TextView)arg0;
			//Log.e("klikniety","id"+tv.getId()+"=="+tv.getText().toString());
			//Log.e("parentClass","=="+arg0.getParent().getParent().getParent().getParent());
			Object parentView = arg0.getParent().getParent().getParent().getParent();
			if (parentView instanceof SummaryList){
				SummaryList temp = (SummaryList)arg0.getParent().getParent().getParent().getParent();
				//Log.e("nazwa","=="+temp.getTitle());	
				this.startActivity(this.prepareIntentForNewScreen(temp));				
			} else if (parentView instanceof SummaryTable){
				SummaryTable temp = (SummaryTable)parentView;
				//Log.e("nazwa","=="+temp.getTitle());	
				this.startActivity(this.prepareIntentForMultipleField(temp, tv.getId(), temp.getValues()));
			}			
		}
	}
	
	private Intent prepareIntentForNewScreen(SummaryList summaryList){
		Intent intent = new Intent(this,FormScreen.class);
		intent.putExtra(getResources().getString(R.string.breadcrumb), this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryList.getTitle());
		if (summaryList.getEntityDefinition().isMultiple()){
			intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.multipleEntityIntent));	
		} else {
			intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.singleEntityIntent));
		}		
		intent.putExtra(getResources().getString(R.string.idmlId), summaryList.getId());
		intent.putExtra(getResources().getString(R.string.instanceNo), 0);
		intent.putExtra(getResources().getString(R.string.parentFormScreenId), this.getFormScreenId());
        //List<NodeDefinition> formFields = ApplicationManager.fieldsDefList;
        List<NodeDefinition> entityAttributes = summaryList.getEntityDefinition().getChildDefinitions();
        int counter = 0;
        for (NodeDefinition formField : entityAttributes){
			intent.putExtra(getResources().getString(R.string.attributeId)+counter, formField.getId());
			counter++;
        }
		return intent;
	}

	private Intent prepareIntentForMultipleField(SummaryTable summaryTable, int clickedInstanceNo, List<List<String>> data){
		Intent intent = new Intent(this,FormScreen.class);
		intent.putExtra(getResources().getString(R.string.breadcrumb), this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryTable.getTitle());
		intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.multipleAttributeIntent));
        intent.putExtra(getResources().getString(R.string.attributeId)+"0", summaryTable.getId());
        intent.putExtra(getResources().getString(R.string.idmlId), summaryTable.getId());
        intent.putExtra(getResources().getString(R.string.instanceNo), clickedInstanceNo);
        intent.putExtra(getResources().getString(R.string.parentFormScreenId), this.getFormScreenId());
        List<List<String>> values = summaryTable.getValues();
        int numberOfInstances = values.size();
        intent.putExtra(getResources().getString(R.string.numberOfInstances), numberOfInstances);
        for (int i=0;i<numberOfInstances;i++){
        	//ArrayList<String> instanceValues = (ArrayList<String>)values.get(i);
        	ArrayList<String> instanceValues = (ArrayList<String>)values.get(i);
        	intent.putStringArrayListExtra(getResources().getString(R.string.instanceValues)+i,instanceValues);
        }
		return intent;
	}
	
    private void changeBackgroundColor(int backgroundColor){
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
		TextView breadcrumb = (TextView)this.ll.getChildAt(0);
		breadcrumb.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		int viewsNo = this.ll.getChildCount();
		for (int i=1;i<viewsNo;i++){
			View tempView = this.ll.getChildAt(i);
			if (tempView instanceof Field){
				Field field = (Field)tempView;
				field.setLabelTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
			}	
			else if (tempView instanceof UIElement){
				if (tempView instanceof SummaryList){
					SummaryList tempSummaryList = (SummaryList)tempView;
					tempSummaryList.changeBackgroundColor(backgroundColor);
				} else if (tempView instanceof SummaryTable){
					SummaryTable tempSummaryTable = (SummaryTable)tempView;
					tempSummaryTable.changeBackgroundColor(backgroundColor);
				}
			}
		}
    }
    
    private RelativeLayout arrangeButtonsInLine(Button btnLeft, String btnLeftLabel, Button btnRight, String btnRightLabel, OnClickListener listener){
		RelativeLayout relativeButtonsLayout = new RelativeLayout(this);
	    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    relativeButtonsLayout.setLayoutParams(lp);
		btnLeft.setText(btnLeftLabel);
		btnRight.setText(btnRightLabel);
		
		btnLeft.setOnClickListener(listener);
		btnRight.setOnClickListener(listener);
		
		RelativeLayout.LayoutParams lpBtnLeft = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lpBtnLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		btnLeft.setLayoutParams(lpBtnLeft);
		relativeButtonsLayout.addView(btnLeft);
		
		RelativeLayout.LayoutParams lpBtnRight = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lpBtnRight.addRule(RelativeLayout.RIGHT_OF,btnLeft.getId());
		lpBtnRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		btnRight.setLayoutParams(lpBtnRight);
		relativeButtonsLayout.addView(btnRight);
		
		btnLeft.setId(getResources().getInteger(R.integer.leftButtonMultipleAttribute));
		btnRight.setId(getResources().getInteger(R.integer.rightButtonMultipleAttribute));
		
		return relativeButtonsLayout;
    }
    
    private String getFormScreenId(){
    	if (this.parentFormScreenId.equals("")){
    		return this.idmlId+getResources().getString(R.string.valuesSeparator1)+this.currInstanceNo;
    	} else 
    		return this.parentFormScreenId+getResources().getString(R.string.valuesSeparator2)+this.idmlId+getResources().getString(R.string.valuesSeparator1)+this.currInstanceNo;
    }

	@Override
	public void afterTextChanged(Editable arg0) {
		if (this.currentMultipleFieldValue!=null){
			//Log.e("currInstanceNo",FormScreen.currentFieldValue.size()+"TEXTFIELD=="+this.currInstanceNo);
			ArrayList<String> tempValue = new ArrayList<String>();
			tempValue.add(arg0.toString());
			this.currentMultipleFieldValue.setValue(this.currInstanceNo, tempValue);
		} else {
			ArrayList<List<String>> valuesLists = new ArrayList<List<String>>();
			ArrayList<String> currentValuesList = new ArrayList<String>();
			currentValuesList.add(arg0.toString());
			valuesLists.add(currentValuesList);
			FieldValue tempValue = new FieldValue(FormScreen.currentFieldValue.getId(), "", valuesLists);
			this.currentNode.addFieldValue(tempValue);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		
	}
}