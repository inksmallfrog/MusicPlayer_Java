import Gui.WebeasyMusicGui;
import MessageManager.MessageManager;
import Music.Music;

import java.awt.*;

/**
 * Created by inksmallfrog on 10/26/15.
 */
public class MainGui {
    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            //main frame
            WebeasyMusicGui webeasyMusic = new WebeasyMusicGui();

            MessageManager.getMessageManager().setMainGui(webeasyMusic);

            //test
            Music music0 = new Music("./a.mp3");
            Music music1 = new Music("./c.mp3");
            webeasyMusic.AddMusic(music0);
            webeasyMusic.AddMusic(music1);
        });
    }
}
