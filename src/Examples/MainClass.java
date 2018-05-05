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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class MainClass {
  public static void main(String args[]) {
    JFrame frame = new JFrame("Editable Color Table");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    TableModel model = new ColorTableModel();
    JTable table = new JTable(model);

    TableColumn column = table.getColumnModel().getColumn(2);

    ComboTableCellRenderer renderer = new ComboTableCellRenderer();
    column.setCellRenderer(renderer);

    TableCellEditor editor = new ColorChooserEditor();
    column.setCellEditor(editor);

    JScrollPane scrollPane = new JScrollPane(table);
    frame.add(scrollPane, BorderLayout.CENTER);
    frame.setSize(400, 150);
    frame.setVisible(true);
  }
}

class ColorChooserEditor extends AbstractCellEditor implements TableCellEditor {

  private JButton delegate = new JButton();

  Color savedColor;

  public ColorChooserEditor() {
    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        Color color = JColorChooser.showDialog(delegate, "Color Chooser", savedColor);
        ColorChooserEditor.this.changeColor(color);
      }
    };
    delegate.addActionListener(actionListener);
  }

  public Object getCellEditorValue() {
    return savedColor;
  }

  private void changeColor(Color color) {
    if (color != null) {
      savedColor = color;
    }
  }

  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
      int row, int column) {
    changeColor((Color) value);
    return delegate;
  }
}

class ComboTableCellRenderer implements TableCellRenderer {
  DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();

  DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();

  private void configureRenderer(JLabel renderer, Object value) {
      renderer.setBackground((Color)value);
  }


  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {
    tableRenderer = (DefaultTableCellRenderer) tableRenderer.getTableCellRendererComponent(table,
        value, isSelected, hasFocus, row, column);
    configureRenderer(tableRenderer, value);
    return tableRenderer;
  }
}

class ColorTableModel extends AbstractTableModel {

  Object rowData[][] = { { "1", Boolean.TRUE, Color.RED },
      { "2", Boolean.TRUE, Color.BLUE },
      { "3", Boolean.FALSE, Color.GREEN },
      { "4", Boolean.TRUE, Color.MAGENTA },
      { "5", Boolean.FALSE, Color.PINK }, };

  String columnNames[] = { "Number", "Boolean", "Color" };

  public int getColumnCount() {
    return columnNames.length;
  }

  public String getColumnName(int column) {
    return columnNames[column];
  }

  public int getRowCount() {
    return rowData.length;
  }

  public Object getValueAt(int row, int column) {
    return rowData[row][column];
  }

  public Class getColumnClass(int column) {
    return (getValueAt(0, column).getClass());
  }

  public void setValueAt(Object value, int row, int column) {
    rowData[row][column] = value;
  }

  public boolean isCellEditable(int row, int column) {
    return (column != 0);
  }
}
