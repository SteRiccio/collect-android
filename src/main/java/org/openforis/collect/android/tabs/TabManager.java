package org.openforis.collect.android.tabs;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openforis.collect.android.R;
import org.openforis.collect.android.database.CollectDatabase;
import org.openforis.collect.android.database.DatabaseWrapper;
import org.openforis.collect.android.fields.BooleanField;
import org.openforis.collect.android.fields.CodeField;
import org.openforis.collect.android.fields.DateField;
import org.openforis.collect.android.fields.MemoField;
import org.openforis.collect.android.fields.NumberField;
import org.openforis.collect.android.fields.RangeField;
import org.openforis.collect.android.fields.TaxonField;
import org.openforis.collect.android.fields.TextField;
import org.openforis.collect.android.fields.TimeField;
import org.openforis.collect.android.fields.UIElement;
import org.openforis.collect.android.lists.ClusterChoiceActivity;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.android.misc.WelcomeScreen;
import org.openforis.collect.manager.RecordManager;
import org.openforis.collect.manager.SurveyManager;
import org.openforis.collect.manager.UserManager;
import org.openforis.collect.metamodel.ui.UIOptions;
import org.openforis.collect.metamodel.ui.UITab;
import org.openforis.collect.metamodel.ui.UITabSet;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectRecord.Step;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.CollectSurveyContext;
import org.openforis.collect.model.Configuration;
import org.openforis.collect.model.User;
import org.openforis.collect.persistence.RecordDao;
import org.openforis.collect.persistence.RecordPersistenceException;
import org.openforis.collect.persistence.SurveyDao;
import org.openforis.collect.persistence.SurveyWorkDao;
import org.openforis.collect.persistence.UserDao;
import org.openforis.collect.persistence.xml.UIOptionsBinder;
import org.openforis.idm.metamodel.BooleanAttributeDefinition;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.DateAttributeDefinition;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
import org.openforis.idm.metamodel.RangeAttributeDefinition;
import org.openforis.idm.metamodel.Schema;
import org.openforis.idm.metamodel.TaxonAttributeDefinition;
import org.openforis.idm.metamodel.TextAttributeDefinition;
import org.openforis.idm.metamodel.TimeAttributeDefinition;
import org.openforis.idm.metamodel.validation.Validator;
import org.openforis.idm.metamodel.xml.SurveyIdmlBinder;
import org.openforis.idm.model.BooleanAttribute;
import org.openforis.idm.model.Code;
import org.openforis.idm.model.CodeAttribute;
import org.openforis.idm.model.DateAttribute;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.NumberAttribute;
import org.openforis.idm.model.NumericRangeAttribute;
import org.openforis.idm.model.TaxonAttribute;
import org.openforis.idm.model.TextAttribute;
import org.openforis.idm.model.Time;
import org.openforis.idm.model.TimeAttribute;
import org.openforis.idm.model.expression.ExpressionFactory;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

public class TabManager extends TabActivity /*implements OnGesturePerformedListener*/ {

	private static final String TAG = "TabManager";
	//private GestureLibrary gestureLib;
	private TabWidget tabWidget;
	private TabHost tabHost;
	
	private static CollectSurvey survey;
	public static List<Configuration> configList;
	public static UITabSet clusterTabSet;
	public static List<UITab> uiTabsList;
	public static Schema schema;

	public static List<NodeDefinition> fieldsList;
	public static Map<Integer,UIElement> uiElementsMap;
	
	private static DatabaseWrapper databaseWrapper;
	
