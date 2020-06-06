package View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.scene.control.*;

import java.io.IOException;

public class HomePage extends Window
{
    public Alert alert;
    public Alert1 alert1;
    public Loading loading;
    private ToggleGroup group;
    HomePage() throws IOException
    {
        //init scene
        root = FXMLLoader.load(getClass().getResource("Fxml/HomePage.fxml"));
        setTitle("HomePage");
        Move();
        initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(root, 600, 450);
        setScene(scene);
        //tool init
        loading = new Loading();
        loading.setModality(this);
        alert = new Alert();
        alert.setModality(this);
        alert1 = new Alert1();
        alert1.setModality(this);
        setNoAction();
        setClose();
        setEdit();
        setGroup();
    }

    //disable some components so that they won't be edited until the button is pressed
    public void setNoAction()
    {
        ((TextField) search("account")).setEditable(false);
        ((TextField) search("email")).setEditable(false);
        ((TextField) search("birth")).setEditable(false);
        ((TextField) search("origin")).setVisible(false);
        ((TextField) search("origin")).setManaged(false);
        ((PasswordField) search("password")).setVisible(false);
        ((TextField) search("password")).setManaged(false);
        ((PasswordField) search("affirm")).setVisible(false);
        ((TextField) search("affirm")).setManaged(false);

        ((RadioButton) search("boy")).setVisible(false);
        ((RadioButton) search("boy")).setManaged(false);
        ((RadioButton) search("girl")).setVisible(false);
        ((RadioButton) search("girl")).setManaged(false);
        ((Button) search("finish")).setManaged(false);
        ((Button) search("finish")).setVisible(false);
    }
    //Min and Close buttons
    public void setClose()
    {
        ((Button) search("Close")).setTooltip(new Tooltip("关闭"));
        ((Button) search("Min")).setTooltip(new Tooltip("最小化"));
        ((Button) search("Close")).setOnAction(event ->{
            this.close();
        });
        ((Button) search("Min")).setOnAction(event ->{
            setIconified(true);
        });
    }
    //edit button
    public void setEdit()
    {
        ((Button) search("edit")).setTooltip(new Tooltip("修改个人信息"));
        ((Button) search("finish")).setTooltip(new Tooltip("完成修改"));
        ((Button) search("edit")).setOnAction(event ->{
            ((TextField) search("account")).setEditable(true);
            ((TextField) search("email")).setEditable(true);
            ((TextField) search("birth")).setEditable(true);
            ((TextField) search("origin")).setVisible(true);
            ((TextField) search("origin")).setManaged(true);
            ((PasswordField) search("password")).setVisible(true);
            ((TextField) search("password")).setManaged(true);
            ((PasswordField) search("affirm")).setVisible(true);
            ((TextField) search("affirm")).setManaged(true);
            ((RadioButton) search("boy")).setVisible(true);
            ((RadioButton) search("boy")).setManaged(true);
            ((RadioButton) search("girl")).setVisible(true);
            ((RadioButton) search("girl")).setManaged(true);
            ((Button) search("finish")).setManaged(true);
            ((Button) search("finish")).setVisible(true);

            ((Label) search("sexb")).setManaged(false);
            ((Label) search("sexb")).setVisible(false);
            ((Label) search("sexg")).setManaged(false);
            ((Label) search("sexg")).setVisible(false);
        });
    }
    // boy and girl can only choose one 233
    public void setGroup()
    {
        group = new ToggleGroup();
        RadioButton boy = ((RadioButton) search("boy"));
        RadioButton girl = ((RadioButton) search("girl"));
        boy.setToggleGroup(group);
        girl.setToggleGroup(group);
    }
    // Modality is used for keep main window unchanged when homepage opens.
    public void setModality(Window window)
    {
        initModality(Modality.APPLICATION_MODAL);
        initOwner(window);
    }
}
