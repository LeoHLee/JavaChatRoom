package message;
/**
 * 提供消息记录相关接口
 * @author Harris
 */

public interface Message {
	/**
	 * 创建当前用户的总消息记录
	 * @param UserID     --用户id
	 * @return 创建成功与否
	 * @throws Exception 数据库连接异常
	 */
	public boolean BuildChattingRecord(int UserID) throws Exception;
	
    /**
     * 创建当前用户与某一好友的消息记录
     * @param UserID     --用户id
     * @param FriendID   --好友id
     * @return 创建成功与否
     * @throws Exception 数据库连接异常
     */
    public boolean BuildMessagewithOne(int UserID, int FriendID) throws Exception;
    
    /**
     * 获取当前用户的全部消息记录
     * @param UserID     --用户id
     * @return 一个 ChattingRecord 类
     * @throws Exception 数据库连接异常
     */
    public ChattingRecord AllMessage(int UserID) throws Exception;
    
    /**
     * 获取当前用户与某一好友的全部消息记录
     * @param UserID     --用户id
     * @param FriendID   --好友id
     * @return 一个 MessagewithOne 类
     * @throws Exception 数据库连接异常
     */
    public MessagewithOne AllMessagewithOne(int UserID, int FriendID) throws Exception;
    
    /**
     * 获取当前用户与某一好友的未读消息记录
     * @param UserID     --用户id
     * @param FriendId   --好友id
     * @return 一个 MessagewithOne 类
     * @throws Exception 数据库连接异常
     */
    public MessagewithOne UnreadMessagewithOne(int UserID, int FriendID) throws Exception;

    /**
     * 获取当前用户与某一好友的未读消息记录数
     * @param UserID     --用户id
     * @param FriendId   --好友id
     * @return 未读消息数
     * @throws Exception 数据库连接异常
     */
    public int NumberofUnreadMessagewithOne(int UserID, int FriendID) throws Exception;

    /**
     * 获取当前用户与某一好友的最近三条消息
     * @param UserID     --用户id
     * @param FriendID   --好友id
     * @return 含有最多三条消息的 MessagewithOne 类
     * @throws Exception 数据库连接异常
     */
    public MessagewithOne LatestThreeMessagewithOne(int UserID, int FriendID) throws Exception;

    /**
     * 返回当前用户登录时所呈现的总消息记录
     * （对有未读消息的好友返回未读消息，
     *   无未读消息则返回最多最近三条消息记录）
     * @param UserID
     * @return 所需的 ChattingRecord 类
     * @throws Exception
     */
    public ChattingRecord AllMessageLogin(int UserID) throws Exception;
    
    /**
     * 添加一条消息记录到数据库
     * @param UserID     --用户id
     * @param FriendID   --好友id
     * @param Time       --发送时间
     * @param Text       --消息内容
     * @param sender	 --发送消息者id
     * @param receiver	 --接收消息者id
     * @return 该消息记录所产生的 MessageID，若存储失败返回-1
     * @throws Exception 数据库连接异常
     */
    public int AddOneMessage(int UserID, int FriendID, String Time, String Text, int sender, int receiver) throws Exception;

    /**
     * 读取一条消息记录（设置为已读）
     * @param UserID     --用户id
     * @param FriendID   --好友id
     * @param MessageID  --消息id
     * @return 读取操作是否成功
     * @throws Exception 数据库连接异常
     */
    public boolean ReadOneMessage(int UserID, int FriendID, int MessageID) throws Exception;
    
    /**
     * 读取当前消息之前的所有消息记录（设置为已读）
     * @param UserID     --用户id
     * @param FriendID   --好友id
     * @param MessageID  --消息id
     * @return 读取操作是否成功
     * @throws Exception 数据库连接异常
     */
    public boolean ReadMessageBeforeOne(int UserID, int FriendID, int MessageID) throws Exception;

    /**
     * 存储一条消息记录到数据库
     * @param UserID     --用户id
     * @param FriendID   --好友id
     * @param Amessage   --一条 OneMessage 类消息
     * @return 该消息记录所产生的 MessageID，若存储失败返回-1
     * @throws Exception 数据库连接异常
     */
    public int SaveOneMessage(int UserID, int FriendID, OneMessage Amessage) throws Exception;

    /**
     * 删除当前用户的全部消息记录
     * @param UserID     --用户id
     * @return 删除操作是否成功
     * @throws Exception 数据库连接异常
     */
    public boolean DeleteAllMessage(int UserID) throws Exception;
    
    /**
     * 删除当前用户与某一好友的全部消息记录
     * @param UserID     --用户id
     * @param FriendID   --好友id
     * @return 删除操作是否成功
     * @throws Exception 数据库连接异常
     */
    public boolean DeleteAllMessagewithOne(int UserID, int FriendID) throws Exception;
    
    /**
     * 删除好友时删表删记录操作
     * @param UserID     --用户id
     * @param FriendID   --好友id
     * @return 删除操作是否成功
     * @throws Exception 数据库连接异常
     */
    public boolean DeleteMessagewhenDeleteFriend(int UserID, int FriendID) throws Exception;
    
    /**
     * 删除当前用户与某一好友消息记录中 ID 为 MessageID 的消息
     * @param UserID     --用户id
     * @param FriendID   --好友id
     * @param MessageID  --消息id
     * @return 删除操作是否成功(若该记录先前已被删除仍返回 true, 
     *         只有当该 MessageID 不存在时返回 false)
     * @throws Exception 数据库连接异常
     */
    public boolean DeleteOneMessage(int UserID, int FriendID, int MessageID) throws Exception;

}