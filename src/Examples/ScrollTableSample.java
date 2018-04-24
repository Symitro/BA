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

import javax.swing.JFrame;
import javax.swing.JTable;

public class ScrollTableSample {
  public static void main(String args[]) {
    Object rows[][] = { { "one", "ichi - \u4E00" },
        { "two", "ni - \u4E8C" }, { "three", "san - \u4E09" },
        { "four", "shi - \u56DB" }, { "five", "go - \u4E94" },
        { "six", "roku - \u516D" }, { "seven", "shichi - \u4E03" },
        { "eight", "hachi - \u516B" }, { "nine", "kyu - \u4E5D" },
        { "ten", "ju - \u5341" } };
    Object headers[] = { "English", "Japanese" };
    JFrame frame = new JFrame("Scrollless Table");
    JTable table = new JTable(rows, headers);
    frame.getContentPane().add(table, BorderLayout.CENTER);
    frame.setSize(300, 150);
    frame.setVisible(true);
  }
}