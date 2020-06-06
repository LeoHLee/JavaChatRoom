package View;

import chatbean.ChatBean;
import chatbean.TypeValue;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.shape.Line;
import javafx.stage.StageStyle;

import java.awt.*;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Vector;
import java.io.IOException;

import View.Control;
import View.Welcome;
import database.AccountInfo;
import database.GroupList;
import database.FriendList;
import message.*;


public class MainWindow extends Window
{
    private ListView friendList;
    private ListView group;
    private Vector<Friendlist> fListVector;
    private Vector<Friendlist> fdVector;
    private Vector<FriendGroup> gListVector;
    //HashMap<Integer, AccountInfo> FriendInfos;
    Confirm confirm;
    Alert alert;
    Alert1 alert1;
    Loading loading;

    MainWindow() throws IOException
    {
        //scene init
        root = FXMLLoader.load(getClass().getResource("Fxml/MainWindow.fxml"));
        Scene scene = new Scene(root, 320,560);
        initStyle(StageStyle.TRANSPARENT);
        setScene(scene);
        setTitle("Main");
        Move();
        //tool init
        ((Line) search("MsgLine")).setVisible(false);
        ((Line) search("FrdLine")).setVisible(false);
        ((Button) search("Message")).requestFocus();
        setClose();
        setMenu();
        LogOut();
        addNewGroup();

        friendList = ((ListView) search("message"));
        group = ((ListView) search("FriendList"));
        fListVector = new Vector<>();
        fdVector = new Vector<>();
        gListVector = new Vector<>();

        confirm = new Confirm();
        confirm.setModality(this);
        alert = new Alert();
        alert.setModality(this);
        alert1 = new Alert1();
        alert1.setModality(this);
        loading = new Loading();
        loading.setModality(this);

    }

    //set the actions and tips of Min & Close button
    public void setClose()
    {
        ((Button) search("Close")).setTooltip(new Tooltip("关闭"));
        ((Button) search("Min")).setTooltip(new Tooltip("最小化"));
        ((Button) search("Close")).setOnAction(event ->{
            this.close();
            /*
            ChatBean off = new ChatBean();
            off.type = TypeValue.MES_OFFLINE;
            try
            {
                Control.network.send(off);
            }catch (Exception e)
            {
                e.printStackTrace();
            }*/
            /*
             * if the close button od main page is pressed, exit the whole program
             */
            System.exit(0);
        });
        ((Button) search("Min")).setOnAction(event ->{
            /*
            SystemTray systray = SystemTray.getSystemTray();
            Image img = Toolkit.getDefaultToolkit().getImage("src/View/Fxml/CSS/Pic/Icons/chat.jpg");
            String hint = "chat";
            PopupMenu menu = new PopupMenu();
            MenuItem item1 = new MenuItem("exit");
            MenuItem item2 = new MenuItem("show");
            menu.add(item2);
            menu.add(item1);
            TrayIcon tray = new TrayIcon(img, hint, menu);
            setIconified(true);
            item2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            show();
                        }
                    });
                }
            });
            item1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            System.exit(0);
                        }
                    });
                    systray.remove(tray);
                }
            });
            try {
                systray.add(tray);
            } catch (AWTException e) {
                e.printStackTrace();
            }
            hide();
            */
            setIconified(true);
        });
    }
    //set tips of menu buttons
    public void setMenu()
    {
        ((Button) search("Exit")).setTooltip(new Tooltip("注销"));
        ((Button) search("Add")).setTooltip(new Tooltip("添加好友"));
        ((Button) search("addGroup")).setTooltip(new Tooltip("新建分组"));
    }
    //set the log out button
    public void LogOut()
    {
        ((Button) search("Exit")).setOnAction(event ->{
            fListVector.clear();
            gListVector.clear();
            friendList.getItems().clear();
            group.getItems().clear();

            /*ChatBean off = new ChatBean();
            off.type = TypeValue.MES_OFFLINE;
            try
            {
                Control.network.send(off);
            }catch (Exception e)
            {
                e.printStackTrace();
            }*/
            Control.network.getOffline();

            close();
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
            TextField user = ((TextField) Control.welcome.search("UserName"));
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
            Control.welcome.show();
        });
    }
    // open group-adding window
    public void addNewGroup()
    {
        ((Button) search("addGroup")).setOnAction(event -> {
            Control.addgroup.show();
        });
    }

    public void clear()
    {
        fListVector.clear();
        gListVector.clear();
        friendList.getItems().clear();
        group.getItems().clear();
    }

    // get method for private members
    public Vector<Friendlist> getFriendVector()
    {
        return fListVector;
    }
    public ListView getFriendlist()
    {
        return friendList;
    }
    public ListView getGroup()
    {
        return group;
    }

    // set personal information
    public void setPersonInfo(String Name, String Sign)
    {
        ((Label) search("Name")).setText(Name);
        ((Label) search("Signature")).setText(Sign);
    }
    // set head for friend
    public static void setHead(Button btn, int i)
    {
        if(i==0)
            btn.setStyle("-fx-background-image: url('View/Fxml/CSS/Pic/Chat/SysHead1.png')");
        else
            btn.setStyle("-fx-background-image: url('View/Fxml/CSS/Pic/Chat/head1.png')");
    }

