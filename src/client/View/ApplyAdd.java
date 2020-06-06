package View;

import chatbean.ChatBean;
import chatbean.TypeValue;
import database.AccountInfo;
import database.FriendList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;
import message.MessagewithOne;
import message.OneMessage;

import java.io.IOException;
import java.util.Vector;

public class ApplyAdd extends Window
{
    private Loading loading;
    private Alert alert;
    private Alert1 alert1;
    public AccountInfo info;
    public int gid2;
    public String remark;
    public ApplyAdd(AccountInfo info, String message) throws IOException
    {
        this.info = info;
        loading = new Loading();
        loading.setModality(this);
        alert = new Alert();
        alert.setModality(this);
        alert1 = new Alert1();
        alert1.setModality(this);

        root = FXMLLoader.load(getClass().getResource("Fxml/ApplyAdd.fxml"));
        Scene scene = new Scene(root, 450, 300);
        initStyle(StageStyle.TRANSPARENT);
        setScene(scene);
        Move();
        setTitle("ApplyAdd");

        ((Label) search("name")).setText(info.NickName);
        ((TextArea) search("message")).setEditable(false);
        ((TextArea) search("message")).setWrapText(true);
        ((TextArea) search("message")).setText(message);
        String[] options = new String[Control.frdList.groupNum];
        for(int i=0;i<Control.frdList.groupNum; ++i)
        {
            options[i] = Control.frdList.Groups[i].GroupName;
        }
        ObservableList<String> option =
                FXCollections.observableArrayList(options);
        ((ComboBox) search("group")).setItems(option);
        reject();
        accept();
    }

    public void reject()
    {
        ((Button) search("cancel")).setOnAction(event -> {
            this.close();
        });
    }

    public void accept()
    {
        ((Button) search("submit")).setOnAction(event -> {
            String group = "";
            if(((ComboBox) search("group")).getSelectionModel().getSelectedItem()!=null)
                group = ((ComboBox) search("group")).getSelectionModel().getSelectedItem().toString();
            String newInfo = ((TextField) search("remark")).getText();
            int id = info.id;
            int group_id = 0;
            for(int i=0; i<Control.frdList.groupNum; ++i)
            {
                if(Control.frdList.Groups[i].GroupName.equals(group))
                {
                    group_id = i;
                    break;
                }
            }
            int gid = group_id;
            if(gid>0)
            {
                loading.exec(this, "正在添加好友...");
                Task task = new Task<ChatBean>(){
                    public ChatBean call()
                    {
                        return SEND_APPLY_INFO_TO_SERVER(Control.usrInfo.id, id, gid, newInfo, gid2, remark);
                    }
                };
                task.setOnSucceeded(event1 -> {
                    ChatBean state = (ChatBean)task.getValue();
                    if(state == null)
                    {
                        loading.close();
                        alert1.exec(this, "网络连接异常，请重试");
                    }
                    else if(state.type == TypeValue.REPLY_CHECK_FAILED)
                    {
                        loading.close();
                        alert1.exec(this, "该用户已成为您的好友");
                    }
                    else if(state.type == TypeValue.REPLY_BAD_ID)
                    {
                        loading.close();
                        alert1.exec(this, "分组不存在或好友不存在");
                    }
                    else if(state.type == TypeValue.REPLY_SERVER_ERROR)
                    {
                        loading.close();
                        alert1.exec(this, "服务器错误，请稍后再试");
                    }
                    else if(state.type == TypeValue.REPLY_OK)
                    {
                        if(state.friendList != null)
                        {
                            try
                            {
                                Control.frdList = state.friendList;
                                if(Control.mainwindow.addGroup(state.friendList)==1)
                                {
                                    int idx = 0;
                                    for(int i=0; i<Control.chattingRecord.FriendNumber; ++i)
                                    {
                                        if(Control.chattingRecord.ChatRecord.get(i).FriendID == info.id)
                                        {
                                            idx = i;
                                            break;
                                        }
                                    }
                                    Control.chattingRecord.ChatRecord.remove(idx);
                                    Control.chattingRecord.FriendNumber = Control.chattingRecord.ChatRecord.size();


                                    Control.chattingRecord.FriendNumber++;
                                    MessagewithOne mwo = new MessagewithOne();
                                    mwo.FriendID = info.id; mwo.ChatRecordwithOne = new Vector<>(); mwo.MessageNumberwithOne = 1;
                                    OneMessage mes = new OneMessage();
                                    mes.valid = true; mes.ID = -3; mes.MessageText = "我们已经是好友啦，一起来聊天吧";mes.sender=info.id;
                                    mwo.ChatRecordwithOne.add(mes);
                                    Control.chattingRecord.ChatRecord.add(mwo);


                                    int pos = -1;
                                    for(int i=0; i<Control.mainwindow.getFriendVector().size(); ++i)
                                    {
                                        if(Control.mainwindow.getFriendVector().get(i).info.id == info.id)
                                        {
                                            Control.mainwindow.getFriendVector().get(i).addMsgTip(1);
                                            pos = i;
                                            break;
                                        }
                                    }
                                    if(pos>0)
                                        Control.mainwindow.getFriendVector().remove(pos);

                                    Friendlist newFriend = new Friendlist(info, newInfo, "", Control.mainwindow);
                                    newFriend.Signature.setText("我们已经是好友啦，一起来聊天吧");
                                    newFriend.addMsgTip(1);
                                    Control.mainwindow.getFriendVector().add(0, newFriend);
                                    Control.mainwindow.getFriendlist().getItems().clear();
                                    for(int i=0; i<Control.mainwindow.getFriendVector().size(); ++i)
                                    {
                                        Control.mainwindow.getFriendlist().getItems().add(Control.mainwindow.getFriendVector().get(i).getPane());
                                    }

                                    loading.close();
                                    ((Label)alert.search("Info")).setText("添加成功");
                                    ((Button)alert.search("Affirm")).setOnAction(event2 -> {
                                        alert.close();
                                        this.close();
                                    });
                                    alert.show();
                                }
                                else
                                {
                                    loading.close();
                                    alert1.exec(this, "刷新好友列表失败，请重试");
                                }
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            loading.close();
                            alert1.exec(this, "数据加载错误，请重试");
                        }
                    }
                    else
                    {
                        loading.close();
                        alert1.exec(this, "添加失败，请重试");
                    }
                });
                task.setOnCancelled(event1 -> {
                    loading.close();
                    alert1.exec(this, "发生错误，请重试");
                });
                task.setOnFailed(event1 -> {
                    loading.close();
                    alert1.exec(this, "发生错误，请重试");
                });
                new Thread(task).start();
            }
            else
            {
                alert1.exec(this, "不可添加到系统助手分组！");
            }
        });
    }

    /**
     * 发送好友验证通过信息到server
     * @param usr_id 用户id
     * @param frd_id 发送好友申请的用户id
     * @param grp_id 欲放入的分组
     * @param newInfo 给新好友的备注
     * @param gid2 对方想把我加进来的分组
     * @param remark 对方为我添加的备注
     * @return 包含更新后的数据的ChatBean
     */
    public ChatBean SEND_APPLY_INFO_TO_SERVER(int usr_id, int frd_id, int grp_id, String newInfo, int gid2, String remark)
    {
        ChatBean info = new ChatBean();
        info.ID = usr_id;
        info.friendID = frd_id;
        info.groupID = grp_id;
        info.newInfo = newInfo;
        info.groupID2 = gid2;
        info.remark = remark;
        info.type = TypeValue.REQ_ACCEPT_FRIEND;
        try
        {
            return Control.network.request(info);
        }catch (Exception e)
        {
            return null;
        }
    }





}
