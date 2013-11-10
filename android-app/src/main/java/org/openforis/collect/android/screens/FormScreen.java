package org.openforis.collect.android.screens;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.fields.BooleanField;
import org.openforis.collect.android.fields.CodeField;
import org.openforis.collect.android.fields.CoordinateField;
import org.openforis.collect.android.fields.DateField;
import org.openforis.collect.android.fields.EntityLink;
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
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.GpsActivity;
import org.openforis.collect.android.misc.LayoutTouchListener;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.android.misc.ScrollViewSwipeDetector;
import org.openforis.collect.android.misc.ViewBacktrack;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.collect.manager.CodeListManager;
import org.openforis.idm.metamodel.BooleanAttributeDefinition;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.CodeList;
import org.openforis.idm.metamodel.CodeListItem;
import org.openforis.idm.metamodel.CoordinateAttributeDefinition;
import org.openforis.idm.metamodel.DateAttributeDefinition;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.FileAttributeDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
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
import org.openforis.idm.model.IntegerRange;
import org.openforis.idm.model.IntegerValue;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.RealRange;
import org.openforis.idm.model.RealValue;
import org.openforis.idm.model.TaxonOccurrence;
import org.openforis.idm.model.TextValue;
import org.openforis.idm.model.Time;

import android.content.DialogInterface;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class FormScreen extends BaseActivity implements OnClickListener {
	
	private static final String TAG = "FormScreen";

	private ScrollView sv;			
    private LinearLayout ll;
	
	private Intent startingIntent;
	private String parentFormScreenId;
	private String breadcrumb;
	private String screenTitle;
	private int intentType;
	private int fieldsNo;
	private int idmlId;
	public int currInstanceNo;
	
	public Entity parentEntity;
	public Entity parentEntitySingleAttribute;
	public Entity parentEntityMultipleAttribute;
	public PhotoField currentPictureField;
	public CoordinateField currentCoordinateField;
	private String photoPath;
	private String latitude;
	private String longitude;
	
	private ScrollViewSwipeDetector onTouchListener;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        	Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
    		
        	ApplicationManager.formScreenActivityList.add(this);
        	
    		this.startingIntent = getIntent();
    		this.breadcrumb = this.startingIntent.getStringExtra(getResources().getString(R.string.breadcrumb));
    		this.screenTitle = this.startingIntent.getStringExtra(getResources().getString(R.string.screenTitle));
    		this.intentType = this.startingIntent.getIntExtra(getResources().getString(R.string.intentType),-1);
    		this.idmlId = this.startingIntent.getIntExtra(getResources().getString(R.string.idmlId),-1);
    		this.currInstanceNo = this.startingIntent.getIntExtra(getResources().getString(R.string.instanceNo),-1);
    		Log.e("cureentINstance","=="+this.currInstanceNo);
    		//this.numberOfInstances = this.startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances),-1);
    		this.parentFormScreenId = this.startingIntent.getStringExtra(getResources().getString(R.string.parentFormScreenId));;
    		this.fieldsNo = this.startingIntent.getExtras().size()-5;
    		//this.parentEntitySingleAttribute = this.findParentEntity(this.getFormScreenId());
    		//this.parentEntityMultipleAttribute = this.findParentEntity(this.parentFormScreenId);

    		this.currentPictureField = null;
    		this.currentCoordinateField = null;
    		this.photoPath = null;
    		this.latitude = null;
    		this.longitude = null;
    		
    		/*final GestureDetector gestureDetector = new GestureDetector(this, new SwipeDetector());
    		View.OnTouchListener gestureListener = new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                	Log.e("onTouch","=========");
                    return gestureDetector.onTouchEvent(event);
                }
            };
            //this.ll.setOnTouchListener(gestureListener);*/
    		//this.onTouchListener = new ScrollViewSwipeDetector(this);
            
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
		long startTime = System.currentTimeMillis();
		try{
			FormScreen.this.parentEntitySingleAttribute = FormScreen.this.findParentEntity(FormScreen.this.getFormScreenId());
			FormScreen.this.parentEntityMultipleAttribute = FormScreen.this.findParentEntity(FormScreen.this.parentFormScreenId);
			String loadedValue = "";
	
			ArrayList<String> tableColHeaders = new ArrayList<String>();
			tableColHeaders.add("Value");
			
			FormScreen.this.sv = new ScrollView(FormScreen.this);
			FormScreen.this.ll = new LinearLayout(FormScreen.this);
			FormScreen.this.ll.setOrientation(android.widget.LinearLayout.VERTICAL);
			//this.ll.setOnTouchListener(new ScrollViewSwipeDetector(this));
			FormScreen.this.sv.addView(ll);
			//this.sv.setOnTouchListener(new ScrollViewSwipeDetector(this));
	
			if (!FormScreen.this.breadcrumb.equals("")){				
				TextView breadcrumb = new TextView(FormScreen.this);
				if (FormScreen.this.intentType != getResources().getInteger(R.integer.singleEntityIntent)){
					if (FormScreen.this.intentType == getResources().getInteger(R.integer.multipleEntityIntent)){
						breadcrumb.setText(FormScreen.this.breadcrumb.substring(0, FormScreen.this.breadcrumb.lastIndexOf(" "))+" "+(FormScreen.this.currInstanceNo+1));	
					} else{
						breadcrumb.setText(FormScreen.this.breadcrumb+" "+(FormScreen.this.currInstanceNo+1));	
					}
				}
				else
					breadcrumb.setText(FormScreen.this.breadcrumb);
	    		breadcrumb.setTextSize(getResources().getInteger(R.integer.breadcrumbFontSize));
	    		breadcrumb.setSingleLine();
	    		HorizontalScrollView scroller = new HorizontalScrollView(FormScreen.this);
	    		scroller.addView(breadcrumb);
	    		FormScreen.this.ll.addView(scroller);
	    		//FormScreen.this.ll.addView(breadcrumb);
	    		FormScreen.this.ll.addView(ApplicationManager.getDividerLine(this));
	    		
	    		TextView screenTitle = new TextView(FormScreen.this);
	    		screenTitle.setText(FormScreen.this.screenTitle);
	    		screenTitle.setTextSize(getResources().getInteger(R.integer.screenTitleFontSize));
	    		FormScreen.this.ll.addView(screenTitle);
	    		FormScreen.this.ll.addView(ApplicationManager.getDividerLine(this));
			}
			
			for (int i=0;i<FormScreen.this.fieldsNo;i++){
				NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(FormScreen.this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+i, -1));
				if (nodeDef instanceof EntityDefinition){
					if (ApplicationManager.currentRecord.getRootEntity().getId()!=nodeDef.getId()){
	    				Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0/*FormScreen.this.currInstanceNo*/);
	    				if (foundNode==null){
	    					EntityBuilder.addEntity(FormScreen.this.parentEntitySingleAttribute, ApplicationManager.getSurvey().getSchema().getDefinitionById(nodeDef.getId()).getName(), 0);
	    				}
					}
					
					EntityDefinition entityDef = (EntityDefinition)nodeDef;
					//for (int e=0;e<FormScreen.this.parentEntitySingleAttribute.getCount(entityDef.getName());e++){
    					/*SummaryList summaryListView = new SummaryList(FormScreen.this, entityDef, calcNoOfCharsFitInOneLine(),
        						FormScreen.this,e);
        				summaryListView.setOnClickListener(FormScreen.this);
        				summaryListView.setId(nodeDef.getId());
        				FormScreen.this.ll.addView(summaryListView);*/
						EntityLink entityLinkView = new EntityLink(FormScreen.this, entityDef, calcNoOfCharsFitInOneLine(),
        						FormScreen.this);
						entityLinkView.setOnClickListener(FormScreen.this);
        				entityLinkView.setId(nodeDef.getId());
        				FormScreen.this.ll.addView(entityLinkView);
    				//}
				}else {					
					if (nodeDef instanceof TextAttributeDefinition){
	    				loadedValue = "";	    				
	
	    				if (((TextAttributeDefinition) nodeDef).getType().toString().toLowerCase().equals("short")){
		    				if (!nodeDef.isMultiple()){
		    					Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
			    				if (foundNode!=null){
			    					TextValue textValue = (TextValue)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
			    					if (textValue!=null)
			    						loadedValue = textValue.getValue();	    				
			    				}
		        				final TextField textField= new TextField(FormScreen.this, nodeDef);
		        				textField.setOnClickListener(FormScreen.this);
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
		        				FormScreen.this.ll.addView(textField);
		    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
		    					Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
			    				if (foundNode!=null){
			    					TextValue textValue = (TextValue)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
			    					if (textValue!=null)
			    						loadedValue = textValue.getValue();	    				
			    				}
		        				final TextField textField= new TextField(FormScreen.this, nodeDef);
		        				textField.setOnClickListener(FormScreen.this);
		        				textField.setId(nodeDef.getId());
		        				textField.setValue(FormScreen.this.currInstanceNo, loadedValue, FormScreen.this.parentFormScreenId,false);
		        				textField.addTextChangedListener(new TextWatcher(){
		        			        public void afterTextChanged(Editable s) {        			            
		        			        	textField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
		        			        }
		        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
		        			    });
		        				ApplicationManager.putUIElement(textField.getId(), textField);
		        				FormScreen.this.ll.addView(textField);
		    				} else {//multiple attribute summary    			    		
	    						SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
	        					summaryTableView.setOnClickListener(FormScreen.this);
	            				summaryTableView.setId(nodeDef.getId());
	            				FormScreen.this.ll.addView(summaryTableView);
	        				}
	    				} else {//memo field
	    					if (!nodeDef.isMultiple()){
		    					Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
			    				if (foundNode!=null){
			    					TextValue textValue = (TextValue)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
			    					if (textValue!=null)
			    						loadedValue = textValue.getValue();	    				
			    				}
		        				final MemoField memoField= new MemoField(FormScreen.this, nodeDef);
		        				memoField.setOnClickListener(FormScreen.this);
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
		        				FormScreen.this.ll.addView(memoField);
		    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
		    					Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
			    				if (foundNode!=null){
			    					TextValue textValue = (TextValue)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
			    					if (textValue!=null)
			    						loadedValue = textValue.getValue();	    				
			    				}
		        				final MemoField memoField= new MemoField(FormScreen.this, nodeDef);
		        				memoField.setOnClickListener(FormScreen.this);
		        				memoField.setId(nodeDef.getId());
		        				memoField.setValue(FormScreen.this.currInstanceNo, loadedValue, FormScreen.this.parentFormScreenId,false);
		        				memoField.addTextChangedListener(new TextWatcher(){
		        			        public void afterTextChanged(Editable s) {        			            
		        			        	memoField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
		        			        }
		        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
		        			    });
		        				ApplicationManager.putUIElement(memoField.getId(), memoField);
		        				FormScreen.this.ll.addView(memoField);
		    				} else {//multiple attribute summary    			    		
	    						SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
	        					summaryTableView.setOnClickListener(FormScreen.this);
	            				summaryTableView.setId(nodeDef.getId());
	            				FormScreen.this.ll.addView(summaryTableView);
	        				}
	    				}
	    			} else if (nodeDef instanceof NumberAttributeDefinition){
	    				loadedValue = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					if (((NumberAttributeDefinition) nodeDef).isInteger()){
		    						IntegerValue intValue = (IntegerValue)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    						if (intValue!=null)
			    						loadedValue = intValue.getValue().toString();
		    					} else {
		    						RealValue realValue = (RealValue)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    						if (realValue!=null)
			    						loadedValue = realValue.getValue().toString();
		    					}
		    				}
	        				final NumberField numberField= new NumberField(FormScreen.this, nodeDef);
	        				numberField.setOnClickListener(FormScreen.this);
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
	        				FormScreen.this.ll.addView(numberField);
	    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    				if (foundNode!=null){
		    					if (((NumberAttributeDefinition) nodeDef).isInteger()){
		    						IntegerValue intValue = (IntegerValue)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    						if (intValue!=null)
			    						loadedValue = intValue.getValue().toString();
		    					} else {
		    						RealValue realValue = (RealValue)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    						if (realValue!=null)
			    						loadedValue = realValue.getValue().toString();
		    					}
		    				}
	        				final NumberField numberField= new NumberField(FormScreen.this, nodeDef);
	        				numberField.setOnClickListener(FormScreen.this);
	        				numberField.setId(nodeDef.getId());
	        				numberField.setValue(FormScreen.this.currInstanceNo, loadedValue, FormScreen.this.parentFormScreenId,false);
	        				numberField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	numberField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(numberField.getId(), numberField);
	        				FormScreen.this.ll.addView(numberField);
	    				} else {//multiple attribute summary    			    		
							SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
	    					summaryTableView.setOnClickListener(FormScreen.this);
	        				summaryTableView.setId(nodeDef.getId());
	        				FormScreen.this.ll.addView(summaryTableView);
	    				}
	    			} else if (nodeDef instanceof BooleanAttributeDefinition){
	    				loadedValue = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					BooleanValue boolValue = (BooleanValue)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	        					if (boolValue!=null){
	        						if (boolValue.getValue()!=null)
	        							loadedValue = boolValue.getValue().toString();
	        					}
		    				}
	        				BooleanField boolField = new BooleanField(FormScreen.this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
	        				boolField.setOnClickListener(FormScreen.this);
	        				boolField.setId(nodeDef.getId());
	        				if (loadedValue.equals("")){
	        					boolField.setValue(0, null, FormScreen.this.getFormScreenId(),false);	
	        				} else {
	        					boolField.setValue(0, Boolean.valueOf(loadedValue), FormScreen.this.getFormScreenId(),false);	
	        				}	        				
	        				ApplicationManager.putUIElement(boolField.getId(), boolField);
	        				FormScreen.this.ll.addView(boolField);
	    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
	    					if (foundNode!=null){
	    						BooleanValue boolValue = (BooleanValue)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    					if (boolValue!=null){
	        						if (boolValue.getValue()!=null)
	        							loadedValue = boolValue.getValue().toString();
	        					}
		    				}
	    					BooleanField boolField = new BooleanField(FormScreen.this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
	    					boolField.setOnClickListener(FormScreen.this);
	    					boolField.setId(nodeDef.getId());
	    					if (loadedValue.equals("")){
	    						boolField.setValue(FormScreen.this.currInstanceNo, null, FormScreen.this.parentFormScreenId,false);
	    					} else {
	    						boolField.setValue(FormScreen.this.currInstanceNo, Boolean.valueOf(loadedValue), FormScreen.this.parentFormScreenId,false);
	    					}
	        				ApplicationManager.putUIElement(boolField.getId(), boolField);
	        				FormScreen.this.ll.addView(boolField);
	    				} else {//multiple attribute summary    			    		
							SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
	    					summaryTableView.setOnClickListener(FormScreen.this);
	        				summaryTableView.setId(nodeDef.getId());
	        				FormScreen.this.ll.addView(summaryTableView);
	    				}
	    			} else if (nodeDef instanceof CodeAttributeDefinition){	    				
	    				loadedValue = "";
	    				//CodeAttributeDefinition codeAttrDef = (CodeAttributeDefinition)nodeDef;
	    				ArrayList<String> options = new ArrayList<String>();
	    				ArrayList<String> codes = new ArrayList<String>();
	    				options.add("");
	    				codes.add("null");
	    				/*CodeListManager codeListManager = ServiceFactory.getCodeListManager();
						CodeList list = codeAttrDef.getList();*/
						/*if ( ! list.isExternal() ) {
							List<CodeListItem> codeListItemsList = codeListManager.loadRootItems(list);
							for (CodeListItem codeListItem : codeListItemsList){
								codes.add(codeListItem.getCode());
								options.add(CodeField.getLabelForCodeListItem(codeListItem));
							}
						}*/
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					Code codeValue = (Code)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	        					if (codeValue!=null){
	        						loadedValue = codeValue.getCode();
	        					}
		    				}
	        				CodeField codeField = new CodeField(FormScreen.this, nodeDef, codes, options, null, FormScreen.this.getFormScreenId());
	        				codeField.setOnClickListener(FormScreen.this);
	        				codeField.setId(nodeDef.getId());
	        				codeField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
	        				ApplicationManager.putUIElement(codeField.getId(), codeField);
	        				FormScreen.this.ll.addView(codeField);
	    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
	    					if (foundNode!=null){
		    					Code codeValue = (Code)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
	        					if (codeValue!=null){
	        						loadedValue = codeValue.getCode();
	        					}
		    				}
	        				CodeField codeField = new CodeField(FormScreen.this, nodeDef, codes, options, null, FormScreen.this.parentFormScreenId);
	        				codeField.setOnClickListener(FormScreen.this);
	        				codeField.setId(nodeDef.getId());
	        				codeField.setValue(FormScreen.this.currInstanceNo, loadedValue, FormScreen.this.parentFormScreenId,false);
	        				ApplicationManager.putUIElement(codeField.getId(), codeField);
	        				FormScreen.this.ll.addView(codeField);
	    				} else {//multiple attribute summary    			    		
							SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
	    					summaryTableView.setOnClickListener(FormScreen.this);
	        				summaryTableView.setId(nodeDef.getId());
	        				FormScreen.this.ll.addView(summaryTableView);
	    				}
	    				
	    			} else if (nodeDef instanceof CoordinateAttributeDefinition){
	    				String loadedValueLon = "";
	    				String loadedValueLat = "";
	    				String loadedSrsId = "";
	    				if (!nodeDef.isMultiple()){
	        				final CoordinateField coordField= new CoordinateField(FormScreen.this, nodeDef);
	        				coordField.setId(nodeDef.getId());
	        				if (FormScreen.this.currentCoordinateField!=null && FormScreen.this.currentCoordinateField.getLabelText().equals(coordField.getLabelText())){
	        					if (FormScreen.this.longitude==null)
	        						FormScreen.this.longitude = "";
	        					if (FormScreen.this.latitude==null)
	        						FormScreen.this.latitude = "";
	        					String srsId = null;
	        					if (FormScreen.this.currentCoordinateField.srs!=null){						
	        						srsId = FormScreen.this.currentCoordinateField.srs.getId();
	        					}
	        					coordField.setValue(0, FormScreen.this.longitude, FormScreen.this.latitude, srsId, FormScreen.this.getFormScreenId(), false);
	    		    			FormScreen.this.currentCoordinateField = null;
	    		    			FormScreen.this.longitude = null;
	    		    			FormScreen.this.latitude = null;
	    		    		}
	    					Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					Coordinate coordValue = (Coordinate)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    					if (coordValue!=null){
		    						if (coordValue.getX()!=null)
		    							loadedValueLon = coordValue.getX().toString();
		    						if (coordValue.getY()!=null)
		    							loadedValueLat = coordValue.getY().toString();
		    						if (coordValue.getSrsId()!=null)
		    							loadedSrsId = coordValue.getSrsId().toString();
		    					}	    				
		    				}
		    				//coordField = new CoordinateField(FormScreen.this, nodeDef);
	        				coordField.setOnClickListener(FormScreen.this);
	        				coordField.setId(nodeDef.getId());
	        				coordField.setValue(0, loadedValueLon, loadedValueLat, loadedSrsId, FormScreen.this.getFormScreenId(),false);
	        				ApplicationManager.putUIElement(coordField.getId(), coordField);
	        				FormScreen.this.ll.addView(coordField);
	    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					final CoordinateField coordField= new CoordinateField(FormScreen.this, nodeDef);
	    					coordField.setId(nodeDef.getId());
	        				if (FormScreen.this.currentCoordinateField!=null && FormScreen.this.currentCoordinateField.getLabelText().equals(coordField.getLabelText())){
	        					if (FormScreen.this.longitude==null)
	        						FormScreen.this.longitude = "";
	        					if (FormScreen.this.latitude==null)
	        						FormScreen.this.latitude = "";
	        					String srsId = null;
	        					if (FormScreen.this.currentCoordinateField.srs!=null){						
	        						srsId = FormScreen.this.currentCoordinateField.srs.getId();
	        					}
	        					coordField.setValue(FormScreen.this.currInstanceNo, FormScreen.this.longitude, FormScreen.this.latitude, srsId, FormScreen.this.parentFormScreenId,false);
	    		    			FormScreen.this.currentCoordinateField = null;
	    		    			FormScreen.this.longitude = null;
	    		    			FormScreen.this.latitude = null;
	    		    		}
	    					Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    				if (foundNode!=null){
		    					Coordinate coordValue = (Coordinate)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    					if (coordValue!=null){
		    						if (coordValue.getX()!=null)
		    							loadedValueLon = coordValue.getX().toString();
		    						if (coordValue.getY()!=null)
		    							loadedValueLat = coordValue.getY().toString();
		    						if (coordValue.getSrsId()!=null)
		    							loadedSrsId = coordValue.getSrsId().toString();
		    					}   				
		    				}
	        				//coordField= new CoordinateField(FormScreen.this, nodeDef);
	        				coordField.setOnClickListener(FormScreen.this);
	        				coordField.setId(nodeDef.getId());
	        				coordField.setValue(FormScreen.this.currInstanceNo, loadedValueLon, loadedValueLat, loadedSrsId, FormScreen.this.parentFormScreenId,false);
	        				ApplicationManager.putUIElement(coordField.getId(), coordField);
	        				FormScreen.this.ll.addView(coordField);
	    				} else {//multiple attribute summary    			    		
							SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
	    					summaryTableView.setOnClickListener(FormScreen.this);
	        				summaryTableView.setId(nodeDef.getId());
	        				FormScreen.this.ll.addView(summaryTableView);
	    				}
	    			} else if (nodeDef instanceof RangeAttributeDefinition){
	    				loadedValue = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
		    					if (rangeAttrDef.isReal()){
		    						RealRange rangeValue = (RealRange)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    						if (rangeValue!=null){
			    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
			    							loadedValue = "";
			    						} else if (rangeValue.getFrom()==null){
			    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			    						} else if (rangeValue.getTo()==null){
			    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
			    						} else {
			    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			    						}		    						
			    					}	
		    					} else {
		    						IntegerRange rangeValue = (IntegerRange)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    						if (rangeValue!=null){
			    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
			    							loadedValue = "";
			    						} else if (rangeValue.getFrom()==null){
			    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			    						} else if (rangeValue.getTo()==null){
			    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
			    						} else {
			    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			    						}		    						
			    					}	
		    					}		    							
		    				}
	        				final RangeField rangeField= new RangeField(FormScreen.this, nodeDef);
	        				rangeField.setOnClickListener(FormScreen.this);
	        				rangeField.setId(nodeDef.getId());
	        				rangeField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
	        				rangeField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	rangeField.setValue(0, s.toString(),  FormScreen.this.getFormScreenId(),true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(rangeField.getId(), rangeField);
	        				FormScreen.this.ll.addView(rangeField);
	    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    				if (foundNode!=null){
		    					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
		    					if (rangeAttrDef.isReal()){
		    						RealRange rangeValue = (RealRange)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    						if (rangeValue!=null){
			    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
			    							loadedValue = "";
			    						} else if (rangeValue.getFrom()==null){
			    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			    						} else if (rangeValue.getTo()==null){
			    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
			    						} else {
			    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			    						}		    						
			    					}	
		    					} else {
		    						IntegerRange rangeValue = (IntegerRange)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    						if (rangeValue!=null){
			    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
			    							loadedValue = "";
			    						} else if (rangeValue.getFrom()==null){
			    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			    						} else if (rangeValue.getTo()==null){
			    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
			    						} else {
			    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			    						}		    						
			    					}	
		    					}  				
		    				}
	        				final RangeField rangeField= new RangeField(FormScreen.this, nodeDef);
	        				rangeField.setOnClickListener(FormScreen.this);
	        				rangeField.setId(nodeDef.getId());
	        				rangeField.setValue(FormScreen.this.currInstanceNo, loadedValue, FormScreen.this.parentFormScreenId,false);
	        				rangeField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	rangeField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(rangeField.getId(), rangeField);
	        				FormScreen.this.ll.addView(rangeField);
	    				} else {//multiple attribute summary    			    		
							SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
	    					summaryTableView.setOnClickListener(FormScreen.this);
	        				summaryTableView.setId(nodeDef.getId());
	        				FormScreen.this.ll.addView(summaryTableView);
	    				}
	    			} else if (nodeDef instanceof DateAttributeDefinition){
	    				loadedValue = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					Date dateValue = (Date)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    					if (dateValue!=null){
		    						loadedValue = formatDate(dateValue);
		    					}
		    				}
	
	        				final DateField dateField= new DateField(FormScreen.this, nodeDef);
	        				dateField.setOnClickListener(FormScreen.this);
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
	        				FormScreen.this.ll.addView(dateField);
	    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    				if (foundNode!=null){
		    					Date dateValue = (Date)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    					if (dateValue!=null){
		    						loadedValue = formatDate(dateValue);
		    					}
		    				}
	        				final DateField dateField= new DateField(FormScreen.this, nodeDef);
	        				dateField.setOnClickListener(FormScreen.this);
	        				dateField.setId(nodeDef.getId());
	        				dateField.setValue(FormScreen.this.currInstanceNo, loadedValue, FormScreen.this.parentFormScreenId,false);
	        				dateField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	dateField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(dateField.getId(), dateField);
	        				FormScreen.this.ll.addView(dateField);
	    				}
	    			} else if (nodeDef instanceof TimeAttributeDefinition){
	    				loadedValue = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					Time timeValue = (Time)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    					if (timeValue!=null){
		    						String hour = "";
		    						if (timeValue.getHour()!=null){
		    							hour = timeValue.getHour().toString();
		    							if (Integer.valueOf(hour)<10){
		    								hour = "0"+hour;
			    						}		
		    						}
		    						String minute = "";
		    						if (timeValue.getMinute()!=null){
		    							minute = timeValue.getMinute().toString();
		    							if (Integer.valueOf(minute)<10){
		    								minute = "0"+minute;
			    						}		
		    						}
		    						if (timeValue.getHour()==null && timeValue.getMinute()==null){
		    							loadedValue = "";
		    						} else if (timeValue.getHour()==null){
		    							loadedValue = getResources().getString(R.string.timeSeparator)+minute;
		    						} else if (timeValue.getMinute()==null){
		    							loadedValue = hour+getResources().getString(R.string.timeSeparator);
		    						} else {
		    							loadedValue = hour+getResources().getString(R.string.timeSeparator)+minute;
		    						}		    						
		    					}	    				
		    				}
	        				final TimeField timeField= new TimeField(FormScreen.this, nodeDef);
	        				timeField.setOnClickListener(FormScreen.this);
	        				timeField.setId(nodeDef.getId());
	        				Log.d("TIME FIELD DEBUG", "Set value: " + loadedValue + " from FormScreen activity"); 
	        				timeField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
	        				timeField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {
	        			        	Log.d("TIME FIELD DEBUG", "Set value: " + s.toString() + " from FormScreen activity (from listener)");
	        			        	timeField.setValue(0, s.toString(), FormScreen.this.getFormScreenId(),true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(timeField.getId(), timeField);
	        				FormScreen.this.ll.addView(timeField);
	    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    				if (foundNode!=null){
		    					Time timeValue = (Time)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    					if (timeValue!=null){
		    						String hour = "";
		    						if (timeValue.getHour()!=null){
		    							hour = timeValue.getHour().toString();
		    							if (Integer.valueOf(hour)<10){
		    								hour = "0"+hour;
			    						}		
		    						}
		    						String minute = "";
		    						if (timeValue.getMinute()!=null){
		    							minute = timeValue.getMinute().toString();
		    							if (Integer.valueOf(minute)<10){
		    								minute = "0"+minute;
			    						}		
		    						}
		    						if (timeValue.getHour()==null && timeValue.getMinute()==null){
		    							loadedValue = "";
		    						} else if (timeValue.getHour()==null){
		    							loadedValue = getResources().getString(R.string.timeSeparator)+minute;
		    						} else if (timeValue.getMinute()==null){
		    							loadedValue = hour+getResources().getString(R.string.timeSeparator);
		    						} else {
		    							loadedValue = hour+getResources().getString(R.string.timeSeparator)+minute;
		    						}		    						
		    					}		   				
		    				}
	        				final TimeField timeField= new TimeField(FormScreen.this, nodeDef);
	        				timeField.setOnClickListener(FormScreen.this);
	        				timeField.setId(nodeDef.getId());
	        				Log.d("TIME FIELD DEBUG", "Set value: " + loadedValue + " from FormScreen activity (multiple instance)");
	        				timeField.setValue(FormScreen.this.currInstanceNo, loadedValue, FormScreen.this.parentFormScreenId,false);
	        				timeField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	Log.d("TIME FIELD DEBUG", "Set value: " + s.toString() + " from FormScreen activity (from listener, multiple instance)");
	        			        	timeField.setValue(FormScreen.this.currInstanceNo, s.toString(), FormScreen.this.parentFormScreenId,true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(timeField.getId(), timeField);
	        				FormScreen.this.ll.addView(timeField);
	    				} else {//multiple attribute summary    			    		
							SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
	    					summaryTableView.setOnClickListener(FormScreen.this);
	        				summaryTableView.setId(nodeDef.getId());
	        				FormScreen.this.ll.addView(summaryTableView);
	    				}
	    			} else if (nodeDef instanceof TaxonAttributeDefinition){
	    				TaxonAttributeDefinition taxonAttrDef = (TaxonAttributeDefinition)nodeDef;
	    				ArrayList<String> options = new ArrayList<String>();
	    				ArrayList<String> codes = new ArrayList<String>();
	    				options.add("");
	    				codes.add("null");
	    				
	    				String code = "";
	    				String sciName = "";
	    				String vernName = "";
	    				String vernLang = "";
	    				String langVariant = "";
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					TaxonOccurrence taxonValue = (TaxonOccurrence)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    					if (taxonValue!=null){
		    						code = taxonValue.getCode();
		    	    				sciName = taxonValue.getScientificName();
		    	    				vernName = taxonValue.getVernacularName();
		    	    				vernLang = taxonValue.getLanguageCode();
		    	    				langVariant = taxonValue.getLanguageVariety();
		    					}	    				
		    				}
	        				final TaxonField taxonField= new TaxonField(FormScreen.this, nodeDef, codes, options, vernLang);
	        				taxonField.setOnClickListener(FormScreen.this);
	        				taxonField.setId(nodeDef.getId());
	        				taxonField.setValue(0, code, sciName, vernName, vernLang, langVariant, FormScreen.this.getFormScreenId(),false);
	        				ApplicationManager.putUIElement(taxonField.getId(), taxonField);
	        				FormScreen.this.ll.addView(taxonField);
	    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    				if (foundNode!=null){
		    					TaxonOccurrence taxonValue = (TaxonOccurrence)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
		    					if (taxonValue!=null){
		    						code = taxonValue.getCode();
		    	    				sciName = taxonValue.getScientificName();
		    	    				vernName = taxonValue.getVernacularName();
		    	    				vernLang = taxonValue.getLanguageCode();
		    	    				langVariant = taxonValue.getLanguageVariety();	    						
		    					}	   				
		    				}
		    				final TaxonField taxonField= new TaxonField(FormScreen.this, nodeDef, codes, options, vernLang);
		    				taxonField.setOnClickListener(FormScreen.this);
		    				taxonField.setId(nodeDef.getId());
		    				taxonField.setValue(FormScreen.this.currInstanceNo, code, sciName, vernName, vernLang, langVariant, FormScreen.this.parentFormScreenId,false);
	        				ApplicationManager.putUIElement(taxonField.getId(), taxonField);
	        				FormScreen.this.ll.addView(taxonField);
	    				} else {//multiple attribute summary    			    		
							SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
	    					summaryTableView.setOnClickListener(FormScreen.this);
	        				summaryTableView.setId(nodeDef.getId());
	        				FormScreen.this.ll.addView(summaryTableView);
	    				}
					} else if (nodeDef instanceof FileAttributeDefinition){
						FileAttributeDefinition fileDef = (FileAttributeDefinition)nodeDef;
						List<String> extensionsList = fileDef.getExtensions();
						
						if (extensionsList.contains("jpg")||extensionsList.contains("jpeg")){
							loadedValue = "";
		    				if (!nodeDef.isMultiple()){
		        				final PhotoField photoField= new PhotoField(FormScreen.this, nodeDef);
		        				if (FormScreen.this.currentPictureField!=null){
		    		    			photoField.setValue(0, FormScreen.this.photoPath, FormScreen.this.getFormScreenId(),false);
		    		    			FormScreen.this.currentPictureField = null;
		    		    			FormScreen.this.photoPath = null;
		    		    		}
		        				Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
			    				if (foundNode!=null){
			    					File fileValue = (File)FormScreen.this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
			    					if (fileValue!=null){
			    						loadedValue = fileValue.getFilename();
			    					}
			    				}
		        				photoField.setOnClickListener(FormScreen.this);
		        				photoField.setId(nodeDef.getId());
		        				photoField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
		        				ApplicationManager.putUIElement(photoField.getId(), photoField);
		        				FormScreen.this.ll.addView(photoField);
		    				} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
		        				final PhotoField photoField= new PhotoField(FormScreen.this, nodeDef);
		        				if (FormScreen.this.currentPictureField!=null){
		        					photoField.setValue(FormScreen.this.currInstanceNo, FormScreen.this.photoPath, FormScreen.this.parentFormScreenId,false);
		    		    			FormScreen.this.currentPictureField = null;
		    		    			FormScreen.this.photoPath = null;
		    		    		}
		        				Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
			    				if (foundNode!=null){
			    					File fileValue = (File)FormScreen.this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), FormScreen.this.currInstanceNo);
			    					if (fileValue!=null){
			    						loadedValue = fileValue.getFilename();
			    					}
			    				}
		        				photoField.setOnClickListener(FormScreen.this);
		        				photoField.setId(nodeDef.getId());
		        				photoField.setValue(FormScreen.this.currInstanceNo, loadedValue, FormScreen.this.parentFormScreenId,false);
		        				ApplicationManager.putUIElement(photoField.getId(), photoField);
		        				FormScreen.this.ll.addView(photoField);
		    				} else {//multiple attribute summary    			    		
	    						SummaryTable summaryTableView = new SummaryTable(FormScreen.this, nodeDef, tableColHeaders, parentEntitySingleAttribute, FormScreen.this);
	        					summaryTableView.setOnClickListener(FormScreen.this);
	            				summaryTableView.setId(nodeDef.getId());
	            				FormScreen.this.ll.addView(summaryTableView);
	        				}
						}
					}
				}    				
			}
			if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
				FormScreen.this.ll.addView(arrangeButtonsInLine(new Button(FormScreen.this),getResources().getString(R.string.previousInstanceButton),new Button(FormScreen.this),getResources().getString(R.string.nextInstanceButton), new Button(this), getResources().getString(R.string.addInstanceButton), new Button(FormScreen.this), getResources().getString(R.string.deleteInstanceButton), FormScreen.this, false));
			} else if (FormScreen.this.intentType==getResources().getInteger(R.integer.multipleEntityIntent)){ 				
				if (ApplicationManager.currentRecord.getRootEntity().getId()!=FormScreen.this.idmlId){
					FormScreen.this.ll.addView(arrangeButtonsInLine(new Button(FormScreen.this),getResources().getString(R.string.previousInstanceButton),new Button(FormScreen.this),getResources().getString(R.string.nextInstanceButton), new Button(this), getResources().getString(R.string.addInstanceButton), new Button(FormScreen.this), getResources().getString(R.string.deleteInstanceButton), FormScreen.this, true));
				}	
			}
			setContentView(FormScreen.this.sv);
				
			int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);		
			changeBackgroundColor(backgroundColor);
	    	/*if (ApplicationManager.selectedView!=null){
	    		if (ApplicationManager.isToBeScrolled){
	    			sv.scrollTo(0, ApplicationManager.selectedView.getTop());
	    			Log.e("sv.height","=="+sv.getHeight());
	    			Log.e("SCROLLED",ApplicationManager.selectedView.getTop()+"================");
	            	ApplicationManager.isToBeScrolled = false;	
	    		}
	    	}*/
	    	sv.post(new Runnable() {
	    	    @Override
	    	    public void run() {
	    	    	if (ApplicationManager.selectedViewsBacktrackList.size()>0){
	    	    		//if (ApplicationManager.isToBeScrolled){
	    	    		if (FormScreen.this.getFormScreenId().equals(ApplicationManager.selectedViewsBacktrackList.get(ApplicationManager.selectedViewsBacktrackList.size()-1).getFormScreenId())
	    	    				||
	    	    				ApplicationManager.selectedViewsBacktrackList.get(ApplicationManager.selectedViewsBacktrackList.size()-1).getFormScreenId()==null){
	    	    			//Log.e("SCROLLTO",ApplicationManager.selectedViewsBacktrackList.size()+"=="+ApplicationManager.selectedViewsBacktrackList.get(ApplicationManager.selectedViewsBacktrackList.size()-1).getClass());
	    	    			sv.scrollTo(0, ApplicationManager.selectedViewsBacktrackList.remove(ApplicationManager.selectedViewsBacktrackList.size()-1).getView().getTop());
	    	            	//ApplicationManager.isToBeScrolled = false;	
	    	    		}	    	    			
	    	    		//}
	    	    } 
	    	    } 	
	    	});
	
			
		} catch (Exception e){
			RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onResume",
					Environment.getExternalStorageDirectory().toString()
					+getResources().getString(R.string.logs_folder)
					+getResources().getString(R.string.logs_file_name)
					+System.currentTimeMillis()
					+getResources().getString(R.string.log_file_extension));
		}
		Log.e("onRESUME time","=="+(System.currentTimeMillis()-startTime));
	}
    
    @Override
    public void onPause(){    
		Log.i(getResources().getString(R.string.app_name),TAG+":onPause");
		/*if (ApplicationManager.selectedViewsBacktrackList.size()>0){
			if (ApplicationManager.selectedViewsBacktrackList.get(ApplicationManager.selectedViewsBacktrackList.size()-1) instanceof SummaryTable){
				SummaryTable temp = (SummaryTable)ApplicationManager.selectedViewsBacktrackList.get(ApplicationManager.selectedViewsBacktrackList.size()-1);
				if (this.idmlId==temp.nodeDefinition.getId()){
					ApplicationManager.isToBeScrolled = true;	
				}
			} else if (ApplicationManager.selectedViewsBacktrackList.get(ApplicationManager.selectedViewsBacktrackList.size()-1) instanceof SummaryList){
				SummaryList temp = (SummaryList)ApplicationManager.selectedViewsBacktrackList.get(ApplicationManager.selectedViewsBacktrackList.size()-1);
				if (this.idmlId==temp.nodeDefinition.getId()){
					ApplicationManager.isToBeScrolled = true;	
				}
			}	
		}	*/	
		super.onPause();
    }
	
	private int calcNoOfCharsFitInOneLine(){
		DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
    	return metrics.widthPixels/10;    	
	}

	@Override
	public void onClick(View arg0) {
		//Log.e("arg0"+arg0.getClass(),"===");
		if (arg0 instanceof Button){
			Button btn = (Button)arg0;
			if (btn.getId()==getResources().getInteger(R.integer.leftButtonMultipleAttribute)){
				refreshMultipleAttributeScreen(0);
			} else if (btn.getId()==getResources().getInteger(R.integer.rightButtonMultipleAttribute)){
				refreshMultipleAttributeScreen(1);				
			} else if (btn.getId()==getResources().getInteger(R.integer.leftButtonMultipleEntity)){
				refreshEntityScreen(0);
			} else if (btn.getId()==getResources().getInteger(R.integer.rightButtonMultipleEntity)){
				refreshEntityScreen(1);
			} else if (btn.getId()==getResources().getInteger(R.integer.deleteButtonMultipleAttribute)){
				Log.e("DELETING","ATTRIBUTE");
				AlertMessage.createPositiveNegativeDialog(FormScreen.this, false, getResources().getDrawable(R.drawable.warningsign),
	 					getResources().getString(R.string.deleteEntityTitle), getResources().getString(R.string.deleteEntity),
	 					getResources().getString(R.string.yes), getResources().getString(R.string.no),
	 		    		new DialogInterface.OnClickListener() {
	 						@Override
	 						public void onClick(DialogInterface dialog, int which) {
	 							NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(FormScreen.this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+"0", -1));
	 							Log.e("nodeDef","=="+nodeDef.getName());
	 							Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo);
	 							Log.e("foundNode!=null","=="+(foundNode!=null));
	 							if (foundNode!=null){
	 								Log.e("not null", nodeDef.getName()+"=="+FormScreen.this.currInstanceNo);
	 								ServiceFactory.getRecordManager().deleteNode(foundNode);
	 								refreshMultipleAttributeScreen(2);
	 								Toast.makeText(FormScreen.this, getResources().getString(R.string.attributeDeletedToast), Toast.LENGTH_SHORT).show();
	 							}
	 						}
	 					},
	 		    		new DialogInterface.OnClickListener() {
	 						@Override
	 						public void onClick(DialogInterface dialog, int which) {

	 						}
	 					},
	 					null).show();			
			} else if (btn.getId()==getResources().getInteger(R.integer.deleteButtonMultipleEntity)){
				Log.e("DELETING","ENTITY");
				AlertMessage.createPositiveNegativeDialog(FormScreen.this, false, getResources().getDrawable(R.drawable.warningsign),
	 					getResources().getString(R.string.deleteEntityTitle), getResources().getString(R.string.deleteEntity),
	 					getResources().getString(R.string.yes), getResources().getString(R.string.no),
	 		    		new DialogInterface.OnClickListener() {
	 						@Override
	 						public void onClick(DialogInterface dialog, int which) {
	 							NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(FormScreen.this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+"0", -1));
	 							NodeDefinition parentNodeDefinition = nodeDef.getParentDefinition();
	 							Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.getParent().get(parentNodeDefinition.getName(), FormScreen.this.currInstanceNo);
	 							if (foundNode!=null){
	 								Log.e("not null",nodeDef.getName()+"=="+FormScreen.this.currInstanceNo);
	 								ServiceFactory.getRecordManager().deleteNode(foundNode);
	 								refreshEntityScreen(2);
	 								Toast.makeText(FormScreen.this, getResources().getString(R.string.entityDeletedToast), Toast.LENGTH_SHORT).show();
	 							} else {
	 								Log.e("null",nodeDef.getName()+"=="+FormScreen.this.currInstanceNo);
	 							}
	 						}
	 					},
	 		    		new DialogInterface.OnClickListener() {
	 						@Override
	 						public void onClick(DialogInterface dialog, int which) {

	 						}
	 					},
	 					null).show();				
			} else if (btn.getId()==getResources().getInteger(R.integer.addButtonMultipleAttribute)){
				Log.e("ADDING","ATTRIBUTE");
				refreshMultipleAttributeScreen(3);
			} else if (btn.getId()==getResources().getInteger(R.integer.addButtonMultipleEntity)){
				Log.e("ADDING","ENTITY");
				refreshEntityScreen(3);
			}
		} else if (arg0 instanceof TextView){
			TextView tv = (TextView)arg0;
			Object parentView = arg0.getParent().getParent().getParent().getParent();
			if (parentView instanceof SummaryList){
				SummaryList temp = (SummaryList)arg0.getParent().getParent().getParent().getParent();
				ViewBacktrack viewBacktrack = new ViewBacktrack(temp,FormScreen.this.getFormScreenId());
				ApplicationManager.selectedViewsBacktrackList.add(viewBacktrack);
				//ApplicationManager.isToBeScrolled = false;
				this.startActivity(this.prepareIntentForNewScreen(temp));				
			} else if (parentView instanceof SummaryTable){
				SummaryTable temp = (SummaryTable)parentView;
				ViewBacktrack viewBacktrack = new ViewBacktrack(temp,FormScreen.this.getFormScreenId());
				ApplicationManager.selectedViewsBacktrackList.add(viewBacktrack);
				//ApplicationManager.isToBeScrolled = false;
				this.startActivity(this.prepareIntentForMultipleField(temp, tv.getId(), temp.getValues()));
			}
			
		} else  if (arg0 instanceof EntityLink){
			Log.e("onClick","entityLink");
			this.startActivity(this.prepareIntentForEntityInstancesList((EntityLink)arg0));
		}
	}
	
	private Intent prepareIntentForEntityInstancesList(EntityLink entityLink){
		Intent intent = new Intent(this,EntityInstancesScreen.class);
		Log.e("========",entityLink.getTitle()+"prepareIntentForEntityInstancesList"+entityLink.getId());
		if (!this.breadcrumb.equals("")){
			String title = "";
			String entityTitle = "";
			if (entityLink.getEntityDefinition().isMultiple()){
				title = this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+entityLink.getTitle();//+" "+(this.currInstanceNo+1);
				entityTitle = entityLink.getTitle();//+" "+(this.currInstanceNo+1);
			} else {
				title = this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+entityLink.getTitle();
				entityTitle = entityLink.getTitle();
			}
			intent.putExtra(getResources().getString(R.string.breadcrumb), title);
			intent.putExtra(getResources().getString(R.string.screenTitle), entityTitle);
		} else {
			intent.putExtra(getResources().getString(R.string.breadcrumb), entityLink.getTitle());
			intent.putExtra(getResources().getString(R.string.screenTitle), entityLink.getTitle());
		}
		/*if (!this.breadcrumb.equals("")){
			String title = "";
			String entityTitle = "";
			if (summaryList.getEntityDefinition().isMultiple()){
				title = this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryList.getTitle()+" "+(this.currInstanceNo+1);
				entityTitle = summaryList.getTitle()+" "+(this.currInstanceNo+1);
			} else {
				title = this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryList.getTitle();
				entityTitle = summaryList.getTitle();
			}
			intent.putExtra(getResources().getString(R.string.breadcrumb), title);
			intent.putExtra(getResources().getString(R.string.screenTitle), entityTitle);
		} else {
			intent.putExtra(getResources().getString(R.string.breadcrumb), summaryList.getTitle());
			intent.putExtra(getResources().getString(R.string.screenTitle), summaryList.getTitle());
		}
		
		if (summaryList.getEntityDefinition().isMultiple()){
			intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.multipleEntityIntent));	
		} else {
			intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.singleEntityIntent));
		}		
		intent.putExtra(getResources().getString(R.string.idmlId), summaryList.getId());
		intent.putExtra(getResources().getString(R.string.instanceNo), summaryList.getInstanceNo());
		intent.putExtra(getResources().getString(R.string.parentFormScreenId), this.getFormScreenId());
        List<NodeDefinition> entityAttributes = summaryList.getEntityDefinition().getChildDefinitions();
        int counter = 0;
        for (NodeDefinition formField : entityAttributes){
			intent.putExtra(getResources().getString(R.string.attributeId)+counter, formField.getId());
			counter++;
        }*/
		intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.multipleEntityIntent));
		Log.e("idmlIdPassedToEntity","=="+entityLink.getId());
		intent.putExtra(getResources().getString(R.string.idmlId), entityLink.getId());
		//intent.putExtra(getResources().getString(R.string.instanceNo), entityLink.getInstanceNo());
		intent.putExtra(getResources().getString(R.string.parentFormScreenId), this.getFormScreenId());
        List<NodeDefinition> entityAttributes = entityLink.getEntityDefinition().getChildDefinitions();
        int counter = 0;
        for (NodeDefinition formField : entityAttributes){
			intent.putExtra(getResources().getString(R.string.attributeId)+counter, formField.getId());
			counter++;
        }
		return intent;
	}
	
	private Intent prepareIntentForNewScreen(SummaryList summaryList){
		Intent intent = new Intent(this,FormScreen.class);
		if (!this.breadcrumb.equals("")){
			String title = "";
			String entityTitle = "";
			if (summaryList.getEntityDefinition().isMultiple()){
				title = this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryList.getTitle()+" "+(this.currInstanceNo+1);
				entityTitle = summaryList.getTitle()/*+" "+(this.currInstanceNo+1)*/;
			} else {
				title = this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryList.getTitle();
				entityTitle = summaryList.getTitle();
			}
			intent.putExtra(getResources().getString(R.string.breadcrumb), title);
			intent.putExtra(getResources().getString(R.string.screenTitle), entityTitle);
		} else {
			intent.putExtra(getResources().getString(R.string.breadcrumb), summaryList.getTitle());
			intent.putExtra(getResources().getString(R.string.screenTitle), summaryList.getTitle());
		}
		
		if (summaryList.getEntityDefinition().isMultiple()){
			intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.multipleEntityIntent));	
		} else {
			intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.singleEntityIntent));
		}		
		intent.putExtra(getResources().getString(R.string.idmlId), summaryList.getId());
		intent.putExtra(getResources().getString(R.string.instanceNo), summaryList.getInstanceNo());
		intent.putExtra(getResources().getString(R.string.parentFormScreenId), this.getFormScreenId());
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
		intent.putExtra(getResources().getString(R.string.screenTitle), summaryTable.getTitle());
		intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.multipleAttributeIntent));
        intent.putExtra(getResources().getString(R.string.attributeId)+"0", summaryTable.getId());
        intent.putExtra(getResources().getString(R.string.idmlId), summaryTable.getId());
        intent.putExtra(getResources().getString(R.string.instanceNo), clickedInstanceNo);
        intent.putExtra(getResources().getString(R.string.parentFormScreenId), this.getFormScreenId());
        List<List<String>> values = summaryTable.getValues();
        int numberOfInstances = values.size();
        intent.putExtra(getResources().getString(R.string.numberOfInstances), numberOfInstances);
        for (int i=0;i<numberOfInstances;i++){
        	ArrayList<String> instanceValues = (ArrayList<String>)values.get(i);
        	intent.putStringArrayListExtra(getResources().getString(R.string.instanceValues)+i,instanceValues);
        }
		return intent;
	}
	
    private void changeBackgroundColor(int backgroundColor){
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
		boolean hasBreadcrumb = !this.breadcrumb.equals("");
		if (hasBreadcrumb){
			ViewGroup scrollbarViews = ((ViewGroup)this.ll.getChildAt(0));
			TextView breadcrumb = (TextView)scrollbarViews.getChildAt(0);
			breadcrumb.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);	
		}
		
		boolean hasTitle = !this.screenTitle.equals("");
		if (hasTitle){
			View dividerLine = (View)this.ll.getChildAt(1);
			dividerLine.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
			TextView screenTitle = (TextView)this.ll.getChildAt(2);
			screenTitle.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
			dividerLine = (View)this.ll.getChildAt(3);
			dividerLine.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
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
				} else if (tempView instanceof CoordinateField){
					CoordinateField tempCoordinateField = (CoordinateField)tempView;
					tempCoordinateField.setCoordinateLabelTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
				}
			}
			else if (tempView instanceof UIElement){
				if (tempView instanceof SummaryList){
					SummaryList tempSummaryList = (SummaryList)tempView;
					tempSummaryList.changeBackgroundColor(backgroundColor);
				} else if (tempView instanceof SummaryTable){
					SummaryTable tempSummaryTable = (SummaryTable)tempView;
					tempSummaryTable.changeBackgroundColor(backgroundColor);
				} else if (tempView instanceof EntityLink){
					EntityLink tempEntityLink = (EntityLink)tempView;
					tempEntityLink.changeBackgroundColor(backgroundColor);
				}
			}
		}
    }
    
    private RelativeLayout arrangeButtonsInLine(Button btnLeft, String btnLeftLabel, Button btnRight, String btnRightLabel, Button btnAdd, String btnAddLabel, Button btnDelete, String btnDeleteLabel, OnClickListener listener, boolean isForEntity){
		RelativeLayout relativeButtonsLayout = new RelativeLayout(this);
	    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    relativeButtonsLayout.setLayoutParams(lp);
		btnLeft.setText(btnLeftLabel);
		btnRight.setText(btnRightLabel);
		btnDelete.setText(btnDeleteLabel);
		btnAdd.setText(btnAddLabel);
		
		btnLeft.setOnClickListener(listener);
		btnRight.setOnClickListener(listener);
		btnDelete.setOnClickListener(listener);
		btnAdd.setOnClickListener(listener);
		
		RelativeLayout.LayoutParams lpBtnLeft = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lpBtnLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		btnLeft.setLayoutParams(lpBtnLeft);
		relativeButtonsLayout.addView(btnLeft);
			
		LinearLayout ll = new LinearLayout(this);
		ll.addView(btnAdd);
		ll.addView(btnDelete);
		RelativeLayout.LayoutParams lpBtnAddDelete = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);		
		lpBtnAddDelete.addRule(RelativeLayout.CENTER_IN_PARENT);
		ll.setLayoutParams(lpBtnAddDelete);
		relativeButtonsLayout.addView(ll);
		/*RelativeLayout.LayoutParams lpBtnDelete = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		//lpBtnDelete.addRule(RelativeLayout.RIGHT_OF,btnLeft.getId());
		lpBtnDelete.addRule(RelativeLayout.CENTER_IN_PARENT);
		lpBtnDelete.addRule(RelativeLayout.LEFT_OF,btnRight.getId());
		//lpBtnDelete.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		btnDelete.setLayoutParams(lpBtnDelete);
		relativeButtonsLayout.addView(btnDelete);
		
		RelativeLayout.LayoutParams lpBtnAdd = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);		
		//lpBtnAdd.addRule(RelativeLayout.CENTER_IN_PARENT);
		lpBtnAdd.addRule(RelativeLayout.LEFT_OF,btnDelete.getId());
		lpBtnAdd.addRule(RelativeLayout.RIGHT_OF,btnLeft.getId());
		btnAdd.setLayoutParams(lpBtnAdd);
		relativeButtonsLayout.addView(btnAdd);*/
		
		RelativeLayout.LayoutParams lpBtnRight = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lpBtnRight.addRule(RelativeLayout.RIGHT_OF,btnLeft.getId());
		lpBtnRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		btnRight.setLayoutParams(lpBtnRight);
		relativeButtonsLayout.addView(btnRight);
		
		if (!isForEntity){
			btnLeft.setId(getResources().getInteger(R.integer.leftButtonMultipleAttribute));
			btnRight.setId(getResources().getInteger(R.integer.rightButtonMultipleAttribute));
			btnAdd.setId(getResources().getInteger(R.integer.addButtonMultipleAttribute));
			btnDelete.setId(getResources().getInteger(R.integer.deleteButtonMultipleAttribute));
		} else {
			btnLeft.setId(getResources().getInteger(R.integer.leftButtonMultipleEntity));
			btnRight.setId(getResources().getInteger(R.integer.rightButtonMultipleEntity));
			btnAdd.setId(getResources().getInteger(R.integer.addButtonMultipleEntity));
			btnDelete.setId(getResources().getInteger(R.integer.deleteButtonMultipleEntity));
		}
		
		return relativeButtonsLayout;
    }
    
    public String getFormScreenId(){
    	
    	if (this.parentFormScreenId.equals("")){
    		//Log.e("1FORMSCREENID","=="+removeDuplicates(this.idmlId+getResources().getString(R.string.valuesSeparator1)+this.currInstanceNo));
    		return removeDuplicates(this.idmlId+getResources().getString(R.string.valuesSeparator1)+this.currInstanceNo);
    	} else {
    		//Log.e("1FORMSCREENID","=="+removeDuplicates(this.parentFormScreenId+getResources().getString(R.string.valuesSeparator2)+this.idmlId+getResources().getString(R.string.valuesSeparator1)+this.currInstanceNo));
    		return removeDuplicates(this.parentFormScreenId+getResources().getString(R.string.valuesSeparator2)+this.idmlId+getResources().getString(R.string.valuesSeparator1)+this.currInstanceNo);
    	}    		
    }
    
    public String removeDuplicates(String text){
        String[] tablica = text.split(";");
        for (int i=0;i<tablica.length;i++){
        	if (tablica[i]!=null){
        		String piece1 = tablica[i];
            	String firstNumber1 = tablica[i].split(",")[0];
            	//Log.e("piece1",firstNumber1+"=="+piece1);
            	for (int j=i+1;j<tablica.length;j++){
            		if (tablica[j]!=null){
            			String piece2 = tablica[j];
                		String firstNumber2 = tablica[j].split(",")[0];
                		//Log.e("piece2",firstNumber2+"=="+piece2);
                		if (piece1.equals(piece2) || firstNumber2.equals(firstNumber1)){
                			//Log.e("i"+i+"==null","==="+j);
                			tablica[i] = null;
                		}	
            		}            	
            	}
        	}
        	
        }
        String newText = "";
        for (int i=0;i<tablica.length;i++){
        	//Log.e("tablica[i]!=null","=="+(tablica[i]!=null));
        	if (tablica[i]!=null){
        		if (i==tablica.length-1){
        			newText += tablica[i];
        		} else{
        			newText += tablica[i]+";";
        		}
        	}
        		
        }
        //Log.e(text,"without duplicate: " + newText);
        return newText;
    }
	
	private Entity findParentEntity(String path){
		//Log.e("pathFORMSCREEN","=="+path);
		Entity parentEntity = ApplicationManager.currentRecord.getRootEntity();
		String screenPath = path;
		String[] entityPath = screenPath.split(getResources().getString(R.string.valuesSeparator2));
		try{
			for (int m=1;m<entityPath.length;m++){
				String[] instancePath = entityPath[m].split(getResources().getString(R.string.valuesSeparator1));				
				int id = Integer.valueOf(instancePath[0]);
				int instanceNo = Integer.valueOf(instancePath[1]);
				try{
					parentEntity = (Entity) parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);	
				} catch (IllegalArgumentException e){
					parentEntity = (Entity) parentEntity.getParent().get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
				}			
			}			
		} catch (ClassCastException e){
			
		}
		/*if (parentEntity!=null)
			Log.e("foundParentEntity","=="+parentEntity.getName());
		else 
			Log.e("foundParentEntity","==NULL");*/
		return parentEntity;
	}
	
	private void refreshEntityScreen(int actionCode){
		//0 - previous, 1 - next, 2 - delete, 3 - add
		//setting current instance number of the entity
		if (actionCode==0){//scroll left to previous entity
			if (this.currInstanceNo>0){
				this.currInstanceNo--;
				refreshEntityScreenFields();
			} else {
				return;
			}
		} else if (actionCode==1) {//scroll right to next (or new) entity
			//Log.e("SCROLLING","RIGHT");
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+0, -1));
			if (!nodeDef.isMultiple()){
				//Log.e("parentSINGLE","=="+this.parentEntitySingleAttribute.getName());
				//Log.e("this.currentInstanceNO","=="+this.currInstanceNo);
				//Log.e("-1","=="+(this.parentEntitySingleAttribute.getParent().get(this.parentEntitySingleAttribute.getName(), this.currInstanceNo-11)==null));
				//Log.e("obecny","=="+(this.parentEntitySingleAttribute.getParent().get(this.parentEntitySingleAttribute.getName(), this.currInstanceNo)==null));
				//Log.e("+1","=="+(this.parentEntitySingleAttribute.getParent().get(this.parentEntitySingleAttribute.getName(), this.currInstanceNo+1)==null));
				Node<?> foundNode = this.parentEntitySingleAttribute.getParent().get(this.parentEntitySingleAttribute.getName(), this.currInstanceNo+1);
				//Log.e("foundNode1!=null","=="+(foundNode!=null));
				if (foundNode!=null){
					//Log.e("foundNode1","=="+foundNode.getName());
					this.currInstanceNo++;	
					refreshEntityScreenFields();
				} else {
					AlertMessage.createPositiveNegativeDialog(FormScreen.this, true, null,
							getResources().getString(R.string.addNewEntityTitle), 
							getResources().getString(R.string.addNewEntityMessage),
								getResources().getString(R.string.yes),
								getResources().getString(R.string.no),
					    		new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										FormScreen.this.currInstanceNo++;	
										refreshEntityScreenFields();
									}
								},
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								},
								null).show();
				}
			} else {
				//Log.e("parentMULTIPLE","=="+this.parentEntityMultipleAttribute.getName());
				Node<?> foundNode = this.parentEntityMultipleAttribute.getParent().get(this.parentEntityMultipleAttribute.getName(), this.currInstanceNo+1);
				//Log.e("foundNode1!=null","=="+(foundNode!=null));
				if (foundNode!=null){
					///Log.e("foundNode1","=="+foundNode.getName());
					this.currInstanceNo++;
					refreshEntityScreenFields();
				} else {
					//Log.e("foudnNODE1","==NULL");
					AlertMessage.createPositiveNegativeDialog(FormScreen.this, true, null,
							getResources().getString(R.string.addNewEntityTitle), 
							getResources().getString(R.string.addNewEntityMessage),
								getResources().getString(R.string.yes),
								getResources().getString(R.string.no),
					    		new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										FormScreen.this.currInstanceNo++;
										refreshEntityScreenFields();
									}
								},
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								},
								null).show();
				}
			}
		} else if (actionCode==2){
			
		} else if (actionCode==3){
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+0, -1));
			if (!nodeDef.isMultiple()){

				AlertMessage.createPositiveNegativeDialog(FormScreen.this, true, null,
						getResources().getString(R.string.addNewEntityTitle), 
						getResources().getString(R.string.addNewEntityMessage),
							getResources().getString(R.string.yes),
							getResources().getString(R.string.no),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.getParent().get(FormScreen.this.parentEntitySingleAttribute.getName(), FormScreen.this.currInstanceNo+1);
									while (foundNode!=null){
										FormScreen.this.currInstanceNo++;
										foundNode = FormScreen.this.parentEntitySingleAttribute.getParent().get(FormScreen.this.parentEntitySingleAttribute.getName(), FormScreen.this.currInstanceNo+1);
									}
									FormScreen.this.currInstanceNo++;	
									refreshEntityScreenFields();
								}
							},
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							},
							null).show();				
			} else {
				AlertMessage.createPositiveNegativeDialog(FormScreen.this, true, null,
						getResources().getString(R.string.addNewEntityTitle), 
						getResources().getString(R.string.addNewEntityMessage),
							getResources().getString(R.string.yes),
							getResources().getString(R.string.no),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.getParent().get(FormScreen.this.parentEntitySingleAttribute.getName(), FormScreen.this.currInstanceNo+1);
									while (foundNode!=null){
										FormScreen.this.currInstanceNo++;
										foundNode = FormScreen.this.parentEntityMultipleAttribute.getParent().get(FormScreen.this.parentEntitySingleAttribute.getName(), FormScreen.this.currInstanceNo+1);
									}
									FormScreen.this.currInstanceNo++;	
									refreshEntityScreenFields();
								}
							},
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							},
							null).show();
			}
		}		
		
	}
	
	private void refreshEntityScreenFields(){
		NodeDefinition currentScreenNodeDef = ApplicationManager.getSurvey().getSchema().getDefinitionById(this.idmlId);
		if (currentScreenNodeDef.getMaxCount()!=null){
			if (currentScreenNodeDef.getMaxCount()<=this.currInstanceNo){			
				this.currInstanceNo--;
				AlertMessage.createPositiveDialog(FormScreen.this, true, null,
						getResources().getString(R.string.maxCountTitle), 
						getResources().getString(R.string.maxCountMessage),
							getResources().getString(R.string.okay),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							},
							null).show();
				return;
			}	
		}
		
		View firstView = this.ll.getChildAt(0);
		if (firstView instanceof HorizontalScrollView){
			ViewGroup scrollbarView = ((ViewGroup)this.ll.getChildAt(0));
			TextView breadcrumb = (TextView)scrollbarView.getChildAt(0);
			breadcrumb.setText(this.breadcrumb.substring(0, this.breadcrumb.lastIndexOf(" "))+" "+(this.currInstanceNo+1));
			breadcrumb.setTextSize(getResources().getInteger(R.integer.breadcrumbFontSize));
			breadcrumb.setSingleLine();
			/*HorizontalScrollView scroller = new HorizontalScrollView(FormScreen.this);
			scroller.addView(breadcrumb);*/
		}
		this.breadcrumb = this.breadcrumb.substring(0, this.breadcrumb.lastIndexOf(" "))+" "+(this.currInstanceNo+1);

		TextView screenTitle = new TextView(FormScreen.this);
		screenTitle.setText(FormScreen.this.screenTitle);
		screenTitle.setTextSize(getResources().getInteger(R.integer.screenTitleFontSize));
		FormScreen.this.ll.addView(screenTitle);
		
		this.ll.removeAllViews();
		this.ll.addView(firstView,0);
		this.ll.addView(screenTitle,1);
		
		//refreshing values of fields in the entity 
		Entity parentEntity = this.findParentEntity(this.getFormScreenId());
		if (parentEntity==null){
			//Log.e("orginalpathTOSearchForParent","=="+this.getFormScreenId());
			String path = this.getFormScreenId().substring(0,this.getFormScreenId().lastIndexOf(getResources().getString(R.string.valuesSeparator2)));
			//Log.e("pathTOSearchForParent","=="+path);
			parentEntity = this.findParentEntity(path);
			try{
				EntityBuilder.addEntity(parentEntity, ApplicationManager.getSurvey().getSchema().getDefinitionById(this.idmlId).getName());
			} catch (IllegalArgumentException e){
				parentEntity = parentEntity.getParent();
				EntityBuilder.addEntity(parentEntity, ApplicationManager.getSurvey().getSchema().getDefinitionById(this.idmlId).getName());
			}
			//Log.e("foundParentEntity","=="+parentEntity.getName());
			parentEntity = this.findParentEntity(this.getFormScreenId());
			//Log.e("newParentEntity","=="+parentEntity.getName());
		}
		
