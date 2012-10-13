package org.openforis.collect.android.database;

import android.database.sqlite.SQLiteDatabase;

public class CollectDatabase{
	
	private SQLiteDatabase db;
	
	public CollectDatabase(SQLiteDatabase database){
		this.db = database;
		this.createTables();
	}
	
	public void createTables(){
		db.execSQL(
				"create table if not exists ofc_application_info" + 
				" (" 
				+ "version text "
			    + ");"    );
		
		db.execSQL(
				"create table if not exists ofc_config" + 
				" (" 
				+ "name text not null primary key,"
				+ "value text not null"
			    + ");"    );
		
		db.execSQL(
				"create table if not exists ofc_logo" + 
				" (" 
				+ "pos integer not null primary key,"
				+ "image blob not null"
			    + ");"    );
		
		db.execSQL(
				"create table if not exists ofc_record" + 
				" (" 
				+ "id integer not null primary key autoincrement,"
				+ "survey_id integer not null,"
				+ "root_entity_definition_id integer not null,"
				+ "date_created text,"
				+ "created_by_id integer,"
				+ "date_modified text,"
				+ "modified_by_id integer,"
				+ "model_version text not null,"
				+ "step integer,"
				+ "state text,"
				+ "skipped integer,"
				+ "missing integer,"
				+ "errors integer,"
				+ "warnings integer,"
				+ "key1 text,"
				+ "key2 text,"
				+ "key3 text,"
				+ "count1 integer,"
				+ "count2 integer,"
				+ "count3 integer,"
				+ "count4 integer,"
				+ "count5 integer,"
				+ "data1 blob,"
				+ "data2 blob"
			    + ");"    );
		
		db.execSQL(
				"create table if not exists ofc_survey" + 
				" (" 
				+ "id integer not null primary key autoincrement,"
				+ "name text not null,"
				+ "uri text not null,"
				+ "idml text not null"
			    + ");"    );
		
		db.execSQL(
				"create table if not exists ofc_taxon" + 
				" (" 
				+ "id integer not null primary key autoincrement,"
				+ "taxon_id integer not null,"
				+ "code text not null,"
				+ "scientific_name text not null,"
				+ "taxon_rank text not null,"
				+ "taxonomy_id integer not null,"
				+ "step integer not null,"
				+ "parent_id integer"
			    + ");"    );
		
		db.execSQL(
				"create table if not exists ofc_taxon_vernacular_name" + 
				" (" 
				+ "id integer not null primary key autoincrement,"
				+ "vernacular_name text,"
				+ "language_code text not null,"
				+ "language_variety text,"
				+ "taxon_id integer,"
				+ "step integer not null,"
				+ "qualifier1 text,"
				+ "qualifier2 text,"
				+ "qualifier3 text"
			    + ");"    );
		
		db.execSQL(
				"create table if not exists ofc_taxonomy" + 
				" (" 
				+ "id integer not null primary key autoincrement,"
				+ "name text not null,"
				+ "metadata text not null"
			    + ");"    );
		
		db.execSQL(
				"create table if not exists ofc_user" + 
				" (" 
				+ "id integer not null primary key autoincrement,"
				+ "username text not null,"
				+ "password text not null,"
				+ "enabled text not null default \'Y\'"
			    + ");"    );
		
		db.execSQL(
				"create table if not exists ofc_user_role" + 
				" (" 
				+ "id integer not null primary key autoincrement,"
				+ "user_id integer not null,"
				+ "role text"
			    + ");"    );
	}
/*
	public void createPreferencesTables(){
		db.execSQL(
				"create table if not exists " + TABLE_PREFERENCES + 
				" (" + TABLE_PREFERENCES_FIELDS[0] + " integer primary key autoincrement, "
				+ TABLE_PREFERENCES_FIELDS[1] + " text, "
				+ TABLE_PREFERENCES_FIELDS[2] + " text "
			    + ");"    );
	}
	
	public void recreatePreferencesTables(){
		db.beginTransaction();
		try{
			db.execSQL("drop table if exists " + TABLE_PREFERENCES);
			this.createPreferencesTables();
			db.setTransactionSuccessful();
		} finally{
			db.endTransaction();
		}
	}
	
	public void saveItem(String preferenceName, String preferenceValue){
		ContentValues initialValues = new ContentValues();
		initialValues.put(TABLE_PREFERENCES_FIELDS[2], preferenceValue);
		if (this.itemExists(preferenceName))
		{
			String condition = "" + TABLE_PREFERENCES_FIELDS[1] + "='" + preferenceName + "'";
			this.updateQuery(TABLE_PREFERENCES, initialValues, condition);
		}
		else{
			initialValues.put(TABLE_PREFERENCES_FIELDS[1], preferenceName);	
			this.insertQuery(TABLE_PREFERENCES, initialValues);
		}
	}
	
	public String getItem(String preferenceName){
		String itemValue = "";
		Cursor mCursor =  db.rawQuery("select * from " + TABLE_PREFERENCES + 
				" where " + TABLE_PREFERENCES_FIELDS[1] + "='" + preferenceName + "'", null);		
		if (mCursor.moveToFirst()){
			itemValue = mCursor.getString(mCursor.getColumnIndex(TABLE_PREFERENCES_FIELDS[2]));
		}
		mCursor.close();
		return itemValue;
	}
	
	public boolean itemExists(String name){
		boolean exists = false;
		Cursor mCursor =  db.rawQuery("select count(*) from " + TABLE_PREFERENCES + 
				" where " + TABLE_PREFERENCES_FIELDS[1] + "='" + name + "'", null);
		if (mCursor.moveToFirst()){
			exists = (mCursor.getInt(0)>0)?true:false;
		}
		mCursor.close();
		return exists;
	}
	
	private long insertQuery(String table, ContentValues values){
		return db.insert(table, null, values);
	}
	
	private long updateQuery(String table, ContentValues values, String whereClause){
		return db.update(table, values, whereClause, null);
	}*/
}