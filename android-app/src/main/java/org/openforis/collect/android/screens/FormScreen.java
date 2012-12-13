package org.openforis.collect.android.screens;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.fields.BooleanField;
import org.openforis.collect.android.fields.CodeField;
import org.openforis.collect.android.fields.CoordinateField;
import org.openforis.collect.android.fields.DateField;
import org.openforis.collect.android.fields.Field;
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
import org.openforis.idm.metamodel.BooleanAttributeDefinition;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
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

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
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

public class FormScreen extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "FormScreen";

	private ScrollView sv;			
    private LinearLayout ll;

	private ArrayList<String> detailsLists;
	
	private int intentType;
	
	private int idmlId;
	private int currInstanceNo;
	private int numberOfInstances;
	private String parentFormScreenId;
	
	private ArrayList<List<String>> values;
	
	String breadcrumb;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        	Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);                                  
            
            this.sv = new ScrollView(this);
    		this.ll = new LinearLayout(this);
    		this.ll.setOrientation(android.widget.LinearLayout.VERTICAL);
    		this.sv.addView(ll);
    		ArrayList<List<String>> keysLists1 = new ArrayList<List<String>>();
    		ArrayList<String> key1 = new ArrayList<String>();
    		key1.add("task");
    		key1.add("id");
    		keysLists1.add(key1);
    		
    		ArrayList<List<String>> keysLists2 = new ArrayList<List<String>>();
    		ArrayList<String> key2 = new ArrayList<String>();
    		key2.add("type2");
    		key2.add("hs_general2");
    		ArrayList<String> key3 = new ArrayList<String>();
    		key3.add("type3");
    		key3.add("hs_general3");
    		keysLists2.add(key2);
    		keysLists2.add(key3);
    		
    		ArrayList<List<String>> detailsLists1 = new ArrayList<List<String>>();
    		ArrayList<String> detail1 = new ArrayList<String>();
    		detail1.add("task");
    		detail1.add("person");
    		detail1.add("date");
    		detail1.add("id");
    		detail1.add("region");
    		detail1.add("district");
    		detail1.add("crew_no");
    		detail1.add("map_sheet");
    		detailsLists1.add(detail1);
    		
    		ArrayList<List<String>> detailsLists2 = new ArrayList<List<String>>();
    		ArrayList<String> detail2 = new ArrayList<String>();
    		detail2.add("type");
    		detail2.add("date");
    		detail2.add("person");
    		detail2.add("status");
    		detail2.add("actor");
    		detailsLists2.add(detail2);
    		
    		ArrayList<String> tableColHeaders = new ArrayList<String>();
    		tableColHeaders.add("ID");
    		tableColHeaders.add("Value");
    		ArrayList<List<String>> tableRowLists = new ArrayList<List<String>>();
    		ArrayList<String> row1 = new ArrayList<String>();
    		row1.add("1");
    		row1.add("value1");
    		tableRowLists.add(row1);
    		ArrayList<String> row2 = new ArrayList<String>();
    		row2.add("2");
    		row2.add("value2");
    		tableRowLists.add(row2);
    		ArrayList<String> row3 = new ArrayList<String>();
    		row3.add("3");
    		row3.add("value3");
    		tableRowLists.add(row3);
    		
    		
    		//breadcrumb of the screen
    		Intent startingIntent = getIntent();
    		this.breadcrumb = startingIntent.getStringExtra(getResources().getString(R.string.breadcrumb));
    		this.intentType = startingIntent.getIntExtra(getResources().getString(R.string.intentType),-1);
    		this.idmlId = startingIntent.getIntExtra(getResources().getString(R.string.idmlId),-1);
    		this.currInstanceNo = startingIntent.getIntExtra(getResources().getString(R.string.instanceNo),-1);
    		this.numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances),-1);
    		this.parentFormScreenId = startingIntent.getStringExtra(getResources().getString(R.string.parentFormScreenId));;
    		
    		ApplicationManager.formScreensMap.put(getFormScreenId(), this);
    		Log.e("FORM SCREEN ID","=="+getFormScreenId());
    		
    		TextView breadcrumb = new TextView(this);
    		breadcrumb.setText(this.breadcrumb);
    		breadcrumb.setTextSize(getResources().getInteger(R.integer.breadcrumbFontSize));
    		this.ll.addView(breadcrumb);
    		
    		int fieldsNo = startingIntent.getExtras().size()-1;
    		for (int i=0;i<fieldsNo;i++){
    			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+i, -1));
    			if ((nodeDef instanceof EntityDefinition)&&(nodeDef.isMultiple())){
    				EntityDefinition entityDef = (EntityDefinition)nodeDef;
    				
    				int entityCardinality = 1;
					ArrayList<List<String>> keysLists = new ArrayList<List<String>>();
    				ArrayList<List<String>> detailsLists = new ArrayList<List<String>>();
    				for (int j=0;j<entityCardinality;j++){
        				ArrayList<String> keysList = new ArrayList<String>();
        				List<NodeDefinition> fieldsList = entityDef.getChildDefinitions();  
        				/*List<AttributeDefinition> attrDefsList = entityDef.getKeyAttributeDefinitions();
        				Log.e("#entityKeysNo","=="+attrDefsList.size());
        				for (AttributeDefinition attrDef : attrDefsList){
        					Log.e("keyAttr"+attrDef.getName(),"==");    					
        					keysList.add(attrDef.getName());
        				}
        				keysLists.add(keysList);*/
        				
        				ArrayList<String> detailsList = new ArrayList<String>();
        				  				
        				for (NodeDefinition childDef : fieldsList){
        					detailsList.add(childDef.getName());
        				}
        				detailsLists.add(detailsList);
    				}    
    				SummaryList summaryListView = new SummaryList(this,entityDef.getId(), entityDef, calcNoOfCharsFitInOneLine(),
    						entityDef.getName(),keysLists2,detailsLists,
    						this);
    				summaryListView.setOnClickListener(this);
    				summaryListView.setId(nodeDef.getId());
    				this.ll.addView(summaryListView);
    			} else if (nodeDef instanceof TextAttributeDefinition){
    				if (!nodeDef.isMultiple()||(this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent))){
    					TextField textField= new TextField(this, nodeDef.getId(), nodeDef.getLabel(Type.INSTANCE, null), null, null, false, false);
        				textField.setOnClickListener(this);
        				textField.setId(nodeDef.getId());
        				int numberOfInstances = startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances), -1);
        				this.values = new ArrayList<List<String>>();
        				ArrayList<String> instanceValues = new ArrayList<String>();
        				for (int k=0;k<numberOfInstances;k++){
        					instanceValues = startingIntent.getStringArrayListExtra(getResources().getString(R.string.instanceValues)+k);
        					values.add(instanceValues);
        				}
        				if (values.size()>this.currInstanceNo){
        					textField.setValue(values.get(this.currInstanceNo).get(1));	
        				}
        				this.ll.addView(textField);
    				} else {
    					SummaryTable summaryTableView = new SummaryTable(this, nodeDef.getId(), nodeDef.getName(), tableColHeaders, tableRowLists, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}    				
    			} else if (nodeDef instanceof NumberAttributeDefinition){
    				if (!nodeDef.isMultiple()||(this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent))){
    					NumberAttributeDefinition numberAttrDef = (NumberAttributeDefinition)nodeDef;
        				NumberField numberField= new NumberField(this, nodeDef.getId(), nodeDef.getLabel(Type.INSTANCE, null), null, null, numberAttrDef.getType().toString(), numberAttrDef.isMultiple(), false);
        				numberField.setOnClickListener(this);
        				numberField.setId(nodeDef.getId());
        				this.ll.addView(numberField);
    				} else {
    					SummaryTable summaryTableView = new SummaryTable(this, nodeDef.getId(), nodeDef.getName(), tableColHeaders, tableRowLists, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}   				
    			} else if (nodeDef instanceof CodeAttributeDefinition){
    				if (!nodeDef.isMultiple()||(this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent))){
    					CodeAttributeDefinition codeAttrDef = (CodeAttributeDefinition)nodeDef;
        				CodeField codeField= new CodeField(this, nodeDef.getId(), nodeDef.getLabel(Type.INSTANCE, null), "select code", detail2, detail2, null, true, codeAttrDef.isMultiple(), false);
        				codeField.setOnClickListener(this);
        				codeField.setId(nodeDef.getId());
        				this.ll.addView(codeField);
    				} else {
    					SummaryTable summaryTableView = new SummaryTable(this, nodeDef.getId(), nodeDef.getName(), tableColHeaders, tableRowLists, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof BooleanAttributeDefinition){
    				if (!nodeDef.isMultiple()||(this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent))){
    					BooleanAttributeDefinition booleanAttrDef = (BooleanAttributeDefinition)nodeDef;
        				BooleanField booleanField= new BooleanField(this, nodeDef.getId(), nodeDef.getLabel(Type.INSTANCE, null), false, false, null, null, booleanAttrDef.isMultiple(), booleanAttrDef.isAffirmativeOnly(), false);
        				booleanField.setOnClickListener(this);
        				booleanField.setId(nodeDef.getId());
        				this.ll.addView(booleanField);
    				} else {
    					SummaryTable summaryTableView = new SummaryTable(this, nodeDef.getId(), nodeDef.getName(), tableColHeaders, tableRowLists, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof CoordinateAttributeDefinition){
    				if (!nodeDef.isMultiple()||(this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent))){
    					CoordinateAttributeDefinition coordAttrDef = (CoordinateAttributeDefinition)nodeDef;
        				CoordinateField coordinateField= new CoordinateField(this, nodeDef.getId(), nodeDef.getLabel(Type.INSTANCE, null), null, null, null, null, coordAttrDef.isMultiple(), false);
        				coordinateField.setOnClickListener(this);
        				coordinateField.setId(nodeDef.getId());
        				this.ll.addView(coordinateField);
    				} else {
    					SummaryTable summaryTableView = new SummaryTable(this, nodeDef.getId(), nodeDef.getName(), tableColHeaders, tableRowLists, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof DateAttributeDefinition){
    				if (!nodeDef.isMultiple()||(this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent))){
    					DateAttributeDefinition dateAttrDef = (DateAttributeDefinition)nodeDef;
        				DateField dateField= new DateField(this, nodeDef.getId(), nodeDef.getLabel(Type.INSTANCE, null), null, null, dateAttrDef.isMultiple(), false);
        				dateField.setOnClickListener(this);
        				dateField.setId(nodeDef.getId());
        				ApplicationManager.uiElementsMap.put(dateField.getId(), dateField);
        				this.ll.addView(dateField);
    				} else {
    					SummaryTable summaryTableView = new SummaryTable(this, nodeDef.getId(), nodeDef.getName(), tableColHeaders, tableRowLists, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof RangeAttributeDefinition){
    				if (!nodeDef.isMultiple()||(this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent))){
    					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
        				RangeField rangeField= new RangeField(this, nodeDef.getId(), nodeDef.getLabel(Type.INSTANCE, null), null, null, rangeAttrDef.isMultiple(), false);
        				rangeField.setOnClickListener(this);
        				rangeField.setId(nodeDef.getId());
        				this.ll.addView(rangeField);
    				} else {
    					SummaryTable summaryTableView = new SummaryTable(this, nodeDef.getId(), nodeDef.getName(), tableColHeaders, tableRowLists, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof TaxonAttributeDefinition){
    				if (!nodeDef.isMultiple()||(this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent))){
    					TaxonAttributeDefinition taxonAttrDef = (TaxonAttributeDefinition)nodeDef;
        				TaxonField taxonField= new TaxonField(this, nodeDef.getId(), nodeDef.getLabel(Type.INSTANCE, null), null, null, null, row3, row3, null, false, taxonAttrDef.isMultiple(), false);
        				taxonField.setOnClickListener(this);
        				taxonField.setId(nodeDef.getId());
        				this.ll.addView(taxonField);
    				} else {
    					SummaryTable summaryTableView = new SummaryTable(this, nodeDef.getId(), nodeDef.getName(), tableColHeaders, tableRowLists, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof TimeAttributeDefinition){
    				if (!nodeDef.isMultiple()||(this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent))){
    					TimeAttributeDefinition timeAttrDef = (TimeAttributeDefinition)nodeDef;
        				TimeField timeField= new TimeField(this, nodeDef.getId(), nodeDef.getLabel(Type.INSTANCE, null), null, null, timeAttrDef.isMultiple(), false);
        				timeField.setOnClickListener(this);
        				timeField.setId(nodeDef.getId());
           				ApplicationManager.uiElementsMap.put(timeField.getId(), timeField);
        				this.ll.addView(timeField);
    				} else {
    					SummaryTable summaryTableView = new SummaryTable(this, nodeDef.getId(), nodeDef.getName(), tableColHeaders, tableRowLists, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			}
    		}
			if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
				Log.e("multiple","ATTRIBUTE");
				this.ll.addView(arrangeButtonsInLine(new Button(this),"LEFT",new Button(this),"RIGHT",this));
			} else {
				Log.e("multiple","ENTITY");
			}
    		setContentView(this.sv);
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
		Log.e("clickedVIEW",arg0.getClass()+"=="+arg0.getId());
		if (arg0 instanceof SummaryList){
			Log.e("summary","list");
			SummaryList temp = (SummaryList)arg0;
			Log.e("nazwa","=="+temp.getTitle());
		} else if (arg0 instanceof Button){
			Button btn = (Button)arg0;
			if (btn.getId()==getResources().getInteger(R.integer.leftButtonMultipleAttribute)){
				Log.e("CLICKED","LEFT");
				if (this.currInstanceNo>0){
					View fieldView = (View)this.ll.getChildAt(1);
					String currValue = "";
					if (fieldView instanceof TextField){
						TextField tempTextField = (TextField)fieldView;
						currValue = tempTextField.getValue();
					}
					values.get(this.currInstanceNo).set(1, currValue);
					this.currInstanceNo--;
					if (fieldView instanceof TextField){
						TextField tempTextField = (TextField)fieldView;
						tempTextField.setValue(this.values.get(this.currInstanceNo).get(1));
					}
				}
			} else if (btn.getId()==getResources().getInteger(R.integer.rightButtonMultipleAttribute)){
				Log.e("CLICKED","RIGHT");
				View fieldView = (View)this.ll.getChildAt(1);
				String currValue = "";
				if (fieldView instanceof TextField){
					TextField tempTextField = (TextField)fieldView;
					currValue = tempTextField.getValue();
				}
				this.values.get(this.currInstanceNo).set(1, currValue);
				this.currInstanceNo++;
				if (this.currInstanceNo<this.numberOfInstances){
					if (fieldView instanceof TextField){
						TextField tempTextField = (TextField)fieldView;
						tempTextField.setValue(this.values.get(this.currInstanceNo).get(1));
					}
				} else {//new instance
					ArrayList<String> newValue = new ArrayList<String>();
					newValue.add(""+this.currInstanceNo);
					newValue.add("");
					this.values.add(this.currInstanceNo,newValue);
					if (fieldView instanceof TextField){
						TextField tempTextField = (TextField)fieldView;
						tempTextField.setValue("");
					}
					this.numberOfInstances++;
				}
				
			}
		} else if (arg0 instanceof TextView){
			Log.e("summary","list row");
			TextView tv = (TextView)arg0;
			Log.e("klikniety","id"+tv.getId()+"=="+tv.getText().toString());
			Log.e("parentClass","=="+arg0.getParent().getParent().getParent().getParent());
			Object parentView = arg0.getParent().getParent().getParent().getParent();
			if (parentView instanceof SummaryList){
				SummaryList temp = (SummaryList)arg0.getParent().getParent().getParent().getParent();
				Log.e("nazwa","=="+temp.getTitle());	
				this.startActivity(this.prepareIntentForNewScreen(temp));				
			} else if (parentView instanceof SummaryTable){
				SummaryTable temp = (SummaryTable)parentView;
				Log.e("nazwa","=="+temp.getTitle());	
				this.startActivity(this.prepareIntentForMultipleField(temp, tv.getId(), temp.getValues()));
			}			
		}
	}
	
	private Intent prepareIntentForNewScreen(SummaryList summaryList){
		Intent intent = new Intent(this,FormScreen.class);
		intent.putExtra(getResources().getString(R.string.breadcrumb), this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryList.getTitle());
		intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.multipleEntityIntent));
		intent.putExtra(getResources().getString(R.string.idmlId), summaryList.getId());
		intent.putExtra(getResources().getString(R.string.instanceNo), 0);
		intent.putExtra(getResources().getString(R.string.parentFormScreenId), this.getFormScreenId());
        //List<NodeDefinition> formFields = ApplicationManager.fieldsDefList;
        List<NodeDefinition> entityAttributes = summaryList.getEntityDefinition().getChildDefinitions();
        int counter = 0;
        for (NodeDefinition formField : entityAttributes){
        	//NodeDefinition parentDef = formField.getParentDefinition();
        	//if (parentDef!=null){
        		//if (parentDef instanceof EntityDefinition){
        			//if (parentDef.getId()==summaryList.getId()){
        				intent.putExtra(getResources().getString(R.string.attributeId)+counter, formField.getId());
        				counter++;
        			//}
        		//}
        	//}
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
    	return this.parentFormScreenId+getResources().getString(R.string.valuesNotVisibleSign)+this.idmlId+getResources().getString(R.string.valuesSeparator)+this.currInstanceNo;
    }
}