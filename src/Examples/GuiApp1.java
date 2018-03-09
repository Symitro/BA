/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Examples;

//Imports are listed in full to show what's being used
//could just import javax.swing.* and java.awt.* etc..
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class GuiApp1 extends javax.swing.JFrame {

    //Note: Typically the main method will be in a
    //separate class. As this is a simple one class
    //example it's all in the one class.
    public static void main(String[] args) {

        new GuiApp1();
    }

    public GuiApp1() {
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
        String[] devices = {"Asparagus", "Beans", "Broccoli", "Cabbage",
            "Carrot", "Celery", "Cucumber", "Leek", "Mushroom",
            "Pepper", "Radish", "Shallot", "Spinach", "Swede",
            "Turnip"};

        //The first JPanel contains a JLabel and JCombobox
        final JPanel consolePanel = new JPanel();
        JLabel consoleLbl = new JLabel("Fruits:");
        JComboBox log = new JComboBox(fruitOptions);

        consolePanel.add(consoleLbl);
        consolePanel.add(log);

        //Create the second JPanel. Add a JLabel and JList and
        //make use the JPanel is not visible.
        final JPanel deviceListPanel = new JPanel();
        deviceListPanel.setVisible(true);
        final JPanel deviceSettingsPanel = new JPanel();
        deviceSettingsPanel.setVisible(true);
        JLabel listLbl = new JLabel("Vegetables:");
        JList vegs = new JList(devices);
        vegs.setLayoutOrientation(JList.HORIZONTAL_WRAP);

        deviceListPanel.add(listLbl);
        deviceListPanel.add(vegs);

        JButton refreshBut = new JButton("Aktualisieren");

        //Creating the MenuBar and adding components
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("Datei");
        JMenu m2 = new JMenu("Firmware");
        JMenu m3 = new JMenu("Hilfe");
        mb.add(m1);
        mb.add(m2);
        mb.add(m3);
        JMenuItem m11 = new JMenuItem("Config laden");
        JMenuItem m12 = new JMenuItem("Config speichern");
        m1.add(m11);
        m1.add(m12);
        JMenuItem m21 = new JMenuItem("Lokale Firmware laden");
        JMenuItem m22 = new JMenuItem("Online-Firmware laden");
        m2.add(m21);
        m2.add(m22);
        JMenuItem m31 = new JMenuItem("?");
        JMenuItem m32 = new JMenuItem("Über");
        m3.add(m31);
        m3.add(m32);
        //The ActionListener class is used to handle the
        //event that happens when the user clicks the button.
        //As there is not a lot that needs to happen we can 
        //define an anonymous inner class to make the code simpler.
        refreshBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                //When the fruit of veg button is pressed
                //the setVisible value of the listPanel and
                //comboPanel is switched from true to 
                //value or vice versa.
                deviceListPanel.setVisible(!deviceListPanel.isVisible());
                consolePanel.setVisible(!consolePanel.isVisible());

            }
        });

        //The JFrame uses the BorderLayout layout manager.
        //Put the two JPanels and JButton in different areas.
        guiFrame.add(consolePanel, BorderLayout.NORTH);
        guiFrame.add(mb, BorderLayout.NORTH);
        guiFrame.add(deviceListPanel, BorderLayout.WEST);
        guiFrame.add(deviceSettingsPanel, BorderLayout.EAST);
        guiFrame.add(refreshBut, BorderLayout.SOUTH);

        //make sure the JFrame is visible
        guiFrame.setVisible(true);
    }

}
