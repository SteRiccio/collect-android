package org.openforis.collect.android.config;

import java.io.File;

import org.openforis.collect.android.R;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * 
 * @author S. Ricci
 *
 */
public class Configuration {

	private static final String DATABASES_FOLDER_NAME = "databases";
	private static final String FILES_FOLDER_NAME = "files";
	
	private String dbName;
	private String dbRootPath;
	private int dbVersion;
	private String dbConnectionUrl;
	
	public static Configuration getDefault(Context ctx) {
		Configuration conf = new Configuration();
		Resources res = ctx.getResources();
		conf.dbName = res.getString(R.string.db_name);
		conf.dbRootPath = getDatabasesFolderPath(ctx);
		conf.dbVersion = res.getInteger(R.integer.db_version);
		conf.dbConnectionUrl = "jdbc:sqldroid:" + conf.dbRootPath + File.separator + conf.dbName;
		return conf;
	}

	private static String getDatabasesFolderPath(Context ctx) {
		String filesDir = ctx.getFilesDir().getAbsolutePath();
		String rootPath = filesDir.substring(0, filesDir.length() - FILES_FOLDER_NAME.length());
		String path = rootPath + DATABASES_FOLDER_NAME;
		//Log.e("database path","=="+path);
		return path;
	}
	
	public String getDbRootPath() {
		return dbRootPath;
	}

	public void setDbRootPath(String dbRootPath) {
		this.dbRootPath = dbRootPath;
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

