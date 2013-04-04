package org.openforis.collect.android.management;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.openforis.collect.manager.RecordManager;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectRecord.Step;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.User;
import org.openforis.collect.persistence.RecordDao;
import org.openforis.collect.persistence.RecordPersistenceException;
import org.openforis.collect.persistence.RecordUnlockedException;
import org.openforis.collect.persistence.xml.DataMarshaller;
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
	
	public DataManager(CollectSurvey survey, String rootEntity, User loggedInUser){
		if (DataManager.recordManager==null){
			DataManager.recordManager = new RecordManager(false);
			DataManager.recordManager.setRecordDao(new RecordDao());
		}				
		this.survey = survey;
		this.rootEntity = rootEntity;
		this.user = loggedInUser;
		
		this.dataMarshaller = new DataMarshaller();
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
			
			FileWriter fwr = new FileWriter(folderToSave+"/"+ApplicationManager.currentRecord.getId()+"_"+ApplicationManager.currRootEntityId+"_"+ApplicationManager.currentRecord.getCreationDate()+"_"+ApplicationManager.currentRecord.getCreatedBy()+".xml");
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

		} catch (RecordPersistenceException e) {

		}
		Log.e("record"+recordId,"LOADED IN "+(System.currentTimeMillis()-startTime)/1000+"s");
		return loadedRecord;
	}
}