package org.openforis.collect.android.management;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openforis.collect.android.database.DatabaseHelper;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectRecord.Step;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.User;
import org.openforis.collect.persistence.RecordPersistenceException;
import org.openforis.collect.persistence.RecordUnlockedException;
import org.openforis.collect.persistence.xml.DataHandler;
import org.openforis.collect.persistence.xml.DataMarshaller;
import org.openforis.collect.persistence.xml.DataUnmarshaller;
import org.openforis.collect.persistence.xml.DataUnmarshaller.ParseRecordResult;
import org.openforis.collect.persistence.xml.DataUnmarshallerException;
import org.sqldroid.SQLiteDatabase;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

public class DataManager {

	private CollectSurvey survey;
	private String rootEntity;
	private User user;
	
	private DataMarshaller dataMarshaller;
	private DataUnmarshaller dataUnmarshaller;
	
	public DataManager(CollectSurvey survey, String rootEntity, User loggedInUser){
		this.survey = survey;
		this.rootEntity = rootEntity;
		this.user = loggedInUser;
		
		this.dataMarshaller = new DataMarshaller();
		HashMap<String,User> users = new HashMap<String, User>();
		users.put(loggedInUser.getName(), loggedInUser);
		DataHandler dataHandler = new DataHandler(survey, users);
		this.dataUnmarshaller = new DataUnmarshaller(dataHandler);
	}
	
	public boolean saveRecord(Context ctx) {
		boolean isSuccess = true;
		try {
//			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
//			jdbcDao.getConnection();
			CollectRecord recordToSave = ApplicationManager.currentRecord;
			
			if (recordToSave.getId()==null){
				recordToSave.setCreatedBy(this.user);
				recordToSave.setCreationDate(new Date());
				recordToSave.setStep(Step.ENTRY);			
			} else {
				recordToSave.setModifiedDate(new Date());
			}
			ServiceFactory.getRecordManager().save(recordToSave, ApplicationManager.getSessionId());
		} catch (RecordUnlockedException e) {
			e.printStackTrace();
			isSuccess = false;
		} catch (RecordPersistenceException e) {
			e.printStackTrace();
			isSuccess = false;
		} catch (NullPointerException e){
			e.printStackTrace();
			isSuccess = false;
		} catch (Exception e){
			e.printStackTrace();
			isSuccess = false;
		} finally {
			
		}
		return isSuccess;
	}
	
	public int saveRecord(CollectRecord recordToSave) {
		try {
//			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
//			jdbcDao.getConnection();
			Log.e("recordToSave==null","=="+(recordToSave==null));
			if (recordToSave.getId()==null){
				recordToSave.setCreatedBy(this.user);
				recordToSave.setCreationDate(new Date());
				recordToSave.setStep(Step.ENTRY);			
			} else {
				recordToSave.setModifiedDate(new Date());
			}
			ServiceFactory.getRecordManager().save(recordToSave, ApplicationManager.getSessionId());
		} catch (RecordUnlockedException e) {
			e.printStackTrace();
		} catch (RecordPersistenceException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		} finally {

		}
		return 0;
	}
	
	public int saveRecordToXml(CollectRecord recordToSave, String folderToSave) {
		long startTime = System.currentTimeMillis();
		try {
			//CollectRecord recordToSave = ApplicationManager.currentRecord;
			
			if (recordToSave.getId()==null){
				recordToSave.setCreatedBy(this.user);
				recordToSave.setCreationDate(new Date());
				recordToSave.setStep(Step.ENTRY);			
			} else {
				recordToSave.setModifiedDate(new Date());
			}
			
			FileWriter fwr = new FileWriter(folderToSave+"/"+ApplicationManager.currentRecord.getId()+"_"+ApplicationManager.currRootEntityId+"_"+ApplicationManager.currentRecord.getCreationDate().getDay()+"_"+ApplicationManager.currentRecord.getCreationDate().getMonth()+"_"+ApplicationManager.currentRecord.getCreationDate().getYear()+"_"+ApplicationManager.currentRecord.getCreationDate().getHours()+"_"+ApplicationManager.currentRecord.getCreationDate().getMinutes()+"_"+ApplicationManager.currentRecord.getCreatedBy().getName()+".xml");
			this.dataMarshaller.write(recordToSave, fwr);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} finally {

		}	
		Log.e("record","SAVED IN XML IN "+(System.currentTimeMillis()-startTime)/1000+"s");
		return 0;
	}
	
