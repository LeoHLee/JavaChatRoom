package View;

import chatbean.ChatBean;
import chatbean.TypeValue;
import database.FriendList;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import database.AccountInfo;
import message.*;

// component in friend list
public class Friendlist
{
    //public boolean read;
    private Button head;
    public AccountInfo info;
    public Label remark;
    String Rk;
    String sgn;
    public Label Signature;
    private Pane pane;
    private ChangeGroup changeGroup;
    private Button send; // cover the whole pane, used for right-click
    public Button MsgTip;// show number of unread messages;
    Vector<MenuItem> items;// right-click menu

    public Friendlist(AccountInfo acc, String Remark, String sign, MainWindow mainWindow) throws  IOException
    {
        Rk = Remark;
        sgn = sign;
        info = acc;
        //read = false;

        pane = new Pane();
        pane.setPrefSize(310, 50);
        pane.getStyleClass().add("listItem");

        head = new Button();
        head.setPrefSize(46, 46);;
        head.setLayoutX(2); head.setLayoutY(2);
        head.getStyleClass().add("head");
        pane.getChildren().add(head);

        remark = new Label();
        remark.setPrefSize(120, 30);
        remark.setLayoutX(55); remark.setLayoutY(5);
        remark.getStyleClass().add("NickName");
        pane.getChildren().add(remark);

        Signature = new Label();
        Signature.setPrefSize(200, 20);
        Signature.setLayoutX(55); Signature.setLayoutY(30);
        Signature.getStyleClass().add("Signature");
        pane.getChildren().add(Signature);

        send = new Button();
        send.setPrefSize(310, 50);
        send.setLayoutX(0); send.setLayoutX(0);
        send.getStyleClass().add("sendMsg");
        pane.getChildren().add(send);

        MsgTip = new Button();
        MsgTip.setPrefSize(15, 15);
        MsgTip.setLayoutX(33); MsgTip.setLayoutY(32);
        MsgTip.getStyleClass().add("no-Tip");
        pane.getChildren().add(MsgTip);

        items = new Vector<>();
        items.add(new MenuItem("标为未读"));
        items.add(new MenuItem("好友资料"));
        items.add(new MenuItem("清除聊天记录"));
        items.add(new MenuItem("删除好友"));
        items.add(new MenuItem("移动分组"));

        String group = "";
        if(acc.id!=0)
            group = Control.frdList.Groups[Control.frdList.Query(info.id)[0]].GroupName;
        else
            group = Control.frdList.Groups[0].GroupName;
        changeGroup = new ChangeGroup(info.id, group);

        MainWindow.setHead(head, info.id);

        setRemark(Remark);
        setSign(sign);
        setMenu();
        groupChange();
        setActionForFriendPage(Control.friendpage, mainWindow);
        setActionForSend();
        setReadOrNot();
        clearMsg(mainWindow);
        removeFriend(mainWindow, info);
    }

    //method for setting and getting text of NickName
    public void setRemark(String rk)
    {
        if(rk==null || rk.equals(""))
            remark.setText(info.NickName);
        else
            remark.setText(rk);
    }
    public String getRemark()
    {
        return remark.getText();
    }
    //method for setting and getting text of Signature
    public void setSign(String s)
    {
        Signature.setText(s);
    }
    public String getSign()
    {
        return Signature.getText();
    }
    // other getting methods
    public Pane getPane(){return pane;}
    public Button getHead(){return head;}

