package org.openforis.collect.android.tabs;

import java.io.File;

import org.openforis.collect.android.R;
import org.openforis.collect.android.idml.StructureReader;
import org.openforis.collect.android.misc.WelcomeScreen;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

public class TabManager extends TabActivity {

	private static final String TAG = "TabManager";
	
	private TabWidget tabWidget;
	private TabHost tabHost;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.applicationwindow);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate"); 
        try{
        	showWelcomeScreen(5000);
        	
        	//creating file structure used by application
        	String sdcardPath = Environment.getExternalStorageDirectory().toString();
			File folder = new File(sdcardPath+getResources().getString(R.string.application_folder));
			folder.mkdirs();
			folder = new File(sdcardPath+getResources().getString(R.string.data_folder));
		    folder.mkdirs();
		    folder = new File(sdcardPath+getResources().getString(R.string.backup_folder));
		    folder.mkdirs();
		    folder = new File(sdcardPath+getResources().getString(R.string.logs_folder));
		    folder.mkdirs();
		    
        	tabHost = getTabHost();
        	tabWidget = tabHost.getTabWidget();
        	
        	//reading form definition
        	StructureReader sReader = new StructureReader(sdcardPath+getResources().getString(R.string.application_folder)+"/test.idm.collect.xml");
        	
        	//adding About tab
        	this.addTab(getResources().getString(R.string.aboutTabTitle), 
        			getResources().getString(R.string.aboutTabTitle), 
        			this.tabWidget.getChildCount(),
        			new Intent(TabManager.this,AboutTab.class),
        			this.calcTabWidth(1),
        			getResources().getInteger(R.integer.tab_height));
        	
        } catch (Exception e){
        	
        }
    }//onCreate
    
    private void addTab(String title, String indicator, int index, Intent content, int width, int height){
    	try{
    		this.tabHost.addTab(tabHost.newTabSpec(getResources().getString(R.string.aboutTabTitle))
                    .setIndicator(getResources().getString(R.string.aboutTabTitle))
                    .setContent(new Intent(TabManager.this,AboutTab.class)));
			this.tabWidget.getChildAt(index).setLayoutParams(new LinearLayout.LayoutParams(width, height));
    	} catch (Exception e){
    		
    	}
    }//addTab
    
    private void showWelcomeScreen(int durationInSec){
    	Intent welcomeIntent = new Intent(TabManager.this,WelcomeScreen.class);
    	welcomeIntent.putExtra("sleepTime", durationInSec);
		startActivity(welcomeIntent);
    }//showWelcomeScreen
    
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