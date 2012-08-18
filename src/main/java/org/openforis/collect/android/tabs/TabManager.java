package org.openforis.collect.android.tabs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.openforis.collect.android.R;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.android.misc.WelcomeScreen;
import org.openforis.collect.context.CollectContext;
import org.openforis.collect.persistence.xml.CollectIdmlBindingContext;
import org.openforis.idm.metamodel.Survey;
import org.openforis.idm.metamodel.SurveyContext;
import org.openforis.idm.metamodel.xml.InvalidIdmlException;
import org.openforis.idm.metamodel.xml.SurveyUnmarshaller;

import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
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
        	showWelcomeScreen(0);
        	
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
        	try {
            	InputStream is = this.getClass().getClassLoader().getResourceAsStream("test.idm.xml");
                byte[] buffer = new byte[1000];
    			is.read(buffer);
    			CollectIdmlBindingContext idmlBindingContext = new CollectIdmlBindingContext((SurveyContext) new CollectContext());
    			SurveyUnmarshaller su = idmlBindingContext.createSurveyUnmarshaller();
    			Survey survey = su.unmarshal(is);
    			survey.setName("Test");
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (InvalidIdmlException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	
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
    public void onBackPressed() {
    	try{
    		AlertMessage.createYesNoDialog(TabManager.this, false, getResources().getDrawable(R.drawable.warningsign),
    				getResources().getString(R.string.exitAppTitle), getResources().getString(R.string.exitAppMessage),
    				getResources().getString(R.string.yes), getResources().getString(R.string.no),
    	    		new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						TabManager.this.finish();
    					}
    				},
    	    		new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						
    					}
    				}).show();
    	}catch (Exception e){
    		RunnableHandler.reportException(e,TAG,"onBackPressed",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
    	}
    }
    
    private void addTab(String title, String indicator, int index, Intent content, int width, int height){
    	try{
    		this.tabHost.addTab(tabHost.newTabSpec(getResources().getString(R.string.aboutTabTitle))
                    .setIndicator(getResources().getString(R.string.aboutTabTitle))
                    .setContent(new Intent(TabManager.this,AboutTab.class)));
			this.tabWidget.getChildAt(index).setLayoutParams(new LinearLayout.LayoutParams(width, height));
    	} catch (Exception e){
    		RunnableHandler.reportException(e,TAG,"addTab",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
    	}
    }
    
    private void showWelcomeScreen(int durationInSec){
    	Intent welcomeIntent = new Intent(TabManager.this,WelcomeScreen.class);
    	welcomeIntent.putExtra("sleepTime", durationInSec);
		startActivity(welcomeIntent);
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