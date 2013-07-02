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

import android.util.Log;

/**
 * 
 * @author K. Waga
 * @author S. Ricci
 *
 */
public abstract class DatabaseHelper {
	
	private static final String LIQUIBASE_CHANGELOG = "org/openforis/collect/db/changelog/db.changelog-master.xml";
	
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
			Connection c = dataSource.getConnection();
			c.close();
		} catch(Exception e) {}
	}

}