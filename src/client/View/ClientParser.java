package View;

import chatbean.ChatBean;
import chatbean.TypeValue;
import database.FriendList;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import message.MessagewithOne;
import message.OneMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

public class ClientParser implements chatbean.Parser
{
    int hint = 0;

    public void refreshMes(ChatBean bean)
    {
        if(bean.type == TypeValue.RECV_CHAT)
        {
            int index = -1;
            for(int i=0; i<Control.mainwindow.getFriendVector().size(); ++i)
            {
                if(Control.mainwindow.getFriendVector().get(i).info.id == bean.friendID)
                {
                    if((!Control.chat.isShowing()) || Control.chat.info.id!=bean.friendID)
                        Control.mainwindow.getFriendVector().get(i).addMsgTip(1);
                    Control.mainwindow.getFriendVector().get(i).Signature.setText(bean.message.MessageText);
                    index = i;
                    break;
                }
            }
            if(index>=0)
            {
                Friendlist tmp = Control.mainwindow.getFriendVector().get(index);
                Control.mainwindow.getFriendVector().remove(index);
                Control.mainwindow.getFriendVector().add(0, tmp);
            }
            Control.mainwindow.getFriendlist().getItems().clear();
            index = 0;
            for(int i=0; i<Control.mainwindow.getFriendVector().size(); ++i)
            {
                if(Control.mainwindow.getFriendVector().get(i).info.id == bean.friendID)
                    index = i;
                //else if(!Control.mainwindow.getFriendVector().get(i).MsgTip.getText().equals(""))
                  //  Control.mainwindow.getFriendlist().getItems().add(0, Control.mainwindow.getFriendVector().get(i).getPane());
                else
                    Control.mainwindow.getFriendlist().getItems().add(Control.mainwindow.getFriendVector().get(i).getPane());
            }
            Control.mainwindow.getFriendlist().getItems().add(0, Control.mainwindow.getFriendVector().get(index).getPane());
        }
        else if(bean.type == TypeValue.RECV_ADD_FRIEND)
        {
            int index = -1;
            for(int i=0; i<Control.mainwindow.getFriendVector().size(); ++i)
            {
                if(Control.mainwindow.getFriendVector().get(i).info.id == 0)
                {
                    index = i;
                    if((!Control.chat.isShowing()) || Control.chat.info.id!=0)
                        Control.mainwindow.getFriendVector().get(i).addMsgTip(1);
                    Control.mainwindow.getFriendVector().get(i).Signature.setText(bean.accountInfo.NickName+"请求添加您为好友");
                    break;
                }
            }
            if(index>=0)
            {
                Friendlist tmp = Control.mainwindow.getFriendVector().get(index);
                Control.mainwindow.getFriendVector().remove(index);
                Control.mainwindow.getFriendVector().add(0, tmp);
            }
            Control.mainwindow.getFriendlist().getItems().clear();
            index = 0;
            for(int i=0; i<Control.mainwindow.getFriendVector().size(); ++i)
            {
                if(Control.mainwindow.getFriendVector().get(i).info.id == 0)
                    index = i;
                //else if(!Control.mainwindow.getFriendVector().get(i).MsgTip.getText().equals(""))
                  //  Control.mainwindow.getFriendlist().getItems().add(0, Control.mainwindow.getFriendVector().get(i).getPane());
                else
                    Control.mainwindow.getFriendlist().getItems().add(Control.mainwindow.getFriendVector().get(i).getPane());
            }
            Control.mainwindow.getFriendlist().getItems().add(0, Control.mainwindow.getFriendVector().get(index).getPane());
        }
        else if(bean.type == TypeValue.RECV_FILE)
        {
            for(int i=0; i<Control.chattingRecord.ChatRecord.size(); ++i)
            {
                if(Control.chattingRecord.ChatRecord.get(i).FriendID==bean.friendID)
                {
                    if((!Control.chat.isShowing()) || Control.chat.info.id!=bean.friendID)
                        Control.mainwindow.getFriendVector().get(i).addMsgTip(1);
                    OneMessage mes = new OneMessage();
                    mes.valid = true; mes.ID = -3; mes.MessageText = "发送了一个文件，请及时接收";mes.sender=bean.friendID;
                    Control.chattingRecord.ChatRecord.get(i).ChatRecordwithOne.add(mes);
                    Control.chattingRecord.ChatRecord.get(i).MessageNumberwithOne=Control.chattingRecord.ChatRecord.get(i).ChatRecordwithOne.size();
                }
            }

            int index = -1;
            for(int i=0; i<Control.mainwindow.getFriendVector().size(); ++i)
            {
                if(Control.mainwindow.getFriendVector().get(i).info.id == bean.friendID)
                {
                    index = i;
                    Control.mainwindow.getFriendVector().get(i).addMsgTip(1);
                    Control.mainwindow.getFriendVector().get(i).Signature.setText("发送了一个文件，请及时接收");
                    break;
                }
            }
            if(index>=0)
            {
                Friendlist tmp = Control.mainwindow.getFriendVector().get(index);
                Control.mainwindow.getFriendVector().remove(index);
                Control.mainwindow.getFriendVector().add(0, tmp);
            }
            Control.mainwindow.getFriendlist().getItems().clear();
            index = 0;
            for(int i=0; i<Control.mainwindow.getFriendVector().size(); ++i)
            {
                if(Control.mainwindow.getFriendVector().get(i).info.id == bean.friendID)
                    index = i;
                //else if(!Control.mainwindow.getFriendVector().get(i).MsgTip.getText().equals(""))
                  //  Control.mainwindow.getFriendlist().getItems().add(0, Control.mainwindow.getFriendVector().get(i).getPane());
                else
                    Control.mainwindow.getFriendlist().getItems().add(Control.mainwindow.getFriendVector().get(i).getPane());
            }
            Control.mainwindow.getFriendlist().getItems().add(0, Control.mainwindow.getFriendVector().get(index).getPane());
        }
        else if(bean.type == TypeValue.RECV_UPDATE_FRIEND)
        {
            int idx = -1;
            for(int i=0; i<Control.chattingRecord.FriendNumber; ++i)
            {
                if(Control.chattingRecord.ChatRecord.get(i).FriendID == bean.accountInfo.id)
                {
                    idx = i;
                    break;
                }
            }
            if(idx>=0)
                Control.chattingRecord.ChatRecord.remove(idx);
            Control.chattingRecord.FriendNumber = Control.chattingRecord.ChatRecord.size();

            Control.chattingRecord.FriendNumber++;
            MessagewithOne mwo = new MessagewithOne();
            mwo.FriendID = bean.accountInfo.id; mwo.ChatRecordwithOne = new Vector<>(); mwo.MessageNumberwithOne = 1;
            OneMessage mes = new OneMessage();
            mes.valid = true; mes.ID = -3; mes.MessageText = "我们已经是好友啦，一起来聊天吧";mes.sender=bean.accountInfo.id;
            mwo.ChatRecordwithOne.add(mes);
            Control.chattingRecord.ChatRecord.add(mwo);

            Friendlist fl = null;
            boolean tf =false;
            int pos = -1;
            for(int i=0; i<Control.mainwindow.getFriendVector().size(); ++i)
            {
                if(Control.mainwindow.getFriendVector().get(i).info.id == bean.accountInfo.id)
                {
                    tf=true;
                    Control.mainwindow.getFriendVector().get(i).addMsgTip(1);
                    fl = Control.mainwindow.getFriendVector().get(i);
                    fl.clearMsgTip();
                    pos = i;
                    break;
                }
            }
            if(tf)
            {
                fl.remark.setText(bean.remark);
                fl.Signature.setText("我们已经是好友啦，一起来聊天吧");
                if(pos>=0)
                    Control.mainwindow.getFriendVector().remove(pos);
                Control.mainwindow.getFriendVector().add(0, fl);
                Control.mainwindow.getFriendlist().getItems().clear();
                for(int i=0; i<Control.mainwindow.getFriendVector().size(); ++i)
                {
                    Control.mainwindow.getFriendlist().getItems().add(Control.mainwindow.getFriendVector().get(i).getPane());
                }
            }
            else
            {
                try
                {
                    fl = new Friendlist(bean.accountInfo, bean.remark, "我们已经是好友啦，一起来聊天吧", Control.mainwindow);
                    fl.addMsgTip(1);
                    Control.mainwindow.getFriendVector().add(0, fl);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                Control.mainwindow.getFriendlist().getItems().clear();
                int index = 0;
                for(int i=0; i<Control.mainwindow.getFriendVector().size(); ++i)
                {
                    //if(!Control.mainwindow.getFriendVector().get(i).MsgTip.getText().equals(""))
                    //  Control.mainwindow.getFriendlist().getItems().add(0, Control.mainwindow.getFriendVector().get(i).getPane());
                    //else
                    Control.mainwindow.getFriendlist().getItems().add(Control.mainwindow.getFriendVector().get(i).getPane());
                }
                //Control.mainwindow.getFriendlist().getItems().add(0, Control.mainwindow.getFriendVector().get(index).getPane());
            }
        }
    }

    /**
     * 向server发送已读信号
     * @param usr_id 用户id
     * @param id 好友id
     * @param mid
     * @return
     */
    public int SEND_LAST_MES_TO_SERVER(int usr_id, int id, int mid)
    {
        ChatBean info = new ChatBean(TypeValue.MES_READ);
        info.ID = usr_id;
        info.friendID = id;
        info.messageID = mid;
        try
        {
            Control.network.send(info);
            return 1;
        }catch (Exception e)
        {
            return -1;
        }
    }

    @Override
    public void parse(ChatBean bean, Socket source)
    {
        if(bean!=null)
        {
            switch (bean.type)
            {
                case RECV_CHAT:
                    for(int i=0;i<Control.chattingRecord.FriendNumber;++i)
                    {
                        if(Control.chattingRecord.ChatRecord.get(i).FriendID == bean.friendID)
                        {
                            Control.chattingRecord.ChatRecord.get(i).MessageNumberwithOne++;
                            Control.chattingRecord.ChatRecord.get(i).ChatRecordwithOne.add(bean.message);
                            break;
                        }
                    }
                    refreshMes(bean);
                    if(Control.chat.info!=null && Control.chat.info.id == bean.friendID)
                    {
                        Control.chat.addLeft(bean.friendID, bean.message.MessageText, bean.messageID);
                        if(Control.chat.isShowing() && Control.chat.info.id==bean.friendID)
                        {
                            Task task = new Task<Integer>()
                            {
                                public Integer call()
                                {
                                    return SEND_LAST_MES_TO_SERVER(Control.usrInfo.id, bean.friendID, bean.messageID);
                                }
                            };
                            task.setOnSucceeded(event1 -> {
                                int state= (Integer) task.getValue();
                                if(state == -1)
                                {
                                    Control.chat.alert1.exec(Control.chat, "网络连接异常，请检查网络");
                                }
                            });
                            task.setOnFailed(event1 -> {
                                Control.chat.alert1.exec(Control.mainwindow, "发生错误，请重试");
                            });
                            task.setOnCancelled(event1 -> {
                                Control.chat.alert1.exec(Control.mainwindow, "发生错误，请重试");
                            });
                            new Thread(task).start();
                        }
                    }
                    new playSound().play();
                    break;
                case RECV_ADD_FRIEND:
                    Control.ApplyFriend.add(bean);
                    refreshMes(bean);
                    if(Control.chat.isShowing() &&  Control.chat.info.id==0)
                    {
                        Control.chat.addLeft1(bean);
                    }
                    new playSound().play();
                    break;
                case RECV_FILE:
                    if(bean.ID == Control.usrInfo.id)
                    {
                        File f = new File("./files");
                        if(!f.exists())
                            f.mkdir();
                        Control.network.saveFile("./files/"+bean.fileName, bean.fileSize, bean.IPAddress, bean.portNo);
                        refreshMes(bean);
                        new playSound().play();
                    }
                    if(Control.chat.info!=null && Control.chat.info.id == bean.friendID)
                    {
                        Control.chat.addLeft(bean.friendID, "发送了一个文件，请及时接收", -3);
                    }
                    break;
                case RECV_UPDATE_FRIEND:
                    FriendList prev = Control.frdList;
                    Control.frdList = bean.friendList;
                    if(Control.mainwindow.addGroup(bean.friendList)==1)
                    {
                        Control.frdList = bean.friendList;
                        refreshMes(bean);
                        new playSound().play();
                    }
                    else
                    {
                        Control.frdList = prev;
                        Control.mainwindow.alert1.exec(Control.mainwindow, "接收好友申请时出现异常，请重新登录");
                    }
                    break;
                case RECV_OFFLINE:
                    Control.network.getOffline();
                    hint = 1;
                    ((Button) Control.mainwindow.alert1.search("Affirm")).setOnAction(event -> {
                        if(Control.chat.isShowing())
                            Control.chat.close();
                        if(Control.searchnew.isShowing())
                            Control.searchnew.close();
                        if(Control.addgroup.isShowing())
                            Control.addgroup.close();
                        if(Control.homepage.isShowing())
                            Control.addgroup.close();
                        if(Control.applyAdd!=null && Control.applyAdd.isShowing())
                            Control.applyAdd.close();
                        if(Control.friendpage.isShowing())
                            Control.friendpage.close();
                        Control.mainwindow.clear();
                        if(Control.mainwindow.isShowing())
                            Control.mainwindow.close();
                        String[] data = {"", "", ""};
                        String line = null;
                        File f = new File("state.conf");
                        try
                        {
                            if (!f.exists())
                                f.createNewFile();
                            BufferedReader br = new BufferedReader(new FileReader(f));
                            int cnt = 0;
                            while ((line = br.readLine()) != null && cnt < 3) {
                                data[cnt++] = line;
                            }
                            br.close();
                        }catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        //for(int i=0;i<3;++i)
                        //System.out.println(data[i]);
                        Button sel = ((Button) Control.welcome.search("AutoLogin1"));
                        Button unsel = ((Button) Control.welcome.search("AutoLogin"));
                        Button rem = ((Button) Control.welcome.search("Remember1"));
                        Button unrem = ((Button) Control.welcome.search("Remember"));
                        javafx.scene.control.TextField user = ((TextField) Control.welcome.search("UserName"));
                        PasswordField pw = ((PasswordField) Control.welcome.search("Password"));
                        if(data[1].equals("2"))
                        {
                            user.setText(data[0]);
                            pw.setText(data[2]);
                            //System.out.println(user.getText());
                            //System.out.println((pw.getText()));
                            unsel.setManaged(false);
                            unsel.setVisible(false);
                            sel.setManaged(true);
                            sel.setVisible(true);
                            rem.setManaged(true);
                            rem.setVisible(true);
                            unrem.setManaged(false);
                            unrem.setVisible(false);
                        }
                        else if(data[1].equals("1"))
                        {
                            user.setText(data[0]);
                            pw.setText(data[2]);
                            //System.out.println(user.getText());
                            //System.out.println((pw.getText()));
                            rem.setManaged(true);
                            rem.setVisible(true);
                            unrem.setManaged(false);
                            unrem.setVisible(false);
                        }
                        else
                        {
                            //System.out.println("0");
                            user.setText(data[0]);
                            pw.setText("");
                        }
                        Control.ApplyFriend.clear();
                        if(!Control.welcome.isShowing())
                            Control.welcome.show();
                        Control.mainwindow.alert1.close();

                    });
                    ((Label) Control.mainwindow.alert1.search("Info")).setText("该用户已在其他位置登录！");
                    Control.mainwindow.alert1.show();
                    break;
                default:
                    break;
            }
        }
        else
        {
            if(hint==0)
            {
                ((Button) Control.mainwindow.alert1.search("Affirm")).setOnAction(event -> {
                    if(Control.chat.isShowing())
                        Control.chat.close();
                    if(Control.searchnew.isShowing())
                        Control.searchnew.close();
                    if(Control.addgroup.isShowing())
                        Control.addgroup.close();
                    if(Control.homepage.isShowing())
                        Control.addgroup.close();
                    if(Control.applyAdd!=null && Control.applyAdd.isShowing())
                        Control.applyAdd.close();
                    if(Control.friendpage.isShowing())
                        Control.friendpage.close();
                    Control.mainwindow.clear();
                    if(Control.mainwindow.isShowing())
                        Control.mainwindow.close();
                    String[] data = {"", "", ""};
                    String line = null;
                    File f = new File("state.conf");
                    try
                    {
                        if (!f.exists())
                            f.createNewFile();
                        BufferedReader br = new BufferedReader(new FileReader(f));
                        int cnt = 0;
                        while ((line = br.readLine()) != null && cnt < 3) {
                            data[cnt++] = line;
                        }
                        br.close();
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    //for(int i=0;i<3;++i)
                    //System.out.println(data[i]);
                    Button sel = ((Button) Control.welcome.search("AutoLogin1"));
                    Button unsel = ((Button) Control.welcome.search("AutoLogin"));
                    Button rem = ((Button) Control.welcome.search("Remember1"));
                    Button unrem = ((Button) Control.welcome.search("Remember"));
                    javafx.scene.control.TextField user = ((TextField) Control.welcome.search("UserName"));
                    PasswordField pw = ((PasswordField) Control.welcome.search("Password"));
                    if(data[1].equals("2"))
                    {
                        user.setText(data[0]);
                        pw.setText(data[2]);
                        //System.out.println(user.getText());
                        //System.out.println((pw.getText()));
                        unsel.setManaged(false);
                        unsel.setVisible(false);
                        sel.setManaged(true);
                        sel.setVisible(true);
                        rem.setManaged(true);
                        rem.setVisible(true);
                        unrem.setManaged(false);
                        unrem.setVisible(false);
                    }
                    else if(data[1].equals("1"))
                    {
                        user.setText(data[0]);
                        pw.setText(data[2]);
                        //System.out.println(user.getText());
                        //System.out.println((pw.getText()));
                        rem.setManaged(true);
                        rem.setVisible(true);
                        unrem.setManaged(false);
                        unrem.setVisible(false);
                    }
                    else
                    {
                        //System.out.println("0");
                        user.setText(data[0]);
                        pw.setText("");
                    }
                    Control.ApplyFriend.clear();
                    if(!Control.welcome.isShowing())
                        Control.welcome.show();
                    Control.mainwindow.alert1.close();
                });
                ((Label) Control.mainwindow.alert1.search("Info")).setText("服务器连接已断开！");
                Control.mainwindow.alert1.show();
            }
            else
            {
                hint = 0;
            }
        }
    }
}
