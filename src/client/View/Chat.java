package View;

import chatbean.ChatBean;
import database.AccountInfo;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import message.OneMessage;

import java.io.*;
import java.io.IOException;

public class Chat extends Window
{
    public Alert1 alert1;
    public AccountInfo info;
    private ListView chatlist;
    private FileChooser picChooser;
    private FileChooser fileChooser;
    public EmojiList emjList;
    public Chat() throws IOException
    {
        //scene init
        root = FXMLLoader.load(getClass().getResource("Fxml/Chat.fxml"));
        Scene scene = new Scene(root, 600, 540);
        initStyle(StageStyle.TRANSPARENT);
        setScene(scene);
        Move();
        setTitle("Chatting");
        //setTool
        setClose();
        setTip();
        setModality(Control.mainwindow);
        chatlist = ((ListView) search("List"));
        ((TextArea) search("Input")).setWrapText(true);

        alert1 = new Alert1();
        alert1.setModality(this);

        info = new AccountInfo();
        info.id = -1;

        picChooser = new FileChooser();
        picChooser.setTitle("选择图片");
        picChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        picChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg")
        );
        SelectPic();
        fileChooser = new FileChooser();
        fileChooser.setTitle("选择文件");
        SelectFile();

        emjList = new EmojiList();
        SelectEmoji();
    }

    public ListView getList()
    {
        return chatlist;
    }
    //set tips for buttons
    public void setTip()
    {
        ((Button) search("Image")).setTooltip(new Tooltip("发送图片"));
        ((Button) search("Emoji")).setTooltip(new Tooltip("发送表情"));
        ((Button) search("File")).setTooltip(new Tooltip("发送文件"));
    }
    //set the close and min buttons
    public void setClose()
    {
        ((Button) search("Close")).setTooltip(new Tooltip("关闭"));
        ((Button) search("Close1")).setTooltip(new Tooltip("关闭"));
        ((Button) search("Min")).setTooltip(new Tooltip("最小化"));
        ((Button) search("Close")).setOnAction(event ->{
            ((ListView) search("List")).getItems().clear();
            ((TextArea) search("Input")).clear();
            this.close();
        });
        ((Button) search("Close1")).setOnAction(event ->{
            ((ListView) search("List")).getItems().clear();
            ((TextArea) search("Input")).clear();
            this.close();
        });
        ((Button) search("Min")).setOnAction(event ->{
            setIconified(true);
        });
    }
    // Modality is used for keep main window unchanged when alert occurs.
    public void setModality(Window window)
    {
        initModality(Modality.APPLICATION_MODAL);
        initOwner(window);
    }
    //add friend message
    public void addLeft(int hd, String Msg, int mid)
    {
        chatlist.getItems().add(new ChatList().setLeft(hd, Msg, bubbleTool.getWidth(Msg), bubbleTool.getHeight(Msg), mid));
    }
    public void addLeft1(ChatBean bean)
    {
        String msg = bean.accountInfo.NickName+"请求添加您为好友";
        ChatList cl = new ChatList();
        cl.send.setCursor(Cursor.HAND);
        cl.send.setOnAction(event -> {
            try
            {
                Control.applyAdd = new ApplyAdd(bean.accountInfo, bean.applyRemark);
            }catch (IOException e)
            {
                e.printStackTrace();
            }
            Control.applyAdd.gid2 = bean.groupID;
            Control.applyAdd.remark = bean.remark;
            Control.applyAdd.show();
        });
        chatlist.getItems().add(cl.setLeft(0, msg, bubbleTool.getWidth(msg), bubbleTool.getHeight(msg), -2));
    }
    //add my message
    public void addRight(int hd, String Msg, int mid)
    {
        chatlist.getItems().add(new ChatList().setRight(hd, Msg, bubbleTool.getWidth(Msg), bubbleTool.getHeight(Msg), mid));
    }
    public void SelectEmoji()
    {
        ((Button) search("Emoji")).setOnAction(event -> {
            emjList.show();
        });
    }
    public void SelectPic()
    {
        ((Button) search("Image")).setOnAction(event -> {
            File pic = picChooser.showOpenDialog(this);
            if(pic != null)
            {
                //System.out.println(pic.getAbsolutePath());
                for(int i=0; i<Control.chattingRecord.ChatRecord.size(); ++i)
                {
                    if(Control.chattingRecord.ChatRecord.get(i).FriendID==Control.chat.info.id)
                    {
                        OneMessage mes = new OneMessage();
                        mes.valid = true; mes.ID = -3; mes.MessageText = "发送了一个文件，请及时接收";mes.sender=Control.usrInfo.id;
                        Control.chattingRecord.ChatRecord.get(i).ChatRecordwithOne.add(mes);
                        Control.chattingRecord.ChatRecord.get(i).MessageNumberwithOne=Control.chattingRecord.ChatRecord.get(i).ChatRecordwithOne.size();
                    }
                }
                Control.chat.addRight(1, "您发送了一个文件。", -1);
                Control.network.sendFile(pic.getAbsolutePath(), info.id);
            }
        });
    }
    public void SelectFile()
    {
        ((Button) search("File")).setOnAction(event -> {
            File file = fileChooser.showOpenDialog(this);
            if(file != null)
            {
                //System.out.println(file.getAbsolutePath());
                for(int i=0; i<Control.chattingRecord.ChatRecord.size(); ++i)
                {
                    if(Control.chattingRecord.ChatRecord.get(i).FriendID==Control.chat.info.id)
                    {
                        OneMessage mes = new OneMessage();
                        mes.valid = true; mes.ID = -3; mes.MessageText = "发送了一个文件，请及时接收";mes.sender=Control.usrInfo.id;
                        Control.chattingRecord.ChatRecord.get(i).ChatRecordwithOne.add(mes);
                        Control.chattingRecord.ChatRecord.get(i).MessageNumberwithOne=Control.chattingRecord.ChatRecord.get(i).ChatRecordwithOne.size();
                    }
                }
                Control.chat.addRight(1, "您发送了一个文件。", -1);
                Control.network.sendFile(file.getAbsolutePath(), info.id);
            }
        });
    }
}
