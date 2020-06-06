package message;

import java.io.*;

/**
 * 消息存储与管理第I级, 包含 OneMessage 类
 * 对各字段提供 get 方法
 * @author Harris
 */
public class OneMessage implements Serializable {
    public String  MessageText; // 消息文本
    public String  MessageTime; // 消息时间
    public boolean hasread;     // 已读标志 已读为true，未读为false
    public boolean valid;       // 有效（未删除）标志 未删为true，已删为false
    public int 	   ID;			// 消息id，要求非负
    public int	   sender;		// 消息发送者id
    public int     receiver;	// 消息接收者id
    private static final long serialVersionUID = 2020052322490L;

    /**
     * 无参构造函数 默认未读未删，ID为0
     */
    public OneMessage() {
        this.MessageText = null;
        this.MessageTime = null;
        this.hasread     = false; // 未读
        this.valid       = true;  // 未删
        this.sender		 = 0;
        this.receiver	 = 0;
        this.ID   = 0;
    }

    /**
     * 所传参数为MessageID 要求非负 默认未读未删
     * @param mid	--MessageID
     */
    public OneMessage(int mid) {
        this.MessageText = null;
        this.MessageTime = null;
        this.hasread     = false; // 未读
        this.valid       = true;  // 未删
        this.sender		 = 0;
        this.receiver    = 0;
        this.ID   = mid;
    }

    /**
     * 所传参数为MessageID、sender、receiver
     * @param mid		--MessageID
     * @param send		--sender
     * @param receive	--receiver
     */
    public OneMessage(int mid, int send, int receive) {
        this.MessageText = null;
        this.MessageTime = null;
        this.hasread     = false; // 未读
        this.valid       = true;  // 未删
        this.sender		 = send;
        this.receiver    = receive;
        this.ID   = mid;
    }

    /**
     * @return the Text of Message
     */
    public String getMessageText(){
        return this.MessageText;
    }

    /**
     * @return the Time of Message like "20xx-xx-xx xx:xx"
     */
    public String getMessageTime(){
        return this.MessageTime;
    }

    /**
     * @return ID of sender
     */
    public int getsender() {
        return this.sender;
    }

    /**
     * @ return ID of receiver
     */
    public int getreceiver() {
        return this.receiver;
    }

    /**
     * @return the id of Message
     */
    public int getMessageID(){
        return this.ID;
    }

    /**
     * @return if this Message has been read
     */
    public boolean has_read(){
        return this.hasread;
    }

    /**
     * @return if this Message is still valid(not been deleted)
     */
    public boolean is_valid(){
        return this.valid;
    }
}
