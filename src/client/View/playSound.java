package View;


import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class playSound {
    Player player;
    public playSound(){

    }
    public void play()  {
        try
        {
            BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(new File("./qq.mp3")));
            player = new Player(buffer);
            player.play();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}