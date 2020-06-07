package message;

import java.io.*;
import java.util.*;

/**
 * 消息存储与管理第III级, 包含 ChattingRecord 类
 * 对各字段提供 get 方法
 * @author Harris
 */
public class ChattingRecord implements Serializable {
    public Vector<MessagewithOne> ChatRecord; // 用户的全部消息记录
    public int UserID;                        // 用户id，要求非负
    public int FriendNumber;                  // 好友总数（即CharRecord中元素数）
    private static final long serialVersionUID = 2020052515022L;

    public ChattingRecord() {
        this.UserID       = 0;
        this.FriendNumber = 0;
        this.ChatRecord   = new Vector<MessagewithOne>();
    }

    public ChattingRecord(int uid, int num) { // 用户id为uid，好友数为num的消息记录，要求非负
        this.UserID = uid;
        this.FriendNumber = num;
        this.ChatRecord = new Vector<MessagewithOne>();
    }

    /**
     * @return the ID of User
     */
    public int getUserID() {
        return this.UserID;
    }

    /**
     * @return the Number of Friend
     */
    public int getFriendNumber() {
        return this.FriendNumber;
    }

}