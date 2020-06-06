package View;

import database.AccountInfo;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.io.*;
import java.io.IOException;

import com.uz.emojione.fx.*;

public class EmojiList extends Window
{
    public EmojiList() throws IOException
    {
        root = FXMLLoader.load(getClass().getResource("Fxml/EmojiList.fxml"));
        Scene scene = new Scene(root, 392, 300);
        setTitle("Emoji List");
        Move();
        setScene(scene);
    }
}
