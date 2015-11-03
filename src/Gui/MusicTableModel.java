package Gui;

import Music.Music;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

/**
 * Created by inksmallfrog on 10/28/15.
 */
public class MusicTableModel extends DefaultTableModel {
    @Override
    public boolean isCellEditable(int row, int column){
        if(columnIdentifiers.get(column) == "button"){
            return true;
        }
        return false;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnIdentifiers.get(columnIndex).toString();
    }

    @Override
    public int getColumnCount(){
        return columnIdentifiers.size();
    }

    @Override
    public int getRowCount(){
        return dataVector.size();
    }

    @Override
    public void setColumnIdentifiers(Object[] identifiers){
        columnIdentifiers = new Vector();
        for(int i = 0; i < identifiers.length; ++i){
            columnIdentifiers.add(identifiers[i]);
        }
    }

    @Override
    public void setDataVector(Vector _dataVector, Vector _columnIdentifiers){
        dataVector = _dataVector;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex < Music.getItemDescribtionLength()) {
            return ((Music) dataVector.get(rowIndex)).getDescribtion(columnIndex);
        }
        else{
            return new JButton("修改信息");
        }
    }

    public void insertRow(Music music, int row){
        dataVector.insertElementAt(music, row);
        fireTableRowsInserted(row, row);
    }

    public void addRow(Music music){
        insertRow(music, dataVector.size());
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        ((Music)dataVector.get(row)).AnalyzeMusicOffLine();
        fireTableRowsUpdated(row, row + 1);
        fireTableDataChanged();
        fireTableCellUpdated(row, 1);
        return;
    }
}
