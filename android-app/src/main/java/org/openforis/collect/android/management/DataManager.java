package org.openforis.collect.android.management;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openforis.collect.manager.RecordManager;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectRecord.Step;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.User;
import org.openforis.collect.persistence.RecordDao;
import org.openforis.collect.persistence.RecordPersistenceException;
import org.openforis.collect.persistence.RecordUnlockedException;
import org.openforis.collect.persistence.xml.DataHandler;
import org.openforis.collect.persistence.xml.DataMarshaller;
import org.openforis.collect.persistence.xml.DataUnmarshaller;
import org.openforis.collect.persistence.xml.DataUnmarshaller.ParseRecordResult;
import org.openforis.collect.persistence.xml.DataUnmarshallerException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class DataManager {

	private static RecordManager recordManager = null;
	private CollectSurvey survey;
	private String rootEntity;
	private User user;
	
	private DataMarshaller dataMarshaller;
	private DataUnmarshaller dataUnmarshaller;
	
	public DataManager(CollectSurvey survey, String rootEntity, User loggedInUser){
		if (DataManager.recordManager==null){
			DataManager.recordManager = new RecordManager(false);
			DataManager.recordManager.setRecordDao(new RecordDao());
		}				
		this.survey = survey;
		this.rootEntity = rootEntity;
		this.user = loggedInUser;
		
		this.dataMarshaller = new DataMarshaller();
		HashMap<String,User> users = new HashMap<String, User>();
		users.put(loggedInUser.getName(), loggedInUser);
		DataHandler dataHandler = new DataHandler(survey, users);
		this.dataUnmarshaller = new DataUnmarshaller(dataHandler);
	}
	
	public int saveRecord(Context ctx) {
		try {
			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
			jdbcDao.getConnection();
			CollectRecord recordToSave = ApplicationManager.currentRecord;
			
			if (recordToSave.getId()==null){
				recordToSave.setCreatedBy(this.user);
				recordToSave.setCreationDate(new Date());
				recordToSave.setStep(Step.ENTRY);			
			} else {
				recordToSave.setModifiedDate(new Date());
			}
			DataManager.recordManager.save(recordToSave, ApplicationManager.getSessionId());
		} catch (RecordUnlockedException e) {
			Log.e("RecordUnlockedException","=="+e.getLocalizedMessage());
		} catch (RecordPersistenceException e) {
			Log.e("RecordPersistenceException",e.getMessage()+"=="+e.getLocalizedMessage());
		} catch (NullPointerException e){
			Log.e("NullPointerException","=="+e.getLocalizedMessage());
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
		filename = Environment.getExternalStorageDirectory().toString()+"/mofc/data/exported/testowyRekord.xml";
		long startTime = System.currentTimeMillis();
		CollectRecord loadedRecord = null;
		try {
			ParseRecordResult result = this.dataUnmarshaller.parse(filename);
			loadedRecord = result.getRecord();
			Log.e("isSuccess","=="+result.isSuccess());
			Log.e("message","=="+result.getMessage());
			Log.e("warningsNO","=="+result.getWarnings());
			Log.e("loadedResult"+(result==null),"LOADED FROM XML IN "+(System.currentTimeMillis()-startTime)/1000+"s");
		} catch (NullPointerException e){
			e.printStackTrace();
		} catch (DataUnmarshallerException e) {
			e.printStackTrace();
		}		
		return loadedRecord;
	}
	
	public void deleteRecord(int position){
		try {
			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
			jdbcDao.getConnection();
			List<CollectRecord> recordsList = DataManager.recordManager.loadSummaries(survey, rootEntity);
			DataManager.recordManager.delete(recordsList.get(position).getId());			
		} catch (RecordPersistenceException e) {
			e.printStackTrace();
		}
	}
	
	public List<CollectRecord> loadSummaries(){
		JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
		jdbcDao.getConnection();
		List<CollectRecord> recordsList = DataManager.recordManager.loadSummaries(survey, rootEntity);		
		JdbcDaoSupport.close();
		return recordsList;
	}
	
	public CollectRecord loadRecord(int recordId){
		long startTime = System.currentTimeMillis();
		CollectRecord loadedRecord = null;
		try {
			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
			jdbcDao.getConnection();
			loadedRecord = DataManager.recordManager.load(survey, recordId, Step.ENTRY.getStepNumber());
			JdbcDaoSupport.close();
		} catch (NullPointerException e){
			e.printStackTrace();
		} /*catch (RecordPersistenceException e) {
			e.printStackTrace();
		}*/
		Log.e("record"+recordId,"LOADED IN "+(System.currentTimeMillis()-startTime)/1000+"s");
		return loadedRecord;
	}
}