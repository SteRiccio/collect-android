package org.openforis.collect.android.screens;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Range;
import org.openforis.collect.android.R;
import org.openforis.collect.android.fields.BooleanField;
import org.openforis.collect.android.fields.CodeField;
import org.openforis.collect.android.fields.CoordinateField;
import org.openforis.collect.android.fields.DateField;
import org.openforis.collect.android.fields.Field;
import org.openforis.collect.android.fields.MemoField;
import org.openforis.collect.android.fields.NumberField;
import org.openforis.collect.android.fields.PhotoField;
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
import org.openforis.idm.metamodel.BooleanAttributeDefinition;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.CodeListItem;
import org.openforis.idm.metamodel.CoordinateAttributeDefinition;
import org.openforis.idm.metamodel.DateAttributeDefinition;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.FileAttributeDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
import org.openforis.idm.metamodel.NumericAttributeDefinition;
import org.openforis.idm.metamodel.RangeAttributeDefinition;
import org.openforis.idm.metamodel.TaxonAttributeDefinition;
import org.openforis.idm.metamodel.TextAttributeDefinition;
import org.openforis.idm.metamodel.TimeAttributeDefinition;
import org.openforis.idm.model.BooleanValue;
import org.openforis.idm.model.Code;
import org.openforis.idm.model.Coordinate;
import org.openforis.idm.model.Date;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.File;
import org.openforis.idm.model.IntegerValue;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.RealValue;
import org.openforis.idm.model.TextValue;
import org.openforis.idm.model.Time;
import org.openforis.idm.model.species.Taxon;

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
	
	private Entity parentEntity;
	private Entity parentEntityMultiple;
	
	public PhotoField currentPictureField;
	private String photoPath;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        	Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
    		
    		this.startingIntent = getIntent();
    		this.breadcrumb = this.startingIntent.getStringExtra(getResources().getString(R.string.breadcrumb));
    		this.intentType = this.startingIntent.getIntExtra(getResources().getString(R.string.intentType),-1);
    		this.idmlId = this.startingIntent.getIntExtra(getResources().getString(R.string.idmlId),-1);
    		this.currInstanceNo = this.startingIntent.getIntExtra(getResources().getString(R.string.instanceNo),-1);
    		//this.numberOfInstances = this.startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances),-1);
    		this.parentFormScreenId = this.startingIntent.getStringExtra(getResources().getString(R.string.parentFormScreenId));;
    		this.fieldsNo = this.startingIntent.getExtras().size()-5;
    		
    		this.parentEntity = this.findParentEntity(this.getFormScreenId());
    		this.parentEntityMultiple = this.findParentEntity(this.parentFormScreenId);
    		
    		this.currentPictureField = null;
    		this.photoPath = null;
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
    					//this.parentEntity = this.findParentEntity(this.getFormScreenId());

        				Node<?> foundNode = this.parentEntity.get(nodeDef.getName(), this.currInstanceNo);
        				if (foundNode==null){
        					EntityBuilder.addEntity(this.parentEntity, ApplicationManager.getSurvey().getSchema().getDefinitionById(nodeDef.getId()).getName());
        				}
    				}
    				
    				EntityDefinition entityDef = (EntityDefinition)nodeDef;
    				//if (ApplicationManager.currentRecord.getRootEntity().getId()!=nodeDef.getId()){    					
        				//Log.e("multipleENTITY"+parentEntity.getName(),parentEntity.getIndex()+""+entityDef.getName()+parentEntity.getCount(entityDef.getName()));
        				for (int e=0;e<this.parentEntity.getCount(entityDef.getName());e++){
        					SummaryList summaryListView = new SummaryList(this, entityDef, calcNoOfCharsFitInOneLine(),
            						this,e);
            				summaryListView.setOnClickListener(this);
            				summaryListView.setId(nodeDef.getId());
            				this.ll.addView(summaryListView);	
        				}
    				/*} else {
    					SummaryList summaryListView = new SummaryList(this, entityDef, calcNoOfCharsFitInOneLine(),
        						this,0);
        				summaryListView.setOnClickListener(this);
        				summaryListView.setId(nodeDef.getId());
        				this.ll.addView(summaryListView);
    				}   */ 				  				
    			}else {
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
	    			} else if (nodeDef instanceof BooleanAttributeDefinition){
	    				loadedValue = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = this.parentEntity.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					BooleanValue boolValue = (BooleanValue)this.parentEntity.getValue(nodeDef.getName(), 0);
	        					if (boolValue!=null){
	        						if (boolValue.getValue()!=null)
	        							loadedValue = boolValue.getValue().toString();
	        					}
		    				}
	        				BooleanField boolField = new BooleanField(this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
	        				boolField.setOnClickListener(this);
	        				boolField.setId(nodeDef.getId());
	        				if (loadedValue.equals("")){
	        					boolField.setValue(0, null, FormScreen.this.getFormScreenId(),false);	
	        				} else {
	        					boolField.setValue(0, Boolean.valueOf(loadedValue), FormScreen.this.getFormScreenId(),false);	
	        				}	        				
	        				ApplicationManager.putUIElement(boolField.getId(), boolField);
	        				this.ll.addView(boolField);
	    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = this.parentEntityMultiple.get(nodeDef.getName(), this.currInstanceNo);
	    					if (foundNode!=null){
	    						BooleanValue boolValue = (BooleanValue)this.parentEntityMultiple.getValue(nodeDef.getName(), this.currInstanceNo);
		    					if (boolValue!=null){
	        						if (boolValue.getValue()!=null)
	        							loadedValue = boolValue.getValue().toString();
	        					}
		    				}
	    					BooleanField boolField = new BooleanField(this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
	    					boolField.setOnClickListener(this);
	    					boolField.setId(nodeDef.getId());
	    					if (loadedValue.equals("")){
	    						boolField.setValue(this.currInstanceNo, null, this.parentFormScreenId,false);
	    					} else {
	    						boolField.setValue(this.currInstanceNo, Boolean.valueOf(loadedValue), this.parentFormScreenId,false);
	    					}
	        				ApplicationManager.putUIElement(boolField.getId(), boolField);
	        				this.ll.addView(boolField);
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
	    				String loadedValueLon = "";
	    				String loadedValueLat = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = this.parentEntity.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					Coordinate coordValue = (Coordinate)this.parentEntity.getValue(nodeDef.getName(), 0);
		    					if (coordValue!=null){
		    						if (coordValue.getX()!=null)
		    							loadedValueLon = coordValue.getX().toString();
		    						if (coordValue.getY()!=null)
		    							loadedValueLat = coordValue.getY().toString();
		    					}	    				
		    				}
	        				final CoordinateField coordField= new CoordinateField(this, nodeDef);
	        				coordField.setOnClickListener(this);
	        				coordField.setId(nodeDef.getId());
	        				coordField.setValue(0, loadedValueLon, loadedValueLat, FormScreen.this.getFormScreenId(),false);
	        				ApplicationManager.putUIElement(coordField.getId(), coordField);
	        				this.ll.addView(coordField);
	    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = this.parentEntityMultiple.get(nodeDef.getName(), this.currInstanceNo);
		    				if (foundNode!=null){
		    					Coordinate coordValue = (Coordinate)this.parentEntityMultiple.getValue(nodeDef.getName(), this.currInstanceNo);
		    					if (coordValue!=null){
		    						if (coordValue.getX()!=null)
		    							loadedValueLon = coordValue.getX().toString();
		    						if (coordValue.getY()!=null)
		    							loadedValueLat = coordValue.getY().toString();
		    					}   				
		    				}
	        				final CoordinateField coordField= new CoordinateField(this, nodeDef);
	        				coordField.setOnClickListener(this);
	        				coordField.setId(nodeDef.getId());
	        				coordField.setValue(this.currInstanceNo, loadedValueLon, loadedValueLat, this.parentFormScreenId,false);
	        				ApplicationManager.putUIElement(coordField.getId(), coordField);
	        				this.ll.addView(coordField);
	    				} else {//multiple attribute summary    			    		
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntity, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
        				}
	    			} else if (nodeDef instanceof RangeAttributeDefinition){
	    				loadedValue = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = this.parentEntity.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					Range rangeValue = (Range)this.parentEntity.getValue(nodeDef.getName(), 0);
		    					if (rangeValue!=null){
		    						if (rangeValue.getMinimum()==null && rangeValue.getMaximum()==null){
		    							loadedValue = "";
		    						} else if (rangeValue.getMinimum()==null){
		    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getMaximum();
		    						} else if (rangeValue.getMaximum()==null){
		    							loadedValue = rangeValue.getMinimum()+getResources().getString(R.string.rangeSeparator);
		    						} else {
		    							loadedValue = rangeValue.getMinimum()+getResources().getString(R.string.rangeSeparator)+rangeValue.getMaximum();
		    						}		    						
		    					}				
		    				}
	        				final RangeField rangeField= new RangeField(this, nodeDef);
	        				rangeField.setOnClickListener(this);
	        				rangeField.setId(nodeDef.getId());
	        				rangeField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
	        				rangeField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	rangeField.setValue(0, s.toString(), FormScreen.this.parentFormScreenId,true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(rangeField.getId(), rangeField);
	        				this.ll.addView(rangeField);
	    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = this.parentEntityMultiple.get(nodeDef.getName(), this.currInstanceNo);
		    				if (foundNode!=null){
		    					Range rangeValue = (Range)this.parentEntityMultiple.getValue(nodeDef.getName(), this.currInstanceNo);
		    					if (rangeValue!=null){
		    						if (rangeValue.getMinimum()==null && rangeValue.getMaximum()==null){
		    							loadedValue = "";
		    						} else if (rangeValue.getMinimum()==null){
		    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getMaximum();
		    						} else if (rangeValue.getMaximum()==null){
		    							loadedValue = rangeValue.getMinimum()+getResources().getString(R.string.rangeSeparator);
		    						} else {
		    							loadedValue = rangeValue.getMinimum()+getResources().getString(R.string.rangeSeparator)+rangeValue.getMaximum();
		    						}		    						
		    					}    				
		    				}
	        				final RangeField rangeField= new RangeField(this, nodeDef);
	        				rangeField.setOnClickListener(this);
	        				rangeField.setId(nodeDef.getId());
	        				rangeField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
	        				rangeField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	rangeField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(rangeField.getId(), rangeField);
	        				this.ll.addView(rangeField);
	    				} else {//multiple attribute summary    			    		
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntity, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
        				}
	    			} else if (nodeDef instanceof DateAttributeDefinition){
	    				loadedValue = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = this.parentEntity.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					Date dateValue = (Date)this.parentEntity.getValue(nodeDef.getName(), 0);
		    					if (dateValue!=null){
		    						if (dateValue.getMonth()==null && dateValue.getDay()==null && dateValue.getYear()==null){
		    							loadedValue = "";
		    						} else if (dateValue.getMonth()==null && dateValue.getDay()==null){
		    							loadedValue = getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+dateValue.getYear();		    							
		    						} else if (dateValue.getMonth()==null && dateValue.getYear()==null){
		    							loadedValue = getResources().getString(R.string.dateSeparator)+dateValue.getDay()+getResources().getString(R.string.dateSeparator)+dateValue.getYear();
		    						} else if (dateValue.getDay()==null && dateValue.getYear()==null){
		    							loadedValue = dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator);
		    						} else if (dateValue.getMonth()==null){
		    							loadedValue = getResources().getString(R.string.dateSeparator)+dateValue.getDay()+getResources().getString(R.string.dateSeparator)+dateValue.getYear();		    							
		    						} else if (dateValue.getDay()==null){
		    							loadedValue = dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+dateValue.getYear();
		    						} else if (dateValue.getYear()==null){
		    							loadedValue = dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay()+getResources().getString(R.string.dateSeparator);
		    						} else {
		    							loadedValue = dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay()+getResources().getString(R.string.dateSeparator)+dateValue.getYear();
		    						}
		    					}
		    				}

	        				final DateField dateField= new DateField(this, nodeDef);
	        				dateField.setOnClickListener(this);
	        				dateField.setId(nodeDef.getId());
	        				dateField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
	        				dateField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	dateField.setValue(0, s.toString(), FormScreen.this.getFormScreenId(),true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(dateField.getId(), dateField);
	        				this.ll.addView(dateField);
	    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = this.parentEntityMultiple.get(nodeDef.getName(), this.currInstanceNo);
		    				if (foundNode!=null){
		    					Date dateValue = (Date)this.parentEntity.getValue(nodeDef.getName(), 0);
		    					if (dateValue!=null){
		    						if (dateValue.getMonth()==null && dateValue.getDay()==null && dateValue.getYear()==null){
		    							loadedValue = "";
		    						} else if (dateValue.getMonth()==null && dateValue.getDay()==null){
		    							loadedValue = getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+dateValue.getYear();		    							
		    						} else if (dateValue.getMonth()==null && dateValue.getYear()==null){
		    							loadedValue = getResources().getString(R.string.dateSeparator)+dateValue.getDay()+getResources().getString(R.string.dateSeparator)+dateValue.getYear();
		    						} else if (dateValue.getDay()==null && dateValue.getYear()==null){
		    							loadedValue = dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator);
		    						} else if (dateValue.getMonth()==null){
		    							loadedValue = getResources().getString(R.string.dateSeparator)+dateValue.getDay()+getResources().getString(R.string.dateSeparator)+dateValue.getYear();		    							
		    						} else if (dateValue.getDay()==null){
		    							loadedValue = dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+dateValue.getYear();
		    						} else if (dateValue.getYear()==null){
		    							loadedValue = dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay()+getResources().getString(R.string.dateSeparator);
		    						} else {
		    							loadedValue = dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay()+getResources().getString(R.string.dateSeparator)+dateValue.getYear();
		    						}
		    					}
		    				}
	        				final DateField dateField= new DateField(this, nodeDef);
	        				dateField.setOnClickListener(this);
	        				dateField.setId(nodeDef.getId());
	        				dateField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
	        				dateField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	dateField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(dateField.getId(), dateField);
	        				this.ll.addView(dateField);
	    				}
	    			} else if (nodeDef instanceof TimeAttributeDefinition){
	    				loadedValue = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = this.parentEntity.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					Time timeValue = (Time)this.parentEntity.getValue(nodeDef.getName(), 0);
		    					if (timeValue!=null){
		    						if (timeValue.getHour()==null && timeValue.getMinute()==null){
		    							loadedValue = "";
		    						} else if (timeValue.getHour()==null){
		    							loadedValue = getResources().getString(R.string.rangeSeparator)+timeValue.getMinute();
		    						} else if (timeValue.getMinute()==null){
		    							loadedValue = timeValue.getHour()+getResources().getString(R.string.rangeSeparator);
		    						} else {
		    							loadedValue = timeValue.getHour()+getResources().getString(R.string.rangeSeparator)+timeValue.getMinute();
		    						}		    						
		    					}	    				
		    				}
	        				final TimeField timeField= new TimeField(this, nodeDef);
	        				timeField.setOnClickListener(this);
	        				timeField.setId(nodeDef.getId());
	        				timeField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
	        				timeField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	timeField.setValue(0, s.toString(), FormScreen.this.parentFormScreenId,true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(timeField.getId(), timeField);
	        				this.ll.addView(timeField);
	    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = this.parentEntityMultiple.get(nodeDef.getName(), this.currInstanceNo);
		    				if (foundNode!=null){
		    					Time timeValue = (Time)this.parentEntityMultiple.getValue(nodeDef.getName(), this.currInstanceNo);
		    					if (timeValue!=null){
		    						if (timeValue.getHour()==null && timeValue.getMinute()==null){
		    							loadedValue = "";
		    						} else if (timeValue.getHour()==null){
		    							loadedValue = getResources().getString(R.string.rangeSeparator)+timeValue.getMinute();
		    						} else if (timeValue.getMinute()==null){
		    							loadedValue = timeValue.getHour()+getResources().getString(R.string.rangeSeparator);
		    						} else {
		    							loadedValue = timeValue.getHour()+getResources().getString(R.string.rangeSeparator)+timeValue.getMinute();
		    						}		    						
		    					}	   				
		    				}
	        				final TimeField timeField= new TimeField(this, nodeDef);
	        				timeField.setOnClickListener(this);
	        				timeField.setId(nodeDef.getId());
	        				timeField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
	        				timeField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	timeField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(timeField.getId(), timeField);
	        				this.ll.addView(timeField);
	    				} else {//multiple attribute summary    			    		
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntity, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
        				}
	    			} else if (nodeDef instanceof TaxonAttributeDefinition){
	    				TaxonAttributeDefinition taxonAttrDef = (TaxonAttributeDefinition)nodeDef;
	    				ArrayList<String> options = new ArrayList<String>();
	    				ArrayList<String> codes = new ArrayList<String>();
	    				options.add("");
	    				codes.add("null");
	    				/*List<CodeListItem> codeListItemsList = taxonAttrDef.getTaxonomy().getItems();
	    				for (CodeListItem codeListItem : codeListItemsList){
	    					codes.add(codeListItem.getCode());
	    					options.add(codeListItem.getLabel(null));
	    				}*/
	    				
	    				loadedValue = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = this.parentEntity.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					Taxon taxonValue = (Taxon)this.parentEntity.getValue(nodeDef.getName(), 0);
		    					if (taxonValue!=null){
		    						//TBI!!!	
		    					}	    				
		    				}
	        				final TaxonField taxonField= new TaxonField(this, nodeDef, codes, options, null);
	        				taxonField.setOnClickListener(this);
	        				taxonField.setId(nodeDef.getId());
	        				//taxonField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
	        				ApplicationManager.putUIElement(taxonField.getId(), taxonField);
	        				this.ll.addView(taxonField);
	    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = this.parentEntityMultiple.get(nodeDef.getName(), this.currInstanceNo);
		    				if (foundNode!=null){
		    					Taxon taxonValue = (Taxon)this.parentEntityMultiple.getValue(nodeDef.getName(), this.currInstanceNo);
		    					if (taxonValue!=null){
		    								    						
		    					}	   				
		    				}
		    				final TaxonField taxonField= new TaxonField(this, nodeDef, codes, options, null);
		    				taxonField.setOnClickListener(this);
		    				taxonField.setId(nodeDef.getId());
		    				//taxonField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
	        				ApplicationManager.putUIElement(taxonField.getId(), taxonField);
	        				this.ll.addView(taxonField);
	    				} else {//multiple attribute summary    			    		
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntity, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
        				}
					} else if (nodeDef instanceof FileAttributeDefinition){
						FileAttributeDefinition fileDef = (FileAttributeDefinition)nodeDef;
						List<String> extensionsList = fileDef.getExtensions();
						
						if (extensionsList.contains("jpg")||extensionsList.contains("jpeg")){
							loadedValue = "";
		    				if (!nodeDef.isMultiple()){
		        				final PhotoField photoField= new PhotoField(this, nodeDef);
		        				if (this.currentPictureField!=null){
		    		    			photoField.setValue(0, this.photoPath, FormScreen.this.getFormScreenId(),false);
		    		    			this.currentPictureField = null;
		    		    			this.photoPath = null;
		    		    		}
		        				Node<?> foundNode = this.parentEntity.get(nodeDef.getName(), 0);
			    				if (foundNode!=null){
			    					File fileValue = (File)this.parentEntity.getValue(nodeDef.getName(), 0);
			    					if (fileValue!=null){
			    						loadedValue = fileValue.getFilename();
			    					}
			    				}
		        				photoField.setOnClickListener(this);
		        				photoField.setId(nodeDef.getId());
		        				photoField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
		        				ApplicationManager.putUIElement(photoField.getId(), photoField);
		        				this.ll.addView(photoField);
		    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
		        				final PhotoField photoField= new PhotoField(this, nodeDef);
		        				if (this.currentPictureField!=null){
		        					photoField.setValue(this.currInstanceNo, this.photoPath, this.parentFormScreenId,false);
		    		    			this.currentPictureField = null;
		    		    			this.photoPath = null;
		    		    		}
		        				Node<?> foundNode = this.parentEntityMultiple.get(nodeDef.getName(), this.currInstanceNo);
			    				if (foundNode!=null){
			    					File fileValue = (File)this.parentEntityMultiple.getValue(nodeDef.getName(), this.currInstanceNo);
			    					if (fileValue!=null){
			    						loadedValue = fileValue.getFilename();
			    					}
			    				}
		        				photoField.setOnClickListener(this);
		        				photoField.setId(nodeDef.getId());
		        				photoField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
		        				ApplicationManager.putUIElement(photoField.getId(), photoField);
		        				this.ll.addView(photoField);
		    				} else {//multiple attribute summary    			    		
	    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntity, this);
	        					summaryTableView.setOnClickListener(this);
	            				summaryTableView.setId(nodeDef.getId());
	            				this.ll.addView(summaryTableView);
	        				}
						}
					}
    			}    				
    		}
			if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
				//Log.e("multiple","ATTRIBUTE");
				this.ll.addView(arrangeButtonsInLine(new Button(this),getResources().getString(R.string.previousInstanceButton),new Button(this),getResources().getString(R.string.nextInstanceButton),this, false));
			} else if (this.intentType==getResources().getInteger(R.integer.multipleEntityIntent)){ 				
				if (ApplicationManager.currentRecord.getRootEntity().getId()!=this.idmlId){
					//Log.e("multiple","ENTITY");
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
				if (tempView instanceof BooleanField){
					BooleanField tempBooleanField = (BooleanField)tempView;
					tempBooleanField.setChoiceLabelsTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
				} else if (tempView instanceof TaxonField){
					TaxonField tempTaxonField = (TaxonField)tempView;
					tempTaxonField.setFieldsLabelsTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
				}
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
			for (int m=1;m<entityPath.length;m++){
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
					}  else if (nodeDef instanceof BooleanAttributeDefinition){
						String loadedValue = "";
						BooleanValue boolValue = (BooleanValue)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
						if (boolValue!=null)
							loadedValue = boolValue.getValue().toString();
						BooleanField boolField = (BooleanField) ApplicationManager.getUIElement(nodeDef.getId());
						if (boolField!=null){
							if (loadedValue.equals("")){
								boolField.setValue(0, null, this.getFormScreenId(), false);
							} else {
								boolField.setValue(0, Boolean.valueOf(loadedValue), this.getFormScreenId(), false);
							}
						}					
					} else if (nodeDef instanceof CodeAttributeDefinition){
						String loadedValue = "";
						Code codeValue = (Code)parentEntity.getValue(nodeDef.getName(), 0);
						if (codeValue!=null)
							loadedValue = codeValue.getCode();
						CodeField codeField = (CodeField) ApplicationManager.getUIElement(nodeDef.getId());
						if (codeField!=null)
							codeField.setValue(0, loadedValue, this.getFormScreenId(), false);
					} else if (nodeDef instanceof CoordinateAttributeDefinition){
						String loadedValueLat = "";
						String loadedValueLon = "";
						Coordinate coordValue = (Coordinate)parentEntity.getValue(nodeDef.getName(), 0);
						if (coordValue!=null){
							loadedValueLon = coordValue.getX().toString();
							loadedValueLat = coordValue.getY().toString();
						}
							
						CoordinateField coordField = (CoordinateField) ApplicationManager.getUIElement(nodeDef.getId());
						if (coordField!=null)
							coordField.setValue(0, loadedValueLon, loadedValueLat, this.getFormScreenId(), false);
					} else if (nodeDef instanceof RangeAttributeDefinition){
						
					} else if (nodeDef instanceof DateAttributeDefinition){
						
					} else if (nodeDef instanceof TimeAttributeDefinition){
						
					} else if (nodeDef instanceof TaxonAttributeDefinition){
						
					}
				}
			}
		} else{//parentEntity is null, because there is no entity added with this instance number in current record
			String path = this.getFormScreenId().substring(0,this.getFormScreenId().lastIndexOf(getResources().getString(R.string.valuesSeparator2)));
			parentEntity = this.findParentEntity(path);
			EntityBuilder.addEntity(parentEntity, ApplicationManager.getSurvey().getSchema().getDefinitionById(this.idmlId).getName());
			parentEntity = this.findParentEntity(this.getFormScreenId());
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
					} else if (nodeDef instanceof BooleanAttributeDefinition){
						String loadedValue = "";
						BooleanValue boolValue = (BooleanValue)parentEntity.getValue(nodeDef.getName(), 0);
						if (boolValue!=null)
							loadedValue = boolValue.getValue().toString();
						BooleanField boolField = (BooleanField) ApplicationManager.getUIElement(nodeDef.getId());
						if (boolField!=null){
							if (loadedValue.equals("")){
								boolField.setValue(0, null, this.getFormScreenId(), false);
							} else {
								boolField.setValue(0, Boolean.valueOf(loadedValue), this.getFormScreenId(), false);
							}
						}					
					} else if (nodeDef instanceof CodeAttributeDefinition){
						String loadedValue = "";
						Code codeValue = (Code)parentEntity.getValue(nodeDef.getName(), 0);
						if (codeValue!=null)
							if (codeValue.getCode()!=null)
								loadedValue = codeValue.getCode();
						CodeField codeField = (CodeField) ApplicationManager.getUIElement(nodeDef.getId());
						if (codeField!=null)
							codeField.setValue(0, loadedValue, this.getFormScreenId(), false);
					} else if (nodeDef instanceof CoordinateAttributeDefinition){
						String loadedValueLat = "";
						String loadedValueLon = "";
						Coordinate coordValue = (Coordinate)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
						if (coordValue!=null){
							if (coordValue.getX()!=null)
								loadedValueLon = coordValue.getX().toString();
							if (coordValue.getY()!=null)
								loadedValueLat = coordValue.getY().toString();
						}							
						CoordinateField coordField = (CoordinateField) ApplicationManager.getUIElement(nodeDef.getId());
						if (coordField!=null)
							coordField.setValue(0, loadedValueLon, loadedValueLat, this.getFormScreenId(), false);
					} else if (nodeDef instanceof RangeAttributeDefinition){
						
					} else if (nodeDef instanceof DateAttributeDefinition){
						
					} else if (nodeDef instanceof TimeAttributeDefinition){
						
					} else if (nodeDef instanceof TaxonAttributeDefinition){
						
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
				} else if (nodeDef instanceof BooleanAttributeDefinition){
					String loadedValue = "";
					BooleanValue boolValue = (BooleanValue)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
					if (boolValue!=null)
						loadedValue = boolValue.getValue().toString();
					BooleanField boolField = (BooleanField) ApplicationManager.getUIElement(nodeDef.getId());
					if (boolField!=null){
						if (loadedValue.equals("")){
							boolField.setValue(this.currInstanceNo, null, this.getFormScreenId(), false);
						} else {
							boolField.setValue(this.currInstanceNo, Boolean.valueOf(loadedValue), this.getFormScreenId(), false);
						}
					}					
				} else if (nodeDef instanceof CodeAttributeDefinition){
					String loadedValue = "";
					Code codeValue = (Code)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
					if (codeValue!=null)
						loadedValue = codeValue.getCode();
					CodeField codeField = (CodeField) ApplicationManager.getUIElement(nodeDef.getId());
					if (codeField!=null)
						codeField.setValue(this.currInstanceNo, loadedValue, this.getFormScreenId(), false);
				} else if (nodeDef instanceof CoordinateAttributeDefinition){
					String loadedValueLat = "";
					String loadedValueLon = "";
					Coordinate coordValue = (Coordinate)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
					if (coordValue!=null){
						loadedValueLon = coordValue.getX().toString();
						loadedValueLat = coordValue.getY().toString();
					}						
					CoordinateField coordField = (CoordinateField) ApplicationManager.getUIElement(nodeDef.getId());
					if (coordField!=null)
						coordField.setValue(this.currInstanceNo, loadedValueLon, loadedValueLat, this.getFormScreenId(), false);
				} else if (nodeDef instanceof RangeAttributeDefinition){
					
				} else if (nodeDef instanceof DateAttributeDefinition){
					
				} else if (nodeDef instanceof TimeAttributeDefinition){
					
				} else if (nodeDef instanceof TaxonAttributeDefinition){
					
				}
			}
		}
	}
	
    public void startCamera(/*Context context, */PhotoField photoField){
		Intent cameraIntent = new Intent(this/*context*/, CameraScreen.class); 
		this.startActivityForResult(cameraIntent,getResources().getInteger(R.integer.cameraStarted));
		//this.currPhotoField = fileField;
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {    	
	    super.onActivityResult(requestCode, resultCode, data);
	    try{
	    	//Log.e("request="+requestCode,"result="+resultCode);
	 	    if (requestCode==getResources().getInteger(R.integer.cameraStarted)){
	 	    	if (resultCode==getResources().getInteger(R.integer.photoTaken)){
	 	    		photoPath = data.getStringExtra(getResources().getString(R.string.photoPath));
	 	    		Log.e("photopath","=="+photoPath);
	 	    	}
	 	    }
	    } catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onActivityResult",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
    	}	   
    }
}