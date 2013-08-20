package org.openforis.collect.android.database;

import static org.openforis.collect.persistence.RecordDao.SUMMARY_FIELDS;
import static org.openforis.collect.persistence.jooq.Tables.OFC_RECORD;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectRecord.Step;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.RecordSummarySortField;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.Schema;
import org.springframework.transaction.annotation.Transactional;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//import org.jooq.SelectQuery;
//import org.jooq.Record;
//import org.jooq.Result;
//import org.jooq.SelectQuery;
//import org.openforis.collect.persistence.CodeListItemDao.JooqFactory;


public class RecordDao extends org.openforis.collect.persistence.RecordDao {

	public RecordDao() {
		super();
	}
	
	@Override
	@Transactional
	public List<CollectRecord> loadSummaries(CollectSurvey survey, String rootEntity, Step step, int offset, int maxRecords, 
			List<RecordSummarySortField> sortFields, String... keyValues) {
		/*JooqFactory jf = getMappingJooqFactory(survey);
		SelectQuery q = jf.selectQuery();	
		q.addFrom(OFC_RECORD);
		q.addSelect(SUMMARY_FIELDS);

		Schema schema = survey.getSchema();
		EntityDefinition rootEntityDefn = schema.getRootEntityDefinition(rootEntity);
		Integer rootEntityDefnId = rootEntityDefn.getId();
		q.addConditions(OFC_RECORD.SURVEY_ID.equal(survey.getId()));
		q.addConditions(OFC_RECORD.ROOT_ENTITY_DEFINITION_ID.equal(rootEntityDefnId));
		if ( step != null ) {
			q.addConditions(OFC_RECORD.STEP.equal(step.getStepNumber()));
		}
		addFilterByKeyConditions(q, keyValues);
		
		if ( sortFields != null ) {
			for (RecordSummarySortField sortField : sortFields) {
				addOrderBy(q, sortField);
			}
		}
		
		//always order by ID to avoid pagination issues
		q.addOrderBy(OFC_RECORD.ID);
		
		//add limit
		q.addLimit(offset, maxRecords);
		
		//fetch results
		Result<Record> result = q.fetch();
		
		return jf.fromResult(result);*/
		long startTime = System.currentTimeMillis();
		List<CollectRecord> result = new ArrayList<CollectRecord>();
		//preparing data for query
		Schema schema = survey.getSchema();
		EntityDefinition rootEntityDefn = schema.getRootEntityDefinition(rootEntity);
		Integer rootEntityDefnId = rootEntityDefn.getId();
		
		//query string
		String query = "SELECT " + 
				OFC_RECORD.DATE_CREATED + "," + OFC_RECORD.CREATED_BY_ID + "," + OFC_RECORD.DATE_MODIFIED + "," + OFC_RECORD.ERRORS + "," + OFC_RECORD.ID + "," + 
			     OFC_RECORD.MISSING + "," + OFC_RECORD.MODEL_VERSION + "," + OFC_RECORD.MODIFIED_BY_ID + "," + 
			     OFC_RECORD.ROOT_ENTITY_DEFINITION_ID + "," + OFC_RECORD.SKIPPED + "," + OFC_RECORD.STATE + "," + OFC_RECORD.STEP + "," + OFC_RECORD.SURVEY_ID + "," + 
			     OFC_RECORD.WARNINGS + "," + OFC_RECORD.KEY1 + "," + OFC_RECORD.KEY2 + "," + OFC_RECORD.KEY3 + "," + 
			     OFC_RECORD.COUNT1 + "," + OFC_RECORD.COUNT2 + "," + OFC_RECORD.COUNT3 + "," + OFC_RECORD.COUNT4 + "," + OFC_RECORD.COUNT5
			     + " FROM " + OFC_RECORD;				
		Log.e("MOBILE RECORD DAO","=="+query);
		
		//executing query
		SQLiteDatabase db = DatabaseHelper.getDb();
		Cursor cursor = db.rawQuery(query, null);
		Log.e("Mobile RECORD DAO", "Number of rows is: " + cursor.getCount());
		db.close();
		//preparing result
		while (cursor.moveToNext()) {
			for (int i=0;i<cursor.getColumnCount();i++){
				Log.e(cursor.getColumnName(i)+"=","=="+cursor.getString(i));
			}
		}	
		
		Log.e("MOBILE RECORD DAO", "Total time: "+(System.currentTimeMillis()-startTime));
		return result;
	}
	
