package database;

import java.io.*;
import java.sql.*;
import java.util.Properties;


class iniPropertiesUtil{
	private static Properties prop;
	static {
		try {
			prop = new Properties();
			prop.load(initial.class.getResourceAsStream("/database/ini/ini.properties"));
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

/**
 * 初始化类，创建数据库并且生成需要的数据表，建立索引
 * @author LightHouse
 *
 */

public class initial{
	private static String key;
	static{
		key = "MySQL";
	}
	/**
	 * 初始化函数
	 * @throws Exception  数据库连接异常
	 */
	public static void initialize(String password) throws Exception{
		Connection conn1 = null;
		Statement  stmt1 = null;
		try {
			Class.forName(iniPropertiesUtil.getDriverProperties(key));
			conn1 = DriverManager.getConnection(
					iniPropertiesUtil.getUrlProperties(key), 
					iniPropertiesUtil.getUsernameProperties(key), 
					password
					);
			stmt1 = conn1.createStatement();
			stmt1.executeUpdate("create database if not exists account");
		}catch(ClassNotFoundException e) {
			System.out.println("驱动注册未成功");
			e.printStackTrace();
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			if(stmt1 != null) {
				stmt1.close();
			}
			if(conn1 != null) {
				conn1.close();
			}
		}
		
		Connection conn = Connect.getConnect(password);
		Statement  stmt = conn.createStatement();
		
		stmt.executeUpdate("create table if not exists indivisual"
				+"(id           int           not null unique auto_increment primary key,"
				+" password     nvarchar(255) not null,"
				+" nickname     nvarchar(255) not null,"
				+" birthday     nvarchar(255) default '2020_11_11',"
				+" gender       nvarchar(255) default '0',"
				+" mailaddress  nvarchar(255) default  '123456@78.910',"
				+" groupnum     int           default 2,"
				+" group0name   nvarchar(255) default  '系统通知',"
				+" group0num    int           default 1,"
				+" group1name   nvarchar(255) default  '我的好友',"
				+" group1num    int           default 0,"
				+" group2name   nvarchar(255),"
				+" group2num    int           default 0,"
				+" group3name   nvarchar(255),"
				+" group3num    int           default 0,"
				+" group4name   nvarchar(255),"
				+" group4num    int           default 0,"
				+" group5name   nvarchar(255),"
				+" group5num    int           default 0,"
				+" group6name   nvarchar(255),"
				+" group6num    int           default 0,"
				+" group7name   nvarchar(255),"
				+" group7num    int           default 0,"
				+" group8name   nvarchar(255),"
				+" group8num    int           default 0,"
				+" group9name   nvarchar(255),"
				+" group9num    int           default 0,"
				+" group10name  nvarchar(255),"
				+" group10num   int           default 0,"
				+" group11name  nvarchar(255),"
				+" group11num   int           default 0,"
				+" group12name  nvarchar(255),"
				+" group12num   int           default 0,"
				+" group13name  nvarchar(255),"
				+" group13num   int           default 0,"
				+" group14name  nvarchar(255),"
				+" group14num   int           default 0,"
				+" group15name  nvarchar(255),"
				+" group15num   int           default 0,"
				+" group16name  nvarchar(255),"
				+" group16num   int           default 0)"
				);
		stmt.executeUpdate("create table if not exists friendNet"
				+"(user1_id      int           not null,"
				+" user2_id      int           not null,"
				+" group1_name   nvarchar(255) default '我的好友',"
				+" group2_name   nvarchar(255) default '我的好友',"
				+" user1_remark  nvarchar(255) default null,"
				+" user2_remark  nvarchar(255) default null)"
				);

		stmt.executeUpdate("alter table indivisual AUTO_INCREMENT = 0");
		stmt.executeUpdate("create index friendship on friendNet(user1_id,user2_id)");
		stmt.executeUpdate("create index uuid on indivisual(id)");
		stmt.executeUpdate("insert into indivisual(password,nickname) values('JavaChatRoom','admin')");
		stmt.executeUpdate("alter table indivisual AUTO_INCREMENT = 123456");
		
		if(stmt != null) {
			stmt.close();
		}
		if(conn != null) {
			conn.close();
		}
	}
}
	