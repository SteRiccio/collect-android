package org.openforis.collect.android.tabs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.fields.BooleanField;
import org.openforis.collect.android.fields.CodeField;
import org.openforis.collect.android.fields.Field;
import org.openforis.collect.android.fields.NumberField;
import org.openforis.collect.android.fields.TextField;
import org.openforis.collect.android.lists.ClusterChoiceActivity;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.idm.metamodel.BooleanAttributeDefinition;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.CodeListItem;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
import org.openforis.idm.metamodel.TextAttributeDefinition;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class Tab extends Activity implements OnGesturePerformedListener {

	private static final String TAG = "Tab";
	private String name;
	private String label;
	
	private GestureLibrary gestureLib;
	
	private List<NodeDefinition> fieldsList;
	public List<Field> uiFields;
	
	private ScrollView sv;			
    public LinearLayout ll;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        
        Intent startingIntent = getIntent();
        this.name = (startingIntent.getStringExtra("tabName")==null)?null:startingIntent.getStringExtra("tabName");
        this.label = (startingIntent.getStringExtra("tabLabel")==null)?null:startingIntent.getStringExtra("tabLabel");
        
        this.fieldsList = new ArrayList<NodeDefinition>();
        this.uiFields = new ArrayList<Field>();
        
        this.sv = new ScrollView(this);
		this.ll = new LinearLayout(this);
		this.ll.setOrientation(android.widget.LinearLayout.VERTICAL);
		this.sv.addView(ll);
		
        //Log.e("iloscDEFINITIONS","=="+TabManager.schema.getAllDefinitions().size());
        Collection<NodeDefinition> formFields = TabManager.schema.getAllDefinitions();
        for (NodeDefinition formField : formFields){
        	if (TabManager.survey.getUIConfiguration().getTab(formField)!=null){
            	if (TabManager.survey.getUIConfiguration().getTab(formField).getName().equals(this.name)){
            		addField(formField);
            	}
        	}

        }
        for (Field formField : this.uiFields){
        	this.ll.addView(formField);
        }
        
    	//gestures detection
    	GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
        gestureOverlayView.addView(this.sv/*inflate*/);
        gestureOverlayView.addOnGesturePerformedListener(this);
        gestureOverlayView.setGestureColor(Color.TRANSPARENT);
        gestureOverlayView.setUncertainGestureColor(Color.TRANSPARENT);
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gestureLib.load()) {
        	//finish();
        }
        setContentView(gestureOverlayView);
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

	private void addField(NodeDefinition formField){
		this.fieldsList.add(formField);
		if (!formField.getClass().equals(EntityDefinition.class)){
			this.uiFields.add(createUiField(formField));
		}
	}
	
	private Field createUiField(NodeDefinition formField){
		Field uiField = null;
		if (formField.getClass().equals(NumberAttributeDefinition.class)){
			uiField = new NumberField(this, formField.getLabel(null, null), null, formField.getLabel(null, null));
		} else if (formField.getClass().equals(CodeAttributeDefinition.class)){
			CodeAttributeDefinition codeAttrDef = (CodeAttributeDefinition) formField; 
			
			ArrayList<String> options = new ArrayList<String>();
			ArrayList<String> codes = new ArrayList<String>();
			
			List<CodeListItem> codeListItemsList = codeAttrDef.getList().getItems();
			for (CodeListItem codeListItem : codeListItemsList){
				codes.add(codeListItem.getCode());
				options.add(codeListItem.getLabel(null));
			}

			uiField = new CodeField(this.getParent(), formField.getLabel(null, null), formField.getName(),
					codes, options,
					null, true);
		} else if (formField.getClass().equals(BooleanAttributeDefinition.class)){
			uiField = new BooleanField(this, formField.getLabel(null, null), false);
		} else if (formField.getClass().equals(TextAttributeDefinition.class)){
			uiField = new TextField(this, formField.getLabel(null, null), null, formField.getLabel(null, null));
		} else {
			uiField = new TextField(this, formField.getLabel(null, null), null, formField.getLabel(null, null));
		}
		return uiField;
	}
	
	 @Override
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
	    }
}