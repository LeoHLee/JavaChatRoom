package database;

import java.io.*;
import java.sql.*;
import java.util.Properties;


class PropertiesUtil{
	private static Properties prop;
	static {
		try {
			prop = new Properties();
			prop.load(initial.class.getResourceAsStream("/database/ini/conn.properties"));
		}catch (FileNotFoundException e) {
			System.out.println("加载文件失败");
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	public static String getDriverProperties(String key) {
		return prop.getProperty(key + "driverClass");
	}
	public static String getUrlProperties(String key) {
		return prop.getProperty(key + "url");
	}
	public static String getUsernameProperties(String key) {
		return prop.getProperty(key + "username");
	}
	public static String getPasswordProperties(String key) {
		return prop.getProperty(key + "password");
	}
}

class Connect{
	private static String key;
	static{
		key = "MySQL";
	}
	
	public static Connection getConnect(String password) throws Exception{
		Connection conn = null;
		try {
			Class.forName(PropertiesUtil.getDriverProperties(key));
			conn = DriverManager.getConnection(
					PropertiesUtil.getUrlProperties(key), 
					PropertiesUtil.getUsernameProperties(key), 
					password
					);
		}catch(ClassNotFoundException e) {
			System.out.println("驱动注册未成功");
			e.printStackTrace();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
}