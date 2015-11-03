import Gui.WebeasyMusicGui;
import MessageManager.MessageManager;
import Music.Music;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.skin.*;

import javax.swing.*;
import java.awt.*;
/**
 * Created by inksmallfrog on 10/26/15.
 */
public class MainGui {
    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            try{
                javax.swing.UIManager.setLookAndFeel(new SubstanceDustLookAndFeel());
            }
            catch (Exception e){
                e.printStackTrace();
            }

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
