package org.openforis.collect.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseWrapper{
	
	private static final String DATABASE_NAME = "collect.db";
	private static final String DB_PATH = "/data/data/org.openforis.collect.android/databases/";
	private static final int DATABASE_VERSION = 3;
	///data/data/org.openforis.collect.android/databases/collect.db
	private static OpenHelper openHelper;
	
	private Context context;
	public static SQLiteDatabase db;
	
	public DatabaseWrapper(Context ctx){
		try{
			this.context = ctx;
			
	        openHelper = new OpenHelper(context);

	       	DatabaseWrapper.db = openHelper.getWritableDatabase();
		}
		catch (IllegalStateException e){
			
		}
	}
	
	public SQLiteDatabase openDataBase(){
        String myPath = DB_PATH + DATABASE_NAME;
        try{
        	db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        }catch(Exception e){

        }
        return db;
    }

	public boolean checkDataBase(){		 
    	SQLiteDatabase checkDB = null; 
    	try{
    		String myPath = DB_PATH + DATABASE_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);    		 
    	}catch(SQLiteException e){
    		checkDB = null;
    	}
    	if(checkDB != null){
    		checkDB.close(); 
    	}
    	return checkDB != null ? true : false;
    }
	
	public void close() 
    {
         openHelper.close();
    }

	private static class OpenHelper extends SQLiteOpenHelper {
		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
	}
}