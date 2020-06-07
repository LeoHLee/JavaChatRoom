package message;

import java.io.*;
import java.util.*;

/**
 * //消息存储与管理第II级, 包含 MessagewithOne 类
 * //对各字段提供 get 方法
 * @author Harris
 */
public class MessagewithOne implements Serializable {
    public Vector<OneMessage> ChatRecordwithOne; // 与某一好友的全部消息记录
    public int FriendID;                         // 好友id，要求非负
    public int MessageNumberwithOne;             // 消息总数
    public int validMessageNumberwithOne;        // 有效（未删）消息总数
    private static final long serialVersionUID = 2020052515107L;

    public MessagewithOne() {
        this.ChatRecordwithOne = new Vector<OneMessage>();
        this.FriendID = 0;
        this.MessageNumberwithOne = 0;
        this.validMessageNumberwithOne = 0;
    }

    public MessagewithOne(int fid, int num) { // 用户与好友id为fid，总数为num的消息记录，要求非负
        this.ChatRecordwithOne = new Vector<OneMessage>();
        this.FriendID = fid;
        this.MessageNumberwithOne = num;
        this.validMessageNumberwithOne = 0;
    }

    /**
     * @return the ID of Friend
     */
    public int getFriendID() {
        return this.FriendID;
    }

    /**
     * @return the Number of Message with Friend
     */
    public int getMessageNumberwithOne() {
        return this.MessageNumberwithOne;
    }

    /**
     * @return the Number of valid Message with Friend
     */
    public int getvalidMessageNumberwithOne() {
        return this.validMessageNumberwithOne;
    }

}