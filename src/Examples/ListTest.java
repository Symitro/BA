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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ListTest extends JFrame implements ListSelectionListener {
  JLabel label = new JLabel("The fox jumps over the lazy dog.");

  public ListTest() {
    setTitle("ListTest: Press control to multi-select");
    setSize(400, 300);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    String[] words = { "quick", "brown", "hungry", "wild", "silent", "huge" };

    JList wordList = new JList(words);
    JScrollPane scrollPane = new JScrollPane(wordList);

    JPanel p = new JPanel();
    p.add(scrollPane);
    wordList.addListSelectionListener(this);

    getContentPane().add(p, "South");
    getContentPane().add(label, "Center");
  }

  public void valueChanged(ListSelectionEvent evt) {
    JList source = (JList) evt.getSource();
    Object[] values = source.getSelectedValues();

    String text = "";
    for (int i = 0; i < values.length; i++) {
      String word = (String) values[i];
      text += word + " ";
    }
    label.setText("The " + text + "fox jumps over the lazy dog.");
  }

  public static void main(String[] args) {
    JFrame frame = new ListTest();
    frame.show();
  }
}