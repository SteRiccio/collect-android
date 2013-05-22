package org.openforis.collect.android.database;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * 
 * @author S. Ricci
 *
 */
public class SQLDroidDataSource implements DataSource {

	private static final String SQLDROID_DRIVER = "org.sqldroid.SQLDroidDriver";
	
	private String url;
	private Connection connection;

	@Override
	public Connection getConnection() throws SQLException {
		return getConnection(null, null);
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		try {
			if ( connection == null || connection.isClosed() ) {
				//register driver
				Class.forName(SQLDROID_DRIVER).newInstance();
				connection = DriverManager.getConnection(getUrl());				
			}
		} catch (Exception e) {
			connection = null;
		} 
		return connection;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	//Compatibility with JDK 7
	public Logger getParentLogger() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
