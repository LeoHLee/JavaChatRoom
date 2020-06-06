package database;

import java.io.Serializable;

/**
 * 分组列表类，包括分组名称，分组内好友数量，好友id以及备注数组
 * @author LightHouse
 *
 */
public class GroupList  implements Serializable {
	private static final long serialVersionUID=1919810114514L;
	public String   GroupName ;
	public int      FriendNum ;
	public AccountInfo[] FriendAccount;
	public String[] FriendRemarks;
	public GroupList(){
		this.GroupName = "空";
		this.FriendNum = 0;
	}
}