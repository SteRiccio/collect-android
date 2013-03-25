package org.openforis.collect.android.management;

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
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import android.content.Context;
import android.util.Log;

public class DataManager {

	private static RecordManager recordManager = null;
	private CollectSurvey survey;
	private String rootEntity;
	private User user;
	
	public DataManager(CollectSurvey survey, String rootEntity, User loggedInUser){
		if (DataManager.recordManager==null){
			DataManager.recordManager = new RecordManager(false);
			DataManager.recordManager.setRecordDao(new RecordDao());
		}				
		this.survey = survey;
		this.rootEntity = rootEntity;
		this.user = loggedInUser;
	}
	
	public int saveRecord(Context ctx) {
		long startTime = System.currentTimeMillis();
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
		Log.e("record","SAVED IN "+(System.currentTimeMillis()-startTime)/1000+"s");
		return 0;
	}
	
	public void deleteRecord(int position){
		/*try {
			List<CollectRecord> recordsList = DataManager.recordManager.loadSummaries(survey, rootEntity);
			Log.e("DELETE record","=="+recordsList.get(position).getId());
			DataManager.recordManager.delete(recordsList.get(position).getId());			
		} catch (RecordPersistenceException e) {
			e.printStackTrace();
		}*/
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