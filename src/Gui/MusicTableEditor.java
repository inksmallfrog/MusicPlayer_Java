package Gui;

import Music.PlayingVector;

import javax.swing.*;
import java.awt.*;


/**
 * Created by inksmallfrog on 10/28/15.
 */
public class MusicTableEditor extends DefaultCellEditor {
    PlayingVector music;

    public MusicTableEditor(PlayingVector _music){
        super(new JTextField());
        setClickCountToStart(1);

        music = _music;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
        JButton button = (JButton)value;
        button.addActionListener(e -> {
            MusicInfoEditGui gui = new MusicInfoEditGui(music.getAudioFile(row), row);
        });
        return button;
    }

    @Override
    public Object getCellEditorValue()
    {
        return "修改信息";
    }

}
