package database;
/**
 * 提供账户相关信息添加、访问、修改接口
 * @author LightHouse
 */
public interface Account {
	/**
	 * 检验账号密码是否匹配
	 * @param  id       --用户id
	 * @param  password --用户密码
	 * @return 2--用户id不存在，1--密码正确，0--密码错误
	 * @throws Exception 数据库连接异常
	 */
	public int LogIn(int id,String password) throws Exception;
	/**
	 * 用昵称和密码注册账号
	 * @param  nickname  --用户昵称
	 * @param  password  --用户密码
	 * @return 注册成功返回id，失败返回0
	 * @throws Exception 数据库连接异常
	 */
	public int Register(String nickname,String password) throws Exception;
	/**
	 * 修改账号相关信息
	 * @param  id      --用户id
	 * @param  edit    --修改类型
	 * @param  newinfo --新的信息，注意：修改性别，传字符串"0","1"或"2"
	 * @return 修改成功与否
	 * @throws Exception 数据库连接异常
	 */
	public boolean EditInfo(int id,EditType edit,String newinfo) throws Exception;
	/**
	 * 获取账号相关信息
	 * @param  id --用户id
	 * @return 用户信息类
	 * @throws Exception  数据库连接异常
	 */
	public AccountInfo GetInfo(int id) throws Exception;
	/**
	 * 获取当前最大的用户id
	 * @return
	 * @throws Exception
	 */
	public int  MaxID() throws Exception;
	/**
	 * 查询用户是否存在
	 * @param id
	 * @return true or false
	 * @throws Exception
	 */
	public boolean IDexists(int id) throws Exception;
}
