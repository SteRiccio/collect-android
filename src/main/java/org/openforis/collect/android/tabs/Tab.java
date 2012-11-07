package org.openforis.collect.android.tabs;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.fields.BooleanField;
import org.openforis.collect.android.fields.CodeField;
import org.openforis.collect.android.fields.DateField;
import org.openforis.collect.android.fields.Field;
import org.openforis.collect.android.fields.MemoField;
import org.openforis.collect.android.fields.NumberField;
import org.openforis.collect.android.fields.RangeField;
import org.openforis.collect.android.fields.Separator;
import org.openforis.collect.android.fields.TaxonField;
import org.openforis.collect.android.fields.TextField;
import org.openforis.collect.android.fields.TimeField;
import org.openforis.collect.android.fields.UIElement;
import org.openforis.idm.metamodel.BooleanAttributeDefinition;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.CodeListItem;
import org.openforis.idm.metamodel.DateAttributeDefinition;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NodeLabel.Type;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
import org.openforis.idm.metamodel.RangeAttributeDefinition;
import org.openforis.idm.metamodel.TextAttributeDefinition;
import org.openforis.idm.metamodel.TimeAttributeDefinition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class Tab extends Activity /*implements OnGesturePerformedListener*/ {

	private static final String TAG = "Tab";
	private String name;
	private String label;
	
	//private GestureLibrary gestureLib;
	
	private List<NodeDefinition> fieldsList;
	public List<UIElement> uiFields;
	
	private ScrollView sv;			
    public LinearLayout ll;
    
    private List<NodeDefinition> multipleEntitiesStack;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        
        Intent startingIntent = getIntent();
        this.name = (startingIntent.getStringExtra("tabName")==null)?null:startingIntent.getStringExtra("tabName");
        this.label = (startingIntent.getStringExtra("tabLabel")==null)?null:startingIntent.getStringExtra("tabLabel");
        
        this.fieldsList = new ArrayList<NodeDefinition>();
        this.uiFields = new ArrayList<UIElement>();
        
        this.sv = new ScrollView(this);
		this.ll = new LinearLayout(this);
		this.ll.setOrientation(android.widget.LinearLayout.VERTICAL);
		this.sv.addView(ll);
		
		this.multipleEntitiesStack = new ArrayList<NodeDefinition>();

        List<NodeDefinition> formFields = TabManager.fieldsList;
        for (NodeDefinition formField : formFields){
        	if (TabManager.getSurvey().getUIOptions().getTab(formField)!=null){
            	if (TabManager.getSurvey().getUIOptions().getTab(formField).getName().equals(this.name)){
            		addUiElement(formField);
            	}
        	}
        }
    	for (int i=0;i<this.multipleEntitiesStack.size();i++){
    		this.addRuler(ViewGroup.LayoutParams.FILL_PARENT, 5, Color.RED, false, -1, null);
    	}

    	for (UIElement formField : this.uiFields){
    			this.ll.addView(formField);
        }
       /* Collection<NodeDefinition> formFields = TabManager.schema.getAllDefinitions();
        formFields = this.sortById(formFields);
        for (NodeDefinition formField : formFields){
        	if (TabManager.survey.getUIConfiguration().getTab(formField)!=null){
            	if (TabManager.survey.getUIConfiguration().getTab(formField).getName().equals(this.name)){
            		addUiElement(formField);
            	}
        	}
        }
    	for (int i=0;i<this.multipleEntitiesStack.size();i++){
    		this.addRuler(ViewGroup.LayoutParams.FILL_PARENT, 5, Color.RED, false, -1);
    	}

        for (UiElement formField : this.uiFields){
        	this.ll.addView(formField);
        }*/
        
		
    	//gestures detection
    	/*GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
        gestureOverlayView.addView(this.sv);
        gestureOverlayView.addOnGesturePerformedListener(this);
        gestureOverlayView.setGestureColor(Color.BLUE);
        gestureOverlayView.setUncertainGestureColor(Color.RED);
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gestureLib.load()) {
        	//finish();
        }
        setContentView(gestureOverlayView);*/
        setContentView(this.sv);
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
	
	@Override 
	public void onBackPressed(){ 
		this.getParent().onBackPressed();
	}

	private void addUiElement(NodeDefinition formField){
		this.fieldsList.add(formField);
		if (!this.multipleEntitiesStack.isEmpty()){
			int stackSize = this.multipleEntitiesStack.size();
			for (int i=0;i<stackSize;i++){				
				NodeDefinition tempNodeDef = this.multipleEntitiesStack.get(this.multipleEntitiesStack.size()-1);
				boolean toBeRemoved = true;
				NodeDefinition parentNodeDef = formField.getParentDefinition();
				while (parentNodeDef!=null){
					if (tempNodeDef.equals(parentNodeDef)){
						toBeRemoved = false;
					}
					parentNodeDef = parentNodeDef.getParentDefinition();
				}
				if (toBeRemoved){;
					this.multipleEntitiesStack.remove(this.multipleEntitiesStack.size()-1);					
					this.addRuler(ViewGroup.LayoutParams.FILL_PARENT, 5, Color.RED, false, -1, null);
				}
			}
		}
		if (!formField.getClass().equals(EntityDefinition.class)){
			UIElement uiEl = createUiField(formField);
			this.uiFields.add(uiEl);
			TabManager.uiElementsMap.put(formField.getId(), uiEl);
		}
		else if (formField.isMultiple()&&(!formField.getName().equals("cluster"))&&(!formField.getName().equals("plot"))&&(!formField.getName().equals("household"))){
			this.addRuler(ViewGroup.LayoutParams.FILL_PARENT, 5, Color.GREEN, true, -1, (EntityDefinition)formField);
			this.multipleEntitiesStack.add(formField);
		}
	}
	
	private Field createUiField(NodeDefinition formField){
		Field uiField = null;
		if (formField.getClass().equals(NumberAttributeDefinition.class)){
			NumberAttributeDefinition numberAttrDef = (NumberAttributeDefinition) formField; 
			uiField = new NumberField(this, numberAttrDef.getId(), numberAttrDef.getLabel(Type.INSTANCE, null), null, null, 
					numberAttrDef.getType().toString(), numberAttrDef.isMultiple());
		} else if (formField.getClass().equals(CodeAttributeDefinition.class)){
			CodeAttributeDefinition codeAttrDef = (CodeAttributeDefinition) formField; 
			
			ArrayList<String> options = new ArrayList<String>();
			ArrayList<String> codes = new ArrayList<String>();
			
			List<CodeListItem> codeListItemsList = codeAttrDef.getList().getItems();
			for (CodeListItem codeListItem : codeListItemsList){
				codes.add(codeListItem.getCode());
				options.add(codeListItem.getLabel(null));
			}

			uiField = new CodeField(this.getParent(), codeAttrDef.getId(), formField.getLabel(Type.INSTANCE, null), formField.getName(),
					codes, options,
					null, true, formField.isMultiple());
		} else if (formField.getClass().equals(BooleanAttributeDefinition.class)){
			BooleanAttributeDefinition booleanField = (BooleanAttributeDefinition) formField;
			uiField = new BooleanField(this, booleanField.getId(), booleanField.getLabel(Type.INSTANCE, null), false, false,
					getResources().getString(R.string.yes), getResources().getString(R.string.no),
					booleanField.isMultiple(), booleanField.isAffirmativeOnly());
		} else if (formField.getClass().equals(TextAttributeDefinition.class)){
			TextAttributeDefinition textField = (TextAttributeDefinition) formField;			
			Object fieldType = textField.getType();
			if (fieldType!=null){
				if(fieldType.toString().equals(getResources().getString(R.string.text_type_long))){//memo
					uiField = new MemoField(this, textField.getId(), textField.getLabel(Type.INSTANCE, null), null, null, textField.isMultiple());
				} else {//short
					uiField = new TextField(this, textField.getId(), textField.getLabel(Type.INSTANCE, null), null, null, textField.isMultiple());
				}
			} else{//no type of text field specified
				uiField = new TextField(this, textField.getId(), textField.getLabel(Type.INSTANCE, null), null, null, textField.isMultiple());
			}
		} else if (formField.getClass().equals(DateAttributeDefinition.class)){
			uiField = new DateField(this, formField.getId(), formField.getLabel(Type.INSTANCE, null), null, null, formField.isMultiple());
		} else if (formField.getClass().equals(TimeAttributeDefinition.class)){
			uiField = new TimeField(this, formField.getId(), formField.getLabel(Type.INSTANCE, null), null, null, formField.isMultiple());
		} else if (formField.getClass().equals(RangeAttributeDefinition.class)){
			uiField = new RangeField(this, formField.getId(), formField.getLabel(Type.INSTANCE, null), null, null, formField.isMultiple());
		} else {
			uiField = new TextField(this, formField.getId(), formField.getLabel(Type.INSTANCE, null), null, null, formField.isMultiple());
			/*uiField = new TaxonField(this, formField.getId(), formField.getLabel(Type.INSTANCE, null), null, null, formField.getLabel(Type.INSTANCE, null),
					null, null,
					null, true,
					formField.isMultiple());*/
		}
		return uiField;
	}
	
	 /*@Override
	    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
			ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
			for (Prediction prediction : predictions) {
				if (prediction.score > 1.0) {
					if (prediction.name.equals(getResources().getString(R.string.save_gesture))){
						AlertMessage.createPositiveNeutralNegativeDialog(Tab.this.getParent(), true, getResources().getDrawable(R.drawable.warningsign),
			    				getResources().getString(R.string.savingDataTitle), getResources().getString(R.string.savingDataMessage),
			    				getResources().getString(R.string.savingToDatabase), getResources().getString(R.string.savingToFile), getResources().getString(R.string.cancel),
			    	    		new DialogInterface.OnClickListener() {
			    					@Override
			    					public void onClick(DialogInterface dialog, int which) {
			    						
			    					}
			    				},
			    				new DialogInterface.OnClickListener() {
			    					@Override
			    					public void onClick(DialogInterface dialog, int which) {
			    						
			    					}
			    				},
			    	    		new DialogInterface.OnClickListener() {
			    					@Override
			    					public void onClick(DialogInterface dialog, int which) {
			    						
			    					}
			    				}).show();
						break;
					}
					else if (prediction.name.equals(getResources().getString(R.string.open_gesture))){
						AlertMessage.createPositiveNegativeDialog(Tab.this.getParent(), true, getResources().getDrawable(R.drawable.warningsign),
			    				getResources().getString(R.string.openingPlotListTitle), getResources().getString(R.string.openingPlotListMessage),
			    				getResources().getString(R.string.yes), getResources().getString(R.string.no),
			    	    		new DialogInterface.OnClickListener() {
			    					@Override
			    					public void onClick(DialogInterface dialog, int which) {
			    						Tab.this.startActivityForResult(new Intent(Tab.this, ClusterChoiceActivity.class),1);
			    					}
			    				},
			    	    		new DialogInterface.OnClickListener() {
			    					@Override
			    					public void onClick(DialogInterface dialog, int which) {
			    						
			    					}
			    				}).show();
						break;
					}				
				}
			}
	    }*/
	 
	private void addRuler(int width, int height, int color, boolean hasArrows, int position, EntityDefinition entityDefn){
		Separator separator = new Separator(this.getParent(), -1 /*id for separator*/, hasArrows, entityDefn);
		separator.setSeparatorColor(color);
		if (position<0){
			this.uiFields.add(separator);
		} else{
			this.uiFields.add(position, separator);
		}
		if (entityDefn!=null)
			TabManager.uiElementsMap.put(entityDefn.getId(), separator);
		else
			TabManager.uiElementsMap.put(-1, separator);
		
	}
}