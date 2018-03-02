/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bachelorarbeit_regent;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 *
 * @author Julian
 */
public class BA_Gui {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame guiFrame = new JFrame();

        //make sure the program exits when the frame closes
        guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setTitle("Diagnoseapplikation");
        guiFrame.setSize(300, 250);

        //This will center the JFrame in the middle of the screen
        guiFrame.setLocationRelativeTo(null);

        //Options for the JComboBox 
        String[] fruitOptions = {"Apple", "Apricot", "Banana",
            "Cherry", "Date", "Kiwi", "Orange", "Pear", "Strawberry"};

        //Options for the JList
        String[] vegOptions = {"Asparagus", "Beans", "Broccoli", "Cabbage",
            "Carrot", "Celery", "Cucumber", "Leek", "Mushroom",
            "Pepper", "Radish", "Shallot", "Spinach", "Swede",
            "Turnip"};

        //The first JPanel contains a JLabel and JCombobox
        final JPanel comboPanel = new JPanel();
        JLabel comboLbl = new JLabel("Fruits:");
        JComboBox fruits = new JComboBox(fruitOptions);

        comboPanel.add(comboLbl);
        comboPanel.add(fruits);

        //Create the second JPanel. Add a JLabel and JList and
        //make use the JPanel is not visible.
        final JPanel listPanel = new JPanel();
        listPanel.setVisible(false);
        JLabel listLbl = new JLabel("Vegetables:");
        JList vegs = new JList(vegOptions);
        vegs.setLayoutOrientation(JList.HORIZONTAL_WRAP);

        listPanel.add(listLbl);
        listPanel.add(vegs);

        JButton vegFruitBut = new JButton("Fruit or Veg");

        //Creating the MenuBar and adding components
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("Datei");
        JMenu m2 = new JMenu("Einstellungen");
        JMenu m3 = new JMenu("Hilfe");
        mb.add(m1);
        mb.add(m2);
        mb.add(m3);
        JMenuItem m11 = new JMenuItem("Open");
        JMenuItem m22 = new JMenuItem("Save as");
        m1.add(m11);
        m1.add(m22);
        //The ActionListener class is used to handle the
        //event that happens when the user clicks the button.
        //As there is not a lot that needs to happen we can 
        //define an anonymous inner class to make the code simpler.
        vegFruitBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                //When the fruit of veg button is pressed
                //the setVisible value of the listPanel and
                //comboPanel is switched from true to 
                //value or vice versa.
                listPanel.setVisible(!listPanel.isVisible());
                comboPanel.setVisible(!comboPanel.isVisible());

            }
        });

        //The JFrame uses the BorderLayout layout manager.
        //Put the two JPanels and JButton in different areas.
        guiFrame.add(comboPanel, BorderLayout.NORTH);
        guiFrame.add(mb, BorderLayout.NORTH);
        guiFrame.add(listPanel, BorderLayout.CENTER);
        guiFrame.add(vegFruitBut, BorderLayout.SOUTH);

        //make sure the JFrame is visible
        guiFrame.setVisible(true);
    }
}
