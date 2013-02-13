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
import org.openforis.collect.persistence.RecordUnlockedException;
import org.openforis.idm.metamodel.TextAttributeDefinition;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.EntityBuilder;
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
	
	public int saveRecord(DataTree dataTree, Context ctx){
		long startTime = System.currentTimeMillis();
		try {
			JdbcDaoSupport jdbcDao = new JdbcDaoSupport();
			jdbcDao.getConnection();
			CollectRecord recordToSave = ApplicationManager.currentRecord;
			
			if (recordToSave.getId()==null){
				recordToSave = new CollectRecord(this.survey, this.survey.getVersions().get(this.survey.getVersions().size()-1).getName());
				recordToSave.setCreatedBy(this.user);
				recordToSave.setCreationDate(new Date());
				recordToSave.setStep(Step.ENTRY);
				Entity rootEntity = recordToSave.createRootEntity(ApplicationManager.getSurvey().getSchema().getRootEntityDefinitions().get(0).getName());
				rootEntity.setId(ApplicationManager.getSurvey().getSchema().getRootEntityDefinitions().get(0).getId());
				/*DataTreeNode dataRoot = ApplicationManager.valuesTree.getRoot();
				List<DataTreeNode> childNodesList = dataRoot.getNodeChildren();
				int childrenNo = childNodesList.size();
				for (int i=0;i<childrenNo;i++){
					Entity rootEntity = recordToSave.createRootEntity(ApplicationManager.getSurvey().getSchema().getRootEntityDefinitions().get(0).getName());
					rootEntity.setId(ApplicationManager.getSurvey().getSchema().getRootEntityDefinitions().get(i).getId());
					traverseNodeChildren(childNodesList.get(i), rootEntity, true);
				}*/
				
			} else {
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
			//loadedRecord = DataManager.recordManager.checkout(survey, user, recordId, Step.ENTRY.getStepNumber(), ApplicationManager.getSessionId(), false);
			//NodeDefinition node = (NodeDefinition)loadedRecord.getRootEntity().get("id", 0);
			//TextAttributeDefinition text = (TextAttributeDefinition)node;
			/*for (int j=0;j<loadedRecord.getRootEntity().getChildren().size();j++){
				Node<? extends NodeDefinition> node = loadedRecord.getRootEntity().getChildren().get(j);
				Log.e("node","=="+node.getName());
				//TextAttributeDefinition text = (TextAttributeDefinition)node.getDefinition();
				if (!(node instanceof Entity)){
					Attribute attr = (Attribute)node;
					Log.e("iloscFieldow","=="+attr.getFieldCount());
					for (int i=0;i<attr.getFieldCount();i++){
						Log.e("fieldValue","=="+attr.getField(i).getValue());
					}
				}				
			}*/
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
	
	public void releaseLock(int recordId){
		DataManager.recordManager.releaseLock(recordId);
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