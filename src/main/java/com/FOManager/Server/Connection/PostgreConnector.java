package com.FOManager.Server.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgreConnector {
	private static Connection conn = null;
	//connection properties 
	private static String url = "jdbc:postgresql://localhost:5432";
	private static String username = "postgres";
	private static String password = "qwerty1@";
	
	public static Connection getConnection() throws SQLException {
		if(conn == null) {
		Properties props = new Properties();
		props.setProperty("user", username);
		props.setProperty("password", password);
		conn = DriverManager.getConnection(url, props);
		}
		return conn;
	}
}
