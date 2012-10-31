package org.openforis.collect.android.tabs;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.util.sqlite.SQLiteFactory;
import org.openforis.collect.android.R;
import org.openforis.collect.android.database.CollectDatabase;
import org.openforis.collect.android.database.DatabaseWrapper;
import org.openforis.collect.android.fields.UIElement;
import org.openforis.collect.android.lists.ClusterChoiceActivity;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.android.misc.WelcomeScreen;
import org.openforis.collect.metamodel.ui.UIOptions;
import org.openforis.collect.metamodel.ui.UITab;
import org.openforis.collect.metamodel.ui.UITabSet;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.CollectSurveyContext;
import org.openforis.collect.model.Configuration;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.Schema;
import org.openforis.idm.metamodel.validation.Validator;
import org.openforis.idm.metamodel.xml.SurveyIdmlBinder;
import org.openforis.idm.model.expression.ExpressionFactory;

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
	
	public static CollectSurvey survey;
	public static List<Configuration> configList;
	public static UITabSet tabSet;
	public static List<UITab> uiTabsList;
	public static Schema schema;

	public static List<NodeDefinition> fieldsList;
	public static Map<Integer,UIElement> uiElementsMap;
	
	public static DatabaseWrapper databaseWrapper;
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
        	
        	//reading form definition
        	long startTime = System.currentTimeMillis();
        	FileInputStream fis = new FileInputStream(sdcardPath+getResources().getString(R.string.formDefinitionFile));
        	ExpressionFactory expressionFactory = new ExpressionFactory();
        	Validator validator = new Validator();
        	CollectSurveyContext collectSurveyContext = new CollectSurveyContext(expressionFactory, validator, null);
    		SurveyIdmlBinder binder = new SurveyIdmlBinder(collectSurveyContext);
    		//binder.addApplicationOptionsBinder(new UIOptionsBinder());
    		TabManager.survey = (CollectSurvey) binder.unmarshal(fis);
    		TabManager.schema = TabManager.survey.getSchema();
    		TabManager.fieldsList = new ArrayList<NodeDefinition>();
    		TabManager.uiElementsMap = new HashMap<Integer,UIElement>();
        	List<EntityDefinition> rootEntitiesDefsList = TabManager.schema.getRootEntityDefinitions();
        	getAllFormFields(rootEntitiesDefsList);
        	Log.e("TIME","=="+(System.currentTimeMillis()-startTime));
        	//Log.e("tabsetsNo","=="+TabManager.survey.getUIOptions().getTabSets().size());
        	//UIOptions uiOptions = TabManager.survey.getUIOptions();
        	
        	UIOptions uiOptions = new UIOptions();
        	UITabSet tabSet = new UITabSet();
        	tabSet.setName("cluster");
        	
        	UITab tab = new UITab();
        	tab.setName("cluster");
        	tab.setLabel("EN", "Cluster");
        	tabSet.addTab(tab);
        	
        	tab = new UITab();
        	tab.setName("plot");
        	tab.setLabel("EN", "Plot");
        	UITab subtab = new UITab();
        	subtab.setName("plot_det");
        	subtab.setLabel("EN", "Details (2)");
        	tab.addTab(subtab);
        	subtab = new UITab();
        	subtab.setName("shrubs_regen");
        	subtab.setLabel("EN", "Shrubs & regeneration (3)");
        	tab.addTab(subtab);
        	tabSet.addTab(tab);
        	
        	uiOptions.addTabSet(tabSet);
        	TabManager.survey.addApplicationOptions(uiOptions);
        	
        	//TabManager.configList = survey.getConfigurations();
        	TabManager.tabSet = uiOptions.getTabSet("cluster");
        	int mainTabsNo = TabManager.tabSet.getTabs().size();
        	if (mainTabsNo>0){
        		TabManager.uiTabsList = TabManager.tabSet.getTabs();
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
            		tabIntent.putExtra("tabLabel", uiTab.getLabel("EN"));
            		tabIntent.putExtra("tabNo", i);
            		tabIntent.putExtra("tabLevel", 1);
            		TabManager.this.addTab(uiTab.getName(), 
            				uiTab.getLabel("EN"),
                			TabManager.this.tabWidget.getChildCount(),
                			tabIntent,
                			TabManager.this.calcTabWidth(mainTabsNo),
                			getResources().getInteger(R.integer.tab_height));
            	}
        	}
        	

            //String url = "jdbc:sqldroid:" + "/data/data/com.mypackage.droid" + "/main.sqlite";
        	String url = "jdbc:sqldroid:"+"/data/data/org.openforis.collect.android/databases/mobileopenforiscollect.db";
            Connection con = null;

            try {
                //Class.forName("SQLite.JDBCDriver");
            	Class.forName("org.sqldroid.SQLDroidDriver").newInstance();
                con = DriverManager.getConnection(url);
                DatabaseMetaData md = con.getMetaData();
                ResultSet rs = md.getTables(null, null, "%", null);
                while (rs.next()) {
                  Log.e("TABLE","=="+rs.getString(3));
                }
                Statement stmt = con.createStatement();
                // creating Query String
                //String query = "INSERT INTO ofc_user VALUES (299,'test_user2','ACFDFD','Y')";
                //int ret = stmt.executeUpdate(query);
                //Log.e("INSERT","=="+ret);
                SQLiteFactory create = new SQLiteFactory(con);
	            // Execute the query "on a single line"
                Result<Record> result = create.select()
					.from("ofc_user")
					.fetch();
	            Log.e("result empty","=="+result.isEmpty());
	            for (int i=0;i<result.size();i++){
	            	Log.e("i=="+i,"=="+result.get(i).getValueAsString("id"));
	            }
	            /*for (Record r : result) {
	                Long id = r.getValue(POSTS.ID);
	                String title = r.getValue(POSTS.TITLE);
	                String description = r.getValue(POSTS.BODY);

	                System.out.println("ID: " + id + " title: " + title + " desciption: " + description);
	            }*/
                con.close();
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally{
            	if (con!=null)
            		con.close();
            }
        	
        	//RecordDao recordDao = new RecordDao();
        	/*Map<String, User> users;

    		users = new HashMap<String, User>();
    		User user = new User();
    		user.setId(1);
    		user.setName("admin");
    		users.put(user.getName(), user);
    		user = new User();
    		user.setId(2);
    		user.setName("data_entry");
    		users.put(user.getName(), user);
    		
        	CollectRecord record = new CollectRecord(survey, "2.0");
    		User collectUser = users.get("admin");
    		record.setCreatedBy(user);
    		record.setModifiedBy(user);
    		Entity cluster = record.createRootEntity("cluster");
    		record.setCreationDate(new GregorianCalendar(2011, 12, 31, 23, 59).getTime());
    		record.setModifiedDate(new GregorianCalendar(2012, 2, 3, 9, 30).getTime());
    		record.setStep(Step.ENTRY);
    		record.setState(State.REJECTED);
    		
    		CollectRecord clusterRecord = (CollectRecord) cluster.getRecord();
    		{
    			Entity ts = EntityBuilder.addEntity(cluster, "task");
    			EntityBuilder.addValue(ts, "type", new Code("formChecked"));
    			EntityBuilder.addValue(ts, "person", "lastname");
    			EntityBuilder.addValue(ts, "date", new Date(2011,2,14));
    		}
    		record.updateRootEntityKeyValues();
    		record.updateEntityCounts();*/
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
	 
	 public static UIElement getUIElement(NodeDefinition nodeDefn){
		 return TabManager.uiElementsMap.get(nodeDefn.getId());
	 }
}