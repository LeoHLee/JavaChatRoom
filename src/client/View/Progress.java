package View;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.StageStyle;
import java.io.IOException;

public class Progress extends Window
{
    public Progress() throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("Fxml/Progress.fxml"));
        Scene scene = new Scene(root, 400, 200);
        initStyle(StageStyle.TRANSPARENT);
        setTitle("Progress");
        setScene(scene);
        Move();
    }
}
