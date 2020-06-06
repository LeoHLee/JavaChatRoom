package View;

import chatbean.ChatBean;
import chatbean.TypeValue;
import database.FriendList;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Vector;

/*
 * Friend Group, used for friend classification
 */
public class FriendGroup
{
    private Pane pane;
    private Button group;
    private Button arrow;
    private Label name;
    private Label number;
    private int gid;
    private ChangeGroupName changeGroupName;
    private Vector<MenuItem> items;

    public FriendGroup(String GroupName, String Num) throws IOException
    {
        /*
         * init pane and components
         */
        pane = new Pane();
        pane.setPrefSize(310, 30);
        pane.getStyleClass().add("listItem");

        arrow = new Button();
        arrow.setPrefSize(15, 15);
        arrow.setLayoutX(5); arrow.setLayoutY(5);
        arrow.getStyleClass().add("arr");
        pane.getChildren().add(arrow);

        name = new Label();
        name.setPrefSize(80, 20);
        name.setLayoutX(30); name.setLayoutY(5);
        name.getStyleClass().add("name");
        pane.getChildren().add(name);

        number = new Label();
        number.setPrefSize(30, 20);
        number.setLayoutX(100);
        number.setLayoutY(5);
        number.getStyleClass().add("num");
        pane.getChildren().add(number);

        group = new Button();
        group.setPrefSize(310, 30);
        group.setLayoutY(0); group.setLayoutX(0);
        group.getStyleClass().add("sendMsg");
        pane.getChildren().add(group);

        items = new Vector<>();
        items.add(new MenuItem("删除分组"));
        items.add(new MenuItem("修改分组名称"));

        gid = getGid(GroupName);
        setLabel(GroupName);
        setNum(Num);
        setMenu();
        deleteGroup();
        changeGroupName = new ChangeGroupName(gid);
        changeGroupname();
    }

    //set the name of this friend group
    public void setLabel(String GroupName)
    {
        name.setText(GroupName);
    }
    //set the total num of this friend group
    public void setNum(String Num)
    {
        number.setText(Num);
    }

    public Pane getPane()
    {
        return pane;
    }
    public int getGid(String groupName)
    {
        int ans=0;
        for(int i=0; i<Control.frdList.groupNum; ++i)
        {
            if(Control.frdList.Groups[i].GroupName.equals(groupName))
            {
                ans = i;
                break;
            }
        }
        return ans;
    }

    // set right-click menu
    public void setMenu()
    {
        ContextMenu menu = new ContextMenu();
        for(int i=0; i<items.size(); ++i)
        {
            menu.getItems().add(items.get(i));
        }
        group.setContextMenu(menu);
    }

    public void deleteGroup()
    {
        items.get(0).setOnAction(event -> {
            ((Label) Control.mainwindow.confirm.search("Info")).setText("您确定要删除这个分组吗？");
            ((Button) Control.mainwindow.confirm.search("Affirm")).setOnAction(event1 -> {
                String GroupName = name.getText();
                int gid = 0;
                for(int i=0; i<Control.frdList.groupNum; ++i)
                {
                    if(Control.frdList.Groups[i].GroupName.equals(GroupName))
                    {
                        gid = i;
                        break;
                    }
                }
                if(gid == 0)
                {
                    Control.mainwindow.alert1.exec(Control.mainwindow, "系统助手，不可删除！");
                }
                else if(gid==1)
                {
                    Control.mainwindow.alert1.exec(Control.mainwindow, "默认分组不可删除！");
                }
                else
                {
                    Control.mainwindow.loading.exec(Control.mainwindow, "正在删除中...");
                    int g = gid;
                    Task task = new Task<ChatBean>(){
                        public ChatBean call()
                        {
                            return SEND_DELETE_GROUP(Control.usrInfo.id, g);
                        }
                    };
                    task.setOnSucceeded(event2 -> {
                        ChatBean state = (ChatBean)task.getValue();
                        if(state == null)
                        {
                            Control.mainwindow.loading.close();
                            Control.mainwindow.alert1.exec(Control.mainwindow, "连接失败，请检查网络");
                        }
                        else if(state.type == TypeValue.REPLY_BAD_ID)
                        {
                            Control.mainwindow.loading.close();
                            Control.mainwindow.alert1.exec(Control.mainwindow, "该分组不存在");
                        }
                        else if(state.type == TypeValue.REPLY_SERVER_ERROR)
                        {
                            Control.mainwindow.loading.close();
                            Control.mainwindow.alert1.exec(Control.mainwindow, "服务器错误，请稍后再试");
                        }
                        else if(state.type == TypeValue.REPLY_OK)
                        {
                            Control.mainwindow.loading.close();
                            Control.mainwindow.loading.exec(Control.mainwindow, "正在更新分组列表...");
                            FriendList newlist = state.friendList;
                            if(newlist == null)
                            {
                                Control.mainwindow.loading.close();
                                Control.mainwindow.alert1.exec(Control.mainwindow, "数据加载错误，请重试");
                            }
                            else
                            {
                                Control.frdList = newlist;
                                Control.mainwindow.addGroup(newlist);
                                Control.mainwindow.loading.close();
                                Control.mainwindow.alert.exec(Control.mainwindow, "删除成功");

                            }
                        }
                        else
                        {
                            Control.mainwindow.loading.close();
                            Control.mainwindow.alert1.exec(Control.mainwindow, "删除失败，请重试");
                        }
                    });
                    task.setOnCancelled(event2 -> {
                        Control.mainwindow.loading.close();
                        Control.mainwindow.alert1.exec(Control.mainwindow, "发生错误，请重试");
                    });
                    task.setOnFailed(event2 -> {
                        Control.mainwindow.loading.close();
                        Control.mainwindow.alert1.exec(Control.mainwindow, "发生错误，请重试");
                    });
                    new Thread(task).start();
                }
                Control.mainwindow.confirm.close();
            });
            Control.mainwindow.confirm.show();
        });
    }

