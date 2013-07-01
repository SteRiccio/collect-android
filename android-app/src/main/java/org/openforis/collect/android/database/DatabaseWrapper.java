package org.openforis.collect.android.database;

import java.sql.Connection;
import java.sql.SQLException;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.core.AndroidSQLiteDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.logging.LogFactory;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.openforis.collect.android.database.liquibase.AndroidLiquibaseLogger;
import org.openforis.collect.android.service.ServiceFactory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public abstract class DatabaseWrapper{
	
	private static final String DATABASE_NAME = "collect.db";
	private static final String DB_PATH = "/data/data/org.openforis.collect.android/databases/";
	private static final String LIQUIBASE_CHANGELOG = "org/openforis/collect/db/changelog/db.changelog-master.xml";
	private static final int DATABASE_VERSION = 3;
	public static final String CONNECTION_URL = "jdbc:sqldroid:"+DB_PATH+"collect.db";
	///data/data/org.openforis.collect.android/databases/collect.db
	private static OpenHelper openHelper;
	public static SQLiteDatabase db;
	
	public static void init(Context ctx){
        openHelper = new OpenHelper(ctx);
       	DatabaseWrapper.db = openHelper.getWritableDatabase();
       	updateDBSchema();
	}
	
	public static SQLiteDatabase openDataBase(){
        String myPath = DB_PATH + DATABASE_NAME;
        try{
        	db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        }catch(Exception e){

        }
        return db;
    }

	public static boolean checkDataBase(){		 
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
	
	protected static void updateDBSchema() {
		Connection c = null;
		try {
			c = ServiceFactory.getDataSource().getConnection();
			LogFactory.putLogger(new AndroidLiquibaseLogger());
			Database database = new AndroidSQLiteDatabase();
			database.setConnection(new JdbcConnection(c));
			Liquibase liquibase = new Liquibase(LIQUIBASE_CHANGELOG, 
					new ClassLoaderResourceAccessor(), database);
		    liquibase.update(null);
		} catch(Exception e) {
			Log.e("==", e.getMessage(), e);
			if (c != null) {
                try {
					c.rollback();
				} catch (SQLException e1) {}
	        }
			throw new RuntimeException(e);
		} finally {
			if (c != null) {
//				c.close();
			}
		}
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