package org.openforis.collect.android.database.liquibase;

import liquibase.logging.LogLevel;
import liquibase.logging.core.DefaultLogger;
import android.util.Log;

/**
 * 
 * @author S. Ricci
 *
 */
public class AndroidLiquibaseLogger extends DefaultLogger {

	private static final String TAG = "Liquibase";

	@Override
	protected void print(LogLevel logLevel, String message) {
		switch(logLevel) {
		case OFF:
			break;
		case DEBUG:
			Log.d(TAG, message);
			break;
		case INFO:
			Log.i(TAG, message);
			break;
		case WARNING:
			Log.w(TAG, message);
			break;
		case SEVERE:
			Log.e(TAG, message);
			break;
		default:
			Log.i(TAG, message);
		}
	}
	
}
