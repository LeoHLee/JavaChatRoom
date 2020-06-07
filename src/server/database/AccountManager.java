package database;
import java.sql.*;

/**
 * Account接口的实例化
 * @author LightHouse
 * @see    Account
 */
public class AccountManager implements Account{
	
	String DBpassword;
	
	public AccountManager(String password) {
		this.DBpassword = password;
	}
	@Override
	public int LogIn(int id, String password) throws Exception {
		Connection conn = null;
		Statement  stmt = null;
		ResultSet  rs   = null;
		
		try{
			conn = Connect.getConnect(this.DBpassword);
			stmt = conn.createStatement();
			rs   = stmt.executeQuery("select password from indivisual where id ="+id);
			
			if(rs.next()) {
				if(password.equals(rs.getString("password"))) {
					return 1;
				}
				else return 0;
			}
			return 2;
		}finally {
			if(rs != null) {
				rs.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			if(conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public int Register(String nickname, String password) throws Exception {
		Connection conn = null;
		Statement  stmt = null;
		ResultSet  rs   = null;
		
		try{
			conn = Connect.getConnect(this.DBpassword);
			stmt = conn.createStatement();
			
			int aff = stmt.executeUpdate("insert into indivisual(password,nickname) values('"+password+"','"+nickname+"')");
		    if(aff > 0) {
		    	rs = stmt.executeQuery("select Max(id) from indivisual");
		    	if(rs.next()) {
					int tmp= rs.getInt("Max(id)");
					stmt.executeUpdate("insert into friendNet(user1_id,user2_id,group1_name) values(0,"+tmp+",'系统通知')");
					stmt.executeUpdate("update indivisual set group1num = group1num + 1 where id = 0");
					return tmp;
		    	}
		    	return 0;
		    }
			return 0;
	    }finally {
	    	if(rs != null) {
				rs.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			if(conn != null) {
				conn.close();
			}
	    }
	}

	@Override
	public boolean EditInfo(int id, EditType edit, String newinfo) throws Exception {
		Connection         conn      = null;
		String             editType  = null;
		PreparedStatement  pstmt     = null;		
		try {
			conn      = Connect.getConnect(this.DBpassword);
			switch(edit) {
				case Password:
					editType = "password";
					break;
				case MailAddress:
					editType =  "mailaddress";
					break;
				case NickName:
					editType =  "nickname";
					break;
				case Birthday:
					editType =  "birthday";
					break;
				case Sex:
					editType =  "gender";
					break;
				case Avatar:
					return false;
			}
			pstmt = conn.prepareStatement("update indivisual set "+editType+" = ? where id = "+id);
			pstmt.setString(1, newinfo);
			int aff = pstmt.executeUpdate();
			if(aff > 0) {
				return true;
			}
			return false;
		}finally {
			if(pstmt != null) {
				pstmt.close();
			}
			if(conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public AccountInfo GetInfo(int id) throws Exception {
		Connection conn = null;
		Statement  stmt = null;
		ResultSet rs = null;
		AccountInfo acc = null;
		try {
			conn = Connect.getConnect(this.DBpassword);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select nickname,birthday,gender,mailaddress from indivisual where id = "+id);
			
			if(rs.next()) {
				acc = new AccountInfo();
				acc.id        = id;
				acc.Birthday  = rs.getString("birthday");
				acc.NickName  = rs.getString("nickname");
				acc.Sex       = rs.getString("gender");
				acc.Mail      = rs.getString("mailaddress");
			}
			return acc;
		}finally {
			if(rs != null) {
				rs.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			if(conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public int MaxID() throws Exception {
		Connection conn = null;
		Statement  stmt = null;
		ResultSet rs = null;
		try {
			conn = Connect.getConnect(this.DBpassword);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select Max(id) from indivisual");
			if(rs.next()) {
				return rs.getInt("Max(id)");
			}
			return 0;
		}finally {
			if(rs != null) {
				rs.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			if(conn != null) {
				conn.close();
			}
		}
	}
	
	@Override
	public boolean IDexists(int id) throws Exception{
		Connection conn = null;
		Statement  stmt = null;
		ResultSet  rs   = null;
		try {
			conn = Connect.getConnect(this.DBpassword);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select count(*) from indivisual where id = "+id);
			rs.next();
			if(rs.getInt(1) < 1) return false;
			return true;
		}finally {
			if(rs != null) {
				rs.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			if(conn != null) {
				conn.close();
			}
		}
	}

}
