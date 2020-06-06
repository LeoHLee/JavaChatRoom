package View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.scene.control.*;

import java.io.IOException;

public class FriendPage extends Window
{
    public Alert alert;
    public Alert1 alert1;
    public Loading loading;
    FriendPage() throws  IOException
    {
        //scene init
        root = FXMLLoader.load(getClass().getResource("Fxml/FriendPage.fxml"));
        setTitle("FriendPage");
        Move();
        initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(root, 400, 520);
        setScene(scene);
        //tool init
        setNoAction();
        setClose();
        setEdit();
        loading = new Loading();
        loading.setModality(this);
        alert = new Alert();
        alert.setModality(this);
        alert1 = new Alert1();
        alert1.setModality(this);
    }
    // Modality is used for keep main window unchanged when alert occurs.
    public void setModality(Window window)
    {
        initModality(Modality.APPLICATION_MODAL);
        initOwner(window);
    }
    //disable some components so that they won't be edited
    public void setNoAction()
    {
        ((TextField) search("username")).setEditable(false);
        ((TextField) search("mail")).setEditable(false);
        ((TextField) search("birthday")).setEditable(false);
        ((TextField) search("Signature")).setEditable(false);
        ((TextField) search("remark")).setEditable(false);
        ((TextField) search("sex")).setEditable(false);
        ((Button) search("finish")).setManaged(false);
        ((Button) search("finish")).setVisible(false);
    }
    //Close buttons
    public void setClose()
    {
        ((Button) search("Close")).setTooltip(new Tooltip("关闭"));
        ((Button) search("Close")).setOnAction(event ->{
            this.close();
        });
    }
    //edit button
    public void setEdit()
    {
        ((Button) search("edit")).setTooltip(new Tooltip("修改备注"));
        ((Button) search("finish")).setTooltip(new Tooltip("完成"));
        ((Button) search("edit")).setOnAction(event ->{
            ((TextField) search("remark")).setEditable(true);
            ((Button) search("finish")).setManaged(true);
            ((Button) search("finish")).setVisible(true);
        });
    }

}
