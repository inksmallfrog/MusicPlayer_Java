package Gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by inksmallfrog on 10/28/15.
 */
public class MusicTableRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if(table.getColumnName(column) == "button"){
            return (JButton)value;
        }

        setText(value.toString());
        setForeground(Color.white);
        if(row % 2 == 0){
            setBackground(new Color(93, 93, 93));
        }
        else{
            setBackground(new Color(70, 70, 70));
        }
        return this;
    }
}
