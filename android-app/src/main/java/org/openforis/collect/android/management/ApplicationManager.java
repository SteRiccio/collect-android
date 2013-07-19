package org.openforis.collect.android.management;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openforis.collect.android.R;
import org.openforis.collect.android.config.Configuration;
import org.openforis.collect.android.database.DatabaseHelper;
import org.openforis.collect.android.fields.UIElement;
import org.openforis.collect.android.lists.FormChoiceActivity;
import org.openforis.collect.android.lists.RecordChoiceActivity;
import org.openforis.collect.android.lists.RootEntityChoiceActivity;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.collect.manager.SurveyManager;

import org.openforis.collect.model.AttributeChange;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.NodeChange;
import org.openforis.collect.model.NodeChangeSet;
import org.openforis.collect.model.User;
import org.openforis.collect.model.validation.ValidationMessageBuilder;

import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.LanguageSpecificText;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NodeLabel.Type;
import org.openforis.idm.metamodel.Survey;
import org.openforis.idm.metamodel.validation.ValidationResult;
import org.openforis.idm.metamodel.validation.ValidationResults;

import org.openforis.idm.model.Attribute;
import org.openforis.idm.model.Entity;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class ApplicationManager extends BaseActivity {
	
	private static final String TAG = "ApplicationManager";
	
	private static String sessionId;





	private static CollectSurvey survey;
	//private static Schema schema;
	private static User loggedInUser;
	
	//public static List<NodeDefinition> fieldsDefList;
	
	public static SharedPreferences appPreferences;
	
	private static Map<Integer,UIElement> uiElementsMap;
	
	public static CollectRecord currentRecord;
	public static int currRootEntityId;
	public static View selectedView;
	public static boolean isToBeScrolled;
	
	public static DataManager dataManager;
	
	public static ProgressDialog pd;
	
	public static String selectedLanguage;
	
	public static Activity mainActivity;
	public static Activity rootEntitySelectionActivity;
	public static Activity recordSelectionActivity;
	public static List<Activity> formScreenActivityList;
	public static Activity formSelectionActivity;
	
	private Thread creationThread = new Thread() {
		@Override
		public void run() {
			try {
				super.run();
				Log.i(getResources().getString(R.string.app_name),TAG+":run");
	        	
	            initSession();
	            




	            Configuration config = Configuration.getDefault(ApplicationManager.this);
	            
	            DatabaseHelper.init(ApplicationManager.this, config);
	        	








			    ServiceFactory.init(config);
			    
	            ApplicationManager.currentRecord = null;
	            ApplicationManager.currRootEntityId = -1;
	            ApplicationManager.selectedView = null;
	            ApplicationManager.isToBeScrolled = false;
	            
//	            ApplicationManager.appPreferences = getPreferences(MODE_PRIVATE);
//				int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);
//				SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
//				editor.putInt(getResources().getString(R.string.backgroundColor), backgroundColor);
//				//editor.commit();
//	            
//				//Set virtual keyboard to 'false' if it's NULL
//		    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
//		    	Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));			
//		    	Boolean valueForText = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnTextField));
//		    	if(valueForNum == null)
//		    		editor.putBoolean(getResources().getString(R.string.showSoftKeyboardOnNumericField), false);
//		    	if(valueForText == null)
//		    		editor.putBoolean(getResources().getString(R.string.showSoftKeyboardOnTextField), false);	    	
//		    	editor.commit();			
				
				//creating file structure used by the application
	        	String sdcardPath = Environment.getExternalStorageDirectory().toString();
				File folder = new File(sdcardPath+getResources().getString(R.string.application_folder));
				folder.mkdirs();
				folder = new File(sdcardPath+getResources().getString(R.string.data_folder));
			    folder.mkdirs();
				folder = new File(sdcardPath+getResources().getString(R.string.exported_data_folder));
			    folder.mkdirs();
			    folder = new File(sdcardPath+getResources().getString(R.string.imported_data_folder));
			    folder.mkdirs();
			    folder = new File(sdcardPath+getResources().getString(R.string.backup_folder));
			    folder.mkdirs();
			    folder = new File(sdcardPath+getResources().getString(R.string.logs_folder));
			    folder.mkdirs();
			    
			    //creating database
			    /*new DatabaseWrapper(ApplicationManager.this);
			    CollectDatabase collectDB = new CollectDatabase(DatabaseWrapper.db);	*/
			    



















	        	//reading form definition if it is not available in database
	        	/*survey = surveyManager.get("Archenland NFI");//default survey
	        	if (survey==null){
	            	long startTimeParsing = System.currentTimeMillis();
	            	FileInputStream fis = new FileInputStream(sdcardPath+getResources().getString(R.string.formDefinitionFileTest));        	
	            	SurveyIdmlBinder binder = new SurveyIdmlBinder(collectSurveyContext);
	        		binder.addApplicationOptionsBinder(new UIOptionsBinder());
	        		survey = (CollectSurvey) binder.unmarshal(fis);
	        		List<LanguageSpecificText> projectNamesList = survey.getProjectNames();
	        		if (projectNamesList.size()>0){
	        			survey.setName(projectNamesList.get(0).getText());
	        		} else {
	        			survey.setName("defaultSurveyName");
	        		}
	        		Log.e("surveyToLoad","name=="+survey.getName());
	        		CollectSurvey loadedSurvey = surveyManager.get(survey.getName());
	        		if (loadedSurvey==null){
		        		surveyManager.importModel(survey);
	        		} else {
	        			survey = loadedSurvey;
	        		}
	            	Log.e("parsingTIME","=="+(System.currentTimeMillis()-startTimeParsing));
	        	}
	        	schema = survey.getSchema();*/
	        	//ApplicationManager.fieldsDefList = new ArrayList<NodeDefinition>();        	
	        	//List<EntityDefinition> rootEntitiesDefsList = schema.getRootEntityDefinitions();
	        	//getAllFormFields(rootEntitiesDefsList);
	        	
	        	ApplicationManager.uiElementsMap = new HashMap<Integer,UIElement>();        	
	        	
	        	//adding default user to database if not exists        	
	        	User defaultUser = new User();
	        	defaultUser.setName(getResources().getString(R.string.defaultUsername));
	        	defaultUser.setPassword(getResources().getString(R.string.defaultUserPassword));
	        	defaultUser.setEnabled(true);
	        	defaultUser.setId(getResources().getInteger(R.integer.defaulUsertId));
	        	defaultUser.addRole(getResources().getString(R.string.defaultUserRole));
	        	/*if (!userExists(defaultUser)){
	        		ServiceFactory.getUserManager().insert(defaultUser);
	        	}*/
	        	ApplicationManager.loggedInUser = defaultUser;
	        	
	        	ApplicationManager.dataManager = null;
	    		


	            ApplicationManager.pd.dismiss();
	            
	            //showRootEntitiesListScreen();
	            showFormsListScreen();
			} catch (Exception e) {
				RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":run",
	    				Environment.getExternalStorageDirectory().toString()
	    				+getResources().getString(R.string.logs_folder)
	    				+getResources().getString(R.string.logs_file_name)
	    				+System.currentTimeMillis()
	    				+getResources().getString(R.string.log_file_extension));
			} finally {
				//finish();
	            DatabaseHelper.closeConnection();
			}
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        	ApplicationManager.pd = ProgressDialog.show(this, getResources().getString(R.string.workInProgress), getResources().getString(R.string.launchAppMessage));
        	Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        	setContentView(R.layout.welcomescreen);        	
        	
        	ApplicationManager.appPreferences = getPreferences(MODE_PRIVATE);

			int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);
			SharedPreferences.Editor editor = ApplicationManager.appPreferences.edit();
			editor.putInt(getResources().getString(R.string.backgroundColor), backgroundColor);
			
			int gpsTimeout = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.gpsTimeout), getResources().getInteger(R.integer.gpsTimeoutInMs));
			editor = ApplicationManager.appPreferences.edit();
			editor.putInt(getResources().getString(R.string.gpsTimeout), gpsTimeout);
            
	    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
	    	Boolean valueForNum = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnNumericField));			
	    	Boolean valueForText = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnTextField));
	    	if(valueForNum == null)
	    		editor.putBoolean(getResources().getString(R.string.showSoftKeyboardOnNumericField), false);
	    	if(valueForText == null)
	    		editor.putBoolean(getResources().getString(R.string.showSoftKeyboardOnTextField), false);
	    	
	    	String language = ApplicationManager.appPreferences.getString(getResources().getString(R.string.selectedLanguage), getResources().getString(R.string.defaultLanguage));
			editor = ApplicationManager.appPreferences.edit();
			editor.putString(getResources().getString(R.string.selectedLanguage), language);
			ApplicationManager.selectedLanguage = language;
			
			String formDefinitionPath = ApplicationManager.appPreferences.getString(getResources().getString(R.string.formDefinitionPath), getResources().getString(R.string.defaultFormDefinitionPath));
			editor = ApplicationManager.appPreferences.edit();
			editor.putString(getResources().getString(R.string.formDefinitionPath), formDefinitionPath);
			
			String survey_id = ApplicationManager.appPreferences.getString(getResources().getString(R.string.surveyId), getResources().getString(R.string.defaultSurveyId));
			editor.putString(getResources().getString(R.string.surveyId), survey_id);
			
			String username = ApplicationManager.appPreferences.getString(getResources().getString(R.string.username), getResources().getString(R.string.defaultUsername));
			editor.putString(getResources().getString(R.string.username), username);





	    	editor.commit();
	    		    
        	creationThread.start();
        	
    		Thread thread = new Thread(new RunnableHandler(0, Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+"DEBUG_LOG"
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension)));
    		thread.start();
    	//	Log.e(this.TAG+"onCREATE","=="+(System.currentTimeMillis()-startTime)/1000+" s");
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
			
		} catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onResume",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
		}
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {    	
	    super.onActivityResult(requestCode, resultCode, data);
	    try{
	    	Log.e("request="+requestCode,"result="+resultCode);
	 	    if (requestCode==getResources().getInteger(R.integer.clusterSelection)){
	 	    	if (resultCode==getResources().getInteger(R.integer.clusterChoiceSuccessful)){//record was selected	 	    		
	 	    		
	 	    		int recordId = data.getIntExtra(getResources().getString(R.string.recordId), -1);
	 	    		Log.e("SELECTEDrecordID","=="+recordId);
	 	    		if (recordId==-1){//new record
	 	    			String versionName = survey.getVersions().isEmpty() ? null: survey.getVersions().get(survey.getVersions().size()-1).getName();
						ApplicationManager.currentRecord = new CollectRecord(ApplicationManager.survey, versionName);//null;	 	    			
	 					Entity rootEntity = ApplicationManager.currentRecord.createRootEntity(ApplicationManager.getSurvey().getSchema().getRootEntityDefinition(ApplicationManager.currRootEntityId).getName());
	 					rootEntity.setId(ApplicationManager.currRootEntityId);
	 	    		} else {//record from database
	 	    			CollectSurvey collectSurvey = (CollectSurvey)ApplicationManager.getSurvey();	        	
			        	//DataManager dataManager = new DataManager(collectSurvey,collectSurvey.getSchema().getRootEntityDefinitions().get(0).getName(),ApplicationManager.getLoggedInUser());
	 	    			ApplicationManager.dataManager = new DataManager(collectSurvey,collectSurvey.getSchema().getRootEntityDefinitions().get(0).getName(),ApplicationManager.getLoggedInUser());
			        	ApplicationManager.currentRecord = dataManager.loadRecord(recordId);
			        	Entity rootEntity = ApplicationManager.currentRecord.getRootEntity();
	    				rootEntity.setId(ApplicationManager.currRootEntityId);
	 	    		}
	 	    		showFormRootScreen();
    	            DataManager dataManager = new DataManager((CollectSurvey) ApplicationManager.getSurvey(),ApplicationManager.getSurvey().getSchema().getRootEntityDefinition(ApplicationManager.currRootEntityId).getName(),ApplicationManager.getLoggedInUser());

    	            /*Log.e("fileNAMEtoLoad","==2_3_0_4_113_9_24_collect.xml");
    	            dataManager.loadRecordFromXml("2_3_0_4_113_9_24_collect.xml");*/
	 	    	} else if (resultCode==getResources().getInteger(R.integer.backButtonPressed)){
	 	    		if (ApplicationManager.getSurvey().getSchema().getRootEntityDefinitions().size()==1){
	 	    			showFormsListScreen();
	 	    		} else {
	 	    			showRootEntitiesListScreen();
	 	    		}	 	    	
	 	    	}
	 	    } else if (requestCode==getResources().getInteger(R.integer.rootEntitySelection)){
	 	    	if (resultCode==getResources().getInteger(R.integer.rootEntityChoiceSuccessful)){//root entity was selected	    	
	 	    		ApplicationManager.currRootEntityId = data.getIntExtra(getResources().getString(R.string.rootEntityId), -1);
	 	    		ApplicationManager.dataManager = new DataManager(survey,survey.getSchema().getRootEntityDefinition(ApplicationManager.currRootEntityId).getName(),ApplicationManager.getLoggedInUser());
	 	    		showRecordsListScreen(ApplicationManager.currRootEntityId);	
	 	    	} else if (resultCode==getResources().getInteger(R.integer.backButtonPressed)){
	 	    		showFormsListScreen();
	 	    	}
	 	    } else if (requestCode==getResources().getInteger(R.integer.formDefinitionSelection)){
	 	    	if (resultCode==getResources().getInteger(R.integer.formDefinitionChoiceSuccessful)){//form was selected
	 	    		int formId = data.getIntExtra(getResources().getString(R.string.formId), -1);
	 	    		if (formId==-1){//new form to be added from file
	 	    			try{
		 	    			long startTimeParsing = System.currentTimeMillis();
		 	    			//opening database connection
//			 	           	JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
//			 	           	jdbcDao.getConnection();
			 	           	
		 	    			String sdcardPath = Environment.getExternalStorageDirectory().toString();



		 		        	String selectedFormDefinitionFile = ApplicationManager.appPreferences.getString(getResources().getString(R.string.formDefinitionPath), getResources().getString(R.string.defaultFormDefinitionPath));
		 		        	Log.e("loadingForm","=FROM=="+selectedFormDefinitionFile);
			            	//FileInputStream fis = new FileInputStream(sdcardPath+getResources().getString(R.string.formDefinitionFile));  	
		 		        	//FileInputStream fis = new FileInputStream(sdcardPath+selectedFormDefinitionFile);



		 		        	SurveyManager surveyManager = ServiceFactory.getSurveyManager();
		 		        	File idmlFile = new File(sdcardPath, selectedFormDefinitionFile);
			        		survey = surveyManager.unmarshalSurvey(idmlFile, false, false);
			        		List<LanguageSpecificText> projectNamesList = survey.getProjectNames();
			        		if (projectNamesList.size()>0){
			        			survey.setName(projectNamesList.get(0).getText());
			        		} else {
			        			survey.setName("defaultSurveyName");
			        		}
			        		Log.e("surveyToLoad","name=="+survey.getName());
			        		CollectSurvey loadedSurvey = surveyManager.get(survey.getName());
			        		Log.e("loadedSurvey==null","=="+(loadedSurvey==null));
			        		if (loadedSurvey==null){
								survey = surveyManager.importModel(idmlFile, survey.getName(), false);
			        		} else {
			        			survey = loadedSurvey;
			        		}
			        		Log.e("survey","=="+(survey.getName()));
			            	Log.e("parsingTIME","=="+(System.currentTimeMillis()-startTimeParsing));			           
	 	    			} catch (Exception e){
	 	    				Log.e("parsingEXCEPTION","===");
	 	    				e.printStackTrace();
	 	    				survey = null;
	 	    			}
		            	if (survey!=null){
		            		showRootEntitiesListScreen();		    	            
		            	}
		            		
		            	else {
		            		AlertMessage.createPositiveDialog(ApplicationManager.this, false, getResources().getDrawable(R.drawable.warningsign),
				 					getResources().getString(R.string.loadFormDefinitionTitle), getResources().getString(R.string.loadFormDefinitionMessage),
				 					getResources().getString(R.string.okay),
				 		    		new DialogInterface.OnClickListener() {
				 						@Override
				 						public void onClick(DialogInterface dialog, int which) {
				 							//ApplicationManager.this.finish();
				 							showFormsListScreen();
				 						}
				 					},
				 					null).show();	
		            	}
	 	    		} else {
	 	    			survey = ServiceFactory.getSurveyManager().getById(formId);
	 	    			showRootEntitiesListScreen();
	 	    		}	 	    		
	 	    	} else if (resultCode==getResources().getInteger(R.integer.backButtonPressed)){
	 	    		ApplicationManager.this.finish();
	 	    	}
	 	    } else if (requestCode==getResources().getInteger(R.integer.startingFormScreen)){
	 	    	CollectSurvey collectSurvey = (CollectSurvey)ApplicationManager.getSurvey();	        	
		    	DataManager dataManager = new DataManager(collectSurvey,collectSurvey.getSchema().getRootEntityDefinition(ApplicationManager.currRootEntityId).getName(),ApplicationManager.getLoggedInUser());
				if (dataManager.loadSummaries().size()==0){
		        	if (ApplicationManager.getSurvey().getSchema().getRootEntityDefinitions().size()==1){
		        		AlertMessage.createPositiveNegativeDialog(ApplicationManager.this, false, getResources().getDrawable(R.drawable.warningsign),
			 					getResources().getString(R.string.selectFormDefinitionTitle), getResources().getString(R.string.selectFormDefinitionMessage),
			 					getResources().getString(R.string.yes), getResources().getString(R.string.no),
			 		    		new DialogInterface.OnClickListener() {
			 						@Override
			 						public void onClick(DialogInterface dialog, int which) {
			 							//ApplicationManager.this.finish();
			 							showFormsListScreen();
			 						}
			 					},
			 		    		new DialogInterface.OnClickListener() {
			 						@Override
			 						public void onClick(DialogInterface dialog, int which) {
			 							showFormRootScreen();
			 						}
			 					},
			 					null).show();		        		
		        	} else {
		        		AlertMessage.createPositiveNegativeDialog(ApplicationManager.this, false, getResources().getDrawable(R.drawable.warningsign),
			 					getResources().getString(R.string.selectRootEntityTitle), getResources().getString(R.string.selectRootEntityMessage),
			 					getResources().getString(R.string.yes), getResources().getString(R.string.no),
			 		    		new DialogInterface.OnClickListener() {
			 						@Override
			 						public void onClick(DialogInterface dialog, int which) {
			 							showRootEntitiesListScreen();						
			 						}
			 					},
			 		    		new DialogInterface.OnClickListener() {
			 						@Override
			 						public void onClick(DialogInterface dialog, int which) {
			 							showFormRootScreen();
			 						}
			 					},
			 					null).show();		        		
		        	}
				} else {
					AlertMessage.createPositiveNegativeDialog(ApplicationManager.this, false, getResources().getDrawable(R.drawable.warningsign),
		 					getResources().getString(R.string.selectRecordTitle), getResources().getString(R.string.selectRecordMessage),
		 					getResources().getString(R.string.yes), getResources().getString(R.string.no),
		 		    		new DialogInterface.OnClickListener() {
		 						@Override
		 						public void onClick(DialogInterface dialog, int which) {
		 							showRecordsListScreen(ApplicationManager.currRootEntityId);						
		 						}
		 					},
		 		    		new DialogInterface.OnClickListener() {
		 						@Override
		 						public void onClick(DialogInterface dialog, int which) {
		 							showFormRootScreen();
		 						}
		 					},
		 					null).show(); 
						
				}	 				    
	 	    }
	    } catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onActivityResult",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
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
    		AlertMessage.createPositiveNegativeDialog(ApplicationManager.this, false, getResources().getDrawable(R.drawable.warningsign),
    				getResources().getString(R.string.exitAppTitle), getResources().getString(R.string.exitAppMessage),
    				getResources().getString(R.string.yes), getResources().getString(R.string.no),
    	    		new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						ApplicationManager.this.finish();
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
    
    private void initSession() {
    	ApplicationManager.sessionId = "1";//UUID.randomUUID().toString();
    	ApplicationManager.mainActivity = this;
    	ApplicationManager.rootEntitySelectionActivity = null;
    	ApplicationManager.recordSelectionActivity = null;
    	ApplicationManager.formScreenActivityList = new ArrayList<Activity>();
    	ApplicationManager.formSelectionActivity = null;
	}
    
	private boolean userExists(User user){
		List<User> usersList = ServiceFactory.getUserManager().loadAll();
		boolean userExists = false;
		Log.e("iloscUserowWBazie","=="+usersList.size());
		for (int i=0;i<usersList.size();i++){
			Log.e("usersList.get(i).equals(user)","=="+usersList.get(i).equals(user));
			if (usersList.get(i).equals(user)){
	 			userExists = true;
	 			break;
	 		}
	 	}
		return userExists;
	}
	
	public static User getLoggedInUser(){
		return ApplicationManager.loggedInUser;
	}
	
	private void showFormRootScreen(){	
		//List<EntityDefinition> rootEntitiesDefsList = schema.getRootEntityDefinitions();		
		Intent intent = new Intent(this,FormScreen.class);
		EntityDefinition rootEntityDef = (EntityDefinition)ApplicationManager.getSurvey().getSchema().getDefinitionById(ApplicationManager.currRootEntityId);
		intent.putExtra(getResources().getString(R.string.breadcrumb), ApplicationManager.getLabel(rootEntityDef/*, null*/));
		intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.singleEntityIntent));
		intent.putExtra(getResources().getString(R.string.parentFormScreenId), "");
		intent.putExtra(getResources().getString(R.string.idmlId), ApplicationManager.currRootEntityId);
		intent.putExtra(getResources().getString(R.string.instanceNo), 0);
		List<NodeDefinition> entityAttributes = rootEntityDef.getChildDefinitions();
        int counter = 0;
        for (NodeDefinition formField : entityAttributes){
			intent.putExtra(getResources().getString(R.string.attributeId)+counter, formField.getId());
			counter++;
        }
		/*intent.putExtra(getResources().getString(R.string.breadcrumb), "");
		intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.singleEntityIntent));
		intent.putExtra(getResources().getString(R.string.parentFormScreenId), "");
        intent.putExtra(getResources().getString(R.string.idmlId), 0);
        intent.putExtra(getResources().getString(R.string.instanceNo), 0);
        intent.putExtra(getResources().getString(R.string.attributeId)+0, ApplicationManager.currRootEntityId);*/
		this.startActivityForResult(intent,getResources().getInteger(R.integer.startingFormScreen));		
	}
	
	public void showRecordsListScreen(int rootEntityId){
		ApplicationManager.currentRecord = null;
		Intent recordLoadIntent = new Intent(this, RecordChoiceActivity.class);
		recordLoadIntent.putExtra(getResources().getString(R.string.rootEntityId), rootEntityId);
		this.startActivityForResult(recordLoadIntent, getResources().getInteger(R.integer.clusterSelection));
	}
	
	public void showRootEntitiesListScreen(){
		ApplicationManager.currRootEntityId = -1;		
		this.startActivityForResult(new Intent(this, RootEntityChoiceActivity.class),getResources().getInteger(R.integer.rootEntitySelection));
	}
	
	public void showFormsListScreen(){		
		this.startActivityForResult(new Intent(this, FormChoiceActivity.class),getResources().getInteger(R.integer.formDefinitionSelection));
	}
	
	public static NodeDefinition getNodeDefinition(int nodeId){
		//return schema.getDefinitionById(nodeId);
		return survey.getSchema().getDefinitionById(nodeId);
	}
	
    public static Survey getSurvey(){
    	return ApplicationManager.survey;
    }
    
    public static void setSurvey(CollectSurvey collectSurvey){
    	ApplicationManager.survey = collectSurvey;
    }
	
	public static UIElement getUIElement(int elementId){
		return ApplicationManager.uiElementsMap.get(elementId);
	}
	
	public static void putUIElement(int key, UIElement uiEl){
		ApplicationManager.uiElementsMap.put(key, uiEl);
	}
	
	public static String getSessionId(){
		return ApplicationManager.sessionId;
	}
	
	public static String getLabel(NodeDefinition nodeDef/*, String language*/){
		//Log.e("getLabel","=="+ApplicationManager.selectedLanguage);
		String label = nodeDef.getLabel(Type.INSTANCE, ApplicationManager.selectedLanguage);
		/*if (label==null){
			label = nodeDef.getLabel(Type.INSTANCE, "en");
		}*/
		if (label==null){
			if (nodeDef.getLabels().size()>0){
				label = nodeDef.getLabels().get(0).getText();	
			} else {
				label = "";
			}			
		}
		return label;
	}
	

    public static void updateUIElementsWithValidationResults(NodeChangeSet nodeChangeSet){
    	List<NodeChange<?>> nodeChangesList = nodeChangeSet.getChanges();
    	Log.e("Size of NodeChangeList","=="+nodeChangesList.size());
    	for (NodeChange<?> nodeChange : nodeChangesList){
    		Log.e("Does nodeChange.getNode() Not Null?","=="+(nodeChange.getNode()!=null));
    		Log.e("Does nodeChange.getNode().getInternalId() Not Null","=="+(nodeChange.getNode().getInternalId()!=null));    		
    		if (nodeChange.getNode().getInternalId() !=null){
    			Log.e("Node ID","=="+nodeChange.getNode().getInternalId());
    			//HERE WE CHECK DOES IT HAVE ANY ERRORS or WARNINGS
    			if (nodeChange instanceof AttributeChange) {
    				ValidationResults results = ((AttributeChange)nodeChange).getValidationResults();
    				Log.e("VALIDATION FOR FIELD", "Errors: " + results.getErrors().size() + " : " + results.getErrors().toString());
    				Log.d("VALIDATION FOR FIELD", "Warnings: "  + results.getWarnings().size() + " : " + results.getWarnings().toString()); 			
    				//Make background color red or yellow if there is any errors/warnings
//        			Log.e("ApplicationManager.getUIElement(nodeChange.getNode().getInternalId())!=null","=="+(ApplicationManager.getUIElement(nodeChange.getNode().getId())!=null));
//        			UIElement uiEl = ApplicationManager.getUIElement(nodeChange.getNode().getInternalId());    				
    				UIElement uiEl = ApplicationManager.getUIElement(nodeChange.getNode().getDefinition().getId());
    				String validationMsg = "";
    				if (uiEl != null){
        				Log.e("UI element is: ", uiEl.nodeDefinition.getName() + " with ID: " + uiEl.nodeDefinition.getId());
        				if (results.getErrors().size() > 0){
        					uiEl.setBackgroundColor(Color.RED);
        					for (ValidationResult error : results.getErrors()){
        						validationMsg += ValidationMessageBuilder.createInstance().getValidationMessage((Attribute<?, ?>)nodeChange.getNode(), error) + " : ";
        					}
        					Log.d("Validation message is: ", validationMsg);        				
        				}
        				else if (results.getWarnings().size() > 0)
        					uiEl.setBackgroundColor(Color.YELLOW);
        				else
        					uiEl.setBackgroundColor(Color.TRANSPARENT);
        			}
        			else{
        				Log.e("ERROR!","ApplicationManager cannot find node with id: "+nodeChange.getNode().getInternalId());
        			} 
    			}		
    		}    		
    	}
    }
}