	private UserManager userManager;
	private SurveyManager surveyManager;
	private RecordManager recordManager;
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
        	/*GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
            View inflate = getLayoutInflater().inflate(R.layout.applicationwindow, null);
            gestureOverlayView.addView(inflate);
            gestureOverlayView.addOnGesturePerformedListener(this);            
            gestureOverlayView.setGestureColor(Color.GREEN);
            gestureOverlayView.setUncertainGestureColor(Color.RED);
            gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
            if (!gestureLib.load()) {
            	//finish();
            }
            gestureOverlayView.setPadding(0, 200, 0, 0);
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
		    
		    //creating database
		    databaseWrapper = new DatabaseWrapper(this);
		    CollectDatabase collectDB = new CollectDatabase(DatabaseWrapper.db);
		    
		    //creating tab container
        	tabHost = getTabHost();
        	tabWidget = tabHost.getTabWidget();
        	
        	//instantiating managers
        	ExpressionFactory expressionFactory = new ExpressionFactory();
        	Validator validator = new Validator();
        	CollectSurveyContext collectSurveyContext = new CollectSurveyContext(expressionFactory, validator, null);
        	
        	this.surveyManager = new SurveyManager();
        	this.surveyManager.setSurveyDao(new SurveyDao());
        	this.surveyManager.setCollectSurveyContext(collectSurveyContext);
        	this.surveyManager.setSurveyWorkDao(new SurveyWorkDao());
        	        	        	
        	this.userManager = new UserManager();
        	this.userManager.setUserDao(new UserDao());
        	this.userManager.setRecordDao(new RecordDao());
        	
        	//reading form definition if it is not available in database
        	setSurvey(this.surveyManager.getSurveyDao().load("Archenland NFI"));
        	if (getSurvey()==null){
            	long startTime = System.currentTimeMillis();
            	Log.e("PARSING","====================");     
            	FileInputStream fis = new FileInputStream(sdcardPath+getResources().getString(R.string.formDefinitionFile));        	
            	SurveyIdmlBinder binder = new SurveyIdmlBinder(collectSurveyContext);
        		binder.addApplicationOptionsBinder(new UIOptionsBinder());
        		setSurvey((CollectSurvey) binder.unmarshal(fis));
        		getSurvey().setName(getSurvey().getProjectName(null));
        		this.surveyManager.importModel(getSurvey());
            	Log.e("TIME","=="+(System.currentTimeMillis()-startTime));       		
        	}
        	TabManager.schema = getSurvey().getSchema();
    		TabManager.fieldsList = new ArrayList<NodeDefinition>();
    		TabManager.uiElementsMap = new HashMap<Integer,UIElement>();
        	List<EntityDefinition> rootEntitiesDefsList = TabManager.schema.getRootEntityDefinitions();
        	getAllFormFields(rootEntitiesDefsList);
        	UIOptions uiOptions = getSurvey().getUIOptions();
        	/*uiOptions = new UIOptions();
        	UITabSet tabSet = new UITabSet();
        	tabSet.setName("cluster");
        	
        	UITab tab = new UITab();
        	tab.setName("cluster");
        	tab.setLabel(null, "Cluster");
        	tabSet.addTab(tab);
        	
        	tab = new UITab();
        	tab.setName("plot");
        	tab.setLabel(null, "Plot");
        	UITab subtab = new UITab();
        	subtab.setName("plot_det");
        	subtab.setLabel(null, "Details (2)");
        	tab.addTab(subtab);
        	subtab = new UITab();
        	subtab.setName("shrubs_regen");
        	subtab.setLabel(null, "Shrubs & regeneration (3)");
        	tab.addTab(subtab);
        	tabSet.addTab(tab);
        	
        	uiOptions.addTabSet(tabSet);*/
        	getSurvey().addApplicationOptions(uiOptions);
        	
        	//TabManager.configList = survey.getConfigurations();
    		TabManager.clusterTabSet = uiOptions.getTabSet("cluster");
        	int mainTabsNo = TabManager.clusterTabSet.getTabs().size();
        	if (mainTabsNo>0){
        		TabManager.uiTabsList = TabManager.clusterTabSet.getTabs();
        		Intent tabIntent;
            	for (int i=0; i<mainTabsNo;i++){
            		UITab uiTab = TabManager.uiTabsList.get(i);
            		if (uiTab.getTabs().size()>0){
            			tabIntent = new Intent(TabManager.this, TabContainer.class);
            		}
            		else{
            			tabIntent = new Intent(TabManager.this, Tab.class);
            		}
            		tabIntent.putExtra("tabName", uiTab.getName());
            		tabIntent.putExtra("tabLabel", uiTab.getLabel(null));
            		tabIntent.putExtra("tabNo", i);
            		tabIntent.putExtra("tabLevel", 1);
            		TabManager.this.addTab(uiTab.getName(), 
            				uiTab.getLabel(null),
                			TabManager.this.tabWidget.getChildCount(),
                			tabIntent,
                			TabManager.this.calcTabWidth(mainTabsNo),
                			getResources().getInteger(R.integer.tab_height));
            	}
        	}
        	
