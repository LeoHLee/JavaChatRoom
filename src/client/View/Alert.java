package View;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import java.io.IOException;

public class Alert extends Window
{
    public Alert() throws IOException
    {
        //scene init
        root = FXMLLoader.load(getClass().getResource("Fxml/Alert.fxml"));
        Scene scene = new Scene(root, 400, 200);
        initStyle(StageStyle.TRANSPARENT);
        setTitle("Alert");
        setScene(scene);
        Move();

        ((Label) search("Info")).setWrapText(true);
    }
    // Used for set explanations of alert.
    public void setTip(String id, String text)
    {
        ((Label) search(id)).setText(text);
    }
    // Modality is used for keep main window unchanged when alert occurs.
    public void setModality(Window window)
    {
        initModality(Modality.APPLICATION_MODAL);
        initOwner(window);
    }
    // show alert window
    public void exec(Window window, String text)
    {
        //setModality(window);
        ((Label) search("Info")).setText(text);
        ((Button) search("Affirm")).setOnAction(event ->{
            close();
        });
        show();
    }
}
