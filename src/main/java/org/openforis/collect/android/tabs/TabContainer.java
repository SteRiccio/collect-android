package org.openforis.collect.android.tabs;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.model.ui.UITab;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

public class TabContainer extends TabActivity {

	private static final String TAG = "TabContainer";
	private TabWidget tabWidget;
	private TabHost tabHost;
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.applicationwindow);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        
        tabHost = getTabHost();
    	tabWidget = tabHost.getTabWidget();
    	
    	Intent tabIntent;
    	int tabNo = TabManager.uiTabsList.size();
    	Intent startingIntent = getIntent();
    	UITab currTab = TabManager.uiTabsList.get(startingIntent.getIntExtra("tabNo", -1));
    	int currLevel = startingIntent.getIntExtra("tabLevel", -1);
    	List<UITab> uiTabs = currTab.getTabs();
    	for (int i=0; i<uiTabs.size();i++){
    		UITab uiTab = uiTabs.get(i);
    		/*Log.e("tabName","=="+uiTab.getName());
    		Log.e("tabLabel","=="+uiTab.getLabel());
    		Log.e("subtabsNo","=="+uiTab.getTabs().size());*/
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
    	}
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