        	//saving schema, user, etc. to database
        	JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
        	jdbcDao.getConnection();
        	
        	//adding default user to database if not exists        	
        	User defaultUser = new User();
        	defaultUser.setName(getResources().getString(R.string.defaultUsername));
        	defaultUser.setPassword(getResources().getString(R.string.defaultUserPassword));
        	defaultUser.setEnabled(true);
        	defaultUser.setId(getResources().getInteger(R.integer.defaulUsertId));
        	defaultUser.addRole(getResources().getString(R.string.defaultUserRole));
        	if (!userExists(defaultUser)){
        		this.userManager.insert(defaultUser);
        	}

        	this.recordManager = new RecordManager();
    		this.recordManager.setRecordDao(new RecordDao());
    		
    		long startTime = System.currentTimeMillis();
    		CollectRecord loadedRecord = loadData(27);
    		Log.e("loadingTIME","=="+(System.currentTimeMillis()-startTime)/1000);
    		displayEntityData(loadedRecord.getRootEntity());
            JdbcDaoSupport.close();
		} catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onCreate",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
		}
    }//onCreate
    
    private void getAllFormFields(List<EntityDefinition> rootEntitiesDefsList){
    	for (int i=0;i<rootEntitiesDefsList.size();i++){
    		TabManager.fieldsList.add(rootEntitiesDefsList.get(i));
    		getFields(rootEntitiesDefsList.get(i).getChildDefinitions());
    	}    	
    	TabManager.fieldsList = this.sortById(TabManager.fieldsList);
    }
    
    private void getFields(List<NodeDefinition> childrenList){
    	for (int i=0;i<childrenList.size();i++){
    		NodeDefinition field = childrenList.get(i);
    		//Log.e("field","=="+field.getName());
    		TabManager.fieldsList.add(field);
    		if (field.getClass().equals(EntityDefinition.class)){
    			EntityDefinition entityDef = (EntityDefinition) field;
    			getFields(entityDef.getChildDefinitions());
    		}
    	}
    }
    
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
    				},
    				null).show();
    	}catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onBackPressed",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
    	}
    }
    
    /*@Override
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
    }*/
    
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
    
    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }
 
    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
 
        switch (item.getItemId())
        {
        case R.id.menu_open:
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
    				},
    				null).show();
            return true;
 
        case R.id.menu_save:
        	saveData();
			/*AlertMessage.createPositiveNegativeDialog(TabManager.this, true, getResources().getDrawable(R.drawable.warningsign),
    				getResources().getString(R.string.savingDataTitle), getResources().getString(R.string.savingDataMessage),
    				getResources().getString(R.string.savingToDatabase), getResources().getString(R.string.cancel),
    	    		new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						saveData();
    					}
    				},
    	    		new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						
    					}
    				}, null).show();*/
            return true;

        case R.id.menu_about:
        	String versionName;
        	try {
				versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				versionName = "";
			}
        	AlertMessage.createPositiveDialog(TabManager.this, true, null,
    				getResources().getString(R.string.aboutTabTitle), 
    				getResources().getString(R.string.lblApplicationName)+getResources().getString(R.string.app_name)
    				+"\n"
    				+getResources().getString(R.string.lblProgramVersionName)+versionName
    				+"\n"
    				+getResources().getString(R.string.lblFormVersionName)+TabManager.survey.getProjectName(null)+" "+TabManager.survey.getVersions().get(TabManager.survey.getVersions().size()-1).getName(),
    				getResources().getString(R.string.okay),
    	    		new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						
    					}
    				},
    				null).show();
            return true;
            
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
	 private List<NodeDefinition> sortById(List<NodeDefinition> nodes){
		 NodeDefinition[] nodesArray = new NodeDefinition[nodes.size()];
		 nodes.toArray(nodesArray);
		 List<NodeDefinition> nodesList = new ArrayList<NodeDefinition>();
		 for (int i=0;i<nodesArray.length;i++){
			 nodesList.add(nodesArray[i]);
		 }
		 Collections.sort(nodesList, new NodeIdComparator());
		 return nodesList;
	 }

	 public class NodeIdComparator implements Comparator<NodeDefinition> {

			@Override
			public int compare(NodeDefinition lhs, NodeDefinition rhs) {				
				return Integer.valueOf(lhs.getId()).compareTo(rhs.getId());//lhs.getId().compareTo(rhs.getId());
			}
		}
	 
	 public static UIElement getUIElement(int elementId){
		 return TabManager.uiElementsMap.get(elementId);
	 }
	 
	 private boolean userExists(User user){
     	List<User> usersList = this.userManager.loadAll();
     	boolean userExists = false;
     	for (int i=0;i<usersList.size();i++){
     		if (usersList.get(i).equals(user)){
     			userExists = true;
     			break;
     		}
     	}
     	return userExists;
	 }
	 
	 //setters and getters for private static fields
	 public static CollectSurvey getSurvey(){
		 return survey;
	 }
	 
	 public static void setSurvey(CollectSurvey surv){
		 survey = surv;
	 }
	 
	public void saveData(){
		try{
			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
			jdbcDao.getConnection();
			
			//initializing data record 
			CollectRecord record = new CollectRecord(TabManager.survey, TabManager.survey.getVersions().get(TabManager.survey.getVersions().size()-1).getName());        	
			record.setCreatedBy(this.userManager.loadById(getResources().getInteger(R.integer.defaulUsertId)));
			Entity cluster = record.createRootEntity(TabManager.survey.getSchema().getRootEntityDefinitions().get(0).getName());
			cluster.setId(TabManager.survey.getSchema().getRootEntityDefinitions().get(0).getId());
			
			record.setCreationDate(new Date());
			record.setStep(Step.ENTRY);
			
			//getting data from fields
			UIElement uiField = null;
			Map<Integer,Entity> entitiesMap = new HashMap<Integer,Entity>();
			entitiesMap.put(TabManager.survey.getSchema().getRootEntityDefinitions().get(0).getId(),cluster);
			Entity parentEntity = cluster;
			for (int i=0;i<1000;i++){
				NodeDefinition nodeDef = TabManager.schema.getDefinitionById(i);
				if (nodeDef!=null){
					if (nodeDef.getClass().equals(EntityDefinition.class)
							&&
						!nodeDef.equals(TabManager.survey.getSchema().getRootEntityDefinitions().get(0))){
						
						//Log.e("entitiesMap==null","=="+(entitiesMap==null));
						//Log.e("entitiesMap.size","=="+entitiesMap.size());
						if (parentEntity!=null){
							if (nodeDef.getParentDefinition()!=null){
								//Log.e("parentID","=="+nodeDef.getParentDefinition().getId());						
								parentEntity = entitiesMap.get(nodeDef.getParentDefinition().getId());
								parentEntity.setId(nodeDef.getParentDefinition().getId());
								//Log.e("parentEntity"+parentEntity.getId(),"=="+parentEntity.getName());
								//Log.e("parentEntity==null","=="+(parentEntity==null));
							}
							else {
								//Log.e("parentID","=="+cluster.getId());						
								parentEntity = cluster;
								//Log.e("parentEntity==null","=="+(parentEntity==null));
							}
							Entity currentEntity = EntityBuilder.addEntity(parentEntity, nodeDef.getName());
							entitiesMap.put(i, currentEntity);			
						}
					}
				}
			}
			/*for (int i=0;i<1000;i++){
				if (entitiesMap.get(i)!=null){
					if (entitiesMap.get(i).getParent()!=null){
						Log.e("MAP:ID"+i,entitiesMap.get(i).getName()+"=="+entitiesMap.get(i).getParent().getName());	
					}
					else {
						Log.e("MAP:id"+i,entitiesMap.get(i).getName()+"=="+entitiesMap.get(i).getId());
					}
				}					
			}*/

			for (NodeDefinition formField : TabManager.fieldsList){
				uiField = TabManager.getUIElement(formField.getId());
				if ((uiField!=null)){
					//Log.e("UIField.id"+uiField.getElementId(),uiField.getClass()+"==");	
				}
				
				if ((uiField!=null)&&(uiField.getElementId()!=-1)){
					//Log.e("parentSearch"+formField.getParentDefinition().getId(),"=="+formField.getParentDefinition().getName());
					Entity parent = entitiesMap.get(formField.getParentDefinition().getId());
					if (parent==null)
						parent = cluster;
					//Log.e("parent",parent.getId()+"=="+parent.getName());
					addValueToRecord(parent, formField, uiField,0);
				}
			}
			{
				Entity ts = EntityBuilder.addEntity(cluster, "task");
				EntityBuilder.addValue(ts, "type", new Code("2"));
				EntityBuilder.addValue(ts, "person", "JANel");
				EntityBuilder.addValue(ts, "date", new org.openforis.idm.model.Date(2010,2,15));
			}
			EntityBuilder.addValue(cluster, "district", new Code("123"));
			
			Log.e("inserting","====================");
			long startTime = System.currentTimeMillis();

			//this.recordManager.getRecordDao().insert(record);
			//Log.e("loadedRecord","=="+record.getId());
			//Log.e("date","#=="+record.toString());
			//Log.e("insertingOVER","=="+(System.currentTimeMillis()-startTime)/1000);
			CollectRecord loadedRecord = this.recordManager.load(getSurvey(), record.getId(), 1);
			Entity rootEntity = loadedRecord.getRootEntity();
			//Log.e("rootEntity","=="+rootEntity.getName());
			//displayEntityData(rootEntity);
			JdbcDaoSupport.close();
		} catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":saveData",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
		}
	}
	
	void displayEntityData(Entity parentEntity){
		for (Node n : parentEntity.getChildren()){
			if (n.getClass().equals(NumberAttribute.class)){
				NumberAttribute number = (NumberAttribute)n;
				Log.e("number"+number.getName(),"=="+number.getValue());
				NumberField nu = (NumberField)TabManager.getUIElement(n.getId());
				nu.setValue("987"+number.getValue());
			} else if (n.getClass().equals(CodeAttribute.class)){
				CodeAttribute code = (CodeAttribute)n;
				Log.e("code"+code.getName(),"=="+code.getValue().getCode());
				Log.e("id"+(n.getId()==null),"==");
				CodeField co = (CodeField)TabManager.getUIElement(n.getId());
				co.setValue(code.getValue().getCode());
			} else if (n.getClass().equals(BooleanAttribute.class)){
				BooleanAttribute bool = (BooleanAttribute)n;
				Log.e("bool"+bool.getName(),"=="+bool.getValue().getValue());
			} else if (n.getClass().equals(TextAttribute.class)){
				TextAttribute text = (TextAttribute)n;
				Log.e("text"+text.getName(),"=="+text.getValue().getValue());
				Log.e("id"+(n.getId()==null),"==");
				TextField te = (TextField)TabManager.getUIElement(n.getId());
				te.setValue("abc"+text.getValue());
			} else if (n.getClass().equals(DateAttribute.class)){
				DateAttribute date = (DateAttribute)n;
				Log.e("date"+date.getName(),"=="+date.getValue().getYear());
				DateField da = (DateField)TabManager.getUIElement(n.getId());
				da.setValue("abc"+date.getValue());
			} else if (n.getClass().equals(TimeAttribute.class)){
				TimeAttribute time = (TimeAttribute)n;
				Log.e("time"+time.getName(),"=="+time.getValue());	
			} else if (n.getClass().equals(NumericRangeAttribute.class)){
				
			} else if (n.getClass().equals(TaxonAttribute.class)){
				
			} else if (n.getClass().equals(Entity.class)){
				displayEntityData((Entity)n);
			}
		}
	}
	
	private void addValueToRecord(Entity parent, NodeDefinition formField, UIElement uiField, int uiFieldInstanceNo){
		//Log.e("addedfield","=="+formField.getName());
		//Log.e("addedparentEntity","=="+formField.getParentDefinition().getName());
		
		if (formField.getClass().equals(NumberAttributeDefinition.class)){
			NumberField field = (NumberField)uiField;
			//Log.e("value",field.getType()+"=="+field.getValue(0));
			if (field.getType().equals("INTEGER")){
				EntityBuilder.addValue(parent, formField.getName(), (field.getValue(0).equals(""))?null:Integer.valueOf(field.getValue(uiFieldInstanceNo)));
			} else {
				EntityBuilder.addValue(parent, formField.getName(), (field.getValue(0).equals(""))?null:Double.valueOf(field.getValue(uiFieldInstanceNo)));
			}
		} else if (formField.getClass().equals(CodeAttributeDefinition.class)){
			CodeField field = (CodeField)uiField;
			//Log.e("value","=="+field.getValue(0));
			EntityBuilder.addValue(parent, formField.getName(), new Code(String.valueOf(field.getValue(uiFieldInstanceNo))));
		} else if (formField.getClass().equals(BooleanAttributeDefinition.class)){
			BooleanField field = (BooleanField)uiField;
			//Log.e("value","=="+field.getValue(0));
			EntityBuilder.addValue(parent, formField.getName(), field.getValue(uiFieldInstanceNo).get(0));
		} else if (formField.getClass().equals(TextAttributeDefinition.class)){
			TextAttributeDefinition textAttrField = (TextAttributeDefinition) formField;			
			Object fieldType = textAttrField.getType();
			if (fieldType!=null){
				if(fieldType.toString().equals(getResources().getString(R.string.text_type_long))){//memo
					MemoField field = (MemoField)uiField;
					//Log.e("value","=="+field.getValue(0));
					EntityBuilder.addValue(parent, formField.getName(), field.getValue(uiFieldInstanceNo));
				} else {//short
					TextField field = (TextField)uiField;
					Log.e("value","=="+field.getValue(0));
					EntityBuilder.addValue(parent, formField.getName(), field.getValue(uiFieldInstanceNo));
				}
			} else{//no type of text field specified
				TextField field = (TextField)uiField;
				//Log.e("value","=="+field.getValue(0));
				EntityBuilder.addValue(parent, formField.getName(), field.getValue(uiFieldInstanceNo));
			}			
		} else if (formField.getClass().equals(DateAttributeDefinition.class)){
			DateField field = (DateField)uiField;
			//Log.e("value","=="+field.getValue(0));
			EntityBuilder.addValue(parent, formField.getName(), new org.openforis.idm.model.Date(2011,2,14)/*field.getValue(uiFieldInstanceNo)*/);
		} else if (formField.getClass().equals(TimeAttributeDefinition.class)){
			TimeField field = (TimeField)uiField;
			//Log.e("value","=="+field.getValue(0));
			EntityBuilder.addValue(parent, formField.getName(), new Time(11,55)/*field.getValue(uiFieldInstanceNo)*/);
		} else if (formField.getClass().equals(RangeAttributeDefinition.class)){
			RangeField field = (RangeField)uiField;
			//Log.e("value","=="+field.getValue(0));
		} else if (formField.getClass().equals(TaxonAttributeDefinition.class)){
			TaxonField field = (TaxonField)uiField;
		}
	}
	/*	CollectRecord record = (CollectRecord) cluster.getRecord();
EntityBuilder.addValue(cluster, "id", new Code("123_456"));
EntityBuilder.addValue(cluster, "gps_realtime", Boolean.TRUE);
EntityBuilder.addValue(cluster, "region", new Code("001"));
CodeAttribute districtAttr = EntityBuilder.addValue(cluster, "district", new Code("XXX"));
record.setErrorConfirmed(districtAttr, true);
EntityBuilder.addValue(cluster, "crew_no", 10);
EntityBuilder.addValue(cluster, "map_sheet", "value 1");
EntityBuilder.addValue(cluster, "map_sheet", "value 2");
EntityBuilder.addValue(cluster, "vehicle_location", new Coordinate((double)432423423l, (double)4324324l, "srs"));
EntityBuilder.addValue(cluster, "gps_model", "TomTom 1.232");
cluster.setChildState("accessibility", 1);
{
Entity ts = EntityBuilder.addEntity(cluster, "time_study");
EntityBuilder.addValue(ts, "date", new Date(2011,2,14));
EntityBuilder.addValue(ts, "start_time", new Time(8,15));
EntityBuilder.addValue(ts, "end_time", new Time(15,29));
}
{
Entity ts = EntityBuilder.addEntity(cluster, "time_study");
EntityBuilder.addValue(ts, "date", new Date(2011,2,15));
EntityBuilder.addValue(ts, "start_time", new Time(8,32));
EntityBuilder.addValue(ts, "end_time", new Time(11,20));
}*/
	
	public CollectRecord loadData(int recordId){
		CollectRecord loadedRecord = null;
		try {
			loadedRecord = this.recordManager.load(getSurvey(), recordId, 1);
		} catch (Exception e) {
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":loadData",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
		}
		return loadedRecord;
	}
}