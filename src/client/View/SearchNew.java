package View;

import database.AccountInfo;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Vector;

public class SearchNew extends Window
{
    private Vector<searchList> items;
    private ListView friendList;
    public Alert1 alert1;
    public Loading loading;
    SearchNew() throws IOException
    {
        //scene init
        root = FXMLLoader.load(getClass().getResource("Fxml/SearchNew.fxml"));
        Scene scene = new Scene(root, 600, 400);
        initStyle(StageStyle.TRANSPARENT);
        setTitle("Search Friends");
        setScene(scene);
        Move();
        //tool init
        setClose();
        items = new Vector<>();
        friendList = ((ListView)search("Result"));
        alert1 = new Alert1();
        alert1.setModality(this);
        loading = new Loading();
        loading.setModality(this);
    }

    // Modality is used for keep main window unchanged when alert occurs.
    public void setModality(Window window)
    {
        initModality(Modality.APPLICATION_MODAL);
        initOwner(window);
    }

    public void clear()
    {
        items.clear();
        friendList.getItems().clear();
        ((TextField) search("Input")).clear();
    }

    public void setClose()
    {
        ((Button) search("Close")).setTooltip(new Tooltip("关闭"));
        ((Button) search("Close")).setOnAction(event -> {
            this.close();
            this.clear();
        });
    }

    public ListView getFriendList()
    {
        return  friendList;
    }

    public void add(AccountInfo user) throws IOException
    {
        items.add(new searchList(user.id, user.id, user.NickName));
        friendList.getItems().add(items.get(items.size()-1).getPane());
    }
}
