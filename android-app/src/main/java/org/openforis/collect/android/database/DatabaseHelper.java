package org.openforis.collect.android.database;

import java.sql.Connection;
import java.sql.SQLException;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.core.AndroidSQLiteDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.logging.LogFactory;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.openforis.collect.android.config.Configuration;
import org.openforis.collect.android.database.liquibase.AndroidLiquibaseLogger;
import org.openforis.collect.android.service.ServiceFactory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 
 * @author K. Waga
 * @author S. Ricci
 *
 */
public abstract class DatabaseHelper {
	
	private static final String LIQUIBASE_CHANGELOG = "org/openforis/collect/db/changelog/db.changelog-master.xml";
	
	public static void init(Context ctx, Configuration config){
        createDatabase(ctx, config);
	}

	private static void createDatabase(Context ctx, Configuration config) {
		OpenHelper openHelper = new OpenHelper(ctx, config);
       	SQLiteDatabase db = openHelper.getWritableDatabase();
       	if ( db == null ) {
       		throw new RuntimeException("Null db");
       	}
       	db.close();
	}

	public static void updateDBSchema() {
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
				try {
					c.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	public static void closeConnection() {
		SQLDroidDataSource dataSource = ServiceFactory.getDataSource();
		try {
			Connection c = dataSource.getConnection(false);
			if ( c != null && ! c.isClosed() ) {
				c.close();
			}
		} catch(Exception e) {}
	}
	

	private static class OpenHelper extends SQLiteOpenHelper {
		OpenHelper(Context context, Configuration config) {
			super(context, config.getDbName(), null, config.getDbVersion());
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

}