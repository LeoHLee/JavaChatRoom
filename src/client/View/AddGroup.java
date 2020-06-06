package View;

import database.FriendList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.io.IOException;

public class AddGroup extends Window
{
    Loading loading;
     Alert alert;
     Alert1 alert1;
    public AddGroup() throws IOException
    {
        loading = new Loading();
        loading.setModality(this);
        alert = new Alert();
        alert.setModality(this);
        alert1 = new Alert1();
        alert1.setModality(this);

        root = FXMLLoader.load(getClass().getResource("Fxml/AddGroup.fxml"));
        Scene scene = new Scene(root, 300, 200);
        initStyle(StageStyle.TRANSPARENT);
        setScene(scene);
        Move();
        setTitle("ApplyAdd");

        reject();
        ((Pane) search("background")).requestFocus();
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
