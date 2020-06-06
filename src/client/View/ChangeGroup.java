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
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ChangeGroup extends Window
{
    private Loading loading;
    private Alert alert;
    private Alert1 alert1;
    int id;
    String ori_group;
    public ChangeGroup(int id, String ori_group) throws IOException
    {
        this.id = id;
        this.ori_group = ori_group;
        loading = new Loading();
        loading.setModality(this);
        alert = new Alert();
        alert.setModality(this);
        alert1 = new Alert1();
        alert1.setModality(this);

        root = FXMLLoader.load(getClass().getResource("Fxml/ChangeGroup.fxml"));
        Scene scene = new Scene(root, 300, 200);
        initStyle(StageStyle.TRANSPARENT);
        setScene(scene);
        Move();
        setTitle("ChangeGroup");


        String[] options = new String[Control.frdList.groupNum];
        for(int i=0;i<Control.frdList.groupNum; ++i)
        {
            options[i] = Control.frdList.Groups[i].GroupName;
        }
        ObservableList<String> option =
                FXCollections.observableArrayList(options);
        ((ComboBox) search("group")).setItems(option);


        setModality(Control.mainwindow);
        reject();
        accept();
    }

    public void setModality(Window window)
    {
        initModality(Modality.APPLICATION_MODAL);
        initOwner(window);
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
            Object item = ((ComboBox) search("group")).getSelectionModel().getSelectedItem();
            String name = "";
            if(item !=null)
                name = item.toString();
            int ori_gid = 0, gid = 0;
            for(int i=0; i<Control.frdList.groupNum; ++i)
            {
                if(Control.frdList.Groups[i].GroupName.equals(ori_group))
                {
                    ori_gid = i;
                }
                if(Control.frdList.Groups[i].GroupName.equals(name))
                {
                    gid = i;
                }
            }
            if(name.equals(""))
            {
                alert1.exec(this, "您未选择分组！");
            }
            else if(ori_gid == 0)
            {
                alert1.exec(this, "系统助手不可移动！");
            }
            else if(gid==0)
            {
                alert1.exec(this, "不可移动到系统助手分组！");
            }
            else
            {
                loading.exec(this, "正在添加...");
                int origin_g = ori_gid, g = gid;
                Task task = new Task<ChatBean>(){
                    public ChatBean call()
                    {
                        return SEND_CHANGE_GROUP_TO_SERVER(Control.usrInfo.id, id, origin_g, g);
                    }
                };
                task.setOnSucceeded(event1 -> {
                    ChatBean state = (ChatBean)task.getValue();
                    if(state == null)
                    {
                        loading.close();
                        alert1.exec(this, "连接失败，请检查网络！");
                    }
                    else if(state.type == TypeValue.REPLY_BAD_ID)
                    {
                        loading.close();
                        alert1.exec(this, "不存在的好友ID或分组ID！");
                    }
                    else if(state.type == TypeValue.REPLY_CHECK_FAILED)
                    {
                        loading.close();
                        alert1.exec(this, "该用户不是您的好友！");
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
                            Control.frdList = state.friendList;
                            Control.mainwindow.addGroup(Control.frdList);
                            loading.close();
                            ((Label) alert.search("Info")).setText("添加成功");
                            ((Button) alert.search("Affirm")).setOnAction(event2 -> {
                                alert.close();
                                this.close();
                            });
                            alert.show();
                        }
                        else
                        {
                            loading.close();
                            alert1.exec(this, "数据加载失败，请重试");
                        }
                    }
                    else
                    {
                        loading.close();
                        alert1.exec(this, "移动失败，请重试");
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
     * 把分组修改信息传递给server，并等待server传回修改后的FriendList
     * @param usr_id 用户id
     * @param id 好友id
     * @param ori_gid 原来的分组id
     * @param gid 新分组id
     * @return 含新的friendList的ChatBean， 若连接失败返回null
     */
    public ChatBean SEND_CHANGE_GROUP_TO_SERVER(int usr_id, int id, int ori_gid, int gid)
    {
        ChatBean info = new ChatBean();
        info.ID = usr_id;
        info.friendID = id;
        info.groupID = ori_gid;
        info.groupID2 = gid;
        info.type = TypeValue.REQ_MOVE_GROUP;
        try
        {
            return Control.network.request(info);
        }catch (Exception e)
        {
            return null;
        }
    }
}
