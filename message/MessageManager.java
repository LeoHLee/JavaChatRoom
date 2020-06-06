package message;
import message.Message;

import java.sql.*;

/**
 * Message 接口的实例化
 * @author Harris
 * @see    Message
 */
public class MessageManager implements Message {
	public String Password;
	
	public MessageManager(String password) {
		this.Password = password;
	}
	
	@Override
	public boolean BuildChattingRecord(int UserID) throws Exception {
		Connection conn = null;
        Statement  stmt = null;
        ResultSet  rs   = null;
        
        try {
        	conn = Connect.getConnect(this.Password);
            stmt = conn.createStatement();
            
            // 在总表中加入新用户记录
        	stmt.executeUpdate("insert into allchattingrecord(UserID, FriendNumber) "
        					 + "values(" + UserID + ", 0)");
            
        	// 建立用户的消息记录
            stmt.executeUpdate("create table if not exists chattingrecordUID" + UserID
                			 + "(FriendID					 int	primary key,"
                			 + " MessageNumberwithOne 		 int	default 0,"
                			 + " validMessageNumberwithOne   int	default 0)"
            				 );
            
            // 建立索引
            stmt.executeUpdate("create index chattingrecordUID" + UserID + "st "
            				 + "on chattingrecordUID" + UserID + "(FriendID)");
            
            return true;            
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
		}
	}

