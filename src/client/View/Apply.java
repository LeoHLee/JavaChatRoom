package View;

import chatbean.ChatBean;
import chatbean.TypeValue;
import database.AccountInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Apply extends Window
{
    private Loading loading;
    private Alert alert;
    private Alert1 alert1;
    private String content;
    private String remark;
    private int h_id;
    private int id;
    private String nickname;
    Apply(int h_id, int id, String nickname) throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("Fxml/Apply.fxml"));
        setScene(new Scene(root, 450, 360));
        initStyle(StageStyle.TRANSPARENT);
        Move();

        this.id = id;
        back();
        MainWindow.setHead(((Button)search("head")), h_id);
        ((Label) search("name")).setText(nickname);
        sendAdd();
        loading = new Loading();
        loading.setModality(this);
        alert = new Alert();
        alert.setModality(this);
        alert1 = new Alert1();
        alert1.setModality(this);

        String[] options = new String[Control.frdList.groupNum];
        for(int i=0;i<Control.frdList.groupNum; ++i)
        {
            options[i] = Control.frdList.Groups[i].GroupName;
        }
        ObservableList<String> option =
                FXCollections.observableArrayList(options);
        ((ComboBox) search("group")).setItems(option);
    }

    //return
    public void back()
    {
        ((Button) search("cancel")).setOnAction(event -> {
            this.clear();
            this.close();
        });
    }
    public void setModality(Window window)
    {
        initModality(Modality.APPLICATION_MODAL);
        initOwner(window);
    }

    public void clear()
    {
        ((TextField) search("remark")).clear();
        ((TextArea) search("content")).clear();
        ((ComboBox) search("group")).setSelectionModel(null);
    }

    //send application
    public void sendAdd()
    {
        ((Button) search("send")).setOnAction(event -> {
            int group_id = -1;
            String group = "";
            if(((ComboBox) search("group")).getSelectionModel().getSelectedItem()!=null)
                group = ((ComboBox) search("group")).getSelectionModel().getSelectedItem().toString();
            for(int i=0; i<Control.frdList.groupNum; ++i)
            {
                if(Control.frdList.Groups[i].GroupName.equals(group))
                {
                    group_id = i;
                    break;
                }
            }
            content = ((TextArea) search("content")).getText();
            remark = ((TextField) search("remark")).getText();
            if(group_id<=0)
            {
                alert1.exec(this, "请选择系统助手分组以外的分组");
            }
            else
            {
                loading.exec(this, "正在发送请求...");
                /*
                 * send id_user, id_frd, message to server
                 */
                int gid = group_id;
                Task task = new Task<Integer>(){
                    public Integer call()
                    {
                        return SEND_APPLY_MESSAGE(Control.usrInfo.id, id, gid, content, remark);
                    }
                };
                task.setOnSucceeded(event1 -> {
                    int state = ((Integer)task.getValue()).intValue();
                    loading.close();
                    if(state == 0)//fail to connect
                    {
                        alert1.exec(this, "发送申请失败，请重试");
                    }
                    else if(state == -1)
                    {
                        alert1.exec(this, "服务器错误，请稍后再试");
                    }
                    else if(state == -2)
                    {
                        alert1.exec(this, "该用户不存在！");
                    }
                    else if(state == -3)
                    {
                        alert1.exec(this, "该用户已成为您的好友！");
                    }
                    else
                    {
                        alert.exec(this, "发送成功，请等待对方接受请求");
                        ((Button) alert.search("Affirm")).setOnAction(event2 -> {
                            Control.searchnew.clear();
                            this.clear();
                            alert.close();
                            this.close();
                            Control.searchnew.close();
                        });
                    }
                });
                task.setOnFailed(event1 -> {
                    loading.close();
                    alert1.exec(this, "发生错误，请重试");
                });
                task.setOnCancelled(event1 -> {
                    loading.close();
                    alert1.exec(this, "发生错误，请重试");
                });
                new Thread(task).start();
            }
        });
    }

    /**
     *  把好友申请发起人id（usr_id）和被申请人id（id）发送至server，生成好友申请消息
     * @param usr_id 用户id
     * @param id 欲添加为好友的用户id
     * @param gid 欲加入的分组id
     * @return 成功返回1，失败（连接失败or生成消息失败）均返回0
     * */
    public int SEND_APPLY_MESSAGE(int usr_id, int id, int gid, String message, String remark)
    {
        ChatBean info = new ChatBean();
        info.type = TypeValue.REQ_ADD_FRIEND;
        info.friendID = id;
        info.ID = usr_id;
        info.groupID = gid;
        info.applyRemark = message;
        info.remark = remark;
        try
        {
            ChatBean back = Control.network.request(info);
            switch(back.type)
            {
                case REPLY_OK:return 1;
                case REPLY_SERVER_ERROR:return -1;
                case REPLY_BAD_ID: return -2;
                case REPLY_CHECK_FAILED: return -3;
                default:return 0;
            }
        }catch (Exception e)
        {
            return 0;
        }
    }






}