	/*@Override
	protected List<PersistedCodeListItem> loadChildItems(CodeList codeList, Integer parentItemId, ModelVersion version) {
		long startTime = System.currentTimeMillis();
		List<PersistedCodeListItem> result = new ArrayList<PersistedCodeListItem>();
		//Log.e("Mobile DAO", "Starts loading child item: " + System.currentTimeMillis());	
		//Prepare query
		CollectSurvey survey = (CollectSurvey) codeList.getSurvey();
		TableField<OfcCodeListRecord, Integer> surveyIdField = getSurveyIdField(survey.isWork());		
		//Check if parent_id is NULL
		String parentIdCondition = "";
		if (parentItemId == null){
			parentIdCondition = OFC_CODE_LIST.PARENT_ID + " is null";
		}else{
			parentIdCondition = OFC_CODE_LIST.PARENT_ID + " = " + parentItemId;
		}
		//Query string
		String query = "select * from " + OFC_CODE_LIST 
				+ " where " + surveyIdField + " = " + survey.getId()
				+ " and " + OFC_CODE_LIST.CODE_LIST_ID + " = " + codeList.getId()
				+ " and " + parentIdCondition
				+ " order by " + OFC_CODE_LIST.SORT_ORDER; 
		
		//Log.e("Mobile DAO", "Query is: " + query);
		Log.e("Mobile DAO", codeList.getId()+"CodeList is: " + codeList.getName());
		//Execute query
		SQLiteDatabase db = DatabaseHelper.getDb();
		Cursor cursor = db.rawQuery(query, null);
		//Log.e("Mobile DAO", "Number of rows is: " + cursor.getCount());
		//Close database
		db.close();
		//Prepare result
		while (cursor.moveToNext()) {
			for (int i=0;i<cursor.getColumnCount();i++){
				Log.e(cursor.getColumnName(i)+"=","=="+cursor.getString(i));
			}
			CodeListItem codeListItem = new CodeListItem(codeList, codeList.getId());
			//codeListItem.setAnnotation(qname, value);
			//Log.e("CODE","=="+cursor.getString(cursor.getColumnIndex(OFC_CODE_LIST.CODE.getName())));
			codeListItem.setCode(cursor.getString(cursor.getColumnIndex(OFC_CODE_LIST.CODE.getName())));
			//codeListItem.setDeprecatedVersion(deprecated);
			//codeListItem.setDeprecatedVersionByName(name);
			//codeListItem.setLabel(language, text);
			//codeListItem.setParentItem(parentItem);
			codeListItem.setQualifiable(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(OFC_CODE_LIST.QUALIFIABLE.getName()))));
			//codeListItem.setSinceVersion(since);
			//codeListItem.setSinceVersionByName(name);
			//codeListItem.addDescription(description);
			
			//LanguageSpecificText label = new LanguageSpecificText(ApplicationManager.selectedLanguage,cursor.getString(cursor.getColumnIndex(OFC_CODE_LIST.LABEL1.getName())));
			//codeListItem.addLabel(label);
			List<String> languageList = codeList.getSurvey().getLanguages();
			//Log.e("languagesNo",languageList.get(0)+"=="+languageList.size());
			//Log.e("LABEL","=="+cursor.getString(cursor.getColumnIndex(OFC_CODE_LIST.LABEL1.getName())));
			LanguageSpecificText label = new LanguageSpecificText(languageList.get(0),cursor.getString(cursor.getColumnIndex(OFC_CODE_LIST.LABEL1.getName())));
			codeListItem.addLabel(label);
			if (cursor.getString(cursor.getColumnIndex(OFC_CODE_LIST.LABEL2.getName()))!=null){
				//Log.e("LABEL","=="+cursor.getString(cursor.getColumnIndex(OFC_CODE_LIST.LABEL2.getName())));
				label = new LanguageSpecificText(languageList.get(1),cursor.getString(cursor.getColumnIndex(OFC_CODE_LIST.LABEL2.getName())));
				codeListItem.addLabel(label);
			}
			if (cursor.getString(cursor.getColumnIndex(OFC_CODE_LIST.LABEL3.getName()))!=null){
				//Log.e("LABEL","=="+cursor.getString(cursor.getColumnIndex(OFC_CODE_LIST.LABEL3.getName())));
				label = new LanguageSpecificText(languageList.get(2),cursor.getString(cursor.getColumnIndex(OFC_CODE_LIST.LABEL3.getName())));
				codeListItem.addLabel(label);
			}
			
			PersistedCodeListItem item = PersistedCodeListItem.fromItem(codeListItem);
			result.add(item);
		}
		//Log.e("Mobile DAO", "Ready to return child item: " + System.currentTimeMillis());
		Log.e("MOBILE DAO", "Total time: "+(System.currentTimeMillis()-startTime));
		return result;
	}*/
	
}