//		Log.e("refreshEntityScreen","this.getFormScreenId()=="+this.getFormScreenId());
//		Log.e("refreshEntityScreen","this.parentFormScreenId=="+this.parentFormScreenId);
		this.parentEntitySingleAttribute = this.findParentEntity(this.getFormScreenId());
		this.parentEntityMultipleAttribute = this.findParentEntity(this.parentFormScreenId);
		/*if (parentEntitySingleAttribute!=null)
			Log.e("refreshEntityScreen","parentEntitySingleAttribute=="+parentEntitySingleAttribute.getName()+"=="+parentEntitySingleAttribute.getIndex());
		if (parentEntityMultipleAttribute!=null)
			Log.e("refreshEntityScreen","parentEntityMultipleAttribute=="+parentEntityMultipleAttribute.getName()+"=="+parentEntityMultipleAttribute.getIndex());*/
		
		String loadedValue = "";
		ArrayList<String> tableColHeaders = new ArrayList<String>();
		tableColHeaders.add("Value");
		for (int i=0;i<this.fieldsNo;i++){
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+i, -1));
			if (nodeDef instanceof EntityDefinition){
				if (ApplicationManager.currentRecord.getRootEntity().getId()!=nodeDef.getId()){
    				Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0/*this.currInstanceNo*/);
    				if (foundNode==null){
    					EntityBuilder.addEntity(this.parentEntitySingleAttribute, ApplicationManager.getSurvey().getSchema().getDefinitionById(nodeDef.getId()).getName()/*, this.currInstanceNo*/);
    				}
				}
				
				EntityDefinition entityDef = (EntityDefinition)nodeDef;
				//for (int e=0;e<this.parentEntitySingleAttribute.getCount(entityDef.getName());e++){
					/*SummaryList summaryListView = new SummaryList(this, entityDef, calcNoOfCharsFitInOneLine(),
    						this,e);
    				summaryListView.setOnClickListener(this);
    				summaryListView.setId(nodeDef.getId());
    				this.ll.addView(summaryListView);*/
    				EntityLink entityLinkView = new EntityLink(FormScreen.this, entityDef, calcNoOfCharsFitInOneLine(),
    						FormScreen.this);
					entityLinkView.setOnClickListener(FormScreen.this);
    				entityLinkView.setId(nodeDef.getId());
    				FormScreen.this.ll.addView(entityLinkView);
				//}			  				
			}else {					
				if (nodeDef instanceof TextAttributeDefinition){
    				loadedValue = "";	    				

    				if (((TextAttributeDefinition) nodeDef).getType().toString().toLowerCase().equals("short")){
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					TextValue textValue = (TextValue)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    					if (textValue!=null)
		    						loadedValue = textValue.getValue();	    				
		    				}
	        				final TextField textField= new TextField(this, nodeDef);
	        				textField.setOnClickListener(this);
	        				textField.setId(nodeDef.getId());
	        				//textField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
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
	    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
		    				if (foundNode!=null){
		    					TextValue textValue = (TextValue)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
		    					if (textValue!=null)
		    						loadedValue = textValue.getValue();	    				
		    				}
	        				final TextField textField= new TextField(this, nodeDef);
	        				textField.setOnClickListener(this);
	        				textField.setId(nodeDef.getId());
	        				//Log.e("this.parentFormScreenId",nodeDef.getName()+"=="+this.parentFormScreenId);
	        				//textField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
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
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
        				}
    				} else {//memo field
    					if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					TextValue textValue = (TextValue)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    					if (textValue!=null)
		    						loadedValue = textValue.getValue();	    				
		    				}
	        				final MemoField memoField= new MemoField(this, nodeDef);
	        				memoField.setOnClickListener(this);
	        				memoField.setId(nodeDef.getId());
	        				//memoField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
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
	    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
		    				if (foundNode!=null){
		    					TextValue textValue = (TextValue)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
		    					if (textValue!=null)
		    						loadedValue = textValue.getValue();	    				
		    				}
	        				final MemoField memoField= new MemoField(this, nodeDef);
	        				memoField.setOnClickListener(this);
	        				memoField.setId(nodeDef.getId());
	        				//memoField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
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
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
        				}
    				}
    			} else if (nodeDef instanceof NumberAttributeDefinition){
    				loadedValue = "";
    				if (!nodeDef.isMultiple()){
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					if (((NumberAttributeDefinition) nodeDef).isInteger()){
	    						IntegerValue intValue = (IntegerValue)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    						if (intValue!=null)
		    						loadedValue = intValue.getValue().toString();
	    					} else {
	    						RealValue realValue = (RealValue)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    						if (realValue!=null)
		    						loadedValue = realValue.getValue().toString();
	    					}
	    				}
        				final NumberField numberField= new NumberField(this, nodeDef);
        				numberField.setOnClickListener(this);
        				numberField.setId(nodeDef.getId());
        				//numberField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
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
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					if (((NumberAttributeDefinition) nodeDef).isInteger()){
	    						IntegerValue intValue = (IntegerValue)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
	    						if (intValue!=null)
		    						loadedValue = intValue.getValue().toString();
	    					} else {
	    						RealValue realValue = (RealValue)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
	    						if (realValue!=null)
		    						loadedValue = realValue.getValue().toString();
	    					}
	    				}
        				final NumberField numberField= new NumberField(this, nodeDef);
        				numberField.setOnClickListener(this);
        				numberField.setId(nodeDef.getId());
        				//numberField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
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
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof BooleanAttributeDefinition){
    				loadedValue = "";
    				if (!nodeDef.isMultiple()){
    					
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					BooleanValue boolValue = (BooleanValue)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
        					if (boolValue!=null){
        						if (boolValue.getValue()!=null)
        							loadedValue = boolValue.getValue().toString();
        					}
	    				}
	    				
        				BooleanField boolField = null;// new BooleanField(this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
        				if (loadedValue.equals("")){
        					boolField = new BooleanField(this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
        				} else if (loadedValue.equals("false")) {
    						boolField = new BooleanField(this, nodeDef, false, true, getResources().getString(R.string.yes), getResources().getString(R.string.no));
    					} else {
    						boolField = new BooleanField(this, nodeDef, true, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
    					}
        				boolField.setOnClickListener(this);
        				boolField.setId(nodeDef.getId());
        				if (loadedValue.equals("")){
        					//boolField.setValue(0, null, FormScreen.this.getFormScreenId(),false);	
        				} else {
        					//boolField.setValue(0, Boolean.valueOf(loadedValue), FormScreen.this.getFormScreenId(),false);	
        				}	        				
        				ApplicationManager.putUIElement(boolField.getId(), boolField);
        				this.ll.addView(boolField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
    					if (foundNode!=null){
    						BooleanValue boolValue = (BooleanValue)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
	    					if (boolValue!=null){
        						if (boolValue.getValue()!=null)
        							loadedValue = boolValue.getValue().toString();
        					}
	    				}
    					//BooleanField boolField = new BooleanField(this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
    					BooleanField boolField = null;// new BooleanField(this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
        				if (loadedValue.equals("")){
        					boolField = new BooleanField(this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
        				} else if (loadedValue.equals("false")) {
    						boolField = new BooleanField(this, nodeDef, false, true, getResources().getString(R.string.yes), getResources().getString(R.string.no));
    					} else {
    						boolField = new BooleanField(this, nodeDef, true, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
    					}
    					boolField.setOnClickListener(this);
    					boolField.setId(nodeDef.getId());
    					if (loadedValue.equals("")){
    						//boolField.setValue(this.currInstanceNo, null, this.parentFormScreenId,false);
    					} else {
    						//boolField.setValue(this.currInstanceNo, Boolean.valueOf(loadedValue), this.parentFormScreenId,false);
    					}
        				ApplicationManager.putUIElement(boolField.getId(), boolField);
        				this.ll.addView(boolField);
    				} else {//multiple attribute summary    			    		
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
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
    				CodeListManager codeListManager = ServiceFactory.getCodeListManager();
					CodeList list = codeAttrDef.getList();
					if ( ! list.isExternal() ) {
						List<CodeListItem> codeListItemsList = codeListManager.loadRootItems(list);
	    				for (CodeListItem codeListItem : codeListItemsList){
	    					codes.add(codeListItem.getCode());
	    					if (codeListItem.getLabel(null)==null){
	    						options.add(codeListItem.getLabel(ApplicationManager.selectedLanguage));
	    					} else {
	    						options.add(codeListItem.getLabel(null));	    						
	    					}
	    				}
					}
    				if (!nodeDef.isMultiple()){
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					Code codeValue = (Code)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
        					if (codeValue!=null){
        						loadedValue = codeValue.getCode();
        					}
	    				}
        				CodeField codeField = new CodeField(this, nodeDef, codes, options, loadedValue,FormScreen.this.getFormScreenId());
        				codeField.setOnClickListener(this);
        				codeField.setId(nodeDef.getId());
        				//codeField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
        				ApplicationManager.putUIElement(codeField.getId(), codeField);
        				this.ll.addView(codeField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
    					if (foundNode!=null){
	    					Code codeValue = (Code)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
        					if (codeValue!=null){
        						loadedValue = codeValue.getCode();
        					}
	    				}
        				CodeField codeField = new CodeField(this, nodeDef, codes, options, loadedValue,this.parentFormScreenId);
        				codeField.setOnClickListener(this);
        				codeField.setId(nodeDef.getId());
        				//Log.e("onResume",this.parentFormScreenId+"=="+this.currInstanceNo);
        				//codeField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
        				ApplicationManager.putUIElement(codeField.getId(), codeField);
        				this.ll.addView(codeField);
    				} else {//multiple attribute summary
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof CoordinateAttributeDefinition){
    				String loadedValueLon = "";
    				String loadedValueLat = "";
    				if (!nodeDef.isMultiple()){
        				final CoordinateField coordField= new CoordinateField(this, nodeDef);
        				if (this.currentCoordinateField!=null){
        					if (this.longitude==null)
        						this.longitude = "";
        					if (this.latitude==null)
        						this.latitude = "";
        					String srsId = null;
        					if (FormScreen.this.currentCoordinateField.srs!=null){						
        						srsId = FormScreen.this.currentCoordinateField.srs.getId();
        					}
        					coordField.setValue(0, this.longitude, this.latitude, srsId, this.parentFormScreenId,false);
    		    			this.currentCoordinateField = null;
    		    			this.longitude = null;
    		    			this.latitude = null;
    		    		}
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					Coordinate coordValue = (Coordinate)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    					if (coordValue!=null){
	    						if (coordValue.getX()!=null)
	    							loadedValueLon = coordValue.getX().toString();
	    						if (coordValue.getY()!=null)
	    							loadedValueLat = coordValue.getY().toString();
	    					}	    				
	    				}
	    				//coordField = new CoordinateField(this, nodeDef);
        				coordField.setOnClickListener(this);
        				coordField.setId(nodeDef.getId());
        				//coordField.setValue(0, loadedValueLon, loadedValueLat, FormScreen.this.getFormScreenId(),false);
        				ApplicationManager.putUIElement(coordField.getId(), coordField);
        				this.ll.addView(coordField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					final CoordinateField coordField= new CoordinateField(this, nodeDef);
        				if (this.currentCoordinateField!=null){
        					if (this.longitude==null)
        						this.longitude = "";
        					if (this.latitude==null)
        						this.latitude = "";
        					String srsId = null;
        					if (FormScreen.this.currentCoordinateField.srs!=null){						
        						srsId = FormScreen.this.currentCoordinateField.srs.getId();
        					}
        					coordField.setValue(this.currInstanceNo, this.longitude, this.latitude, srsId, this.parentFormScreenId,false);
    		    			this.currentCoordinateField = null;
    		    			this.longitude = null;
    		    			this.latitude = null;
    		    		}
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					Coordinate coordValue = (Coordinate)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
	    					if (coordValue!=null){
	    						if (coordValue.getX()!=null)
	    							loadedValueLon = coordValue.getX().toString();
	    						if (coordValue.getY()!=null)
	    							loadedValueLat = coordValue.getY().toString();
	    					}   				
	    				}
        				//coordField= new CoordinateField(this, nodeDef);
        				coordField.setOnClickListener(this);
        				coordField.setId(nodeDef.getId());
        				//coordField.setValue(this.currInstanceNo, loadedValueLon, loadedValueLat, this.parentFormScreenId,false);
        				ApplicationManager.putUIElement(coordField.getId(), coordField);
        				this.ll.addView(coordField);
    				} else {//multiple attribute summary    			    		
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof RangeAttributeDefinition){
    				loadedValue = "";
    				if (!nodeDef.isMultiple()){
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
	    					if (rangeAttrDef.isReal()){
	    						RealRange rangeValue = (RealRange)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    						if (rangeValue!=null){
		    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
		    							loadedValue = "";
		    						} else if (rangeValue.getFrom()==null){
		    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						} else if (rangeValue.getTo()==null){
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
		    						} else {
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						}		    						
		    					}	
	    					} else {
	    						IntegerRange rangeValue = (IntegerRange)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    						if (rangeValue!=null){
		    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
		    							loadedValue = "";
		    						} else if (rangeValue.getFrom()==null){
		    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						} else if (rangeValue.getTo()==null){
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
		    						} else {
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						}		    						
		    					}	
	    					}		    							
	    				}
        				final RangeField rangeField= new RangeField(this, nodeDef);
        				rangeField.setOnClickListener(this);
        				rangeField.setId(nodeDef.getId());
        				//rangeField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
        				rangeField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {        			            
        			        	rangeField.setValue(0, s.toString(),  FormScreen.this.getFormScreenId(),true);
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.putUIElement(rangeField.getId(), rangeField);
        				this.ll.addView(rangeField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
	    					if (rangeAttrDef.isReal()){
	    						RealRange rangeValue = (RealRange)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    						if (rangeValue!=null){
		    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
		    							loadedValue = "";
		    						} else if (rangeValue.getFrom()==null){
		    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						} else if (rangeValue.getTo()==null){
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
		    						} else {
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						}		    						
		    					}	
	    					} else {
	    						IntegerRange rangeValue = (IntegerRange)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    						if (rangeValue!=null){
		    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
		    							loadedValue = "";
		    						} else if (rangeValue.getFrom()==null){
		    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						} else if (rangeValue.getTo()==null){
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
		    						} else {
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						}		    						
		    					}	
	    					}  				
	    				}
        				final RangeField rangeField= new RangeField(this, nodeDef);
        				rangeField.setOnClickListener(this);
        				rangeField.setId(nodeDef.getId());
        				//rangeField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
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
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof DateAttributeDefinition){
    				loadedValue = "";
    				if (!nodeDef.isMultiple()){
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					Date dateValue = (Date)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    					if (dateValue!=null){
	    						if (dateValue.getMonth()==null && dateValue.getDay()==null && dateValue.getYear()==null){
	    							loadedValue = "";
	    						} else if (dateValue.getMonth()==null && dateValue.getDay()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator);		    							
	    						} else if (dateValue.getMonth()==null && dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						} else if (dateValue.getDay()==null && dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator);
	    						} else if (dateValue.getMonth()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+dateValue.getDay();		    							
	    						} else if (dateValue.getDay()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator);
	    						} else if (dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						} else {
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						}
	    					}
	    				}

        				final DateField dateField= new DateField(this, nodeDef);
        				dateField.setOnClickListener(this);
        				dateField.setId(nodeDef.getId());
        				//dateField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
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
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					Date dateValue = (Date)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    					if (dateValue!=null){
	    						if (dateValue.getMonth()==null && dateValue.getDay()==null && dateValue.getYear()==null){
	    							loadedValue = "";
	    						} else if (dateValue.getMonth()==null && dateValue.getDay()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator);		    							
	    						} else if (dateValue.getMonth()==null && dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						} else if (dateValue.getDay()==null && dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator);
	    						} else if (dateValue.getMonth()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+dateValue.getDay();		    							
	    						} else if (dateValue.getDay()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator);
	    						} else if (dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						} else {
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						}
	    					}
	    				}
        				final DateField dateField= new DateField(this, nodeDef);
        				dateField.setOnClickListener(this);
        				dateField.setId(nodeDef.getId());
        				//dateField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
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
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					Time timeValue = (Time)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    					if (timeValue!=null){
	    						if (timeValue.getHour()==null && timeValue.getMinute()==null){
	    							loadedValue = "";
	    						} else if (timeValue.getHour()==null){
	    							loadedValue = getResources().getString(R.string.timeSeparator)+timeValue.getMinute();
	    						} else if (timeValue.getMinute()==null){
	    							loadedValue = timeValue.getHour()+getResources().getString(R.string.timeSeparator);
	    						} else {
	    							loadedValue = timeValue.getHour()+getResources().getString(R.string.timeSeparator)+timeValue.getMinute();
	    						}		    						
	    					}	    				
	    				}
        				final TimeField timeField= new TimeField(this, nodeDef);
        				timeField.setOnClickListener(this);
        				timeField.setId(nodeDef.getId());
        				//timeField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
        				timeField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {
        			        	timeField.setValue(0, s.toString(), FormScreen.this.getFormScreenId(),true);
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.putUIElement(timeField.getId(), timeField);
        				this.ll.addView(timeField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					Time timeValue = (Time)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
	    					if (timeValue!=null){
	    						if (timeValue.getHour()==null && timeValue.getMinute()==null){
	    							loadedValue = "";
	    						} else if (timeValue.getHour()==null){
	    							loadedValue = getResources().getString(R.string.timeSeparator)+timeValue.getMinute();
	    						} else if (timeValue.getMinute()==null){
	    							loadedValue = timeValue.getHour()+getResources().getString(R.string.timeSeparator);
	    						} else {
	    							loadedValue = timeValue.getHour()+getResources().getString(R.string.timeSeparator)+timeValue.getMinute();
	    						}		    						
	    					}	   				
	    				}
        				final TimeField timeField= new TimeField(this, nodeDef);
        				timeField.setOnClickListener(this);
        				timeField.setId(nodeDef.getId());
        				//timeField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
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
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
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
    				
    				String code = "";
    				String sciName = "";
    				String vernName = "";
    				String vernLang = "";
    				String langVariant = "";
    				if (!nodeDef.isMultiple()){
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					TaxonOccurrence taxonValue = (TaxonOccurrence)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    					if (taxonValue!=null){
	    						code = taxonValue.getCode();
	    	    				sciName = taxonValue.getScientificName();
	    	    				vernName = taxonValue.getVernacularName();
	    	    				vernLang = taxonValue.getLanguageCode();
	    	    				langVariant = taxonValue.getLanguageVariety();
	    					}	    				
	    				}
        				final TaxonField taxonField= new TaxonField(this, nodeDef, codes, options, vernLang);
        				taxonField.setOnClickListener(this);
        				taxonField.setId(nodeDef.getId());
        				//taxonField.setValue(0, code, sciName, vernName, vernLang, langVariant, FormScreen.this.getFormScreenId(),false);
        				ApplicationManager.putUIElement(taxonField.getId(), taxonField);
        				this.ll.addView(taxonField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					TaxonOccurrence taxonValue = (TaxonOccurrence)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
	    					if (taxonValue!=null){
	    						code = taxonValue.getCode();
	    	    				sciName = taxonValue.getScientificName();
	    	    				vernName = taxonValue.getVernacularName();
	    	    				vernLang = taxonValue.getLanguageCode();
	    	    				langVariant = taxonValue.getLanguageVariety();	    						
	    					}	   				
	    				}
	    				final TaxonField taxonField= new TaxonField(this, nodeDef, codes, options, vernLang);
	    				taxonField.setOnClickListener(this);
	    				taxonField.setId(nodeDef.getId());
	    				//taxonField.setValue(this.currInstanceNo, code, sciName, vernName, vernLang, langVariant, this.parentFormScreenId,false);
        				ApplicationManager.putUIElement(taxonField.getId(), taxonField);
        				this.ll.addView(taxonField);
    				} else {//multiple attribute summary    			    		
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
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
	        				Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					File fileValue = (File)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    					if (fileValue!=null){
		    						loadedValue = fileValue.getFilename();
		    					}
		    				}
	        				photoField.setOnClickListener(this);
	        				photoField.setId(nodeDef.getId());
	        				//photoField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
	        				ApplicationManager.putUIElement(photoField.getId(), photoField);
	        				this.ll.addView(photoField);
	    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	        				final PhotoField photoField= new PhotoField(this, nodeDef);
	        				if (this.currentPictureField!=null){
	        					photoField.setValue(this.currInstanceNo, this.photoPath, this.parentFormScreenId,false);
	    		    			this.currentPictureField = null;
	    		    			this.photoPath = null;
	    		    		}
	        				Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
		    				if (foundNode!=null){
		    					File fileValue = (File)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
		    					if (fileValue!=null){
		    						loadedValue = fileValue.getFilename();
		    					}
		    				}
	        				photoField.setOnClickListener(this);
	        				photoField.setId(nodeDef.getId());
	        				//photoField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
	        				ApplicationManager.putUIElement(photoField.getId(), photoField);
	        				this.ll.addView(photoField);
	    				} else {//multiple attribute summary    			    		
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
        				}
					}
				}
			}    				
		}
		
		if (this.intentType==getResources().getInteger(R.integer.multipleEntityIntent)){ 				
			if (ApplicationManager.currentRecord.getRootEntity().getId()!=this.idmlId){
				this.ll.addView(arrangeButtonsInLine(new Button(this),getResources().getString(R.string.previousInstanceButton),new Button(this),getResources().getString(R.string.nextInstanceButton), new Button(this), getResources().getString(R.string.addInstanceButton), new Button(this), getResources().getString(R.string.deleteInstanceButton), this, true));
			}	
		}
		
		int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);		
		changeBackgroundColor(backgroundColor);
		
		for (int i=0;i<this.fieldsNo;i++){
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+i, -1));
			if (nodeDef!=null){
				if (nodeDef instanceof TextAttributeDefinition){
					loadedValue = "";
					if (((TextAttributeDefinition) nodeDef).getType().toString().toLowerCase().equals("short")){
						TextValue textValue = (TextValue)parentEntity.getValue(nodeDef.getName(), 0);							
						if (textValue!=null)
							if (textValue.getValue()!=null)
								loadedValue = textValue.getValue();
    					TextField textField = (TextField) ApplicationManager.getUIElement(nodeDef.getId());
    					if (textField!=null)
    						textField.setValue(0, loadedValue, this.getFormScreenId(), false);
					} else {
						TextValue textValue = (TextValue)parentEntity.getValue(nodeDef.getName(), 0);							
						if (textValue!=null)
							if (textValue.getValue()!=null)
								loadedValue = textValue.getValue();
    					MemoField memoField = (MemoField) ApplicationManager.getUIElement(nodeDef.getId());
    					if (memoField!=null)
    						memoField.setValue(0, loadedValue, this.getFormScreenId(), false);
					}								
				} else if (nodeDef instanceof NumberAttributeDefinition){
					loadedValue = "";
					if (((NumberAttributeDefinition) nodeDef).isInteger()){
						IntegerValue intValue = (IntegerValue)parentEntity.getValue(nodeDef.getName(), 0);
						if (intValue!=null)
							if (intValue.getValue()!=null)
								loadedValue = intValue.getValue().toString();	
					} else {
						RealValue realValue = (RealValue)parentEntity.getValue(nodeDef.getName(), 0);					
						if (realValue!=null)
							if (realValue.getValue()!=null)
								loadedValue = realValue.getValue().toString();
					}					
					NumberField numberField = (NumberField) ApplicationManager.getUIElement(nodeDef.getId());
					if (numberField!=null)
						numberField.setValue(0, loadedValue, this.getFormScreenId(), false);
				}  else if (nodeDef instanceof BooleanAttributeDefinition){
					loadedValue = "";
					BooleanValue boolValue = (BooleanValue)parentEntity.getValue(nodeDef.getName(), 0);
					if (boolValue!=null)
						if (boolValue.getValue()!=null)
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
					loadedValue = "";
					Code codeValue = (Code)parentEntity.getValue(nodeDef.getName(), 0);
					if (codeValue!=null)
						if (codeValue.getCode()!=null)
							loadedValue = codeValue.getCode();
					CodeField codeField = (CodeField) ApplicationManager.getUIElement(nodeDef.getId());
					if (codeField!=null){
						//Log.e("refreshENTITY",this.getFormScreenId()+"=="+0);
						codeField.setValue(0, loadedValue, this.getFormScreenId(), false);
					}
						
				} else if (nodeDef instanceof CoordinateAttributeDefinition){
					String loadedValueLat = "";
					String loadedValueLon = "";
					String loadedSrsId = "";
					Coordinate coordValue = (Coordinate)parentEntity.getValue(nodeDef.getName(), 0);
					if (coordValue!=null){
						if (coordValue.getX()!=null)
							loadedValueLon = coordValue.getX().toString();
						if (coordValue.getY()!=null)
							loadedValueLat = coordValue.getY().toString();
						if (coordValue.getSrsId()!=null)
							loadedSrsId = coordValue.getSrsId().toString();
					}
						
					CoordinateField coordField = (CoordinateField) ApplicationManager.getUIElement(nodeDef.getId());
					if (coordField!=null)
						coordField.setValue(0, loadedValueLon, loadedValueLat, loadedSrsId, this.getFormScreenId(), false);
				} else if (nodeDef instanceof RangeAttributeDefinition){
					String from = "";
					String to = "";
					
					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
					if (rangeAttrDef.isReal()){
						RealRange rangeValue = (RealRange)parentEntity.getValue(nodeDef.getName(), 0);
						if (rangeValue!=null){
							if (rangeValue.getFrom()!=null)
								from = rangeValue.getFrom().toString();
							if (rangeValue.getTo()!=null)
								to = rangeValue.getTo().toString();						
						}
					} else {
						IntegerRange rangeValue = (IntegerRange)parentEntity.getValue(nodeDef.getName(), 0);
						if (rangeValue!=null){
							if (rangeValue.getFrom()!=null)
								from = rangeValue.getFrom().toString();
							if (rangeValue.getTo()!=null)
								to = rangeValue.getTo().toString();						
						}
					}
											
					RangeField rangeField = (RangeField) ApplicationManager.getUIElement(nodeDef.getId());
					if (rangeField!=null)
						rangeField.setValue(0, from+getResources().getString(R.string.rangeSeparator)+to, this.getFormScreenId(), false);
				} else if (nodeDef instanceof DateAttributeDefinition){
					String day = "";
					String month = "";
					String year = "";
					Date dateValue = (Date)parentEntity.getValue(nodeDef.getName(), 0);
					if (dateValue!=null){
						loadedValue = formatDate(dateValue);
					}
					DateField dateField = (DateField) ApplicationManager.getUIElement(nodeDef.getId());
					if (dateField!=null)
						dateField.setValue(0, loadedValue, this.getFormScreenId(), false);
				} else if (nodeDef instanceof TimeAttributeDefinition){
					String hour = "";
					String minute = "";
					Time timeValue = (Time)parentEntity.getValue(nodeDef.getName(), 0);
					if (timeValue!=null){
						if (timeValue.getHour()!=null)
							hour = timeValue.getHour().toString();
						if (timeValue.getMinute()!=null)
							minute = timeValue.getMinute().toString();
					}						
					TimeField timeField = (TimeField) ApplicationManager.getUIElement(nodeDef.getId());
					if (timeField!=null)
						timeField.setValue(0, hour+getResources().getString(R.string.timeSeparator)+minute, this.getFormScreenId(), false);					
				} else if (nodeDef instanceof TaxonAttributeDefinition){
    				String code = "";
    				String sciName = "";
    				String vernName = "";
    				String vernLang = "";
    				String langVariant = "";
					TaxonOccurrence taxonValue = (TaxonOccurrence)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
					if (taxonValue!=null){
						code = taxonValue.getCode();
	    				sciName = taxonValue.getScientificName();
	    				vernName = taxonValue.getVernacularName();
	    				vernLang = taxonValue.getLanguageCode();
	    				langVariant = taxonValue.getLanguageVariety();	    						
					}
					TaxonField taxonField = (TaxonField) ApplicationManager.getUIElement(nodeDef.getId());
					if (taxonField!=null)
						taxonField.setValue(0, code, sciName, vernName, vernLang, langVariant, this.getFormScreenId(), false);
				} else if (nodeDef instanceof FileAttributeDefinition){
					String fileName = "";
					File fileValue = (File)parentEntity.getValue(nodeDef.getName(), 0);
					if (fileValue!=null){
						if (fileValue.getFilename()!=null)
							fileName = fileValue.getFilename();
					}						
					PhotoField photoField = (PhotoField) ApplicationManager.getUIElement(nodeDef.getId());
					if (photoField!=null)
						photoField.setValue(0, fileName, this.getFormScreenId(), false);					
				}
			}
		}
		sv.post(new Runnable() {
    	    @Override
    	    public void run() {
				sv.scrollTo(0, 0);
			}	
    	});
	}
	
	private void refreshMultipleAttributeScreen(int actionCode){
		//0 - previous, 1 - next, 2 - delete, 3 - add
		if (actionCode==0){
			if (this.currInstanceNo>0){
				this.currInstanceNo--;
				refreshMultipleAttributeScreenField();
			} else {
				return;
			}	
		} else if (actionCode==1) {
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+0, -1));
			if (!nodeDef.isMultiple()){
				//Log.e("parentSINGLE","=="+this.parentEntitySingleAttribute.getName());
				//Log.e("this.currentInstanceNO","=="+this.currInstanceNo);
				//Log.e("-1","=="+(this.parentEntitySingleAttribute.getParent().get(this.parentEntitySingleAttribute.getName(), this.currInstanceNo-11)==null));
				//Log.e("obecny","=="+(this.parentEntitySingleAttribute.getParent().get(this.parentEntitySingleAttribute.getName(), this.currInstanceNo)==null));
				//Log.e("+1","=="+(this.parentEntitySingleAttribute.getParent().get(this.parentEntitySingleAttribute.getName(), this.currInstanceNo+1)==null));
				Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), this.currInstanceNo+1);
				//Log.e("foundNode1!=null","=="+(foundNode!=null));
				if (foundNode!=null){
					//Log.e("foundNode1","=="+foundNode.getName());
					this.currInstanceNo++;
					refreshMultipleAttributeScreenField();
				} else {
					AlertMessage.createPositiveNegativeDialog(FormScreen.this, true, null,
							getResources().getString(R.string.addNewAttributeTitle), 
							getResources().getString(R.string.addNewAttributeMessage),
								getResources().getString(R.string.yes),
								getResources().getString(R.string.no),
					    		new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										FormScreen.this.currInstanceNo++;	
										refreshMultipleAttributeScreenField();
									}
								},
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								},
								null).show();
				}
			} else {
				Log.e("parentMULTIPLE","=="+this.parentEntityMultipleAttribute.getName());
				Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo+1);
				//Log.e("foundNode1!=null","=="+(foundNode!=null));
				if (foundNode!=null){
					///Log.e("foundNode1","=="+foundNode.getName());
					this.currInstanceNo++;
					refreshMultipleAttributeScreenField();
				} else {
					//Log.e("foudnNODE1","==NULL");
					AlertMessage.createPositiveNegativeDialog(FormScreen.this, true, null,
							getResources().getString(R.string.addNewAttributeTitle), 
							getResources().getString(R.string.addNewAttributeMessage),
								getResources().getString(R.string.yes),
								getResources().getString(R.string.no),
					    		new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										FormScreen.this.currInstanceNo++;
										refreshMultipleAttributeScreenField();
									}
								},
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								},
								null).show();
				}
			}
		} else if (actionCode==2){
			
		} else if (actionCode==3) {
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+0, -1));
			if (!nodeDef.isMultiple()){
				//Log.e("parentSINGLE","=="+this.parentEntitySingleAttribute.getName());
				AlertMessage.createPositiveNegativeDialog(FormScreen.this, true, null,
						getResources().getString(R.string.addNewAttributeTitle), 
						getResources().getString(R.string.addNewAttributeMessage),
							getResources().getString(R.string.yes),
							getResources().getString(R.string.no),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(FormScreen.this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+0, -1));
									Node<?> foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo+1);
									while (foundNode!=null){
										FormScreen.this.currInstanceNo++;
										foundNode = FormScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo+1);
									}
									FormScreen.this.currInstanceNo++;										
									refreshMultipleAttributeScreenField();
								}
							},
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							},
							null).show();
			} else {
				//Log.e("parentMULTIPLE","=="+this.parentEntityMultipleAttribute.getName());				
				//Log.e("foudnNODE1","==NULL");
				AlertMessage.createPositiveNegativeDialog(FormScreen.this, true, null,
						getResources().getString(R.string.addNewAttributeTitle), 
						getResources().getString(R.string.addNewAttributeMessage),
							getResources().getString(R.string.yes),
							getResources().getString(R.string.no),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(FormScreen.this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+0, -1));
									Node<?> foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo+1);
									while (foundNode!=null){
										FormScreen.this.currInstanceNo++;
										foundNode = FormScreen.this.parentEntityMultipleAttribute.get(nodeDef.getName(), FormScreen.this.currInstanceNo+1);
									}
									FormScreen.this.currInstanceNo++;										
									refreshMultipleAttributeScreenField();
								}
							},
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							},
							null).show();
			}		
		}				
	}
	
	private void refreshMultipleAttributeScreenField(){
		NodeDefinition currentScreenNodeDef = ApplicationManager.getSurvey().getSchema().getDefinitionById(this.idmlId);
		if (currentScreenNodeDef.getMaxCount()!=null)
			if (currentScreenNodeDef.getMaxCount()<=this.currInstanceNo){
				this.currInstanceNo--;
				AlertMessage.createPositiveDialog(FormScreen.this, true, null,
						getResources().getString(R.string.maxCountTitle), 
						getResources().getString(R.string.maxCountMessage),
							getResources().getString(R.string.okay),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							},
							null).show();
				return;
			}
		
		View firstView = this.ll.getChildAt(0);
		if (firstView instanceof TextView){
			TextView screenTitle = (TextView)firstView;
			screenTitle.setText(this.breadcrumb/*+" "+(this.currInstanceNo+1)*/);
		}
		//Log.e("REFRESHINGentity","=="+this.parentFormScreenId);
		///Entity parentEntity = this.findParentEntity(this.parentFormScreenId);
		Entity parentEntity = this.parentEntityMultipleAttribute;
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
				} else if (nodeDef instanceof NumberAttributeDefinition){
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
					if (codeField!=null){
						//Log.e("refreshMULTattr",this.getFormScreenId()+"=="+this.currInstanceNo);
						codeField.setValue(this.currInstanceNo, loadedValue, this.getFormScreenId(), false);
					}						
				} else if (nodeDef instanceof CoordinateAttributeDefinition){
					String loadedValueLat = "";
					String loadedValueLon = "";
					String loadedSrsId = "";
					Coordinate coordValue = (Coordinate)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
					if (coordValue!=null){
						loadedValueLon = coordValue.getX().toString();
						if (loadedValueLon==null)
							loadedValueLon = "";
						loadedValueLat = coordValue.getY().toString();
						if (loadedValueLat==null)
							loadedValueLat = "";
						loadedSrsId = coordValue.getSrsId().toString();
						if (loadedSrsId==null)
							loadedSrsId = "";
					}						
					CoordinateField coordField = (CoordinateField) ApplicationManager.getUIElement(nodeDef.getId());
					if (coordField!=null)
						coordField.setValue(this.currInstanceNo, loadedValueLon, loadedValueLat, loadedSrsId, this.getFormScreenId(), false);
				} else if (nodeDef instanceof RangeAttributeDefinition){
					String from = "";
					String to = "";
					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
					if (rangeAttrDef.isReal()){
						RealRange rangeValue = (RealRange)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
						if (rangeValue!=null){
							from = rangeValue.getFrom().toString();
							if (from==null)
								from = "";
							to = rangeValue.getTo().toString();
							if (to == null)
								to = "";						
						}
					} else {
						IntegerRange rangeValue = (IntegerRange)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
						if (rangeValue!=null){
							from = rangeValue.getFrom().toString();
							if (from==null)
								from = "";
							to = rangeValue.getTo().toString();
							if (to == null)
								to = "";						
						}
					}															
					RangeField rangeField = (RangeField) ApplicationManager.getUIElement(nodeDef.getId());
					if (rangeField!=null)
						rangeField.setValue(this.currInstanceNo, from+getResources().getString(R.string.rangeSeparator)+to, this.getFormScreenId(), false);
				} else if (nodeDef instanceof DateAttributeDefinition){
					String day = "";
					String month = "";
					String year = "";
					Date dateValue = (Date)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
					String loadedValue = "";
					if (dateValue!=null){
						loadedValue = formatDate(dateValue);
					}	
					DateField dateField = (DateField) ApplicationManager.getUIElement(nodeDef.getId());
					if (dateField!=null)
						dateField.setValue(this.currInstanceNo, loadedValue, this.getFormScreenId(), false);
				} else if (nodeDef instanceof TimeAttributeDefinition){
					String hour = "";
					String minute = "";
					Time timeValue = (Time)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
					if (timeValue!=null){
						hour = timeValue.getHour().toString();
						if (hour==null)
							hour = "";
						minute = timeValue.getMinute().toString();
						if (minute==null)
							minute = "";
					}						
					TimeField timeField = (TimeField) ApplicationManager.getUIElement(nodeDef.getId());
					if (timeField!=null)
						timeField.setValue(this.currInstanceNo, hour+getResources().getString(R.string.timeSeparator)+minute, this.getFormScreenId(), false);					
				} else if (nodeDef instanceof TaxonAttributeDefinition){
					String code = "";
    				String sciName = "";
    				String vernName = "";
    				String vernLang = "";
    				String langVariant = "";
					TaxonOccurrence taxonValue = (TaxonOccurrence)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
					if (taxonValue!=null){
						code = taxonValue.getCode();
	    				sciName = taxonValue.getScientificName();
	    				vernName = taxonValue.getVernacularName();
	    				vernLang = taxonValue.getLanguageCode();
	    				langVariant = taxonValue.getLanguageVariety();	    						
					}
					TaxonField taxonField = (TaxonField) ApplicationManager.getUIElement(nodeDef.getId());
					if (taxonField!=null)
						taxonField.setValue(this.currInstanceNo, code, sciName, vernName, vernLang, langVariant, this.getFormScreenId(), false);
				} else if (nodeDef instanceof FileAttributeDefinition){
					
				}
			}
		}
	}
	
    public void startCamera(PhotoField photoField){
		Intent cameraIntent = new Intent(this, CameraScreen.class); 
		this.startActivityForResult(cameraIntent,getResources().getInteger(R.integer.cameraStarted));
	}
    
    public void startInternalGps(CoordinateField coordField){
		Intent gpsIntent = new Intent(this, GpsActivity.class); 
		this.startActivityForResult(gpsIntent,getResources().getInteger(R.integer.internalGpsStarted));
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {    	
	    super.onActivityResult(requestCode, resultCode, data);
	    try{
	 	    if (requestCode==getResources().getInteger(R.integer.cameraStarted)){
	 	    	if (resultCode==getResources().getInteger(R.integer.photoTaken)){
	 	    		this.photoPath = data.getStringExtra(getResources().getString(R.string.photoPath));
	 	    	}
	 	    } else if (requestCode==getResources().getInteger(R.integer.internalGpsStarted)){
	 	    	if (resultCode==getResources().getInteger(R.integer.internalGpsLocationReceived)){
	 	    		this.latitude = data.getStringExtra(getResources().getString(R.string.latitude));
	 	    		this.longitude = data.getStringExtra(getResources().getString(R.string.longitude));
	 	    		AlertMessage.createPositiveDialog(FormScreen.this, true, null,
							getResources().getString(R.string.gettingCoordsFinishedTitle), 
							getResources().getString(R.string.gettingCoordsSuccessMessage),
								getResources().getString(R.string.okay),
					    		new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								},
								null).show();
	 	    	} else {
	 	    		AlertMessage.createPositiveDialog(FormScreen.this, true, null,
							getResources().getString(R.string.gettingCoordsFinishedTitle), 
							getResources().getString(R.string.gettingCoordsFailureMessage),
								getResources().getString(R.string.okay),
					    		new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								},
								null).show();
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
    
    private String formatDate(Date dateValue){
    	String formattedDateValue = "";
    	String year = String.valueOf(dateValue.getYear());
    	String month = String.valueOf(dateValue.getMonth());
    	if (month!=null){
    		if (month.length()==1){
    			month = "0"+month;
    		}
    	}
    	String day = String.valueOf(dateValue.getDay());
    	if (day!=null){
    		if (day.length()==1){
    			day = "0"+day;
    		}
    	}
		if (dateValue.getMonth()==null && dateValue.getDay()==null && dateValue.getYear()==null){
			formattedDateValue = "";
		} else if (dateValue.getMonth()==null && dateValue.getDay()==null){
			formattedDateValue = year+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator);		    							
		} else if (dateValue.getMonth()==null && dateValue.getYear()==null){
			formattedDateValue = getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+day;
		} else if (dateValue.getDay()==null && dateValue.getYear()==null){
			formattedDateValue = getResources().getString(R.string.dateSeparator)+month+getResources().getString(R.string.dateSeparator);
		} else if (dateValue.getMonth()==null){
			formattedDateValue = year+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+day;		    							
		} else if (dateValue.getDay()==null){
			formattedDateValue = year+getResources().getString(R.string.dateSeparator)+month+getResources().getString(R.string.dateSeparator);
		} else if (dateValue.getYear()==null){
			formattedDateValue = getResources().getString(R.string.dateSeparator)+month+getResources().getString(R.string.dateSeparator)+day;
		} else {
			formattedDateValue = year+getResources().getString(R.string.dateSeparator)+month+getResources().getString(R.string.dateSeparator)+day;
		}
		return formattedDateValue;
    }
}