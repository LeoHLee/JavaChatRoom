package serverNet;

import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import chatbean.ChatBean;
import chatbean.Parser;
import database.*;
import message.Message;

import static chatbean.TypeValue.*;
import static database.EditType.*;

public class ServerBeanParser implements Parser {
    final ServerNetManager net;
    final Account accountSys;
    final FriendSys friendSys;
    final Message messageSys;
    final MailVerifier mailVerifier;
    private static final ChatBean SERVERERROR=new ChatBean(REPLY_SERVER_ERROR);
    private static final ChatBean BADID=new ChatBean(REPLY_BAD_ID);
    private static final ChatBean CHECKFAILED=new ChatBean(REPLY_CHECK_FAILED);
    public ServerBeanParser(ServerNetManager net, Account account, FriendSys friendSys, Message message,
                            MailVerifier mail) {
        this.net=net;
        this.friendSys=friendSys;
        this.accountSys=account;
        this.mailVerifier=mail;
        this.messageSys=message;
        net.parser=this;
    }
    boolean send(int id,ChatBean bean) {
        return net.send(id,bean);
    }
    void send(Socket socket, ChatBean bean) {
        net.send(socket,bean);
    }

    /**
     * parse a ChatBean
     * @param bean the ChatBean to parse
     * @param socket source of bean
     */
    synchronized public void parse(ChatBean bean,Socket socket) {
        ChatBean reply = new ChatBean(REPLY_OK);
        int errorLevel;
        try {
            //examine ID before doing anything
            if(bean.type!=REQ_REGISTER&&bean.type!=MES_READ&&!accountSys.IDexists(bean.ID)) {
                send(socket,BADID);
                return;
            }
            switch (bean.type) {
                case MES_READ:
                    if(accountSys.IDexists(bean.ID) && accountSys.IDexists(bean.friendID) &&
                            friendSys.Query(bean.ID,bean.friendID) )
                        messageSys.ReadMessageBeforeOne(bean.ID,bean.friendID,bean.messageID);
                    break;
                case REQ_LOGIN:
                    errorLevel=accountSys.LogIn(bean.ID, bean.password);
                    switch(errorLevel) {
                        case 0:
                            send(socket,CHECKFAILED);
                            break;
                        case 1:
                            net.authorize(bean.ID,socket);
                            reply.accountInfo=accountSys.GetInfo(bean.ID);
                            reply.friendList=friendSys.GetFriendList(bean.ID);
                            reply.chattingRecord=messageSys.AllMessageLogin(bean.ID);
                            send(socket,reply);
                            break;
                        case 2:
                            send(socket,BADID);
                            break;
                    }
                    break;
                case REQ_BIND:
                    errorLevel=accountSys.LogIn(bean.ID, bean.password);
                    switch(errorLevel) {
                        case 0:
                            send(socket,CHECKFAILED);
                            break;
                        case 1:
                            net.bind(bean.ID,socket);
                            send(socket,new ChatBean(REPLY_OK));
                            break;
                        case 2:
                            send(socket,BADID);
                            break;
                    }
                    break;
                case REQ_FORGET_PASSWORD:
                    forgetPassword(socket, bean.ID);
                    break;
                case REQ_CHECK_CAPTCHA:
                    checkCaptcha(socket, bean.ID, bean.captcha, bean.password);
                    break;
                case REQ_REGISTER:
                    int id = register(bean);
                    if (id == 0) send(socket, SERVERERROR);
                    else {
                        ChatBean success = new ChatBean(REPLY_OK);
                        success.ID = id;
                        send(socket, success);
                    }
                    break;
                case REQ_GET_INFO:
                    reply = new ChatBean(REPLY_OK);
                    reply.accountInfo = accountSys.GetInfo(bean.ID);
                    send(socket, reply);
                    break;
                case REQ_EDIT_INFO:
                    if (accountSys.LogIn(bean.ID, bean.password) != 1) {
                        send(socket, CHECKFAILED);
                        return;
                    }
                    applyEdit(bean);
                    send(socket, new ChatBean(REPLY_OK));
                    break;
                case REQ_CHAT:
                    if(friendIDInvalid(socket,bean)) return;
                    bean.message.MessageTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    bean.message.receiver=bean.friendID;
                    bean.message.sender=bean.ID;
                    int mID = messageSys.SaveOneMessage(bean.ID, bean.friendID, bean.message);
                    int mID2= messageSys.SaveOneMessage(bean.friendID,bean.ID,bean.message);
                    assert mID==mID2;
                    assert mID!=-1;
                    bean.message.ID=mID;
                    tryForward(bean);
                    reply = new ChatBean(REPLY_OK);
                    reply.message=bean.message;
                    send(socket,reply);
                    break;
                case REQ_FILE:
                    if(friendIDInvalid(socket,bean)) break;
//                    if(accountSys.LogIn(bean.ID,bean.password)!=1) {
//                        send(socket,CHECKFAILED);
//                        break;
//                    }
//                    bean.IPAddress=socket.getInetAddress().getHostAddress();
//                    bean.portNo=socket.getPort();
                    if(fileTransfer(bean))
                        send(socket,new ChatBean(REPLY_OK));
                    else send(socket,SERVERERROR);
                    break;
                case REQ_ADJUST_GROUP_ORDER:
                    if(friendSys.AdjustGroupOrder(bean.ID,bean.groupID,bean.groupID2)==1)
                        sendNewFriendList(socket,bean.ID);
                    else send(socket,BADID);
                    break;
                case REQ_EDIT_GROUP_NAME:
                    if(friendSys.EditGroupName(bean.ID,bean.groupID,bean.newInfo)==1)
                        sendNewFriendList(socket,bean.ID);
                    else send(socket,BADID);
                    break;
                case REQ_EDIT_REMARK:
                    if(friendIDInvalid(socket,bean)) break;
                    friendSys.EditRemark(bean.ID,bean.friendID,bean.newInfo);
                    sendNewFriendList(socket,bean.ID);
                    break;
                case REQ_BUILD_GROUP:
                    friendSys.BuildGroup(bean.ID,bean.newInfo);
                    sendNewFriendList(socket,bean.ID);
                    break;
                case REQ_MOVE_GROUP:
                    if(friendIDInvalid(socket,bean)) break;
                    if(friendSys.MoveGroup(bean.ID,bean.friendID,bean.groupID,bean.groupID2)==1)
                        sendNewFriendList(socket,bean.ID);
                    else send(socket,BADID);
                    break;
                case REQ_ADD_FRIEND:
                    if(friendSys.Query(bean.ID,bean.friendID)) {
                        send(socket,CHECKFAILED);
                        break;
                    }
                    if(tryAdd(bean))
                        send(socket,new ChatBean(REPLY_OK));
                    else send(socket,SERVERERROR);//TODO: save it in database
                    break;
                case REQ_ACCEPT_FRIEND:
                    if(friendSys.Query(bean.ID,bean.friendID)) {
                        send(socket,CHECKFAILED);
                        break;
                    }
                    messageSys.BuildMessagewithOne(bean.ID,bean.friendID);
                    messageSys.BuildMessagewithOne(bean.friendID,bean.ID);
                    errorLevel=friendSys.AddFriend(bean.ID,bean.friendID,bean.groupID);
                    if(errorLevel==1) {
                        friendSys.EditRemark(bean.ID,bean.friendID,bean.newInfo);
                        friendSys.AddFriend(bean.friendID,bean.ID,bean.groupID2);
                        friendSys.EditRemark(bean.friendID,bean.ID,bean.remark);
                        sendNewFriendList(socket,bean.ID);
                        reply.type=RECV_UPDATE_FRIEND;
                        reply.friendList=friendSys.GetFriendList(bean.friendID);
                        reply.accountInfo=accountSys.GetInfo(bean.ID);
                        reply.remark=bean.remark;
                        send(bean.friendID,reply);
                    }
                    else send(socket, BADID);
                    break;
                case REQ_REMOVE_FRIEND:
                    if(friendIDInvalid(socket,bean)) break;
                    friendSys.RemoveFriend(bean.ID,bean.friendID);
                    messageSys.DeleteMessagewhenDeleteFriend(bean.ID,bean.friendID);
                    messageSys.DeleteMessagewhenDeleteFriend(bean.friendID,bean.ID);
                    sendNewFriendList(socket,bean.ID);
//                    reply=new ChatBean(RECV_UPDATE_FRIEND);
//                    reply.friendList=friendSys.GetFriendList(bean.friendID);
//                    reply.accountInfo=accountSys.GetInfo(bean.ID);
//                    send(bean.friendID,reply);
                    break;
                case REQ_REMOVE_GROUP:
                    if(friendSys.RemoveGroup(bean.ID,bean.groupID)==1)
                        sendNewFriendList(socket,bean.ID);
                    else send(socket,BADID);
                    break;
                case REQ_DELETE_RECORD:
                    if(friendIDInvalid(socket,bean)) break;
                    if(messageSys.DeleteAllMessagewithOne(bean.ID,bean.friendID))
                        send(socket,new ChatBean(REPLY_OK));
                    else send(socket,SERVERERROR);
                    break;
                default:
                    send(socket,new ChatBean(RECV_OFFLINE));
            }
        } catch (Exception e) {
            send(socket,SERVERERROR);
            e.printStackTrace();
        }
    }