	public CollectRecord loadRecordFromXml(String filename) {
		filename = Environment.getExternalStorageDirectory().toString()+"/ofcm/data/imported/"+filename;
		long startTime = System.currentTimeMillis();
		CollectRecord loadedRecord = null;
		try {
			ParseRecordResult result = this.dataUnmarshaller.parse(filename);
			loadedRecord = result.getRecord();
			Log.e("isSuccess","=="+result.isSuccess());
			Log.e("message","=="+result.getMessage());
			//Log.e("warningsNo","=="+result.getWarnings().size());
			if (result.getFailures()!=null){
				Log.e("failuresNo","=="+result.getFailures().size());
				for (int i=0;i<result.getFailures().size();i++){
					Log.e("failure"+i,"=="+result.getFailures().get(i).getMessage());
					Log.e("failure"+i,"=="+result.getFailures().get(i).getPath());
				}
			} else {
				Log.e("failures==null","==true");
			}
			Log.e("loadedResult"+(result==null),"LOADED FROM XML IN "+(System.currentTimeMillis()-startTime)/1000+"s");
			Log.e("loadedRecord","=="+(loadedRecord==null));
			this.saveRecord(loadedRecord);
			Log.e("record","SAVED");
		} catch (NullPointerException e){
			e.printStackTrace();
		} catch (DataUnmarshallerException e) {
			e.printStackTrace();
		}			
		return loadedRecord;
	}
	
	public void deleteRecord(int position){
		try {
//			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
//			jdbcDao.getConnection();
			List<CollectRecord> recordsList = ServiceFactory.getRecordManager().loadSummaries(survey, rootEntity);
			ServiceFactory.getRecordManager().delete(recordsList.get(position).getId());			
		} catch (RecordPersistenceException e) {
			e.printStackTrace();
		} finally {
			DatabaseHelper.closeConnection();
		}
		
	}
	
	public List<CollectRecord> loadSummaries(){
		Log.e("loading","SUMMARIES");
		long startTime = System.currentTimeMillis();
		//JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
		//jdbcDao.getConnection();
		Log.e("jdbcDaoconnection","=="+((System.currentTimeMillis()-startTime)));
		startTime = System.currentTimeMillis();
		List<CollectRecord> recordsList = ServiceFactory.getRecordManager().loadSummaries(survey, rootEntity);		
		Log.e("loadSummaries","=="+((System.currentTimeMillis()-startTime)));
		//JdbcDaoSupport.close();
		DatabaseHelper.closeConnection();
		startTime = System.currentTimeMillis();
		String selectQuery = "select ofc_record.date_created, ofc_record.created_by_id, ofc_record.date_modified, ofc_record.errors, ofc_record.id, ofc_record.missing, ofc_record.model_version, ofc_record.modified_by_id, ofc_record.root_entity_definition_id, ofc_record.skipped, ofc_record.state, ofc_record.step, ofc_record.survey_id, ofc_record.warnings, ofc_record.key1, ofc_record.key2, ofc_record.key3, ofc_record.count1, ofc_record.count2, ofc_record.count3, ofc_record.count4, ofc_record.count5 from ofc_record where (ofc_record.survey_id = 1 and ofc_record.root_entity_definition_id = 3) order by ofc_record.id asc limit 2147483647 offset 0";	
		try{
			SQLiteDatabase db = new SQLiteDatabase("/data/data/org.openforis.collect.android/databases/collect.db",10000,10,1);
			
		    Cursor cursor = db.rawQuery(selectQuery, null);	
		    Log.e("NATIVEselectQUERY","=="+((System.currentTimeMillis()-startTime)));
		    int counter = 0;
		    if (cursor.moveToFirst()) {
		        do {
		        	Log.e("record"+counter,"=="+cursor.getString(0));
		        	Log.e("record"+counter,"=="+cursor.getString(1));
		        	Log.e("record"+counter,"=="+cursor.getString(2));
		        	Log.e("record"+counter,"=="+cursor.getString(3));
		        	Log.e("record"+counter,"=="+cursor.getString(4));
		            counter++;
		        } while (cursor.moveToNext());
		    }
		}catch (Exception e){
			e.printStackTrace();
		}
		
		
		
		return recordsList;
	}
	
	public CollectRecord loadRecord(int recordId){
		long startTime = System.currentTimeMillis();
		CollectRecord loadedRecord = null;
		try {
//			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
//			jdbcDao.getConnection();
			loadedRecord = ServiceFactory.getRecordManager().load(survey, recordId, Step.ENTRY);
//			JdbcDaoSupport.close();
			DatabaseHelper.closeConnection();
		} catch (NullPointerException e){
			e.printStackTrace();
		} /*catch (RecordPersistenceException e) {
			e.printStackTrace();
		}*/
		Log.e("record"+recordId,"LOADED IN "+(System.currentTimeMillis()-startTime)+"ms");
		return loadedRecord;
	}
}