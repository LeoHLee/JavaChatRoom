package chatbean;

import java.io.Serializable;

import database.AccountInfo;
import database.FriendList;
import message.*;
public class ChatBean implements Serializable{
	private static final long serialVersionUID=1919810114514L;
	public ChatBean(){}
	public ChatBean(TypeValue type){
		this.type=type;
	}
	public TypeValue type;
	public AccountInfo accountInfo;
	public FriendList friendList;
	public ChattingRecord chattingRecord;
	public int ID;
	public int friendID;
	public int groupID;
	public int groupID2;
	public String applyRemark;
	public String password;
	public String captcha;
	public String newInfo;
	public String remark;
	public String IPAddress;
	public int portNo;
	public String fileName;
	public long fileSize;
	public int messageID;
	public OneMessage message;
}













