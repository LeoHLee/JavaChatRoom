package View;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.StageStyle;
import java.io.IOException;

/*
 * Register Page
 */
public class Register extends Window
{
    public Loading loading;
    public Alert alert;
    public Alert1 alert1;
    //ensure we can only select onr from boy & girl
    private ToggleGroup group;
    Register() throws IOException
    {
        //scene init
        root = FXMLLoader.load(getClass().getResource("Fxml/Register.fxml"));
        Scene scene = new Scene(root, 360, 480);
        initStyle(StageStyle.TRANSPARENT);
        setTitle("Register");
        setScene(scene);
        Move();
        //tool init
        setGroup();
        (((Label) search("Icon"))).requestFocus();//set initial focus
        ((RadioButton) search("boy")).setSelected(true);
        loading = new Loading();
        loading.setModality(this);
        alert = new Alert();
        alert.setModality(this);
        alert1 = new Alert1();
        alert1.setModality(this);
    }
    //clear the input when exit
    public void clear()
    {
        ((TextField) search("UserName")).clear();
        ((TextField) search("Mail")).clear();
        ((PasswordField) search("Password")).clear();
        ((PasswordField) search("Affirm")).clear();
        ((DatePicker) search("Birthday")).setValue(null);
        RadioButton boy = ((RadioButton) search("boy")), girl = ((RadioButton) search("girl"));
        boy.setSelected(false);
        girl.setSelected(false);
        ((Label) search("Icon")).requestFocus();//set initial focus
        ClrErrTip();
    }
    //set Error Tip for input
    public void setErrTip(String id, String text)
    {
        ((Label) search(id)).setText(text);
    }
    //clear Error Tip
    public void ClrErrTip()
    {
        setErrTip("UserNameErr","");
        setErrTip("AffirmErr","");
        setErrTip("PasswordErr", "");
        setErrTip("MailErr","");
        setErrTip("DateErr", "");
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
}
