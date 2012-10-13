package org.openforis.collect.android.tabs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.metamodel.ui.UITab;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;

public class TabContainer extends TabActivity {

	private static final String TAG = "TabContainer";
	private TabWidget tabWidget;
	private TabHost tabHost;
	
	private String name;
	private String label;
	
	private Spinner cmbPlotList;
	private ImageButton cmdAddPlot;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.tabcontainer);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        
        tabHost = getTabHost();
    	tabWidget = tabHost.getTabWidget();
    	
    	Intent startingIntent = getIntent();
        this.name = (startingIntent.getStringExtra("tabName")==null)?null:startingIntent.getStringExtra("tabName");
        this.label = (startingIntent.getStringExtra("tabLabel")==null)?null:startingIntent.getStringExtra("tabLabel");
        Collection<EntityDefinition> rootEntitiesDefs = TabManager.schema.getRootEntityDefinitions();
        Log.e("rootsNo","=="+rootEntitiesDefs.size());
        
        Collection<NodeDefinition> formFields = TabManager.schema.getRootEntityDefinitions().get(0).getChildDefinitions();
        boolean hasFields = false;
        EntityDefinition entityDef = null;
        for (NodeDefinition formField : formFields){
        	if (TabManager.survey.getUIOptions().getTab(formField)!=null){
        		if (formField.getClass().equals(EntityDefinition.class)){
        			entityDef = (EntityDefinition)formField;
        		}
            	if (TabManager.survey.getUIOptions().getTab(formField).getName().equals(this.name)
            			&&
            			!formField.getClass().equals(EntityDefinition.class)){
            		
            		hasFields = true;
            		break;
            	}
        	}
        	
        	/*if (TabManager.survey.getUIConfiguration().getTab(formField)!=null){
        		if (formField.getClass().equals(EntityDefinition.class)){
        			entityDef = (EntityDefinition)formField;
        		}
            	if (TabManager.survey.getUIConfiguration().getTab(formField).getName().equals(this.name)
            			&&
            			!formField.getClass().equals(EntityDefinition.class)){
            		
            		hasFields = true;
            		break;
            	}
        	}*/
        }
        
        List<EntityDefinition> rootEntities = TabManager.schema.getRootEntityDefinitions();
        boolean isRootEntity = false;
        for (EntityDefinition rootEntity : rootEntities){
        	if (rootEntity.equals(entityDef)){
        		isRootEntity = true;
        		break;
        	}
        }
        
        this.cmbPlotList = (Spinner) findViewById(R.id.cmbPlotList);
        this.cmdAddPlot = (ImageButton)this.findViewById(R.id.btnNewPlot);
        Log.e("!hasFields","=="+(!hasFields));
        Log.e("entityDef!=null","=="+(entityDef!=null));
        Log.e("!isRootEntity","=="+(!isRootEntity));
        if ((!hasFields)&&(entityDef!=null)&&(!isRootEntity)){//show add button above tabs
            ArrayList<String> options=new ArrayList<String>();
            options.add("plot1");
            options.add("plot2");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,options);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.cmbPlotList.setAdapter(adapter);
            this.cmbPlotList = (Spinner)this.findViewById(R.id.cmbPlotList);
            
               		
    		this.cmdAddPlot.setOnClickListener(new OnClickListener(){
    			@Override
    	        public void onClick(View v) {

    			}    				
    		});
        } else {
        	this.cmbPlotList.setVisibility(View.GONE);
        	this.cmdAddPlot.setVisibility(View.GONE);
        }
    	Log.e("Adding tabs","TABCONTAINER");
        Intent tabIntent;
        int tabNo = TabManager.tabSet.getTabs().size();// TabManager.uiTabsList.size();
        UITab currTab = TabManager.tabSet.getTabs().get(startingIntent.getIntExtra("tabNo", -1));//TabManager.uiTabsList.get(startingIntent.getIntExtra("tabNo", -1));
    	int currLevel = startingIntent.getIntExtra("tabLevel", -1);
    	List<UITab> uiTabs = currTab.getTabs();
    	for (int i=0; i<uiTabs.size();i++){
    		UITab uiTab = uiTabs.get(i);
    		if (uiTab.getTabs().size()>0){
    			tabIntent = new Intent(TabContainer.this, TabContainer.class);
    		}
    		else{
    			tabIntent = new Intent(TabContainer.this, Tab.class);
    		}            		
    		tabIntent.putExtra("tabName", uiTab.getName());
    		tabIntent.putExtra("tabLabel", uiTab.getLabel("EN"));
    		tabIntent.putExtra("tabNo", i);
    		tabIntent.putExtra("tabLevel", currLevel+1);
    		this.addTab(uiTab.getName(), 
    				uiTab.getLabel("EN"),
        			this.tabWidget.getChildCount(),
        			tabIntent,
        			this.calcTabWidth(tabNo),
        			getResources().getInteger(R.integer.tab_height));        	
    	}
    	/*Intent tabIntent;
    	int tabNo = TabManager.uiTabsList.size();
    	UITab currTab = TabManager.uiTabsList.get(startingIntent.getIntExtra("tabNo", -1));
    	int currLevel = startingIntent.getIntExtra("tabLevel", -1);
    	List<UITab> uiTabs = currTab.getTabs();
    	for (int i=0; i<uiTabs.size();i++){
    		UITab uiTab = uiTabs.get(i);
    		if (uiTab.getTabs().size()>0){
    			tabIntent = new Intent(TabContainer.this, TabContainer.class);
    		}
    		else{
    			tabIntent = new Intent(TabContainer.this, Tab.class);
    		}            		
    		tabIntent.putExtra("tabName", uiTab.getName());
    		tabIntent.putExtra("tabLabel", uiTab.getLabel());
    		tabIntent.putExtra("tabNo", i);
    		tabIntent.putExtra("tabLevel", currLevel+1);
    		this.addTab(uiTab.getName(), 
    				uiTab.getLabel(),
        			this.tabWidget.getChildCount(),
        			tabIntent,
        			this.calcTabWidth(tabNo),
        			getResources().getInteger(R.integer.tab_height));        	
    	}*/
	}
	
	private void addTab(String title, String indicator, int index, Intent content, int width, int height){
    	try{
    		this.tabHost.addTab(tabHost.newTabSpec(title)
                    .setIndicator(indicator)
                    .setContent(content));
			this.tabWidget.getChildAt(index).setLayoutParams(new LinearLayout.LayoutParams(width, height));
    	} catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":addTab",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
    	}
    }
    
    private int calcTabWidth(int tabsNo){
    	DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
    	int screenWidth = metrics.widthPixels-(metrics.widthPixels/(15*tabsNo));
    	int tabWidth = getResources().getInteger(R.integer.tab_width);
    	if ((screenWidth/tabsNo)>=tabWidth){//extending tabs to take whole screen
    		tabWidth = screenWidth/(tabsNo);
    	}
    	return tabWidth;
    }
	
}
