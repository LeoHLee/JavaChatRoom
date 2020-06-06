package message;

import java.io.*;
import java.util.*;

/**
 * 消息存储与管理第IV级, 包含 ALLChattingRecord 类
 * 对各字段提供 get 方法
 * @author Harris
 */
public class AllChattingRecord implements Serializable {
    public Vector<ChattingRecord> AllChatRecord;
    public int UserNumber; // 用户总数，要求非负
    private static final long serialVersionUID = 202005250L;

    public AllChattingRecord() {
        this.UserNumber = 0;
        this.AllChatRecord = new Vector<ChattingRecord>();
    }

    public AllChattingRecord(int unum) {
        this.UserNumber = unum;
        this.AllChatRecord = new Vector<ChattingRecord>();
    }

    /**
     * @return Number of Users
     */
    public int getUserNumber() {
        return this.UserNumber;
    }
   
}
