package org.openforis.collect.android.database;

import static org.openforis.collect.persistence.jooq.tables.OfcCodeList.OFC_CODE_LIST;

import java.util.ArrayList;
import java.util.List;

//import org.jooq.SelectQuery;
import org.jooq.TableField;
import org.openforis.collect.android.config.Configuration;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.persistence.jooq.tables.records.OfcCodeListRecord;
//import org.jooq.Record;
//import org.jooq.Result;
//import org.jooq.SelectQuery;
//import org.openforis.collect.persistence.CodeListItemDao.JooqFactory;
import org.openforis.idm.metamodel.CodeList;
import org.openforis.idm.metamodel.ModelVersion;
import org.openforis.idm.metamodel.PersistedCodeListItem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class CodeListItemDao extends org.openforis.collect.persistence.CodeListItemDao {

	public CodeListItemDao() {
		super();
	}
	
	@Override
	protected List<PersistedCodeListItem> loadChildItems(CodeList codeList, Integer parentItemId, ModelVersion version) {
		List<PersistedCodeListItem> result = new ArrayList<PersistedCodeListItem>();
		Log.e("Mobile DAO", "Starts loading child item: " + System.currentTimeMillis());	
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
		
		Log.e("Mobile DAO", "Query is: " + query);
		//Execute query
		SQLiteDatabase db = DatabaseHelper.getDb();
		Cursor cursor = db.rawQuery(query, null);
		Log.e("Mobile DAO", "Number of rows is: " + cursor.getCount());
		//Close database
		db.close();
		//Prepare result
		
		Log.e("Mobile DAO", "Ready to return child item: " + System.currentTimeMillis());
		return result;
	}
	
}