    public void changeGroupname()
    {
        items.get(1).setOnAction(event -> {
            if(gid == 0)
            {
                Control.mainwindow.alert1.exec(Control.mainwindow, "系统助手不可修改！");
            }
            else
            {
                ((Button) changeGroupName.search("submit")).setOnAction(event1 -> {
                    String newName = ((TextField) changeGroupName.search("group")).getText();
                    boolean tf = true;
                    for(int i=0; i<Control.frdList.groupNum; ++i)
                    {
                        if(Control.frdList.Groups[i].GroupName.equals(newName))
                        {
                            tf = false;
                            break;
                        }
                    }
                    if(!tf)
                    {
                        Control.mainwindow.alert1.exec(Control.mainwindow, "该分组已存在！");
                    }
                    else if(newName.equals(""))
                    {
                        Control.mainwindow.alert1.exec(Control.mainwindow, "分组名不可为空！");
                    }
                    else
                    {
                        Control.mainwindow.loading.exec(Control.mainwindow, "正在修改...");
                        Task task = new Task<ChatBean>(){
                            public ChatBean call()
                            {
                                return SEND_NEW_NAME(Control.usrInfo.id, gid, newName);
                            }
                        };
                        task.setOnSucceeded(event2 -> {
                            ChatBean state = (ChatBean)task.getValue();
                            if(state == null)
                            {
                                Control.mainwindow.loading.close();
                                Control.mainwindow.alert1.exec(Control.mainwindow, "连接失败，请重试");
                            }
                            else if(state.type == TypeValue.REPLY_BAD_ID)
                            {
                                Control.mainwindow.loading.close();
                                Control.mainwindow.alert1.exec(Control.mainwindow, "该分组不存在");
                            }
                            else if(state.type == TypeValue.REPLY_SERVER_ERROR)
                            {
                                Control.mainwindow.loading.close();
                                Control.mainwindow.alert1.exec(Control.mainwindow, "服务器错误，请稍后再试");
                            }
                            else if(state.type == TypeValue.REPLY_OK)
                            {
                                if(state.friendList != null)
                                {
                                    //Control.mainwindow.addFriend(state.friendList);
                                    if(Control.mainwindow.addGroup(state.friendList)==1)
                                    {
                                        Control.frdList = state.friendList;
                                        Control.mainwindow.loading.close();
                                        ((Label) Control.mainwindow.alert.search("Info")).setText("修改成功");
                                        ((Button) Control.mainwindow.alert.search("Affirm")).setOnAction(event3 -> {
                                            changeGroupName.close();
                                            Control.mainwindow.alert.close();
                                        });
                                        Control.mainwindow.alert.show();
                                    }
                                    else
                                    {
                                        Control.mainwindow.loading.close();
                                        Control.mainwindow.alert1.exec(Control.mainwindow, "发生错误，请重试");
                                    }
                                }
                                else
                                {
                                    Control.mainwindow.loading.close();
                                    Control.mainwindow.alert1.exec(Control.mainwindow, "数据加载错误，请重试");
                                }
                            }
                            else
                            {
                                Control.mainwindow.loading.close();
                                Control.mainwindow.alert1.exec(Control.mainwindow, "修改失败，请重试");
                            }
                        });
                        new Thread(task).start();
                    }
                });
                changeGroupName.show();
            }
        });
    }

    /**
     * 用于发送删除分组的信息
     * @param usr_id 用户账号
     * @param gid 分组id
     * @return ChatBean，包含状态信息和新的friendList，连接失败返回null
     */
    public ChatBean SEND_DELETE_GROUP(int usr_id, int gid)
    {
        ChatBean info = new ChatBean();
        info.type = TypeValue.REQ_REMOVE_GROUP;
        info.ID = usr_id;
        info.groupID = gid;
        try
        {
            return Control.network.request(info);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 向server发送用户usr_id要更改名称的分组gid和新的分组名称
     * @param usr_id 账号
     * @param gid 分组id
     * @param newName 新分组名
     * @return 含新的信息的ChatBean，连接失败返回null
     */
    public ChatBean SEND_NEW_NAME(int usr_id, int gid, String newName)
    {
        ChatBean info = new ChatBean();
        info.type = TypeValue.REQ_EDIT_GROUP_NAME;
        info.groupID = gid;
        info.ID = usr_id;
        info.newInfo = newName;
        try
        {
            return Control.network.request(info);
        }catch (Exception e)
        {
            return null;
        }
    }
}
