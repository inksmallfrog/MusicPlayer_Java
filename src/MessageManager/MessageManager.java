package MessageManager;

import Gui.WebeasyMusicGui;
import Player.Player;
import Music.PlayingVector;

import javax.swing.*;
import javax.swing.table.TableModel;

/**
 * Created by inksmallfrog on 10/29/15.
 */
public class MessageManager{
    private static MessageManager manager = new MessageManager();

    private WebeasyMusicGui mainGui;
    private Player player;
    private PlayingVector playingVector;

    private MessageManager(){}

    public static MessageManager getMessageManager(){
        return manager;
    }

    public static void setMainGui(WebeasyMusicGui gui){
        manager.mainGui = gui;
    }
    public static void setPlayer(Player player){
        manager.player = player;
    }
    public static void setPlayingVector(PlayingVector playingVector){
        manager.playingVector = playingVector;
    }

    public static WebeasyMusicGui getMainGui(){
        return manager.mainGui;
    }
}
