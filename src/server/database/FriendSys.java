package database;
/**
 * 提供好友系统的查询修改相关接口
 * @author LightHouse
 *
 */
public interface FriendSys {
	/**
	 * 获取好友列表相关信息
	 * @param id--用户id
	 * @return 好友列表类
	 * @throws Exception 数据库连接异常
	 */
	public FriendList GetFriendList(int id) throws Exception;
	/**
	 * 调整索引为s分组至索引为d，其余分组的相对位置不变
	 * @param id--用户id
	 * @param s--调整分组起始索引
	 * @param d--调整分组目标索引
	 * @return 0--用户不存在   1--修改成功  2--组索引不合法
	 * @throws Exception 数据库连接异常 
	 */
	public int AdjustGroupOrder(int id,int s,int d) throws Exception;
	/**
	 * 编辑分组名称
	 * @param id --用户id
	 * @param group_idx --编辑分组的索引
	 * @param new_name --新的组名
	 * @return 0--用户不存在   1--修改成功  2--组索引不合法
	 * @throws Exception 数据库连接异常
	 */
	public int EditGroupName(int id,int group_idx,String new_name) throws Exception;
	/**
	 * 编辑好友备注
	 * @param id1 -- 用户id
	 * @param id2 -- 好友id
	 * @param new_name --好友备注名
	 * @return 0--id1=id2  1--修改成功  2--两人非好友关系  3--id1不存在  4--id2不存在
	 * @throws Exception 数据库连接异常 
	 */
	public int EditRemark(int id1,int id2,String new_name) throws Exception;
	/**
	 * 新建分组
	 * @param id --用户id
	 * @param group_name --新建组名
	 * @return 0--用户不存在   1--修改成功  
	 * @throws Exception 数据库连接异常
	 */
	public int BuildGroup(int id,String group_name) throws Exception;
	/**
	 * 移动好友分组
	 * @param id --用户id
	 * @param moved_id --好友id
	 * @param origin_group --移动前分组索引
	 * @param new_group --移动后分组索引
	 * @return 0--用户不存在   1--修改成功  2--组索引不合法  3--原分组下没有指定好友 
	 * @throws Exception 数据库连接异常
	 */
	public int MoveGroup(int id,int moved_id,int origin_group,int new_group) throws Exception;
	/**
	 * 添加好友关系，承认id1与id2互为好友，并将id2放入指定分组group，id1放入默认分组，若两人已为好友，则会将id2放入指定分组
	 * @param id1
	 * @param id2
	 * @param group
	 * @return 0--id1=id2  1--修改成功  2--组索引错误  3--id1不存在  4--id2不存在
	 * @throws Exception 数据库连接异常
	 */
	public int AddFriend(int id1,int id2,int group) throws Exception;
	/**
	 * 
	 * 添加好友关系，承认id1与id2互为好友，并将彼此都放入默认分组,若两人已为好友，则会将id2放入默认分组
	 * @param id1
	 * @param id2
	 * @return 是否成功
	 * @throws Exception 数据库连接异常
	 */
	public int AddFriend(int id1,int id2) throws Exception;
	/**
	 * 移除好友，两人互相都不再为好友
	 * @param id1
	 * @param id2
	 * @return false 两人不是好友（有可能有一方id错误）  true 修改成功
	 * @throws Exception 数据库连接异常
	 */
	public boolean RemoveFriend(int id1,int id2) throws Exception;
	/**
	 * 移除分组，并将分组内所有好友移至默认分组（索引为1分组）
	 * @param id
	 * @param group
	 * @return 0--用户不存在   1--修改成功  2--组索引不合法
	 * @throws Exception 数据库连接异常
	 */
	public int RemoveGroup(int id,int group) throws Exception;
	/**
	 * 查询用户是否为好友关系
	 * @param id1
	 * @param id2
	 * @return 是或否
	 * @throws Exception
	 */
	public boolean Query(int id1,int id2) throws Exception;

}
