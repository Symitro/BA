/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Examples;

/**
 *
 * @author Julian
 */
import javax.swing.*;
import javax.swing.table.*;
import java.util.Comparator;

class TableSorting {
    public static void main(String[] args) {
        Object[][] data = {
            {new Integer(1), "Don't Let Go", new Integer(179)},
            {new Integer(2), "Photograph", new Integer(29)},
            {new Integer(3), "Hash Pipe", new Integer(186)},
            {new Integer(4), "Island In The Sun", new Integer(200)},
            {new Integer(5), "Crab", new Integer(154)},
            {new Integer(6), "Knock-Down Drag-Out", new Integer(128)},
            {new Integer(7), "Smile", new Integer(158)},
            {new Integer(8), "Simple Pages", new Integer(176)},
            {new Integer(9), "Glorious Day", new Integer(160)},
            {new Integer(10), "O Girlfriend", new Integer(230)}
        };
        Object[] columns = {"Track #", "Title", "Length"};
        DefaultTableModel model = new DefaultTableModel(data,columns) {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Integer.class;
                    case 1:
                        return String.class;
                    case 2:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        table.setAutoCreateRowSorter(true);
        JOptionPane.showMessageDialog(null, scroll);
    }
}