    // add friend to window
    public int addFriend(FriendList fl, ChattingRecord chattingRecord)
    {
        fListVector.clear();
        friendList.getItems().clear();

        for (int i=0; i<fl.groupNum; ++i)
        {
            GroupList gl = fl.Groups[i];
            for(int j=0; j<gl.FriendNum; ++j)
            {
                String rk = gl.FriendRemarks[j];
                AccountInfo a = gl.FriendAccount[j];
                String sign = "Hello!";

                if(a == null && i>0)
                {
                    return -1;
                }
                else
                {
                    if(i==0)
                    {
                        a = new AccountInfo();
                        a.id = 0;
                        a.NickName = "tsugu~";
                        a.Sex = "2";
                        a.Birthday = "1111-1-1";
                        a.Mail = "1111@111";
                        sign = "tsugu~ってる～";
                    }
                    String showingMes = "";
                    boolean hasRead = true;
                    int num = 0;

                    for(int p=0; p<chattingRecord.FriendNumber; ++p)
                    {
                       if(chattingRecord.ChatRecord.get(p).FriendID == a.id)
                       {
                           int size = chattingRecord.ChatRecord.get(p).ChatRecordwithOne.size();
                           if(size>0)
                           {
                               num = chattingRecord.ChatRecord.get(p).MessageNumberwithOne;
                               showingMes = chattingRecord.ChatRecord.get(p).ChatRecordwithOne.get(size-1).MessageText;
                               if(chattingRecord.ChatRecord.get(p).ChatRecordwithOne.get(size-1).sender==a.id)
                                   hasRead = chattingRecord.ChatRecord.get(p).ChatRecordwithOne.get(size-1).hasread;
                           }
                       }
                    }

                    try
                    {
                        addFriend(a, rk, sign, Control.friendpage, showingMes, hasRead, num);
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        return 1;
    }
    public void addFriend(AccountInfo acc, String Remark, String Sign, FriendPage friendPage,
                          String showMes, boolean hasRead, int num) throws  IOException
    {
        Friendlist fl = new Friendlist(acc, Remark, Sign, this);
        if(!showMes.equals(""))
        {
            fl.Signature.setText(showMes);
            if(hasRead==false)
                fl.addMsgTip(num);
        }
        if(hasRead)
        {
            fListVector.add(fl);
            int index = fListVector.size()-1;
            friendList.getItems().add(fListVector.get(index).getPane());
        }
        else
        {
            fListVector.add(0, fl);
            friendList.getItems().add(0, fListVector.get(0).getPane());
        }
    }
    // add group to window
    public int addGroup(FriendList fl)
    {
        gListVector.clear();
        group.getItems().clear();
        fdVector.clear();
        for(int i=0;i<fl.groupNum;++i)
        {
            //add group
            GroupList gl = fl.Groups[i];
            try
            {
                gListVector.add(new FriendGroup(gl.GroupName, ""+gl.FriendNum));
            }catch(IOException e)
            {
                e.printStackTrace();
            }

            group.getItems().add(gListVector.get(gListVector.size()-1).getPane());

            // add group members
            for(int j=0;j<gl.FriendNum;++j)
            {
                String rk = gl.FriendRemarks[j];
                String Sign = "Hello!";
                AccountInfo a = gl.FriendAccount[j];

                if(a == null && i>0)
                {
                    return -1;
                }
                else
                {
                    if(i==0)
                    {
                        a = new AccountInfo();
                        a.id = 0;
                        a.NickName = "tsugu~";
                        a.Sex = "2";
                        a.Birthday = "";
                        a.Mail = "";
                        Sign = "tsugu~ってる～";
                    }

                    try
                    {
                        fdVector.add(new Friendlist(a, rk, Sign, this));
                        group.getItems().add(fdVector.get(fdVector.size()-1).getPane());
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        return 1;
    }

    /*
     * 从server获取好友的账号信息，用于构建好友主页
     * 这个方法在addFriend和addGroup都要调用
     * 由于好友应该都是有数据的（因为不涉及注销的问题），所以只要判断是否连接成功
     * @param id 欲查找资料的id
     * @return 成功返回AccountInfo结构，失败返回null
     *
    public AccountInfo GET_FRIEND_INFO_FROM_SERVER(int id)
    {
        ChatBean info = new ChatBean();
        info.type = TypeValue.REQ_GET_INFO;
        info.ID = id;
        try
        {
            ChatBean back = Control.network.request(info);
            switch (back.type)
            {
                case REPLY_OK:return back.accountInfo;
                case REPLY_BAD_ID:AccountInfo bad = new AccountInfo();bad.id = -1;return bad;
                case REPLY_SERVER_ERROR: AccountInfo bad1 = new AccountInfo(); bad1.id = 0; return bad1;
                default: return null;
            }
        }catch (Exception e)
        {
            return null;
        }
    }
    */
}