    // set right-click menu
    public void setMenu()
    {
        ContextMenu menu = new ContextMenu();
        for(int i=0; i<items.size(); ++i)
        {
            menu.getItems().add(items.get(i));
        }
        send.setContextMenu(menu);
    }
    // change group
    public void groupChange()
    {
        items.get(4).setOnAction(event -> {
            changeGroup.show();
        });
    }
    // show friend page
    public void setActionForFriendPage(FriendPage friendPage, MainWindow mainWindow)
    {
        items.get(1).setOnAction(event -> {
            if(friendPage.isShowing())
                friendPage.close();

            String remark = getRemark();
            String signature = getSign();
            editRemark(friendPage, mainWindow);
            AccountInfo frdInfo = info;
            ((TextField) friendPage.search("Signature")).setText(sgn);
            ((TextField) friendPage.search("remark")).setText(Rk);
            ((TextField) friendPage.search("username")).setText(info.NickName+"("+info.id+")");
            if(frdInfo.Sex.equals("1"))
                ((TextField) friendPage.search("sex")).setText("男");
            else if(frdInfo.Sex.equals("2"))
                ((TextField) friendPage.search("sex")).setText("女");
            ((TextField) friendPage.search("mail")).setText(frdInfo.Mail);
            ((TextField) friendPage.search("birthday")).setText(frdInfo.Birthday);
            //System.out.println(frdInfo.NickName);
            MainWindow.setHead(((Button) friendPage.search("head")),info.id);
            friendPage.show();
        });
    }

    // message counter setting methods
    public void addMsgTip(int value)
    {
        MsgTip.getStyleClass().clear();
        MsgTip.getStyleClass().add("Tip");
        if(!MsgTip.getText().equals(""))
        {
            value = value + Integer.valueOf(MsgTip.getText());
        }
        MsgTip.setText(String.valueOf(value));
    }
    public void clearMsgTip()
    {
        MsgTip.getStyleClass().clear();
        MsgTip.getStyleClass().add("no-Tip");
        if(!MsgTip.getText().equals(""))
        {
            MsgTip.setText("");
        }
    }
    // read or not setting
    public void setReadOrNot()
    {
        if(MsgTip.getStyleClass().equals("no-Tip"))
            items.get(0).setText("标为未读");
        else if(MsgTip.getStyleClass().equals("Tip"))
            items.get(0).setText("标为已读");
        items.get(0).setOnAction(event ->{
            if(items.get(0).getText().equals("标为已读"))
            {
                clearMsgTip();
                items.get(0).setText("标为未读");
            }
            else
            {
                addMsgTip(1);
                items.get(0).setText("标为已读");
            }
        });
    }

    // revising remark
    public void editRemark(FriendPage friendPage, MainWindow mainWindow)
    {
        ((Button) friendPage.search("finish")).setOnAction(event -> {
            String rk = ((TextField) friendPage.search("remark")).getText();
            friendPage.loading.exec(friendPage, "正在修改...");
            Task task = new Task<ChatBean>()
            {
                public ChatBean call()
                {
                    return SEND_NEW_REMARK(Control.usrInfo.id, info.id, rk);
                }
            };
            task.setOnSucceeded(event1 -> {
                ChatBean state = (ChatBean)task.getValue();
                if(state == null)
                {
                    friendPage.loading.close();
                    friendPage.alert1.exec(friendPage, "连接失败，请重试");
                    ((TextField) friendPage.search("remark")).setText("");
                }
                else if(state.type == TypeValue.REPLY_SERVER_ERROR)
                {
                    friendPage.loading.close();
                    friendPage.alert1.exec(friendPage, "服务器错误，请稍后再试");
                    ((TextField) friendPage.search("remark")).setText("");
                }
                else if(state.type == TypeValue.REPLY_BAD_ID)
                {
                    friendPage.loading.close();
                    friendPage.alert1.exec(friendPage, "该ID不存在");
                    ((TextField) friendPage.search("remark")).setText("");
                }
                else if(state.type == TypeValue.REPLY_CHECK_FAILED)
                {
                    friendPage.loading.close();
                    friendPage.alert1.exec(friendPage, "该用户不是您的好友");
                    ((TextField) friendPage.search("remark")).setText("");
                }
                else if(state.type == TypeValue.REPLY_OK)
                {
                    if(mainWindow.addGroup(state.friendList)==1)
                    {
                        Control.frdList = state.friendList;
                        int size = mainWindow.getFriendVector().size();
                        for(int i=0; i<size; ++i)
                        {
                            if(mainWindow.getFriendVector().get(i).info.id == info.id)
                            {
                                mainWindow.getFriendVector().get(i).remark.setText(rk);
                                break;
                            }
                        }
                        friendPage.loading.close();
                        friendPage.alert.exec(friendPage, "修改成功");
                        ((TextField) friendPage.search("remark")).setText(rk);
                        remark.setText(rk);
                    }
                    else
                    {
                        friendPage.loading.close();
                        friendPage.alert1.exec(friendPage, "刷新好友列表失败，请重试");
                    }

                }
                else
                {
                    friendPage.loading.close();
                    friendPage.alert1.exec(friendPage, "修改失败，请重试");
                    ((TextField) friendPage.search("remark")).setText("");
                }
            });
            task.setOnFailed(event1 -> {
                friendPage.loading.close();
                friendPage.alert1.exec(friendPage, "发生错误，请重试");
            });
            task.setOnCancelled(event1 -> {
                friendPage.loading.close();
                friendPage.alert1.exec(friendPage, "发生错误，请重试");
            });
            new Thread(task).start();
        });
    }