	@Override
	public boolean BuildMessagewithOne(int UserID, int FriendID) throws Exception {
		Connection  conn = null;
        Statement   stmt = null;
        ResultSet   rs  = null;
        
        try {
            conn = Connect.getConnect(this.Password);
            stmt = conn.createStatement();
            rs  = stmt.executeQuery("select * from allchattingrecord where UserID = " + UserID);
            
            if (rs.next()) {
    			// 在总表中更新用户好友数
        		stmt.executeUpdate("update allchattingrecord set FriendNumber = FriendNumber + 1 "
                                 + "where UserID = " + UserID);

                // 在用户表中加入新好友记录
        	    stmt.executeUpdate("insert into chattingrecordUID" + UserID
                                 + "(FriendID, MessageNumberwithOne, validMessageNumberwithOne) "
                                 + "values(" + FriendID + ",0,0)");

                // 建立用户与每个好友的消息记录
                stmt.executeUpdate("create table if not exists messageUID" + UserID + "FID" + FriendID
                                 + "(MessageID    int        	 primary key,"
                                 + " MessageText  nvarchar(255)  not null,"
                                 + " MessageTime  nvarchar(255)  not null,"
                                 + " sender       int            default 0,"
                                 + " receiver     int            default 0,"
                                 + " hasread	  int		 	 default 0,"
                                 + " valid  	  int	  		 default 1)");

                // 对每个好友的聊天记录建立索引
                stmt.executeUpdate("create index messageUID" + UserID + "FID" + FriendID + "st "
                                 + "on messageUID" + UserID + "FID" + FriendID + "(MessageID)");

                return true;
            }

            return false;
        } finally {
        	if (rs != null) {
        		rs.close();
        	}
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
	}

	@Override
	public ChattingRecord AllMessage(int UserID) throws Exception {
		Connection      conn = null;
        Statement       stmt = null;
        ResultSet       rs   = null;
        ChattingRecord  chr  = null;
        
        try {
            conn = Connect.getConnect(this.Password);
            stmt = conn.createStatement();
            rs   = stmt.executeQuery("select * from chattingrecordUID" + UserID);
            chr  = new ChattingRecord(UserID, 0);

            while (rs.next()) {
                int FriendID = rs.getInt("FriendID");
                MessagewithOne mwo = AllMessagewithOne(UserID, FriendID);
                chr.ChatRecord.add(mwo);
                chr.FriendNumber++;
            }
            
            return chr;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
	}

	@Override
	public MessagewithOne AllMessagewithOne(int UserID, int FriendID) throws Exception {
		Connection     conn = null;
        Statement      stmt = null;
        ResultSet      rs   = null;
        MessagewithOne mwo  = null;

        try {
            conn = Connect.getConnect(this.Password);
            stmt = conn.createStatement();
            rs   = stmt.executeQuery("select * from messageUID" + UserID + "FID" + FriendID);
            mwo = new MessagewithOne(FriendID, 0);

            while (rs.next()){
                OneMessage cur  = new OneMessage();
                cur.MessageText = rs.getString("MessageText");
                cur.MessageTime = rs.getString("MessageTime");
                cur.hasread		= rs.getBoolean("hasread");
                cur.valid		= rs.getBoolean("valid");
                cur.sender		= rs.getInt("sender");
                cur.receiver    = rs.getInt("receiver");
                cur.ID  		= rs.getInt("MessageID");
                if (cur.is_valid()){
                    mwo.validMessageNumberwithOne++;
                }
                mwo.ChatRecordwithOne.add(cur);
            }
            mwo.MessageNumberwithOne = mwo.ChatRecordwithOne.size();
            
            return mwo;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
	}

	@Override
	public MessagewithOne UnreadMessagewithOne(int UserID, int FriendID) throws Exception {
		Connection     conn = null;
        Statement      stmt = null;
        ResultSet      rs   = null;
        MessagewithOne mwo  = null;

        try {
        	conn = Connect.getConnect(this.Password);
            stmt = conn.createStatement();
            rs   = stmt.executeQuery("select * from messageUID" + UserID + "FID" + FriendID);
            mwo  = new MessagewithOne(FriendID, 0);
            
            while (rs.next()) {
                boolean f = rs.getBoolean("hasread");
                if (f) continue;
                OneMessage cur = new OneMessage();
                cur.MessageText = rs.getString("MessageText");
                cur.MessageTime = rs.getString("MessageTime");
                cur.hasread		= rs.getBoolean("hasread");
                cur.valid		= rs.getBoolean("valid");
                cur.sender 		= rs.getInt("sender");
                cur.receiver	= rs.getInt("receiver");
                cur.ID          = rs.getInt("MessageID");
                mwo.ChatRecordwithOne.add(cur);                
            }
            mwo.MessageNumberwithOne = mwo.validMessageNumberwithOne = mwo.ChatRecordwithOne.size();
            
            return mwo;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
	}

	@Override
	public int NumberofUnreadMessagewithOne(int UserID, int FriendID) throws Exception {
		Connection conn = null;
        Statement  stmt = null;
        ResultSet  rs   = null;
        int 	   num  = 0;

        try {
        	conn = Connect.getConnect(this.Password);
            stmt = conn.createStatement();
            rs   = stmt.executeQuery("select * from messageUID" + UserID + "FID" + FriendID);
            
            while (rs.next()){
                boolean f = rs.getBoolean("hasread");
                if (f) continue;
                num++;
            }
            
            return num;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
	}

	@Override
	public MessagewithOne LatestThreeMessagewithOne(int UserID, int FriendID) throws Exception {
		Connection     conn = null;
        Statement      stmt = null;
        ResultSet      rs   = null;
        MessagewithOne tmp  = null;
        MessagewithOne cct  = null;
        MessagewithOne mwo  = null;

        try {
        	conn = Connect.getConnect(this.Password);
            stmt = conn.createStatement();
            rs   = stmt.executeQuery("select * from messageUID" + UserID + "FID" + FriendID);
            tmp = new MessagewithOne(FriendID, 0);
                        
            while (rs.next()) {
            	OneMessage cur  = new OneMessage();
                cur.MessageText = rs.getString("MessageText");
                cur.MessageTime = rs.getString("MessageTime");
                cur.hasread		= rs.getBoolean("hasread");
                cur.valid		= rs.getBoolean("valid");
                cur.sender 		= rs.getInt("sender");
                cur.receiver	= rs.getInt("receiver");
                cur.ID		    = rs.getInt("MessageID");
                if (cur.is_valid()) {
                	tmp.ChatRecordwithOne.add(cur);
                }
            }
            
            int cnt = 0;
            cct = new MessagewithOne(FriendID, 0);
            for (int i = tmp.ChatRecordwithOne.size()-1; i >= 0 && cnt < 3; i--) {
            	if (tmp.ChatRecordwithOne.elementAt(i).is_valid()) {
            		cct.ChatRecordwithOne.add(tmp.ChatRecordwithOne.elementAt(i));
            		cnt++;
            	}
            }
            
            mwo = new MessagewithOne(FriendID, 0);
            for (int i = cct.ChatRecordwithOne.size()-1; i >= 0; i--) {
            	mwo.ChatRecordwithOne.add(cct.ChatRecordwithOne.elementAt(i));
            }
            mwo.MessageNumberwithOne = mwo.validMessageNumberwithOne = cnt;

            return mwo;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
	}

	@Override
	public ChattingRecord AllMessageLogin(int UserID) throws Exception {
		Connection      conn = null;
        Statement       stmt = null;
        ResultSet       rs   = null;
        ResultSet		rs1  = null;
        ChattingRecord  chr  = null;
        
        try {
        	conn = Connect.getConnect(this.Password);
            stmt = conn.createStatement();
            rs   = stmt.executeQuery("select * from chattingrecordUID" + UserID);
            chr  = new ChattingRecord(UserID, 0);

            while (rs.next()) {
            	int FriendID = rs.getInt("FriendID");
            	int UnreadNumber = NumberofUnreadMessagewithOne(UserID, FriendID);
            	MessagewithOne mwo = null;
            	if (UnreadNumber > 0) {
            		mwo = UnreadMessagewithOne(UserID, FriendID);
            	}
            	else {
            		mwo = LatestThreeMessagewithOne(UserID, FriendID);
            	}
                chr.ChatRecord.add(mwo);
            }
            chr.FriendNumber = chr.ChatRecord.size();

            return chr;
        } finally {
            if (rs1 != null) {
                rs1.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
	}

	@Override
	public int AddOneMessage(int UserID, int FriendID, String Time, String Text, int sender, int receiver) throws Exception {
		Connection conn = null;
        Statement  stmt = null;
        ResultSet  rs   = null;
        ResultSet  rs1  = null;

        try {
        	conn = Connect.getConnect(this.Password);
            stmt = conn.createStatement();
            rs   = stmt.executeQuery("select count(*) from messageUID" + UserID + "FID" + FriendID);
            rs.next();
            int MessageID = rs.getInt(1);
            MessageID = MessageID + 100000;
            
            // 添加到消息记录表中
            stmt.executeUpdate("insert into messageUID" + UserID + "FID" + FriendID
                			 + "(MessageID, MessageText, MessageTime, sender, receiver, hasread, valid) "
                			 + "values("+MessageID+",'"+Text+"','"+Time+"',"+sender+","+receiver+","+0+","+1+")");
            
            rs1  = stmt.executeQuery("select * from chattingrecordUID" + UserID
            					   + " where FriendID = " + FriendID);
            if (rs1.next()) {
            	// 更新消息总数
            	stmt.executeUpdate("update chattingrecordUID" + UserID
            					 + " set MessageNumberwithOne = MessageNumberwithone + 1"
            					 + " where FriendID = " + FriendID);
            	
            	// 更新有效消息总数
            	stmt.executeUpdate("update chattingrecordUID" + UserID
            					 + " set validMessageNumberwithOne = validMessageNumberwithone + 1"
            					 + " where FriendID = " + FriendID);
            }
            
            return MessageID;
        } finally {
        	if (rs1 != null) {
        		rs1.close();
        	}
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
	}

	@Override
	public boolean ReadOneMessage(int UserID, int FriendID, int MessageID) throws Exception {
		Connection conn = null;
        Statement  stmt = null;
        ResultSet  rs   = null;

        try {
            conn = Connect.getConnect(this.Password);
            stmt = conn.createStatement();
            rs   = stmt.executeQuery("select * from messageUID" + UserID + "FID" + FriendID
                                   + " where MessageID = " + MessageID);

            if (rs.next()) {
                stmt.executeUpdate("update messageUID" + UserID + "FID" + FriendID 
                				 + " set hasread = 1 where MessageID = " + MessageID);
                return true;
            }

            return false;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
	}
	
	@Override
	public boolean ReadMessageBeforeOne(int UserID, int FriendID, int MessageID) throws Exception {
		Connection conn = null;
        Statement  stmt = null;
        ResultSet  rs   = null;

        try {
            conn = Connect.getConnect(this.Password);
            stmt = conn.createStatement();
            rs   = stmt.executeQuery("select * from messageUID" + UserID + "FID" + FriendID
                                   + " where MessageID = " + MessageID);

            if (rs.next()) {
                stmt.executeUpdate("update messageUID" + UserID + "FID" + FriendID 
                				 + " set hasread = 1 where MessageID <= " + MessageID);
                return true;
            }

            return false;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
	}

	@Override
	public int SaveOneMessage(int UserID, int FriendID, OneMessage Amessage) throws Exception {
		Connection conn = null;
        Statement  stmt = null;
        ResultSet  rs   = null;
        ResultSet  rs1  = null;

        try {
            conn = Connect.getConnect(this.Password);
            stmt = conn.createStatement();
            rs   = stmt.executeQuery("select count(*) from messageUID" + UserID + "FID" + FriendID);
            rs.next();
            int MessageID = rs.getInt(1);
            MessageID = MessageID + 100000;
            
            int boolhasread = 0; if (Amessage.hasread) boolhasread = 1;
            int boolvalid   = 1; if (!Amessage.valid)  boolvalid   = 0;
            
            // 添加到消息记录表中
            stmt.executeUpdate("insert into messageUID" + UserID + "FID" + FriendID
                			 + "(MessageID, MessageText, MessageTime, sender, receiver, hasread, valid) "
                			 + "values("+MessageID+",'"+Amessage.MessageText+"','"+Amessage.MessageTime
                			 + "',"+Amessage.sender+","+Amessage.receiver+","+boolhasread+","+boolvalid+")");
            
            rs1  = stmt.executeQuery("select * from chattingrecordUID" + UserID
            					   + " where FriendID = " + FriendID);
            if (rs1.next()) {
            	// 更新消息总数
            	stmt.executeUpdate("update chattingrecordUID" + UserID
            					 + " set MessageNumberwithOne = MessageNumberwithone + 1"
            					 + " where FriendID = " + FriendID);
            	
            	// 更新有效消息总数
            	if (Amessage.is_valid()) {
            		stmt.executeUpdate("update chattingrecordUID" + UserID
            						 + " set validMessageNumberwithOne = validMessageNumberwithone + 1"
            						 + " where FriendID = " + FriendID);
            	}
            }
            
            return MessageID;
        } finally {
        	if (rs1 != null) {
        		rs1.close();
        	}
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
	}

	@Override
	public boolean DeleteAllMessage(int UserID) throws Exception {
		Connection conn = null;
        Statement  stmt = null;
        ResultSet  rs   = null;
        ResultSet  rs1  = null;

        try {
            conn = Connect.getConnect(this.Password);
            stmt = conn.createStatement();
            rs1  = stmt.executeQuery("select * from allchattingrecord where UserID = " + UserID);
            
            if (rs1.next()) {
            	rs = stmt.executeQuery("select * from chattingrecordUID" + UserID);
            	while (rs.next()) {
            		int FriendID = rs.getInt("FriendID");
            		DeleteAllMessagewithOne(UserID, FriendID);
            	}
            	
            	return true;
            }

            return false;
        } finally {
            if (rs1 != null) {
                rs1.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null){
                conn.close();
            }
        }
	}

	@Override
	public boolean DeleteAllMessagewithOne(int UserID, int FriendID) throws Exception {
		Connection conn = null;
        Statement  stmt = null;
        ResultSet  rs   = null;

        try{
            conn = Connect.getConnect(this.Password);
            stmt = conn.createStatement();
            rs   = stmt.executeQuery("select * from chattingrecordUID" + UserID
            					   + " where FriendID = " + FriendID);

            if (rs.next()){
            	// 消息总数赋为零
            	stmt.executeUpdate("update chattingrecordUID" + UserID
            					 + " set MessageNumberwithOne = 0 where FriendID = " + FriendID);
            	
            	// 有效消息条数赋为零
                stmt.executeUpdate("update chattingrecordUID" + UserID
       				 			 + " set validMessageNumberwithOne = 0 where FriendID = " + FriendID);
                
                // 删除与该好友的所有消息记录
                stmt.executeUpdate("delete from messageUID" + UserID + "FID" + FriendID);
                
//              // 将所有消息置为“无效”
//              stmt.executeUpdate("update messageUID" + UserID + "FID" + FriendID
//              				 + " set valid = 0");
                
                return true;
            }
            
            return false;
        } finally {
            if (rs != null){
                rs.close();
            }
            if (stmt != null){
                stmt.close();
            }
            if (conn != null){
                conn.close();
            }
        }
	}
	
	
    public boolean DeleteMessagewhenDeleteFriend(int UserID, int FriendID) throws Exception{
    	Connection conn = null;
        Statement  stmt = null;
        ResultSet  rs   = null;
        ResultSet  rs1  = null;
        ResultSet  rs2  = null;
        
        try {
        	conn = Connect.getConnect(this.Password);
            stmt = conn.createStatement();
            rs   = stmt.executeQuery("select * from chattingrecordUID" + UserID
            					   + " where FriendID = " + FriendID);
            
            if (rs.next()) {
            	// 删除用户表中的记录
        		stmt.executeUpdate("delete from chattingrecordUID" + UserID
        						 + " where FriendID = " + FriendID);
        		
            	// 删除用户与好友的消息记录索引
            	stmt.executeUpdate("drop index messageUID" + UserID + "FID" + FriendID + "st "
            					 + "on messageUID" + UserID + "FID" + FriendID);
            	
            	// 删除用户与好友的消息记录表
            	stmt.executeUpdate("drop table messageUID" + UserID + "FID" + FriendID);
            	
            	rs1 = stmt.executeQuery("select * from allchattingrecord where UserID = " + UserID);
            	if (rs1.next()) {
            		// 总表中用户好友数减一
                	stmt.executeUpdate("update allchattingrecord set FriendNumber = FriendNumber - 1 "
                					 + "where UserID = " + UserID);

                	return true;
            	}
            }
        	
        	return false;
        } finally {
        	if (rs2 != null) {
        		rs2.close();
        	}
        	if (rs1 != null) {
        		rs1.close();
        	}
        	if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
		}
    }

	@Override
	public boolean DeleteOneMessage(int UserID, int FriendID, int MessageID) throws Exception {
		Connection conn = null;
        Statement  stmt = null;
        ResultSet  rs   = null;
        ResultSet  rs1  = null;

        try{
            conn = Connect.getConnect(this.Password);
            stmt = conn.createStatement();
            rs   = stmt.executeQuery("select * from messageUID" + UserID + "FID" + FriendID
            					   + " where MessageID = " + MessageID);

            if (rs.next()){
//            	// 删除该消息记录
//            	stmt.executeUpdate("delete from messageUID" + UserID + "FID" + FriendID
//            					 + " where MessageID = " + MessageID);
            	
            	rs1 = stmt.executeQuery("select * from chattingrecordUID" + UserID
            						  + " where FriendID = " + FriendID);
            	if (rs1.next()) {
//            		// 消息总数减一
//            		stmt.executeUpdate("update chattingrecordUID" + UserID
//            						 + " set MessageNumberwithOne = MessageNumberwithOne - 1 "
//            						 + "where FriendID = " + FriendID);
            		
            		// 有效消息总数减一
            		stmt.executeUpdate("update chattingrecordUID" + UserID
   						 			 + " set MessageNumberwithOne = MessageNumberwithOne - 1 "
   						 			 + "where FriendID = " + FriendID);
            	}
            	
            	// 设置该消息为“无效”
            	stmt.executeUpdate("update messageUID" + UserID + "FID" + FriendID
                				 + " set valid = 0 where MessageID = " + MessageID);
                
                return true;
            }

            return false;
        }finally{
            if (rs != null){
                rs.close();
            }
            if (stmt != null){
                stmt.close();
            }
            if (conn != null){
                conn.close();
            }
        }
	}

}