    /**
     * check friendID field in a ChatBean
     * @param socket where request came
     * @param bean bean of request
     * @return 1 when invalid, 0 when valid
     */
    private boolean friendIDInvalid(Socket socket,ChatBean bean) throws Exception{
        if(!accountSys.IDexists(bean.friendID))
            send(socket,BADID);
        else if(!friendSys.Query(bean.ID,bean.friendID))
            send(socket,CHECKFAILED);
        else return false;
        return true;
    }
    /**
     * @param socket where request came
     * @param id ID to replace password
     * @throws Exception database exceptions
     */
    private void forgetPassword(Socket socket,int id) throws Exception{
        AccountInfo info=accountSys.GetInfo(id);
        String mail = info.Mail;
        if(mail==null)
            send(socket,CHECKFAILED);
        mailVerifier.sendRandomCode(id, mail);
        send(socket,new ChatBean(REPLY_OK));
    }
    /**
     * @param socket where request came
     * @param id id to check code
     * @param code code to check
     * @throws Exception database exceptions
     */
    private void checkCaptcha(Socket socket,int id,String code,String newPSW) throws Exception{
        if(mailVerifier.checkCode(id,code)) {
            accountSys.EditInfo(id,Password,newPSW);
            send(socket,new ChatBean(REPLY_OK));
        }
        else send(socket,CHECKFAILED);
    }