    // delete friend
    public void removeFriend(MainWindow mainwindow, AccountInfo acc)
    {
        items.get(3).setOnAction(event -> {
            if(acc.id==0)
            {
                mainwindow.alert1.exec(mainwindow, "系统助手不可删除");
            }
            else
            {
                ((Label) mainwindow.confirm.search("Info")).setText("您确定要删除吗？");
                ((Button) mainwindow.confirm.search("Affirm")).setOnAction(event1 -> {
                    mainwindow.confirm.close();
                    mainwindow.loading.exec(mainwindow, "正在删除...");
                    Task task = new Task<ChatBean>(){
                        public ChatBean call()
                        {
                            return DELETE_FRIEND(Control.usrInfo.id, info.id);
                        }
                    };
                    task.setOnSucceeded(event2 -> {
                        ChatBean state = (ChatBean)task.getValue();
                        if(state == null)
                        {
                            mainwindow.loading.close();
                            mainwindow.alert1.exec(mainwindow, "连接失败，请重试");
                        }
                        else if(state.type == TypeValue.REPLY_SERVER_ERROR)
                        {
                            mainwindow.loading.close();
                            mainwindow.alert1.exec(mainwindow, "服务器错误，请稍后再试");
                        }
                        else if(state.type == TypeValue.REPLY_BAD_ID)
                        {
                            mainwindow.loading.close();
                            mainwindow.alert1.exec(mainwindow, "该用户不存在");
                        }
                        else if(state.type == TypeValue.REPLY_CHECK_FAILED)
                        {
                            mainwindow.loading.close();
                            mainwindow.alert1.exec(mainwindow, "该用户已不是您的好友");
                        }
                        else if(state.type == TypeValue.REPLY_OK)
                        {
                            if(state.friendList != null)
                            {
                                ((Label) mainwindow.loading.search("Info")).setText("正在更新好友列表，请稍候...");
                                FriendList prev = Control.frdList;
                                Control.frdList = state.friendList;
                                if(mainwindow.addGroup(state.friendList)==1)
                                {
                                    int size = mainwindow.getFriendVector().size();
                                    int index = 0;

                                    for(int i=0; i<size; ++i)
                                    {
                                        if(mainwindow.getFriendVector().get(i).info.id == info.id)
                                        {
                                            index = i;
                                            break;
                                        }
                                    }
                                    mainwindow.getFriendVector().remove(index);
                                    mainwindow.getFriendlist().getItems().clear();
                                    for(int i=0; i<mainwindow.getFriendVector().size(); ++i)
                                    {
                                        if(mainwindow.getFriendVector().get(i).MsgTip.getText().equals(""))
                                            mainwindow.getFriendlist().getItems().add(mainwindow.getFriendVector().get(i).getPane());
                                        else
                                            mainwindow.getFriendlist().getItems().add(0, mainwindow.getFriendVector().get(i).getPane());
                                    }
                                    index=-1;
                                    for(int i=0; i<Control.chattingRecord.FriendNumber; ++i)
                                    {
                                        if(Control.chattingRecord.ChatRecord.get(i).FriendID == info.id)
                                        {
                                            index = i;
                                            break;
                                        }
                                    }
                                    if(index>=0)
                                        Control.chattingRecord.ChatRecord.remove(index);
                                    Control.chattingRecord.FriendNumber = Control.chattingRecord.ChatRecord.size();

                                    mainwindow.loading.close();
                                    ((Label) mainwindow.alert.search("Info")).setText("删除成功");
                                    ((Button) mainwindow.alert.search("Affirm")).setOnAction(event3 -> {
                                        //mainwindow.confirm.close();
                                        mainwindow.alert.close();
                                    });
                                    mainwindow.alert.show();
                                }
                                else
                                {
                                    Control.frdList = prev;
                                    mainwindow.loading.close();
                                    mainwindow.alert1.exec(mainwindow, "刷新好友列表失败，请重试");
                                }
                            }
                            else
                            {
                                mainwindow.loading.close();
                                mainwindow.alert1.exec(mainwindow, "数据获取出现异常，请重试");
                            }
                        }
                        else
                        {
                            mainwindow.loading.close();
                            mainwindow.alert1.exec(mainwindow, "删除失败，请重试");
                        }
                    });
                    new Thread(task).start();
                });
                mainwindow.confirm.show();
            }
        });

    }

