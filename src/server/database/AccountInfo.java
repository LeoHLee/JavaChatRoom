package database;

import java.io.Serializable;

/**
 * 用户信息类，提供用户相关信息，其中id提供访问权限，NickName,Birthday,Sex,Mail_Head,Mail_Tail提供get方法
 * @author LightHouse
 */
public class AccountInfo implements Serializable {
	private static final long serialVersionUID=1919810114514L;
	public int    id;
	public String NickName;
	public String Birthday;
	public String Sex;
	public String Mail;
	/**
	 * @return user's nickname
	 */
	public String getNickName() {
		return this.NickName;
	}
	/**
	 * @return user's birthday like 'xxxx-xx-xx'
	 */
	public String getBirthday() {
		return this.Birthday;
	}
	/**
	 * @return user's gender, 0 for unknown,1 for male,2 for female 
	 */
	public String getSex() {
		return this.Sex;
	}
}
