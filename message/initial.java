package message;

import java.io.*;
import java.sql.*;
import java.util.Properties;

class iniPropertiesUtil {
    private static Properties prop;
    static {
        try {
            prop = new Properties();
            prop.load(initial.class.getResourceAsStream("/message/ini/ini.properties"));
        } catch (FileNotFoundException e) {
            System.out.println("加载文件失败");
            e.printStackTrace();
        } catch (IOException e) {
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
 * 初始化类，创建数据库并生成所需的数据表，建立索引
 * @author Harris
 */
public class initial {
    private static String key;
    static{
        key = "MySQL";
    }
    /**
     * 初始化函数
     * @throws Exception  数据库连接异常
     */
    public static void initialize(String password) throws Exception {
        Connection conn1 = null;
        Statement  stmt1 = null;
        try {
            Class.forName(iniPropertiesUtil.getDriverProperties(key));
            conn1 = DriverManager.getConnection(
                    iniPropertiesUtil.getUrlProperties(key),
                    iniPropertiesUtil.getUsernameProperties(key),
                    password);
            stmt1 = conn1.createStatement();
            stmt1.executeUpdate("create database if not exists account");
        } catch(ClassNotFoundException e) {
            System.out.println("驱动注册未成功");
            e.printStackTrace();
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt1 != null) {
                stmt1.close();
            }
            if (conn1 != null) {
                conn1.close();
            }
        }

        Connection conn = Connect.getConnect(password);
        Statement  stmt = conn.createStatement();
        
        try {
            // 建立总表
            stmt.executeUpdate("create table if not exists allchattingrecord"
                             + "(UserID        int    auto_increment primary key,"
                             + " FriendNumber  int    default 0)"
                             );
        
            // 建立索引
            stmt.executeUpdate("create index allchattingrecordst on allchattingrecord(UserID)");
            
        } finally {
            if (stmt != null){
                stmt.close();
            }
            if (conn != null){
                conn.close();
            }
        }
    }
}