    // message sending event
    public void setActionForSend()
    {
        send.setOnAction(event -> {
            String frdName = remark.getText();
            Control.chat.info = info;
            Control.chat.getList().getItems().clear();
            this.clearMsgTip();
            ((Label) Control.chat.search("Name")).setText(frdName);

            if(info.id == 0)
            {
                if(Control.ApplyFriend.isEmpty())
                    Control.chat.addLeft(0,"tsugu可爱~", -1);
                else
                {
                    for(int i=0; i<Control.ApplyFriend.size(); ++i)
                    {
                        Control.chat.addLeft1(Control.ApplyFriend.get(i));
                    }
                }
                Control.chat.show();
            }
            else
            {
                Control.mainwindow.loading.exec(Control.mainwindow, "正在加载...");
                int size = Control.chattingRecord.ChatRecord.size();
                int last_ID = -1;
                for(int i=0; i<size; ++i)
                {
                    if(Control.chattingRecord.ChatRecord.get(i).FriendID == info.id)
                    {
                        int num_mes = Control.chattingRecord.ChatRecord.get(i).ChatRecordwithOne.size();
                        Vector<OneMessage> tmp = Control.chattingRecord.ChatRecord.get(i).ChatRecordwithOne;
                        for(int j=0; j<num_mes; ++j)
                        {
                            if(tmp.get(j).valid)
                            {
                                if(tmp.get(j).sender==info.id)
                                    Control.chat.addLeft(info.id, tmp.get(j).MessageText, tmp.get(j).ID);
                                else
                                    Control.chat.addRight(info.id, tmp.get(j).MessageText, tmp.get(j).ID);
                                if(tmp.get(j).ID>0)
                                    last_ID = tmp.get(j).ID;
                            }
                        }
                        break;
                    }
                }
                if(last_ID>0)
                {
                    int finalLast_ID = last_ID;
                    Task task = new Task<Integer>()
                    {
                        public Integer call()
                        {
                            return SEND_LAST_MES_TO_SERVER(Control.usrInfo.id, info.id, finalLast_ID);
                        }
                    };
                    task.setOnSucceeded(event1 -> {
                        Control.mainwindow.loading.close();
                        int state= (Integer) task.getValue();
                        if(state == -1)
                        {
                            Control.chat.alert1.exec(Control.chat, "网络连接异常，请检查网络");
                        }
                        Control.chat.show();
                    });
                    task.setOnFailed(event1 -> {
                        Control.mainwindow.loading.close();
                        Control.mainwindow.alert1.exec(Control.mainwindow, "发生错误，请重试");
                    });
                    task.setOnCancelled(event1 -> {
                        Control.mainwindow.loading.close();
                        Control.mainwindow.alert1.exec(Control.mainwindow, "发生错误，请重试");
                    });
                    new Thread(task).start();
                }
                else
                {
                    Control.mainwindow.loading.close();
                    Control.chat.show();
                }
            }
        });
    }

