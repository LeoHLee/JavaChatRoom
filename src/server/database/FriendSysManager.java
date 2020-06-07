package database;

import java.sql.*;
/**
 * FriendSys接口的实例化
 * @author LightHouse
 *
 */
public class FriendSysManager implements FriendSys{
	String DBpassword;
	public FriendSysManager(String password) {
		this.DBpassword = password;
	}
	@Override
	public FriendList GetFriendList(int id) throws Exception {
		Connection         conn   = null;
		Statement          stmt   = null;
		ResultSet          rs1    = null;
		FriendList         frl    = null;
		PreparedStatement  pstmt1 = null;
		PreparedStatement  pstmt2 = null;
		ResultSet          rs2    = null;
		ResultSet          rs3    = null;
		try{
			conn   = Connect.getConnect(this.DBpassword);
			stmt   = conn.createStatement();
			rs1    = stmt.executeQuery("select * from indivisual where id = "+id);
			pstmt1 = conn.prepareStatement("select * from friendNet where user1_id = "+id+" and group2_name = ? order by user2_id desc");
			pstmt2 = conn.prepareStatement("select * from friendNet where user2_id = "+id+" and group1_name = ? order by user1_id desc");
			
			if(rs1.next()) {
				frl = new FriendList(rs1.getInt("groupnum"));
				for(int i = 0; i < frl.groupNum; ++i) {
					frl.Groups[i].GroupName     = rs1.getString("group"+i+"name"); 
					frl.Groups[i].FriendNum     = rs1.getInt("group"+i+"num");
					frl.Groups[i].FriendAccount = new AccountInfo[frl.Groups[i].FriendNum];
					frl.Groups[i].FriendRemarks = new String[frl.Groups[i].FriendNum];
				}
				for(int i = 0; i < frl.groupNum;++i) {
						int j = 0;
						pstmt1.setString(1, frl.Groups[i].GroupName);
						rs2 = pstmt1.executeQuery();
						while(rs2.next()) {
							int fid = rs2.getInt("user2_id");
							Account ac = new AccountManager(this.DBpassword);
							frl.Groups[i].FriendAccount[j] = ac.GetInfo(fid);
							frl.Groups[i].FriendRemarks[j] = rs2.getString("user2_remark");
							j++;
						}
						pstmt2.setString(1, frl.Groups[i].GroupName);
						rs3 = pstmt2.executeQuery();
						while(rs3.next()) {
							int fid = rs3.getInt("user1_id");
							Account ac = new AccountManager(this.DBpassword);
							frl.Groups[i].FriendAccount[j] = ac.GetInfo(fid);
							frl.Groups[i].FriendRemarks[j] = rs3.getString("user1_remark");
							j++;
						}
				}
			} 
			return frl;
		}finally {
			if(rs1 != null) {
				rs1.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			if(rs2 != null) {
				rs2.close();
			}
			if(rs3 != null) {
				rs3.close();
			}
			if(pstmt1 != null) {
				pstmt1.close();
			}
			if(pstmt2 != null) {
				pstmt2.close();
			}
			if(conn != null) {
				conn.close();
			}

		}
	}

	@Override
	public int AdjustGroupOrder(int id, int s, int d) throws Exception {
		Connection         conn      = null;
		Statement          stmt      = null;
		ResultSet          rs        = null;
		String[]           groupName = null;
		int[]              groupNum  = null;
		try{

			conn      = Connect.getConnect(this.DBpassword);
			stmt      = conn.createStatement();
			rs        = stmt.executeQuery("select * from indivisual where id = "+id);
			
			if(rs.next()) {
				int num   = rs.getInt("groupnum");

				if(d >= num || s >= num || d <0|| s<0) return 2;
				
				groupName = new String[num];
				groupNum  = new int[num]; 
				for(int i = 0 ; i <num; ++i) {
					groupName[i] = rs.getString("group"+i+"name");
					groupNum[i]  = rs.getInt("group"+i+"num");
				}
				if(s == d) return 1;
				else if(s < d) {
					String tmps = groupName[s];
					int    tmpi = groupNum[s];
					
					for(int i = s; i < d; ++i) {
						groupName[i] = groupName[i+1];
						groupNum[i]  = groupNum[i+1];
					}
					groupName[d] = tmps;
					groupNum[d]  = tmpi;
					
					for(int i = s; i <= d; ++i) {
						stmt.executeUpdate("update indivisual set group"+i+"name"+" = '"+groupName[i]+"' where id = "+id);
						stmt.executeUpdate("update indivisual set group"+i+"num"+" = "+groupNum[i]+" where id = "+id);
					}
					return 1;
				}
				else {
					String tmps = groupName[s];
					int tmpi = groupNum[s]; 
					for(int i = s; i > d; --i) {
						groupName[i] = groupName[i-1];
						groupNum[i] = groupNum[i-1];
					}
					groupName[d] = tmps;
					groupNum[d] = tmpi;
					
					for(int i = d; i <= s; ++i) {
						stmt.executeUpdate("update indivisual set group"+i+"name"+" = '"+groupName[i]+"' where id = "+id);
						stmt.executeUpdate("update indivisual set group"+i+"num"+" = "+groupNum[i]+" where id = "+id);
					}
					return 1;
				}
				
			}
			return 0;
		}finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if(conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public int EditGroupName(int id, int group_idx, String new_name) throws Exception {
		Connection         conn      = null;
		Statement          stmt      = null;
		ResultSet          rs        = null;
		String             oldName   = null;
		PreparedStatement  pstmt1    = null;
		PreparedStatement  pstmt2    = null;
		PreparedStatement  pstmt3    = null;
		try{
			conn      = Connect.getConnect(this.DBpassword);
			stmt      = conn.createStatement();
			rs        = stmt.executeQuery("select * from indivisual where id = "+id);
			pstmt1    = conn.prepareStatement("update friendNet set group1_name = ? where group1_name = ? and user2_id = "+id);
			pstmt2    = conn.prepareStatement("update friendNet set group2_name = ? where group2_name = ? and user1_id = "+id);
			pstmt3    = conn.prepareStatement("update indivisual set group"+group_idx+"name = ? where id ="+id);
			
			if(rs.next()) {
				int num = rs.getInt("groupnum");
				if(group_idx >= num) return 2;
 				oldName = rs.getString("group"+group_idx+"name");
				pstmt3.setString(1,new_name);
				pstmt3.executeUpdate();
				pstmt2.setString(1,new_name);
				pstmt2.setString(2,oldName);
				pstmt2.executeUpdate();
				pstmt1.setString(1,new_name);
				pstmt1.setString(2,oldName);
				pstmt1.executeUpdate();
				return 1;
			}
			return 0;
		}finally {
			if(rs != null) {
				rs.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			if(pstmt1 != null) {
				pstmt1.close();
			}
			if(pstmt2 != null) {
				pstmt2.close();
			}
			if(pstmt3 != null) {
				pstmt3.close();
			}
			if(conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public int EditRemark(int id1, int id2, String new_name) throws Exception {
		Connection         conn      = null;
		Statement          stmt      = null;
		ResultSet          rs        = null;
		int                num       = 0;
		
		if(id1 == id2)    return 0;
		try{
			conn      = Connect.getConnect(this.DBpassword);
			stmt      = conn.createStatement();
			rs = stmt.executeQuery("select * from indivisual where id = "+id1);
			if(!rs.next())  return 3;
			rs = stmt.executeQuery("select * from indivisual where id = "+id2);
			if(!rs.next())  return 4;
			
			if(id1 > id2) {
				num = stmt.executeUpdate("update friendNet set user1_remark = '"+new_name+"' where user1_id = "+id2+" and user2_id = "+id1);
			}
			else if(id1 < id2){
				num = stmt.executeUpdate("update friendNet set user2_remark = '"+new_name+"' where user1_id = "+id1+" and user2_id = "+id2);
			}
			if(num > 0) return 1;
			else return 2;
		}finally {
			if(stmt != null) {
				stmt.close();
			}
			if(conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public int BuildGroup(int id, String group_name) throws Exception {
		Connection         conn      = null;
		Statement          stmt      = null;
		ResultSet          rs        = null;
		try {
			conn      = Connect.getConnect(this.DBpassword);
			stmt      = conn.createStatement();
			rs        = stmt.executeQuery("select * from indivisual where id = "+id);
			
			if(rs.next()) {
				int num = rs.getInt("groupnum");
				stmt.executeUpdate("update indivisual set groupnum = groupnum + 1 where id = "+id);
				stmt.executeUpdate("update indivisual set group"+num+"name = '"+group_name+"' where id = "+id);
				return 1;
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
	public int MoveGroup(int id, int moved_id,int origin_group, int new_group) throws Exception {
		Connection         conn      = null;
		Statement          stmt      = null;
		ResultSet          rs        = null;
		int                num       = 0;
		try {
			conn      = Connect.getConnect(this.DBpassword);
			stmt      = conn.createStatement();
			rs        = stmt.executeQuery("select * from indivisual where id = "+id);
			
			if(rs.next()) {
				num = rs.getInt("groupnum");
				if(origin_group >= num || new_group >= num || origin_group <1 || new_group <1) return 2;
				
				String group_name = rs.getString("group"+new_group+"name");
				String old_name = rs.getString("group"+origin_group+"name"); 
				
				if(id > moved_id) {
					num = stmt.executeUpdate("update friendNet set group1_name = '"+group_name+"' where group1_name = '"+old_name+"' and user1_id = "+moved_id+" and user2_id = "+id);
				}
				else if(id < moved_id){
		
					num = stmt.executeUpdate("update friendNet set group2_name = '"+group_name+"' where group2_name = '"+old_name+"' and user1_id = "+id+" and user2_id = "+moved_id);
				}
				if(num == 0) return 3;
				stmt.executeUpdate("update indivisual set group"+origin_group+"num = group"+origin_group+"num - 1 where id ="+id);
				stmt.executeUpdate("update indivisual set group"+new_group+"num = group"+new_group+"num + 1 where id ="+id);
				return 1;
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
	public int AddFriend(int id1, int id2, int group) throws Exception {
		Connection         conn      = null;
		Statement          stmt      = null;
		ResultSet          rs        = null;
		int 			   num		 = 0; 
		try {
			
			conn      = Connect.getConnect(this.DBpassword);
			stmt      = conn.createStatement();
			rs        = stmt.executeQuery("select * from indivisual where id = "+id1);
			
			if(rs.next()) {
				int group_num = rs.getInt("groupnum");
				if(group >= group_num || group<1 ) return 2;
				String group_name = rs.getString("group"+group+"name");
				
				if(id1 > id2) {
					rs = stmt.executeQuery("select * from friendNet where user1_id = "+id2+" and user2_id = "+id1);
					if(!rs.next()) {
						num = stmt.executeUpdate("update indivisual set group1num = group1num + 1 where id ="+id2);
						if(num == 0) return 4;
						stmt.executeUpdate("update indivisual set group"+group+"num = group"+group+"num + 1 where id ="+id1);
						stmt.executeUpdate("insert into friendNet(user1_id,user2_id) values("+id2+","+id1+")");
					}
					else {
						stmt.executeUpdate("update indivisual set group"+group+"num = group"+group+"num + 1 where id ="+id1);
						stmt.executeUpdate("update indivisual set group1num = group1num - 1 where id ="+id1);
					}
					stmt.executeUpdate("update friendNet set group1_name = '"+group_name+"' where user1_id = "+id2+" and user2_id = "+id1);
				}
				else if(id1 < id2){
					rs = stmt.executeQuery("select * from friendNet where user1_id = "+id1+" and user2_id = "+id2);
					if(!rs.next()) {
						num = stmt.executeUpdate("update indivisual set group1num = group1num + 1 where id ="+id2);
						if(num == 0) return 4;
						stmt.executeUpdate("update indivisual set group"+group+"num = group"+group+"num + 1 where id ="+id1);
						stmt.executeUpdate("insert into friendNet(user1_id,user2_id) values("+id1+","+id2+")");
					}
					else {
						stmt.executeUpdate("update indivisual set group"+group+"num = group"+group+"num + 1 where id ="+id1);
						stmt.executeUpdate("update indivisual set group1num = group1num - 1 where id ="+id1);
					}
					stmt.executeUpdate("update friendNet set group2_name = '"+group_name+"' where user1_id = "+id1+" and user2_id = "+id2);
				}
				else return 0;
				return 1;
			}
			return 3;
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
	public int AddFriend(int id1, int id2) throws Exception {
		return AddFriend(id1,id2,1);
	}

	@Override
	public boolean RemoveFriend(int id1, int id2) throws Exception {
		Connection         conn      = null;
		Statement          stmt      = null;
		ResultSet          rs1       = null;
		ResultSet          rs2       = null;
		String             group1    = null;
		String             group2    = null;
		int                groupnum1 = 0;
		int                groupnum2 = 0;
		try{	
			
			conn      = Connect.getConnect(this.DBpassword);
			stmt      = conn.createStatement();
			
			if(id1 > id2) {
				rs1 = stmt.executeQuery("select * from friendNet where user1_id = "+id2+" and user2_id = "+id1);
				if(rs1.next()) {
					group1    = rs1.getString("group1_name");
					group2    = rs1.getString("group2_name");
					
					rs2       = stmt.executeQuery("select * from indivisual where id = "+ id1);
					if(!rs2.next()) return false;
					groupnum1 = rs2.getInt("groupnum");
					for(int i = 0 ; i < groupnum1; ++i) {
						if(group1.equals(rs2.getString("group"+i+"name"))) {
							stmt.executeUpdate("update indivisual set group"+i+"num = group"+i+"num -1 where id ="+id1);
							break;
						}
					}
					
					rs2       = stmt.executeQuery("select * from indivisual where id = "+ id2);
					if(!rs2.next()) return false;
					groupnum2 = rs2.getInt("groupnum");
					for(int i = 0 ; i < groupnum2; ++i) {
						if(group2.equals(rs2.getString("group"+i+"name"))) {
							stmt.executeUpdate("update indivisual set group"+i+"num = group"+i+"num -1 where id ="+id2);
							break;
						}
					}
					stmt.executeUpdate("delete from friendNet where user1_id = "+id2+" and user2_id = "+id1);
				}
				return true;
			}
			else if(id1 < id2){
				rs1 = stmt.executeQuery("select * from friendNet where user1_id = "+id1+" and user2_id = "+id2);
				if(rs1.next()) {
					group1    = rs1.getString("group2_name");
					group2    = rs1.getString("group1_name");
					
					rs2       = stmt.executeQuery("select * from indivisual where id = "+ id1);
					if(!rs2.next()) return false;
					groupnum1 = rs2.getInt("groupnum");
					for(int i = 0 ; i < groupnum1; ++i) {
						if(group1.equals(rs2.getString("group"+i+"name"))) {
							stmt.executeUpdate("update indivisual set group"+i+"num = group"+i+"num -1 where id ="+id1);
							break;
						}
					}
					
					rs2       = stmt.executeQuery("select * from indivisual where id = "+ id2);
					if(!rs2.next()) return false;
					groupnum2 = rs2.getInt("groupnum");
					for(int i = 0 ; i < groupnum2; ++i) {
						if(group2.equals(rs2.getString("group"+i+"name"))) {
							stmt.executeUpdate("update indivisual set group"+i+"num = group"+i+"num -1 where id ="+id2);
							break;
						}
					}
					stmt.executeUpdate("delete from friendNet where user1_id = "+id1+" and user2_id = "+id2);
				}
				return true;
			}
			return false;
		}finally{
			if(rs1 != null) {
				rs1.close();
			}
			if(rs2 != null) {
				rs2.close();
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
	public int RemoveGroup(int id, int group) throws Exception {
		Connection         conn       = null;
		Statement          stmt       = null;
		String[]           group_name = null;
		int[]              group_num  = null;
		ResultSet          rs         = null;
		int                groupnum   = 0;
		String             group_rn   = null;
		int                group_rnum = 0;
		String             group_def  = null;
		try {
			conn       = Connect.getConnect(this.DBpassword);
			stmt       = conn.createStatement();
			rs         = stmt.executeQuery("select * from indivisual where id = "+id);
				
			if(rs.next()) {
				group_def  = rs.getString("group1name");
				group_rn   = rs.getString("group"+group+"name");
				group_rnum = rs.getInt("group"+group+"num");
				groupnum   = rs.getInt("groupnum");
				if(group >= groupnum || group < 2) return 2;
				group_name = new String[groupnum];
				group_num  = new int[groupnum];
				for(int i = group; i<groupnum-1; ++i) {
					group_name[i] = rs.getString("group"+(i+1)+"name");
					group_num[i]  = rs.getInt("group"+(i+1)+"num");
				}
				group_name[groupnum-1] = null;
				group_num[groupnum-1] = 0;
				for(int i = group; i<groupnum; ++i) {
					System.out.println(i);
					stmt.executeUpdate("update indivisual set group"+i+"name = '"+group_name[i]+"' where id = "+id);
					stmt.executeUpdate("update indivisual set group"+i+"num = "+group_num[i]+" where id = "+id);
				}
				stmt.executeUpdate("update indivisual set group1num = group1num + "+group_rnum+" where id = "+id);
				stmt.executeUpdate("update friendNet set group1_name = '"+group_def+"' where user2_id = "+id+" and group1_name = '"+group_rn+"'");
				stmt.executeUpdate("update friendNet set group2_name = '"+group_def+"' where user1_id = "+id+" and group2_name = '"+group_rn+"'");
				stmt.executeUpdate("update indivisual set groupnum = groupnum - 1  where id = "+id);
				
				return 1;
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
	public boolean Query(int id1, int id2) throws Exception {
		Connection         conn      = null;
		Statement          stmt      = null;
		ResultSet          rs       = null;
		try {
			conn      = Connect.getConnect(this.DBpassword);
			stmt      = conn.createStatement();
			rs        = stmt.executeQuery("select count(*) from friendNet where user1_id = "+id1+" and user2_id = "+id2);
			if(rs.next()) {
				int a = rs.getInt(1);
				if(a > 0) return true;
				else {
					rs   = stmt.executeQuery("select count(*) from friendNet where user1_id = "+id2+" and user2_id = "+id1);
					if(rs.next()) {
						a = rs.getInt(1);
						if(a > 0) return true;
					}
				}
			}
			return false;
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
