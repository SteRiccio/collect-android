package org.openforis.collect.android.config;

import org.openforis.collect.android.R;

import android.content.Context;
import android.content.res.Resources;

/**
 * 
 * @author S. Ricci
 *
 */
public class Configuration {

	private String dbName;
	private String dbPath;
	private int dbVersion;
	private String dbConnectionUrl;
	
	public static Configuration getDefault(Context ctx) {
		Configuration conf = new Configuration();
		Resources res = ctx.getResources();
		conf.dbName = res.getString(R.string.db_name);
		conf.dbPath = res.getString(R.string.db_path);
		conf.dbVersion = res.getInteger(R.integer.db_version);
		conf.dbConnectionUrl = "jdbc:sqldroid:" + conf.dbPath + conf.dbName;
		return conf;
	}
	
	public String getDbPath() {
		return dbPath;
	}

	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public int getDbVersion() {
		return dbVersion;
	}

	public void setDbVersion(int dbVersion) {
		this.dbVersion = dbVersion;
	}
	
	public String getDbConnectionUrl() {
		return dbConnectionUrl;
	}
	
	public void setDbConnectionUrl(String dbConnectionUrl) {
		this.dbConnectionUrl = dbConnectionUrl;
	}
	
}