    //clear talk record
    public void clearMsg(MainWindow mainwindow)
    {
        items.get(2).setOnAction(event -> {
            ((Label) mainwindow.confirm.search("Info")).setText("您确定要清空所有聊天记录吗？");
            ((Button) mainwindow.confirm.search("Affirm")).setOnAction(event1 -> {
                    mainwindow.loading.exec(mainwindow, "正在清空...");
                    Task task = new Task<Integer>(){
                        public Integer call()
                        {
                            return DELETE_ALL_MESSAGE(Control.usrInfo.id, info.id);
                        }
                    };
                    task.setOnSucceeded(event2 -> {
                        int state = ((Integer) task.getValue()).intValue();
                        if(state == -1)
                        {
                            mainwindow.loading.close();
                            mainwindow.alert1.exec(mainwindow, "连接失败，请重试");
                        }
                        else if(state == -2)
                        {
                            mainwindow.loading.close();
                            mainwindow.alert1.exec(mainwindow, "服务器错误，请稍后再试");
                        }
                        else if(state == 0)
                        {
                            mainwindow.loading.close();
                            mainwindow.alert1.exec(mainwindow, "该用户不存在");
                        }
                        else if(state == 1)
                        {
                            for(int i=0; i<Control.chattingRecord.FriendNumber; ++i)
                            {
                                if(Control.chattingRecord.ChatRecord.get(i).FriendID == info.id)
                                {
                                    Control.chattingRecord.ChatRecord.get(i).ChatRecordwithOne.clear();
                                    Control.chattingRecord.ChatRecord.get(i).MessageNumberwithOne = 0;
                                    break;
                                }
                            }
                            Signature.setText("Hello!");
                            mainwindow.loading.close();
                            ((Label) mainwindow.alert.search("Info")).setText("清空成功");
                            ((Button) mainwindow.alert.search("Affirm")).setOnAction(event3 -> {
                                mainwindow.alert.close();
                                mainwindow.confirm.close();
                            });
                            mainwindow.alert.show();
                        }
                        else
                        {
                            mainwindow.loading.close();
                            mainwindow.alert1.exec(mainwindow, "删除失败，请重试");
                        }
                    });
                    task.setOnCancelled(event2 -> {
                        mainwindow.loading.close();
                        mainwindow.alert1.exec(mainwindow, "发生错误，请重试");
                    });
                    task.setOnFailed(event2 -> {
                        mainwindow.loading.close();
                        mainwindow.alert1.exec(mainwindow, "发生错误，请重试");
                    });
                    new Thread(task).start();
                });
            mainwindow.confirm.show();
        });
    }

    /**
     * 向server发送好友备注的修改信息
     * @param usr_id 用户id
     * @param id 好友id
     * @param remark 新备注
     * @return 返回修改状态信息ChatBean
     */
    public ChatBean SEND_NEW_REMARK(int usr_id, int id, String remark)
    {
        ChatBean info = new ChatBean();
        info.ID = usr_id;
        info.friendID = id;
        info.newInfo = remark;
        info.type = TypeValue.REQ_EDIT_REMARK;
        try
        {
            return Control.network.request(info);
        }catch (Exception e)
        {
            return null;
        }
    }


    /**
     * 发送usr_id用户删除id好友的请求
     * @param usr_id 用户id
     * @param id 待删好友id
     * @return ChatBean，包含状态信息，连接失败则返回null
     */
    public ChatBean DELETE_FRIEND(int usr_id, int id)
    {
        ChatBean info = new ChatBean();
        info.type = TypeValue.REQ_REMOVE_FRIEND;
        info.friendID = id;
        info.ID = usr_id;
        try
        {
            return Control.network.request(info);
        }catch (Exception e)
        {
            return null;
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

    /**
     * 发送清空聊天记录的信号
     * @param usr_id 用户id
     * @param id 好友id
     * @return 状态信息
     */
    public int DELETE_ALL_MESSAGE(int usr_id, int id)
    {
        ChatBean info = new ChatBean(TypeValue.REQ_DELETE_RECORD);
        info.ID = usr_id;
        info.friendID = id;
        try
        {
            ChatBean back = Control.network.request(info);
            switch (back.type)
            {
                case REPLY_OK: return 1;
                case REPLY_BAD_ID: return 0;
                case REPLY_SERVER_ERROR: return -2;
                default: return -1;
            }
        }catch (Exception e)
        {
            return -1;
        }
    }

}
