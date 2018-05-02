/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Examples;

import javax.swing.JFrame;

/**
 *
 * @author Julian
 */
public class GUITest extends JFrame {

    public static void main(String[] args) {
        Examples.Diagnoseapplikation gui = new Examples.Diagnoseapplikation();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Diagnoseapplikation().setVisible(true);
            }
        });
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(500, 500);
        jf.add(gui);
        jf.setVisible(true);
    }

}
