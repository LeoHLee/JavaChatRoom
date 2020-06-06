package View;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.StageStyle;
import java.io.IOException;

public class Forget extends Window
{
    public Loading loading;
    public Alert alert;
    public Alert1 alert1;
    public int get = 0;
    public Forget() throws IOException
    {
        //scene init
        root = FXMLLoader.load(getClass().getResource("Fxml/Forget.fxml"));
        Scene scene = new Scene(root, 360, 480);
        initStyle(StageStyle.TRANSPARENT);
        setTitle("Get Back");
        Move();
        setScene(scene);
        ((Label) search("Icon")).requestFocus();//set initial focus
        loading = new Loading();
        loading.setModality(this);
        alert = new Alert();
        alert.setModality(this);
        alert1 = new Alert1();
        alert1.setModality(this);
    }
    // clear input when exit
    public void clear()
    {
        ((TextField) search("User")).clear();
        ((PasswordField) search("New")).clear();
        ((PasswordField) search("Affirm")).clear();
        ((PasswordField) search("Check")).clear();
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
        setErrTip("UserErr","");
        setErrTip("AffirmErr","");
        setErrTip("NewErr", "");
        setErrTip("CheckErr", "");
    }
}