    /**
     * handle a register request
     * @param bean bean of register request
     * @return allocated ID, 0 when failed
     */
    private int register(ChatBean bean) {
        try {
            int id=accountSys.Register(bean.accountInfo.NickName,bean.password);
            if(id>0) {
                messageSys.BuildChattingRecord(id);
                messageSys.BuildMessagewithOne(id,0);
                accountSys.EditInfo(id,Birthday,bean.accountInfo.Birthday);
                accountSys.EditInfo(id,Sex,bean.accountInfo.Sex);
                accountSys.EditInfo(id,MailAddress,bean.accountInfo.Mail);
            }
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * apply all edits in a ChatBean of type REQ_EDIT_INFO
     * @param bean as described above
     * @throws Exception database exception
     */
    void applyEdit(ChatBean bean) throws Exception{
        accountSys.EditInfo(bean.ID,Password,bean.newInfo);
        accountSys.EditInfo(bean.ID,NickName,bean.accountInfo.NickName);
        accountSys.EditInfo(bean.ID,MailAddress,bean.accountInfo.Mail);
        accountSys.EditInfo(bean.ID,Sex,bean.accountInfo.Sex);
        accountSys.EditInfo(bean.ID,Birthday,bean.accountInfo.Birthday);
    }
    /**
     * transfer file
     * @param bean ChatBean of file message
     * @return whether succeed
     */
    private boolean fileTransfer(ChatBean bean) {
        ChatBean forward=new ChatBean(RECV_FILE);
        forward.IPAddress=bean.IPAddress;
        forward.portNo=bean.portNo;
        forward.friendID=bean.ID;
        forward.ID=bean.friendID;
        forward.fileSize=bean.fileSize;
        forward.fileName=bean.fileName;
        return send(bean.friendID,forward);
    }

    /**
     * forward a text message
     * @param bean the request bean
     */
    private void tryForward(ChatBean bean) {
        ChatBean forward=new ChatBean(RECV_CHAT);
        forward.message=bean.message;
        forward.friendID=bean.ID;
        send(bean.friendID,forward);
    }

    /**
     *
     * @param socket send to whom
     * @param ID friendList of whom
     * @throws Exception database Exception
     */
    private void sendNewFriendList(Socket socket,int ID) throws Exception {
        ChatBean nb=new ChatBean(REPLY_OK);
        nb.friendList=friendSys.GetFriendList(ID);
        send(socket,nb);
    }

    /**
     * send RECV_ADD_FRIEND-type bean to a client
     * @param bean request bean
     * @return whether send application successful
     * @throws Exception database exception
     */
    private boolean tryAdd(ChatBean bean) throws Exception{
        ChatBean forward=new ChatBean(RECV_ADD_FRIEND);
        forward.accountInfo=accountSys.GetInfo(bean.ID);
        forward.applyRemark=bean.applyRemark;
        forward.groupID=bean.groupID;
        forward.remark=bean.remark;
        return send(bean.friendID,forward);
    }
}
