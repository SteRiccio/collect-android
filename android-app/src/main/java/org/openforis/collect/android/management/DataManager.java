package org.openforis.collect.android.management;

import java.util.Date;
import java.util.List;

import org.openforis.collect.android.data.DataTree;
import org.openforis.collect.android.data.DataTreeNode;
import org.openforis.collect.android.data.FieldValue;
import org.openforis.collect.manager.RecordManager;
import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectRecord.Step;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.User;
import org.openforis.collect.persistence.RecordDao;
import org.openforis.collect.persistence.RecordPersistenceException;
import org.openforis.idm.metamodel.TextAttributeDefinition;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.EntityBuilder;
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
	
	public int saveRecord(DataTree dataTree, Context ctx){
		dataTree.printTree();
		try {
			long startTime = System.currentTimeMillis();
			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
			jdbcDao.getConnection();
			CollectRecord recordToSave;
			//recordToSave = this.recordManager.create(this.survey, this.survey.getSchema().getRootEntityDefinitions().get(0), this.user, "2.0", ApplicationManager.getSessionId());
			recordToSave = new CollectRecord(this.survey, this.survey.getVersions().get(this.survey.getVersions().size()-1).getName());
			recordToSave.setCreatedBy(this.user);
			recordToSave.setCreationDate(new Date());
			recordToSave.setStep(Step.ENTRY);
			//recordToSave.createRootEntity(1);
			
			//Entity cluster = recordToSave.createRootEntity(ApplicationManager.getSurvey().getSchema().getRootEntityDefinitions().get(0).getName());
			//cluster.setId(ApplicationManager.getSurvey().getSchema().getRootEntityDefinitions().get(0).getId());
			
			DataTreeNode dataRoot = ApplicationManager.valuesTree.getRoot();
			List<DataTreeNode> childNodesList = dataRoot.getNodeChildren();
			int childrenNo = childNodesList.size();
			for (int i=0;i<childrenNo;i++){
				Entity rootEntity = recordToSave.createRootEntity(ApplicationManager.getSurvey().getSchema().getRootEntityDefinitions().get(0).getName());
				rootEntity.setId(ApplicationManager.getSurvey().getSchema().getRootEntityDefinitions().get(i).getId());
				traverseNodeChildren(childNodesList.get(i), rootEntity, true);
			}
			
			/*Entity ts = EntityBuilder.addEntity(cluster, "task");
			EntityBuilder.addValue(ts, "type", new Code("2"));
			EntityBuilder.addValue(ts, "person", "JANel");
			EntityBuilder.addValue(ts, "date", new org.openforis.idm.model.Date(2010,2,15));			
			EntityBuilder.addValue(cluster, "district", new Code("123"));*/			

			this.recordManager.save(recordToSave, ApplicationManager.getSessionId());
			Log.e("record","SAVED IN "+(System.currentTimeMillis()-startTime)/1000+"s");
		} catch (RecordPersistenceException e) {
			return 1;
		} catch (NullPointerException e){
			
		} finally {
			JdbcDaoSupport.close();
		}
		//this.recordManager.getRecordDao().insert(recordToSave);		
		return 0;
	}
	
	public List<CollectRecord> loadSummaries(){
		long startTime = System.currentTimeMillis();
		JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
		jdbcDao.getConnection();
		List<CollectRecord> recordsList = this.recordManager.loadSummaries(survey, rootEntity);		
		JdbcDaoSupport.close();
		Log.e("loadingSummaries","=="+(System.currentTimeMillis()-startTime)/1000);	
		return recordsList;
	}
	
	public int loadRecord(int recordId){
		try {
			long startTime = System.currentTimeMillis();
			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
			jdbcDao.getConnection();

			JdbcDaoSupport.close();
			Log.e("record"+recordId,"LOADED IN "+(System.currentTimeMillis()-startTime)/1000+"s");			
		} catch (NullPointerException e){
			
		}
		return 0;
	}
	
	private void traverseNodeChildren(DataTreeNode node, Entity parentEntity, boolean isRoot){
		Log.e("visitedNode",ApplicationManager.getSurvey().getSchema().getDefinitionById(node.getNodeId()).getName()+"=="+node.getNodePath());
		Entity currentEntity = null;
		if (!isRoot){
			currentEntity = EntityBuilder.addEntity(parentEntity, ApplicationManager.getSurvey().getSchema().getDefinitionById(node.getNodeId()).getName());	
		} else {
			currentEntity = parentEntity;
		}
		addNodeDataToRecord(node, currentEntity);
		List<DataTreeNode> childNodesList = node.getNodeChildren();
		int childrenNo = childNodesList.size();
		for (int i=0;i<childrenNo;i++){
			traverseNodeChildren(childNodesList.get(i), parentEntity, false);
		}
	}
	
	private void addNodeDataToRecord(DataTreeNode dataNode, Entity parentEntity){
		List<FieldValue> nodeValuesList = dataNode.getNodeValues();
		for (int i=0;i<nodeValuesList.size();i++){
			FieldValue fieldValue = nodeValuesList.get(i);
			if (fieldValue!=null){
				if (fieldValue.getValues()!=null){
					String valueToAdd = fieldValue.getValue(0).get(0);
					Log.e("valueToAdd","=="+valueToAdd);
					if (valueToAdd!=null && !valueToAdd.equals("null") 
							&& ApplicationManager.getSurvey().getSchema().getDefinitionById(fieldValue.getId()) instanceof TextAttributeDefinition)
						EntityBuilder.addValue(parentEntity, ApplicationManager.getSurvey().getSchema().getDefinitionById(fieldValue.getId()).getName(), valueToAdd);
				}
			}
		}
	}
}