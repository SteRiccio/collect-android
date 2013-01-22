package org.openforis.collect.android.management;

import java.util.Date;
import java.util.List;

import org.openforis.collect.android.data.DataTree;
import org.openforis.collect.manager.RecordManager;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectRecord.Step;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.User;
import org.openforis.collect.persistence.RecordDao;
import org.openforis.collect.persistence.RecordPersistenceException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import android.content.Context;
import android.util.Log;

public class DataManager {

	private RecordManager recordManager;
	private CollectSurvey survey;
	private String rootEntity;
	private User user;
	
	public DataManager(CollectSurvey survey, String rootEntity, User loggedInUser){
		this.recordManager = new RecordManager();
		this.recordManager.setRecordDao(new RecordDao());
		this.survey = survey;
		this.rootEntity = rootEntity;
		this.user = loggedInUser;
		Log.e("rootEntityToSave","=="+this.rootEntity);
	}
	
	public int save(DataTree dataTree, Context ctx){
		dataTree.printTree();
		try {
			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
			jdbcDao.getConnection();
			CollectRecord recordToSave;
			//recordToSave = this.recordManager.create(this.survey, this.survey.getSchema().getRootEntityDefinitions().get(0), this.user, "2.0", ApplicationManager.getSessionId());
			recordToSave = new CollectRecord(this.survey, this.survey.getVersions().get(this.survey.getVersions().size()-1).getName());
			recordToSave.setCreatedBy(this.user);
			recordToSave.setCreationDate(new Date());
			recordToSave.setStep(Step.ENTRY);
			recordToSave.createRootEntity(340);
			this.recordManager.save(recordToSave, ApplicationManager.getSessionId());
		} catch (RecordPersistenceException e) {
			return 1;
		} catch (NullPointerException e){
			
		} finally {
			JdbcDaoSupport.close();
		}
		//this.recordManager.getRecordDao().insert(recordToSave);		
		return 0;
	}
	
	public int loadSummaries(){
		JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
		jdbcDao.getConnection();
		//this.recordManager.loadSummaries(survey, rootEntity);
		long startTime = System.currentTimeMillis();
		List<CollectRecord> recordsList = this.recordManager.loadSummaries(survey, rootEntity);
		Log.e("loadingSummaries","=="+(System.currentTimeMillis()-startTime)/1000);
		String[] clusterList = new String[recordsList.size()];
		for (int i=0;i<recordsList.size();i++){
			clusterList[i] = recordsList.get(i).getId()+" "+recordsList.get(i).getCreatedBy().getName()
					+" "+recordsList.get(i).getCreationDate().toLocaleString();
			Log.e("cluster","=="+recordsList.get(i).getId()+" "+recordsList.get(i).getCreatedBy().getName()
					+" "+recordsList.get(i).getCreationDate().toLocaleString());
		}	
		JdbcDaoSupport.close();
		return 0;
	}
	
	public int load(){
		JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
		jdbcDao.getConnection();
		//this.recordManager.loadSummaries(survey, rootEntity);
		long startTime = System.currentTimeMillis();
		List<CollectRecord> recordsList = this.recordManager.loadSummaries(survey, rootEntity);
		Log.e("loadingSummaries","=="+(System.currentTimeMillis()-startTime)/1000);
		String[] clusterList = new String[recordsList.size()];
		for (int i=0;i<recordsList.size();i++){
			clusterList[i] = recordsList.get(i).getId()+" "+recordsList.get(i).getCreatedBy().getName()
					+" "+recordsList.get(i).getCreationDate().toLocaleString();
			Log.e("cluster","=="+recordsList.get(i).getId()+" "+recordsList.get(i).getCreatedBy().getName()
					+" "+recordsList.get(i).getCreationDate().toLocaleString());
		}	
		JdbcDaoSupport.close();
		return 0;
	}
}