package org.openforis.collect.android.screens;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.fields.CodeField;
import org.openforis.collect.android.fields.CoordinateField;
import org.openforis.collect.android.fields.DateField;
import org.openforis.collect.android.fields.Field;
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
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.CodeListItem;
import org.openforis.idm.metamodel.CoordinateAttributeDefinition;
import org.openforis.idm.metamodel.DateAttributeDefinition;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
import org.openforis.idm.metamodel.NumericAttributeDefinition;
import org.openforis.idm.metamodel.RangeAttributeDefinition;
import org.openforis.idm.metamodel.TaxonAttributeDefinition;
import org.openforis.idm.metamodel.TextAttributeDefinition;
import org.openforis.idm.metamodel.TimeAttributeDefinition;
import org.openforis.idm.model.Code;
import org.openforis.idm.model.Coordinate;
import org.openforis.idm.model.Date;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.IntegerRange;
import org.openforis.idm.model.IntegerValue;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.RealRange;
import org.openforis.idm.model.RealValue;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class FormScreen extends BaseActivity implements OnClickListener, TextWatcher{
	
	private static final String TAG = "FormScreen";

	private ScrollView sv;			
    private LinearLayout ll;
	
	private Intent startingIntent;
	private String parentFormScreenId;
	private String breadcrumb;
	private int intentType;
	private int fieldsNo;
	private int idmlId;
	public int currInstanceNo;
	//private int numberOfInstances;
	
	private Entity parentEntity;
	private Entity parentEntityMultiple;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        	Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
    		
            //breadcrumb of the screen
    		this.startingIntent = getIntent();
    		this.breadcrumb = this.startingIntent.getStringExtra(getResources().getString(R.string.breadcrumb));
    		this.intentType = this.startingIntent.getIntExtra(getResources().getString(R.string.intentType),-1);
    		this.idmlId = this.startingIntent.getIntExtra(getResources().getString(R.string.idmlId),-1);
    		this.currInstanceNo = this.startingIntent.getIntExtra(getResources().getString(R.string.instanceNo),-1);
    		//this.numberOfInstances = this.startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances),-1);
    		this.parentFormScreenId = this.startingIntent.getStringExtra(getResources().getString(R.string.parentFormScreenId));;
    		this.fieldsNo = this.startingIntent.getExtras().size()-1;
    		
    		this.parentEntity = this.findParentEntity(this.getFormScreenId());
    		this.parentEntityMultiple = this.findParentEntity(this.parentFormScreenId);
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

    		ArrayList<String> tableColHeaders = new ArrayList<String>();
    		tableColHeaders.add("Value");
    		
    		this.sv = new ScrollView(this);
    		this.ll = new LinearLayout(this);
    		this.ll.setOrientation(android.widget.LinearLayout.VERTICAL);
    		this.sv.addView(ll);
    		
    		if (!this.breadcrumb.equals("")){
    			TextView breadcrumb = new TextView(this);
    			breadcrumb.setText(this.breadcrumb);
        		breadcrumb.setTextSize(getResources().getInteger(R.integer.breadcrumbFontSize));
        		this.ll.addView(breadcrumb);
    		}    		
    		
    		
    		for (int i=0;i<this.fieldsNo;i++){
    			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+i, -1));
    			if (nodeDef instanceof EntityDefinition){
    				//Entity parentEntity = ApplicationManager.currentRecord.getRootEntity();
    				if (ApplicationManager.currentRecord.getRootEntity().getId()!=nodeDef.getId()){
    					this.parentEntity = this.findParentEntity(this.getFormScreenId());

        				Node<?> foundNode = this.parentEntity.get(nodeDef.getName(), this.currInstanceNo);
        				if (foundNode!=null){
        				} else {
        					EntityBuilder.addEntity(this.parentEntity, ApplicationManager.getSurvey().getSchema().getDefinitionById(nodeDef.getId()).getName());
        				}
    				}
    				
    				EntityDefinition entityDef = (EntityDefinition)nodeDef;
    				if (ApplicationManager.currentRecord.getRootEntity().getId()!=nodeDef.getId()){    					
        				//Log.e("multipleENTITY"+parentEntity.getName(),parentEntity.getIndex()+""+entityDef.getName()+parentEntity.getCount(entityDef.getName()));
        				for (int e=0;e<this.parentEntity.getCount(entityDef.getName());e++){
        					SummaryList summaryListView = new SummaryList(this, entityDef, calcNoOfCharsFitInOneLine(),
            						this,e);
            				summaryListView.setOnClickListener(this);
            				summaryListView.setId(nodeDef.getId());
            				this.ll.addView(summaryListView);	
        				}
    				} else {
    					SummaryList summaryListView = new SummaryList(this, entityDef, calcNoOfCharsFitInOneLine(),
        						this,0);
        				summaryListView.setOnClickListener(this);
        				summaryListView.setId(nodeDef.getId());
        				this.ll.addView(summaryListView);
    				}    				  				
    			} else {
					/*Entity parentEntity = ApplicationManager.currentRecord.getRootEntity();
					String screenPath = this.getFormScreenId();
					String[] entityPath = screenPath.split(getResources().getString(R.string.valuesSeparator2));
					int pathLength = entityPath.length;
					if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
						pathLength--;
					}
					for (int m=2;m<pathLength;m++){
						String[] instancePath = entityPath[m].split(getResources().getString(R.string.valuesSeparator1));
						int id = Integer.valueOf(instancePath[0]);
						int instanceNo = Integer.valueOf(instancePath[1]);
						parentEntity = (Entity) parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
						if (parentEntity!=null){
							Log.e("formScreenParentEnt",parentEntity.getName()+id+"=="+instanceNo);
						} else{
							Log.e("formScreenParentEnt","NULL");
						}
						parentEntity.setId(id);
					}*/
					
					if (nodeDef instanceof TextAttributeDefinition){
	    				loadedValue = "";	    				

	    				if (((TextAttributeDefinition) nodeDef).getType().toString().toLowerCase().equals("short")){
		    				if (!nodeDef.isMultiple()){
		    					Node<?> foundNode = this.parentEntity.get(nodeDef.getName(), 0);
			    				if (foundNode!=null){
			    					TextValue textValue = (TextValue)this.parentEntity.getValue(nodeDef.getName(), 0);
			    					if (textValue!=null)
			    						loadedValue = textValue.getValue();	    				
			    				}
		        				final TextField textField= new TextField(this, nodeDef);
		        				textField.setOnClickListener(this);
		        				textField.setId(nodeDef.getId());
		        				textField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
		        				textField.addTextChangedListener(new TextWatcher(){
		        			        public void afterTextChanged(Editable s) {        			            
		        			        	textField.setValue(0, s.toString(), FormScreen.this.getFormScreenId(),true);
		        			        }
		        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
		        			    });
		        				ApplicationManager.putUIElement(textField.getId(), textField);
		        				this.ll.addView(textField);
		    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
		    					Node<?> foundNode = this.parentEntityMultiple.get(nodeDef.getName(), this.currInstanceNo);
			    				if (foundNode!=null){
			    					TextValue textValue = (TextValue)this.parentEntityMultiple.getValue(nodeDef.getName(), this.currInstanceNo);
			    					if (textValue!=null)
			    						loadedValue = textValue.getValue();	    				
			    				}
		        				final TextField textField= new TextField(this, nodeDef);
		        				textField.setOnClickListener(this);
		        				textField.setId(nodeDef.getId());
		        				textField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
		        				textField.addTextChangedListener(new TextWatcher(){
		        			        public void afterTextChanged(Editable s) {        			            
		        			        	textField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
		        			        }
		        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
		        			    });
		        				ApplicationManager.putUIElement(textField.getId(), textField);
		        				this.ll.addView(textField);
		    				} else {//multiple attribute summary    			    		
        						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntity, this);
            					summaryTableView.setOnClickListener(this);
                				summaryTableView.setId(nodeDef.getId());
                				this.ll.addView(summaryTableView);
	        				}
	    				} else {//memo field
	    					if (!nodeDef.isMultiple()){
		    					Node<?> foundNode = this.parentEntity.get(nodeDef.getName(), 0);
			    				if (foundNode!=null){
			    					TextValue textValue = (TextValue)this.parentEntity.getValue(nodeDef.getName(), 0);
			    					if (textValue!=null)
			    						loadedValue = textValue.getValue();	    				
			    				}
		        				final MemoField memoField= new MemoField(this, nodeDef);
		        				memoField.setOnClickListener(this);
		        				memoField.setId(nodeDef.getId());
		        				memoField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
		        				memoField.addTextChangedListener(new TextWatcher(){
		        			        public void afterTextChanged(Editable s) {        			            
		        			        	memoField.setValue(0, s.toString(), FormScreen.this.getFormScreenId(),true);
		        			        }
		        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
		        			    });
		        				ApplicationManager.putUIElement(memoField.getId(), memoField);
		        				this.ll.addView(memoField);
		    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
		    					Node<?> foundNode = this.parentEntityMultiple.get(nodeDef.getName(), this.currInstanceNo);
			    				if (foundNode!=null){
			    					TextValue textValue = (TextValue)this.parentEntityMultiple.getValue(nodeDef.getName(), this.currInstanceNo);
			    					if (textValue!=null)
			    						loadedValue = textValue.getValue();	    				
			    				}
		        				final MemoField memoField= new MemoField(this, nodeDef);
		        				memoField.setOnClickListener(this);
		        				memoField.setId(nodeDef.getId());
		        				memoField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
		        				memoField.addTextChangedListener(new TextWatcher(){
		        			        public void afterTextChanged(Editable s) {        			            
		        			        	memoField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
		        			        }
		        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
		        			    });
		        				ApplicationManager.putUIElement(memoField.getId(), memoField);
		        				this.ll.addView(memoField);
		    				} else {//multiple attribute summary    			    		
        						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntity, this);
            					summaryTableView.setOnClickListener(this);
                				summaryTableView.setId(nodeDef.getId());
                				this.ll.addView(summaryTableView);
	        				}
	    				}	
	    			} else if (nodeDef instanceof NumericAttributeDefinition){
	    				loadedValue = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = this.parentEntity.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					if (((NumberAttributeDefinition) nodeDef).isInteger()){
		    						IntegerValue intValue = (IntegerValue)this.parentEntity.getValue(nodeDef.getName(), 0);
		    						if (intValue!=null)
			    						loadedValue = intValue.getValue().toString();
		    					} else {
		    						RealValue realValue = (RealValue)this.parentEntity.getValue(nodeDef.getName(), 0);
		    						if (realValue!=null)
			    						loadedValue = realValue.getValue().toString();
		    					}
		    				}
	        				final NumberField numberField= new NumberField(this, nodeDef);
	        				numberField.setOnClickListener(this);
	        				numberField.setId(nodeDef.getId());
	        				numberField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
	        				numberField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	numberField.setValue(0, s.toString(), FormScreen.this.getFormScreenId(),true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(numberField.getId(), numberField);
	        				this.ll.addView(numberField);
	    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = this.parentEntityMultiple.get(nodeDef.getName(), this.currInstanceNo);
		    				if (foundNode!=null){
		    					if (((NumberAttributeDefinition) nodeDef).isInteger()){
		    						IntegerValue intValue = (IntegerValue)this.parentEntityMultiple.getValue(nodeDef.getName(), this.currInstanceNo);
		    						if (intValue!=null)
			    						loadedValue = intValue.getValue().toString();
		    					} else {
		    						RealValue realValue = (RealValue)this.parentEntityMultiple.getValue(nodeDef.getName(), this.currInstanceNo);
		    						if (realValue!=null)
			    						loadedValue = realValue.getValue().toString();
		    					}
		    				}
	        				final NumberField numberField= new NumberField(this, nodeDef);
	        				numberField.setOnClickListener(this);
	        				numberField.setId(nodeDef.getId());
	        				numberField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
	        				numberField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	numberField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(numberField.getId(), numberField);
	        				this.ll.addView(numberField);
	    				} else {//multiple attribute summary    			    		
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntity, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
        				}
	    			} else if (nodeDef instanceof CodeAttributeDefinition){
	    				loadedValue = "";
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
	    					Node<?> foundNode = this.parentEntity.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					Code codeValue = (Code)this.parentEntity.getValue(nodeDef.getName(), 0);
	        					if (codeValue!=null){
	        						loadedValue = codeValue.getCode();
	        					}
		    				}
	        				CodeField codeField = new CodeField(this, nodeDef, codes, options, null);
	        				codeField.setOnClickListener(this);
	        				codeField.setId(nodeDef.getId());
	        				codeField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
	        				ApplicationManager.putUIElement(codeField.getId(), codeField);
	        				this.ll.addView(codeField);
	    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = this.parentEntityMultiple.get(nodeDef.getName(), this.currInstanceNo);
	    					if (foundNode!=null){
		    					Code codeValue = (Code)this.parentEntityMultiple.getValue(nodeDef.getName(), this.currInstanceNo);
	        					if (codeValue!=null){
	        						loadedValue = codeValue.getCode();
	        					}
		    				}
	        				CodeField codeField = new CodeField(this, nodeDef, codes, options, null);
	        				codeField.setOnClickListener(this);
	        				codeField.setId(nodeDef.getId());
	        				codeField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
	        				ApplicationManager.putUIElement(codeField.getId(), codeField);
	        				this.ll.addView(codeField);
	    				} else {//multiple attribute summary    			    		
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntity, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
        				}
	    			} else if (nodeDef instanceof CoordinateAttributeDefinition){
	    				String loadedValueLatitude = null;
	    				String loadedValueLongitude = null;
	    				Node<?> foundNode = parentEntity.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					Coordinate coordinateValue = (Coordinate)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
        					if (coordinateValue!=null){
        						loadedValueLatitude = coordinateValue.getX().toString();
        						loadedValueLongitude = coordinateValue.getY().toString();
        					}
        					try{
    	    					EntityBuilder.addValue(parentEntity, nodeDef.getName(), coordinateValue, 0);	
        					} catch (Exception e){
        						
        					}
	    				}
	    				if (!nodeDef.isMultiple()){
	        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
	        				if (numberOfInstances!=-1){
	        					ArrayList<String> instanceValues = new ArrayList<String>();
	        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+0);
	        				}
	        				else {
	        					ArrayList<String> instanceValues = new ArrayList<String>();
	        					instanceValues.add(loadedValueLatitude);
	        					instanceValues.add(loadedValueLongitude);
	        				}
	        				
	        				CoordinateField coordinateField= new CoordinateField(this, nodeDef);
	        				coordinateField.setOnClickListener(this);
	        				coordinateField.setId(nodeDef.getId());
	        				this.ll.addView(coordinateField);  				
	    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){    					
	        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
	        				ArrayList<String> instanceValues = new ArrayList<String>();
	        				for (int k=0;k<numberOfInstances;k++){
	        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+k);
	        				}

	        				CoordinateField coordinateField= new CoordinateField(this, nodeDef);
	        				coordinateField.setOnClickListener(this);
	        				coordinateField.setId(nodeDef.getId());
	        				this.ll.addView(coordinateField);
	    				} else {/*
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
	    					}			*/
	    				}	
	    			} else if (nodeDef instanceof DateAttributeDefinition){
	    				loadedValue = "";
	    				Node<?> foundNode = parentEntity.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					Date dateValue = (Date)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
	    					loadedValue = dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay()+getResources().getString(R.string.dateSeparator)+dateValue.getYear();
	    					if (dateValue.getDay()!=null)
	    						EntityBuilder.addValue(parentEntity, nodeDef.getName(), Date.parseDate(loadedValue), 0);
	    				}
	    				if (!nodeDef.isMultiple()){
	        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
	        				if (numberOfInstances!=-1){
	        					ArrayList<String> instanceValues = new ArrayList<String>();
	        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+0);
	            				//}	
	        				}
	        				else {
	        					ArrayList<String> instanceValues = new ArrayList<String>();
	        					instanceValues.add(loadedValue);
	        				}
	        				
	        				final DateField dateField = new DateField(this, nodeDef);
	        				dateField.setOnClickListener(this);
	        				dateField.setId(nodeDef.getId());
	        				dateField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {
	        			            ArrayList<String> value = new ArrayList<String>();
	        			            value.add(s.toString());
	        			            dateField.setValue(0, s.toString(), FormScreen.this.getFormScreenId(),true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(dateField.getId(), dateField);
	        				this.ll.addView(dateField);
	    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){    					
	        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
	        				ArrayList<String> instanceValues = new ArrayList<String>();
	        				for (int k=0;k<numberOfInstances;k++){
	        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+k);
	        				}

	        				DateField dateField= new DateField(this, nodeDef);
	        				dateField.setOnClickListener(this);
	        				dateField.setId(nodeDef.getId());
	        				dateField.txtBox.addTextChangedListener(this);
	        				ApplicationManager.putUIElement(dateField.getId(), dateField);
	        				this.ll.addView(dateField);
	    				} else {/*
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
	    					}		*/	
	    				}	
	    			} else if (nodeDef instanceof RangeAttributeDefinition){
	    				loadedValue = "";
	    				Node<?> foundNode = parentEntity.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					if (((RangeAttributeDefinition) nodeDef).isReal()){
	    						RealRange rangeValue = (RealRange)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);	
	    						loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
	    					} else {
	    						IntegerRange rangeValue = (IntegerRange)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);	
	    						loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
	    					}
	    					
	    					String[] rangeArray = loadedValue.split(getResources().getString(R.string.rangeSeparator));
	    					try{
	    						if (rangeArray.length>0){
	    							if (((RangeAttributeDefinition) nodeDef).isReal()){
	    								RealRange rangeValue = null;
	    								if (rangeArray.length==1){
	    									rangeValue = new RealRange(Double.valueOf(rangeArray[0]), null);					
	    								} else {
	    									rangeValue = new RealRange(Double.valueOf(rangeArray[0]),Double.valueOf(rangeArray[1]), null);	
	    								}	    								
	    								EntityBuilder.addValue(parentEntity, nodeDef.getName(), rangeValue, 0);
	    							} else {
	    								IntegerRange rangeValue = null;
	    								if (rangeArray.length==1){
	    									rangeValue = new IntegerRange(Integer.valueOf(rangeArray[0]), null);					
	    								} else {
	    									rangeValue = new IntegerRange(Integer.valueOf(rangeArray[0]),Integer.valueOf(rangeArray[1]), null);	
	    								}	    								
	    								EntityBuilder.addValue(parentEntity, nodeDef.getName(), rangeValue, 0);
	    							}	
	    						}	
	    					} catch (Exception e){
	    						
	    					}    					
	    				}
	    				if (!nodeDef.isMultiple()){
	        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
	        				if (numberOfInstances!=-1){
	        					ArrayList<String> instanceValues = new ArrayList<String>();
	        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+0);
	        				}
	        				else {
	        					ArrayList<String> instanceValues = new ArrayList<String>();
	        					instanceValues.add(loadedValue);
	        				}
	        				
	        				final RangeField rangeField= new RangeField(this, nodeDef);
	        				rangeField.setOnClickListener(this);
	        				rangeField.setId(nodeDef.getId());
	        				rangeField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {
	        			            ArrayList<String> value = new ArrayList<String>();
	        			            value.add(s.toString());
	        			            rangeField.setValue(0, s.toString(), FormScreen.this.getFormScreenId(),true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				this.ll.addView(rangeField);
	    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){    					
	        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
	        				ArrayList<String> instanceValues = new ArrayList<String>();
	        				for (int k=0;k<numberOfInstances;k++){
	        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+k);
	        				}

	        				RangeField rangeField= new RangeField(this, nodeDef);
	        				rangeField.setOnClickListener(this);
	        				rangeField.setId(nodeDef.getId());
	        				rangeField.txtBox.addTextChangedListener(this);
	        				this.ll.addView(rangeField);
	    				} else {/*
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
	    					}			*/
	    				}
	    			} else if (nodeDef instanceof TaxonAttributeDefinition){
	    				ArrayList<String> taxonOptions = new ArrayList<String>();
	    				ArrayList<String> taxonCodes = new ArrayList<String>();
	    				taxonOptions.add("");
	    				taxonCodes.add("");
	    				
	    				if (!nodeDef.isMultiple()){
	        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
	        				if (numberOfInstances!=-1){
	        					ArrayList<String> instanceValues = new ArrayList<String>();
	        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+0);
	        				}
	        				else {
	        					ArrayList<String> instanceValues = new ArrayList<String>();
	        					instanceValues.add("");
	        					instanceValues.add("");
	        					instanceValues.add("");
	        					instanceValues.add("");
	        					instanceValues.add("");
	        				}
	        				TaxonField taxonField = new TaxonField(this, nodeDef, taxonCodes, taxonOptions, null);
	        				taxonField.setOnClickListener(this);
	        				taxonField.setId(nodeDef.getId());
	        				ApplicationManager.putUIElement(taxonField.getId(), taxonField);
	        				this.ll.addView(taxonField);       				
	    				}
	    				else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
	        				ArrayList<String> instanceValues = new ArrayList<String>();
	        				for (int k=0;k<numberOfInstances;k++){
	        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+k);
	        				}

	        				TaxonField taxonField = new TaxonField(this, nodeDef, taxonCodes, taxonOptions, null);
	        				taxonField.setOnClickListener(this);
	        				taxonField.setId(nodeDef.getId());
	        				ApplicationManager.putUIElement(taxonField.getId(), taxonField);
	        				this.ll.addView(taxonField);
	    				}
	    				else {/*
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
	    					} */
	    				}
	    			} else if (nodeDef instanceof TimeAttributeDefinition){
	    				loadedValue = "";
	    				Node<?> foundNode = parentEntity.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					Time timeValue = (Time)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
	    					loadedValue = timeValue.getHour()+getResources().getString(R.string.timeSeparator)+timeValue.getMinute();
	    					//Log.e("wczytanaWARTOSCtime",nodeDef.getName()+"=="+loadedValue);
	    					if (timeValue.getHour()!=null)
	    						EntityBuilder.addValue(parentEntity, nodeDef.getName(), Time.parseTime(loadedValue), 0);
	    				}
	    				if (!nodeDef.isMultiple()){
	        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
	        				if (numberOfInstances!=-1){
	        					ArrayList<String> instanceValues = new ArrayList<String>();
	        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+0);
	            				//}
	        				}
	        				else {
	        					ArrayList<String> instanceValues = new ArrayList<String>();
	        					instanceValues.add(loadedValue);
	        				}
	        				
	        				final TimeField timeField = new TimeField(this, nodeDef);
	        				timeField.setOnClickListener(this);
	        				timeField.setId(nodeDef.getId());
	        				timeField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {
	        			            ArrayList<String> value = new ArrayList<String>();
	        			            value.add(s.toString());
	        			            timeField.setValue(0, s.toString(), FormScreen.this.getFormScreenId(),true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(timeField.getId(), timeField);
	        				this.ll.addView(timeField);
	    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){    					
	        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
	        				ArrayList<String> instanceValues = new ArrayList<String>();
	        				for (int k=0;k<numberOfInstances;k++){
	        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+k);
	        				}

	        				TimeField timeField= new TimeField(this, nodeDef);
	        				timeField.setOnClickListener(this);
	        				timeField.setId(nodeDef.getId());
	        				timeField.txtBox.addTextChangedListener(this);
	        				ApplicationManager.putUIElement(timeField.getId(), timeField);
	        				this.ll.addView(timeField);
	    				} else {/*
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
	    					}*/	
	    				}
	    			}
    			}
    				
    				
    		}
			if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
				Log.e("multiple","ATTRIBUTE");
				this.ll.addView(arrangeButtonsInLine(new Button(this),getResources().getString(R.string.previousInstanceButton),new Button(this),getResources().getString(R.string.nextInstanceButton),this, false));
			} else if (this.intentType==getResources().getInteger(R.integer.multipleEntityIntent)){ 				
				if (ApplicationManager.currentRecord.getRootEntity().getId()!=this.idmlId){
					Log.e("multiple","ENTITY");
					this.ll.addView(arrangeButtonsInLine(new Button(this),getResources().getString(R.string.previousInstanceButton),new Button(this),getResources().getString(R.string.nextInstanceButton),this, true));
				}	
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
		super.onPause();
    }
	
	private int calcNoOfCharsFitInOneLine(){
		DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
				refreshMultipleAttributeScreen(true);
			} else if (btn.getId()==getResources().getInteger(R.integer.rightButtonMultipleAttribute)){
				refreshMultipleAttributeScreen(false);				
			} else if (btn.getId()==getResources().getInteger(R.integer.leftButtonMultipleEntity)){
				refreshEntityScreen(true);
			} else if (btn.getId()==getResources().getInteger(R.integer.rightButtonMultipleEntity)){
				refreshEntityScreen(false);
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
		if (!this.breadcrumb.equals("")){
			intent.putExtra(getResources().getString(R.string.breadcrumb), this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryList.getTitle());	
		} else {
			intent.putExtra(getResources().getString(R.string.breadcrumb), summaryList.getTitle());
		}
		if (summaryList.getEntityDefinition().isMultiple()){
			intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.multipleEntityIntent));	
		} else {
			intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.singleEntityIntent));
		}		
		intent.putExtra(getResources().getString(R.string.idmlId), summaryList.getId());
		/*int instanceNo = summaryList.getInstanceNo();
		Log.e("instanceNoPrepared","=="+instanceNo);
		/*Log.e("parentFormScreenId","=="+this.parentFormScreenId+"==");
		if (!this.parentFormScreenId.equals("")){
			String[] pathToScreenArray = this.parentFormScreenId.split(getResources().getString(R.string.valuesSeparator2));
			Log.e("pathTOParentScreen"+this.parentFormScreenId,"=="+pathToScreenArray.length);
			Log.e("EXTRACTEDinstanceNo"+instanceNo,pathToScreenArray[pathToScreenArray.length-1]+"=="+pathToScreenArray[pathToScreenArray.length-1].substring(pathToScreenArray[pathToScreenArray.length-1].indexOf(getResources().getString(R.string.valuesSeparator1))));
			instanceNo = Integer.valueOf(pathToScreenArray[pathToScreenArray.length-1].substring(pathToScreenArray[pathToScreenArray.length-1].indexOf(getResources().getString(R.string.valuesSeparator1)+1)));
			Log.e("EXTRACTEDinstanceNo"+instanceNo,pathToScreenArray[pathToScreenArray.length-1]+"==");	
		}*/
		intent.putExtra(getResources().getString(R.string.instanceNo), summaryList.getInstanceNo());
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
		boolean hasBreadcrumb = !this.breadcrumb.equals("");
		if (hasBreadcrumb){
			TextView breadcrumb = (TextView)this.ll.getChildAt(0);
			breadcrumb.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);	
		}
		int viewsNo = this.ll.getChildCount();
		int start = (hasBreadcrumb)?1:0;
		for (int i=start;i<viewsNo;i++){
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
    
    private RelativeLayout arrangeButtonsInLine(Button btnLeft, String btnLeftLabel, Button btnRight, String btnRightLabel, OnClickListener listener, boolean isForEntity){
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
		
		if (!isForEntity){
			btnLeft.setId(getResources().getInteger(R.integer.leftButtonMultipleAttribute));
			btnRight.setId(getResources().getInteger(R.integer.rightButtonMultipleAttribute));	
		} else {
			btnLeft.setId(getResources().getInteger(R.integer.leftButtonMultipleEntity));
			btnRight.setId(getResources().getInteger(R.integer.rightButtonMultipleEntity));
		}
		
		return relativeButtonsLayout;
    }
    
    public String getFormScreenId(){
    	if (this.parentFormScreenId.equals("")){
    		return this.idmlId+getResources().getString(R.string.valuesSeparator1)+this.currInstanceNo;
    	} else 
    		return this.parentFormScreenId+getResources().getString(R.string.valuesSeparator2)+this.idmlId+getResources().getString(R.string.valuesSeparator1)+this.currInstanceNo;
    }

	@Override
	public void afterTextChanged(Editable arg0) {
		/*if (this.currentMultipleFieldValue!=null){
			Log.e("currInstanceNo",FormScreen.currentFieldValue.size()+"TEXTFIELD=="+this.currInstanceNo);
			ArrayList<String> tempValue = new ArrayList<String>();
			tempValue.add(arg0.toString());
			this.currentMultipleFieldValue.setValue(this.currInstanceNo, tempValue);
		} else {
			Log.e("afterTextChanged2","formSCREEN"+arg0.toString()+"==");
			ArrayList<List<String>> valuesLists = new ArrayList<List<String>>();
			ArrayList<String> currentValuesList = new ArrayList<String>();
			currentValuesList.add(arg0.toString());
			valuesLists.add(currentValuesList);
			FieldValue tempValue = new FieldValue(FormScreen.currentFieldValue.getId(), "", valuesLists);
			this.currentNode.addFieldValue(tempValue);
		}*/
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		
	}
	
	private Entity findParentEntity(String path){		
		Entity parentEntity = ApplicationManager.currentRecord.getRootEntity();
		String screenPath = path;
		String[] entityPath = screenPath.split(getResources().getString(R.string.valuesSeparator2));
		try{
			for (int m=2;m<entityPath.length;m++){
				String[] instancePath = entityPath[m].split(getResources().getString(R.string.valuesSeparator1));
				
				int id = Integer.valueOf(instancePath[0]);
				int instanceNo = Integer.valueOf(instancePath[1]);
				parentEntity = (Entity) parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
			}			
		} catch (ClassCastException e){
			
		}
		return parentEntity;
	}
	
	private void refreshEntityScreen(boolean isPreviousEntity){
		//setting current instance number of the entity
		if (isPreviousEntity){//scroll left to previous entity
			if (this.currInstanceNo>0){
				this.currInstanceNo--;
			} else {
				return;
			}
		} else {//scroll right to next (or new) entity
			this.currInstanceNo++;
		}
		//Log.e("REFRESHING",this.getFormScreenId()+"currentInstanceNo"+this.currInstanceNo);
		//refreshing values of fields in the entity 
		Entity parentEntity = this.findParentEntity(this.getFormScreenId());
		if (parentEntity!=null){
			//Log.e("REFRESHING",parentEntity.getIndex()+"parentEntity"+parentEntity.getName());
			//Log.e("REFRESHING1","parentEntity"+parentEntity.getName());
			for (int i=0;i<this.fieldsNo;i++){
				NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+i, -1));
				if (nodeDef!=null){
					//Log.e("refreshing existing field","=="+nodeDef.getName());
					if (nodeDef instanceof TextAttributeDefinition){
						String loadedValue = "";
						if (((TextAttributeDefinition) nodeDef).getType().toString().toLowerCase().equals("short")){
							TextValue textValue = (TextValue)parentEntity.getValue(nodeDef.getName(), 0);							
							if (textValue!=null)
								loadedValue = textValue.getValue();
	    					TextField textField = (TextField) ApplicationManager.getUIElement(nodeDef.getId());
	    					if (textField!=null)
	    						textField.setValue(0, loadedValue, this.getFormScreenId(), false);					
						} else {
							TextValue textValue = (TextValue)parentEntity.getValue(nodeDef.getName(), 0);							
							if (textValue!=null)
								loadedValue = textValue.getValue();
	    					MemoField memoField = (MemoField) ApplicationManager.getUIElement(nodeDef.getId());
	    					if (memoField!=null)
	    						memoField.setValue(0, loadedValue, this.getFormScreenId(), false);
						}								
					} else if (nodeDef instanceof NumericAttributeDefinition){
						String loadedValue = "";
						if (((NumberAttributeDefinition) nodeDef).isInteger()){
							IntegerValue intValue = (IntegerValue)parentEntity.getValue(nodeDef.getName(), 0);
							if (intValue!=null)
								loadedValue = intValue.getValue().toString();	
						} else {
							RealValue realValue = (RealValue)parentEntity.getValue(nodeDef.getName(), 0);					
							if (realValue!=null)
								loadedValue = realValue.getValue().toString();
						}					
						NumberField numberField = (NumberField) ApplicationManager.getUIElement(nodeDef.getId());
						if (numberField!=null)
							numberField.setValue(0, loadedValue, this.getFormScreenId(), false);
					} else if (nodeDef instanceof CodeAttributeDefinition){
						String loadedValue = "";
						Code codeValue = (Code)parentEntity.getValue(nodeDef.getName(), 0);
						if (codeValue!=null)
							loadedValue = codeValue.getCode();
						CodeField codeField = (CodeField) ApplicationManager.getUIElement(nodeDef.getId());
						if (codeField!=null)
							codeField.setValue(0, loadedValue, this.getFormScreenId(), false);
					}
				}
			}
		} else{//parentEntity is null, because there is no entity added with this instance number in current record
			String path = this.getFormScreenId().substring(0,this.getFormScreenId().lastIndexOf(getResources().getString(R.string.valuesSeparator2)));
			//Log.e("REFRESHING","path=="+path+"==");
			parentEntity = this.findParentEntity(path);
			EntityBuilder.addEntity(parentEntity, ApplicationManager.getSurvey().getSchema().getDefinitionById(this.idmlId).getName());
			parentEntity = this.findParentEntity(this.getFormScreenId());
			//Log.e("REFRESHING2",parentEntity.getIndex()+"parentEntity"+parentEntity.getName());
			for (int i=0;i<this.fieldsNo;i++){
				NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+i, -1));
				if (nodeDef!=null){
					//Log.e("refreshing new field","=="+nodeDef.getName());
					if (nodeDef instanceof TextAttributeDefinition){
						String loadedValue = "";
						if (((TextAttributeDefinition) nodeDef).getType().toString().toLowerCase().equals("short")){							
	    					TextField textField = (TextField) ApplicationManager.getUIElement(nodeDef.getId());
	    					if (textField!=null)
	    						textField.setValue(0, loadedValue, this.getFormScreenId(), false);	
						} else {
	    					MemoField memoField = (MemoField) ApplicationManager.getUIElement(nodeDef.getId());
	    					if (memoField!=null)
	    						memoField.setValue(0, loadedValue, this.getFormScreenId(), false);
						}    					
					} else if (nodeDef instanceof NumericAttributeDefinition){
						String loadedValue = "";
						if (((NumberAttributeDefinition) nodeDef).isInteger()){
							IntegerValue intValue = (IntegerValue)parentEntity.getValue(nodeDef.getName(), 0);
							if (intValue!=null)
								loadedValue = intValue.getValue().toString();	
						} else {
							RealValue realValue = (RealValue)parentEntity.getValue(nodeDef.getName(), 0);					
							if (realValue!=null)
								loadedValue = realValue.getValue().toString();
						}					
						NumberField numberField = (NumberField) ApplicationManager.getUIElement(nodeDef.getId());
						if (numberField!=null)
							numberField.setValue(0, loadedValue, this.getFormScreenId(), false);
					} else if (nodeDef instanceof CodeAttributeDefinition){
						String loadedValue = "";
						Code codeValue = (Code)parentEntity.getValue(nodeDef.getName(), 0);
						if (codeValue!=null)
							loadedValue = codeValue.getCode();
						CodeField codeField = (CodeField) ApplicationManager.getUIElement(nodeDef.getId());
						if (codeField!=null)
							codeField.setValue(0, loadedValue, this.getFormScreenId(), false);
					}			
				}				
			}
		}
	}
	
	private void refreshMultipleAttributeScreen(boolean isPreviousField){
		if (isPreviousField){
			if (this.currInstanceNo>0){
				this.currInstanceNo--;
			} else {
				return;
			}	
		} else {
			this.currInstanceNo++;
		}
		
		//Entity parentEntity = this.findParentEntity(this.parentFormScreenId);
		Entity parentEntity = this.parentEntityMultiple;
		if (parentEntity!=null){
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+0, -1));
			
			if (nodeDef!=null){
				if (nodeDef instanceof TextAttributeDefinition){
					String loadedValue = "";
					if (((TextAttributeDefinition) nodeDef).getType().toString().toLowerCase().equals("short")){
						TextValue textValue = (TextValue)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);						
						if (textValue!=null)
							loadedValue = textValue.getValue();
    					TextField textField = (TextField) ApplicationManager.getUIElement(nodeDef.getId());
    					if (textField!=null)
    						textField.setValue(this.currInstanceNo, loadedValue, this.getFormScreenId(), false);	
					} else {
						TextValue textValue = (TextValue)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
						if (textValue!=null)
							loadedValue = textValue.getValue();
    					TextField textField = (TextField) ApplicationManager.getUIElement(nodeDef.getId());
    					if (textField!=null)
    						textField.setValue(this.currInstanceNo, loadedValue, this.getFormScreenId(), false);
					} 
				} else if (nodeDef instanceof NumericAttributeDefinition){
					String loadedValue = "";
					if (((NumberAttributeDefinition) nodeDef).isInteger()){
						IntegerValue intValue = (IntegerValue)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
						if (intValue!=null)
							loadedValue = intValue.getValue().toString();	
					} else {
						RealValue realValue = (RealValue)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);					
						if (realValue!=null)
							loadedValue = realValue.getValue().toString();
					}					
					NumberField numberField = (NumberField) ApplicationManager.getUIElement(nodeDef.getId());
					if (numberField!=null)
						numberField.setValue(this.currInstanceNo, loadedValue, this.getFormScreenId(), false);
				} else if (nodeDef instanceof CodeAttributeDefinition){
					String loadedValue = "";
					Code codeValue = (Code)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
					if (codeValue!=null)
						loadedValue = codeValue.getCode();
					CodeField codeField = (CodeField) ApplicationManager.getUIElement(nodeDef.getId());
					if (codeField!=null)
						codeField.setValue(this.currInstanceNo, loadedValue, this.getFormScreenId(), false);
				}
			}
		}
	}
}