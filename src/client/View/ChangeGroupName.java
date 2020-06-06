package View;

import database.AccountInfo;
import database.FriendList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ChangeGroupName extends Window
{
    private int gid;
    private Loading loading;
    private Alert alert;
    private Alert1 alert1;
    ChangeGroupName(int gid) throws IOException
    {
        this.gid = gid;
        loading = new Loading();
        loading.setModality(this);
        alert = new Alert();
        alert.setModality(this);
        alert1 = new Alert1();
        alert1.setModality(this);

        root = FXMLLoader.load(getClass().getResource("Fxml/ChangeGroupName.fxml"));
        Scene scene = new Scene(root, 300, 200);
        initStyle(StageStyle.TRANSPARENT);
        setScene(scene);
        Move();
        setTitle("ChangeGroupName");

        setModality(Control.mainwindow);
        reject();
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

}
