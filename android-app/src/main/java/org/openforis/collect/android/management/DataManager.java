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
	
	public int saveRecord(Context ctx){
		long startTime = System.currentTimeMillis();
		try {
			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
			jdbcDao.getConnection();
			CollectRecord recordToSave = ApplicationManager.currentRecord;
			
			if (recordToSave.getId()==null){
				//Log.e("SAVING", "NEW RECORD");
				recordToSave.setCreatedBy(this.user);
				recordToSave.setCreationDate(new Date());
				recordToSave.setStep(Step.ENTRY);			
			} else {
				//Log.e("updating", "existing RECORD");
				recordToSave.setModifiedDate(new Date());
				/*DataTreeNode dataRoot = ApplicationManager.valuesTree.getRoot();
				List<DataTreeNode> childNodesList = dataRoot.getNodeChildren();
				int childrenNo = childNodesList.size();
				//Log.e("PRZED","FOR2");
				for (int i=0;i<childrenNo;i++){
					Entity rootEntity = recordToSave.getRootEntity();
					//rootEntity.setId(ApplicationManager.getSurvey().getSchema().getRootEntityDefinitions().get(i).getId());
					traverseNodeChildren(childNodesList.get(i), rootEntity, true);
				}*/
			}
			
			Log.e("recordManager==null","=="+(DataManager.recordManager==null));
			Log.e("recordToSave==null","=="+(recordToSave==null));
			Log.e("sesssionID","=="+ApplicationManager.getSessionId());
			//Log.e("isRECORDlocked","=="+DataManager.recordManager.checkIsLocked(recordToSave.getId(), user, "1"/* ApplicationManager.getSessionId()*/));						
			//Log.e("AFTER","CHECKING LOCKED");
			DataManager.recordManager.save(recordToSave, ApplicationManager.getSessionId());
		} catch (RecordUnlockedException e) {
			Log.e("RecordUnlockedException","=="+e.getMessage());
			//return 1;
		} catch (RecordPersistenceException e) {
			Log.e("RecordPersistenceException","=="+e.getMessage());
			//return 1;
		} catch (NullPointerException e){
			Log.e("NullPointerException","=="+e.getMessage());
			//return 1;
		} finally {
			//Log.e("FINALLY","==");
			//JdbcDaoSupport.close();
		}
		//this.recordManager.getRecordDao().insert(recordToSave);		
		Log.e("record","SAVED IN "+(System.currentTimeMillis()-startTime)/1000+"s");
		return 0;
	}
	
	public List<CollectRecord> loadSummaries(){
		long startTime = System.currentTimeMillis();
		JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
		jdbcDao.getConnection();
		List<CollectRecord> recordsList = DataManager.recordManager.loadSummaries(survey, rootEntity);		
		JdbcDaoSupport.close();
		Log.e("loadingSummaries","=="+(System.currentTimeMillis()-startTime)/1000);	
		return recordsList;
	}
	
	public CollectRecord loadRecord(int recordId){
		long startTime = System.currentTimeMillis();
		CollectRecord loadedRecord = null;
		try {
			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
			jdbcDao.getConnection();
			Log.e("LOAD",recordId+"==");
			loadedRecord = this.recordManager.load(survey, recordId, Step.ENTRY.getStepNumber());
			/*Log.e("clusterNode",node.getId()+"=="+node.getName());
			Log.e("loadedRecord==null","=="+(loadedRecord==null));
			Log.e("owner","==="+loadedRecord.getCreatedBy());
			Log.e("data","==="+loadedRecord.getCreationDate());
			Log.e("entityiesNo","==="+loadedRecord.getEntityCounts().size());
			Log.e("root","==="+loadedRecord.getRootEntity().getName());
			Log.e("printRecord","=="+loadedRecord.toString());*/
			JdbcDaoSupport.close();
		} catch (NullPointerException e){
			Log.e("NullPointerException","=="+e.getMessage());
		} catch (RecordPersistenceException e) {
			Log.e("RecordPersistenceException","=="+e.getMessage());
		}
		Log.e("record"+recordId,"LOADED IN "+(System.currentTimeMillis()-startTime)/1000+"s");
		return loadedRecord;
	}
}