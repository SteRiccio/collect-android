package org.springframework.jdbc.core.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//move to new project
public class JdbcDaoSupport {

	private static Connection connection;
	//private static DataSource dataSource;
	
	private final String url = "jdbc:sqldroid:"+"/data/data/org.openforis.collect.android/databases/collect.db";
	
	public Connection getConnection() {
		try {
			if ( connection == null || connection.isClosed() ) {
				Class.forName("org.sqldroid.SQLDroidDriver").newInstance();
				connection = DriverManager.getConnection(url);				
			}
		} catch (Exception e) {
			connection = null;
		} 
		return connection;
	}
	
	public static void commit() {
		try {
			connection.commit();
		} catch (SQLException e) {
			
		}
	}
	
	public static void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			
		}
	}
	
	public static boolean isOpen(){
		try {
			return !connection.isClosed();
		} catch (SQLException e) {
			return false;
		} catch (NullPointerException e){
			return false;
		}
	}
	
	public static boolean isClosed(){
		try {
			return connection.isClosed();
		} catch (SQLException e) {
			return true;
		} catch (NullPointerException e){
			return true;
		}
	}
}