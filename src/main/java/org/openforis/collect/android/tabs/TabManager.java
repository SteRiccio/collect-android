package org.openforis.collect.android.tabs;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import org.openforis.collect.android.R;
import org.openforis.collect.android.lists.ClusterChoiceActivity;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.android.misc.WelcomeScreen;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.CollectSurveyContext;
import org.openforis.collect.persistence.xml.CollectIdmlBindingContext;
import org.openforis.idm.metamodel.validation.Validator;
import org.openforis.idm.metamodel.xml.SurveyUnmarshaller;
import org.openforis.idm.model.expression.ExpressionFactory;

import android.app.TabActivity;
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
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

public class TabManager extends TabActivity implements OnGesturePerformedListener {

	private static final String TAG = "TabManager";
	private GestureLibrary gestureLib;
	private TabWidget tabWidget;
	private TabHost tabHost;

    /*private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.applicationwindow);
            Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
            
        	showWelcomeScreen(000);
        	
        	//gestures detection
        	GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
            View inflate = getLayoutInflater().inflate(R.layout.applicationwindow, null);
            gestureOverlayView.addView(inflate);
            gestureOverlayView.addOnGesturePerformedListener(this);            
            gestureOverlayView.setGestureColor(Color.TRANSPARENT);
            gestureOverlayView.setUncertainGestureColor(Color.TRANSPARENT);
            gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
            if (!gestureLib.load()) {
            	//finish();
            }
            setContentView(gestureOverlayView);
            //swipe detection
        	/*gestureDetector = new GestureDetector(new SwipeDetector(this));
            gestureListener = new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            };
            Button btn = (Button)findViewById(R.id.btnFooterSave);
            btn.setOnTouchListener(gestureListener);*/
        	
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

        	//startActivity(new Intent(TabManager.this, GpsReader.class));
        	
        	//reading form definition
        	/*FileInputStream fis = new FileInputStream(sdcardPath+getResources().getString(R.string.formDefinitionFile));
        	IdmlBindingContext idmlBindingContext = new IdmlBindingContext();
        	UIConfigurationAdapter configurationAdapter = new UIConfigurationAdapter();
        	idmlBindingContext.setConfigurationAdapter(configurationAdapter);
        	SurveyUnmarshaller surveyUnmarshaller = idmlBindingContext.createSurveyUnmarshaller();
			long startTime = System.currentTimeMillis();
			CollectSurvey survey = (CollectSurvey) surveyUnmarshaller.unmarshal(fis);
        	//InputStream is = this.getClass().getClassLoader().getResourceAsStream(getResources().getString(R.string.formDefinitionFile));
        	//Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        	*/
        	/*Survey survey = new Survey();
        	IdmlBindingContext idmlBindingContext = new IdmlBindingContext(survey.getContext());
			SurveyUnmarshaller su = idmlBindingContext.createSurveyUnmarshaller();
			long startTime = System.currentTimeMillis();
			FileInputStream fis = new FileInputStream(sdcardPath+getResources().getString(R.string.formDefinitionFile));
			Log.e("preparing form file","=="+((System.currentTimeMillis()-startTime))+"ms");
			startTime = System.currentTimeMillis();
			survey = su.unmarshal(fis);
			Log.e("XML","PARSED in "+((System.currentTimeMillis()-startTime)/1000)+"s");
        	/*CollectSurvey survey = new CollectSurvey();
        	ExpressionFactory expressionFactory = new ExpressionFactory();        	
        	Validator validator = new Validator();
        	SurveyManager surveyManager = new SurveyManager();
        	surveyManager.importModel(survey);
        	
    		CollectSurveyContext surveyContext = new CollectSurveyContext(expressionFactory, validator, null);
    		CollectIdmlBindingContext idmlBindingContext = new CollectIdmlBindingContext(surveyContext);
    		idmlBindingContext.setConfigurationAdapter(new ConfigurationAdapter());
    		SurveyUnmarshaller surveyUnmarshaller = idmlBindingContext.createSurveyUnmarshaller();*/
        	FileInputStream fis = new FileInputStream(sdcardPath+getResources().getString(R.string.formDefinitionFile));
        	ExpressionFactory expressionFactory = new ExpressionFactory();
        	Log.e("Expression","FACTORY");
        	Validator validator = new Validator();
        	Log.e("VALIDATOR","==");
        	CollectSurveyContext surveyContext = new CollectSurveyContext(expressionFactory, validator, null);
        	Log.e("SURVEY","CONTEXT");
    		CollectIdmlBindingContext idmlBindingContext = new CollectIdmlBindingContext(surveyContext);
    		Log.e("COLLECTIDMLBINDING","CONTEXT");
    		SurveyUnmarshaller surveyUnmarshaller = idmlBindingContext.createSurveyUnmarshaller();
    		Log.e("SURVEY","UNMARSHALLER");
    		CollectSurvey survey = (CollectSurvey) surveyUnmarshaller.unmarshal(fis);
    		Log.e("KONIEC","====");
        	/*IdmlBindingContext idmlBindingContext = new IdmlBindingContext();
        	//UIConfigurationAdapter configurationAdapter = new UIConfigurationAdapter();
        	//idmlBindingContext.setConfigurationAdapter(configurationAdapter);
        	SurveyUnmarshaller surveyUnmarshaller = idmlBindingContext.createSurveyUnmarshaller();
			long startTime = System.currentTimeMillis();
			Survey survey = (Survey) surveyUnmarshaller.unmarshal(fis);
    		
    		//CollectSurvey survey = (CollectSurvey) surveyUnmarshaller.unmarshal(fis);
    		Log.e("XML","PARSED in "+((System.currentTimeMillis()-startTime)/1000)+"s");
    		survey.setName("archenland1");
        	
			
			Log.e("surveyNAME","=="+survey.getName());
			Log.e("surveyURI","=="+survey.getUri());
			Log.e("surveyDESC","=="+survey.getDescription(null));
			Log.e("iloscCODELISTS","=="+survey.getCodeLists().size());
			Log.e("unit","=="+survey.getUnits().get(0).getName());
			
			//adding all tabs from form definition
        	List<Configuration> configList = survey.getConfigurations();
        	Log.e("configList.size","=="+configList.size());
        	if (configList.size()>0){
        		UIConfiguration uiConfig = (UIConfiguration)configList.get(0);
            	List<UITabDefinition> uiTabDefList = uiConfig.getTabDefinitions();
            	Log.e("uiTabDefList.size","=="+uiTabDefList.size());
            	UITabDefinition uiTabDef = uiTabDefList.get(0);
            	List<UITab> uiTabsList = uiTabDef.getTabs();
            	Log.e("uiTabsList.size","=="+uiTabsList.size());
            	Intent tabIntent;
            	for (UITab uiTab : uiTabsList){
            		Log.e("tabName","=="+uiTab.getName());
            		Log.e("tabLabel","=="+uiTab.getLabel());
            		Log.e("subtabsNo","=="+uiTab.getTabs().size());
            		tabIntent = new Intent(TabManager.this,Tab.class);
            		tabIntent.putExtra("tabName", uiTab.getName());
            		tabIntent.putExtra("tabLabel", uiTab.getLabel());
            		//tabIntent.putExtra(name, value);
            		this.addTab(uiTab.getLabel(), 
            				uiTab.getName(), 
                			this.tabWidget.getChildCount(),
                			tabIntent,
                			this.calcTabWidth(1),
                			getResources().getInteger(R.integer.tab_height));
            		Log.e("==========","==========");
            	}	
        	}
			
        	//InputStream is = new FileInputStream(sdcardPath+getResources().getString(R.string.application_folder)+getResources().getString(R.string.formDefinitionFile));
            /*byte[] buffer = new byte[1000];
			is.read(buffer);
			String str = new String(buffer,"UTF8");
			Log.e("FILE","=="+str);
			CollectIdmlBindingContext idmlBindingContext = new CollectIdmlBindingContext((SurveyContext) new CollectContext());
			SurveyUnmarshaller su = idmlBindingContext.createSurveyUnmarshaller();
			CollectSurvey survey = (CollectSurvey) su.unmarshal(is);
			Log.e("surveyName","=="+survey.getName());
        
			//adding all tabs from form definition
        	List<Configuration> configList = survey.getConfiguration();
        	Log.e("configList.size","=="+configList.size());
        	UIConfiguration uiConfig = (UIConfiguration)configList.get(0);
        	List<UITabDefinition> uiTabDefList = uiConfig.getTabDefinitions();
        	UITabDefinition uiTabDef = uiTabDefList.get(0);
        	List<UITab> uiTabsList = uiTabDef.getTabs();
        	Intent tabIntent;
        	for (UITab uiTab : uiTabsList){
        		Log.e("tabName","=="+uiTab.getName());
        		Log.e("tabLabel","=="+uiTab.getLabel());
        		Log.e("subtabsNo","=="+uiTab.getTabs().size());
        		tabIntent = new Intent(TabManager.this,Tab.class);
        		tabIntent.putExtra("tabName", uiTab.getName());
        		tabIntent.putExtra("tabLabel", uiTab.getLabel());
        		//tabIntent.putExtra(name, value);
        		this.addTab(uiTab.getLabel(), 
        				uiTab.getName(), 
            			this.tabWidget.getChildCount(),
            			tabIntent,
            			this.calcTabWidth(1),
            			getResources().getInteger(R.integer.tab_height));
        		Log.e("==========","==========");
        	}*/
        	//adding About tab
        	this.addTab(getResources().getString(R.string.aboutTabTitle), 
        			getResources().getString(R.string.aboutTabTitle), 
        			this.tabWidget.getChildCount(),
        			new Intent(TabManager.this,AboutTab.class),
        			this.calcTabWidth(1),
        			getResources().getInteger(R.integer.tab_height));
		} catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onCreate",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
		}
    }//onCreate
    
    @Override
	public void onResume()
	{
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
	}
		
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.e("CheckStartActivity","onActivityResult and resultCode = "+resultCode);
	    super.onActivityResult(requestCode, resultCode, data);
	    if(resultCode==1){
	    	TabManager.this.finish();
	    }
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
    public void onBackPressed() {
    	try{
    		AlertMessage.createPositiveNegativeDialog(TabManager.this, false, getResources().getDrawable(R.drawable.warningsign),
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
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onBackPressed",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
    	}
    }
    
    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
		for (Prediction prediction : predictions) {
			if (prediction.score > 1.0) {
				if (prediction.name.equals(getResources().getString(R.string.save_gesture))){
					AlertMessage.createPositiveNeutralNegativeDialog(TabManager.this, true, getResources().getDrawable(R.drawable.warningsign),
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
					AlertMessage.createPositiveNegativeDialog(TabManager.this, true, getResources().getDrawable(R.drawable.warningsign),
		    				getResources().getString(R.string.openingPlotListTitle), getResources().getString(R.string.openingPlotListMessage),
		    				getResources().getString(R.string.yes), getResources().getString(R.string.no),
		    	    		new DialogInterface.OnClickListener() {
		    					@Override
		    					public void onClick(DialogInterface dialog, int which) {
		    						TabManager.this.startActivityForResult(new Intent(TabManager.this, ClusterChoiceActivity.class),1);
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
    
    private void addTab(String title, String indicator, int index, Intent content, int width, int height){
    	try{
    		this.tabHost.addTab(tabHost.newTabSpec(getResources().getString(R.string.aboutTabTitle))
                    .setIndicator(getResources().getString(R.string.aboutTabTitle))
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
    
    private void showWelcomeScreen(int durationInSec){
    	Intent welcomeIntent = new Intent(TabManager.this,WelcomeScreen.class);
    	welcomeIntent.putExtra(getResources().getString(R.string.sleepTime), durationInSec);
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