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
//import gnu.io.*;
import javax.comm.*;
import java.util.Enumeration;
import java.io.*;
import java.util.TooManyListenersException;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO Dialog zur Konfiguration der Schnittstellenparameter
public class OeffnenUndSenden extends JFrame {

    /**
     * Variable declaration
     */
    CommPortIdentifier serialPortId;
    Enumeration enumComm;
    SerialPort serialPort;
    OutputStream outputStream;
    InputStream inputStream;
    Boolean serialPortGeoeffnet = false;

    int baudrate = 115200;
    int dataBits = SerialPort.DATABITS_8;
    int stopBits = SerialPort.STOPBITS_2;
    int parity = SerialPort.PARITY_NONE;

    /**
     * Fenster
     */
    JPanel panel = new JPanel(new GridBagLayout());

    JPanel panelSetup = new JPanel(new GridBagLayout());
    JPanel panelKommuniziere = new JPanel(new GridBagLayout());

    JComboBox auswahl = new JComboBox();
    JButton oeffnen = new JButton("Öffnen");
    JButton schliessen = new JButton("Schließen");
    JButton aktualisieren = new JButton("Aktualisieren");

    JButton senden = new JButton("Nachricht senden");
    JTextField nachricht = new JTextField();
    JCheckBox echo = new JCheckBox("Echo");

    JTextArea empfangen = new JTextArea();
    JScrollPane empfangenJScrollPane = new JScrollPane();

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Programm gestartet");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new OeffnenUndSenden();
            }
        });
        System.out.println("Main durchlaufen");
    }

    /**
     * Konstruktor
     */
    public OeffnenUndSenden() {
        System.out.println("Konstruktor aufgerufen");
        initComponents();
    }

    protected void finalize() {
        System.out.println("Destruktor aufgerufen");
    }

    void initComponents() {
        GridBagConstraints constraints = new GridBagConstraints();

        setTitle("Öffnen und Senden");
        addWindowListener(new WindowListener());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // TODO schliessen.setEnabled(false);
        // TODO senden.setEnabled(false);
        oeffnen.addActionListener(new oeffnenActionListener());
        schliessen.addActionListener(new schliessenActionListener());
        aktualisieren.addActionListener(new aktualisierenActionListener());
        senden.addActionListener(new sendenActionListener());

        empfangenJScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        empfangenJScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        empfangenJScrollPane.setViewportView(empfangen);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;
        constraints.insets = new Insets(5, 5, 5, 5);
        panelSetup.add(auswahl, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0;
        panelSetup.add(oeffnen, constraints);

        constraints.gridx = 2;
        panelSetup.add(schliessen, constraints);

        constraints.gridx = 3;
        panelSetup.add(aktualisieren, constraints);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        panel.add(panelSetup, constraints);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0;
        panelKommuniziere.add(senden, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        panelKommuniziere.add(nachricht, constraints);

        constraints.gridx = 2;
        constraints.weightx = 0;
        panelKommuniziere.add(echo, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        panel.add(panelKommuniziere, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        panel.add(empfangenJScrollPane, constraints);

        aktualisiereSerialPort();

        add(panel);
        pack();
        setSize(600, 300);
        setVisible(true);

        System.out.println("Fenster erzeugt");
    }

    boolean oeffneSerialPort(String portName) {
        Boolean foundPort = false;
        if (serialPortGeoeffnet != false) {
            System.out.println("Serialport bereits geöffnet");
            return false;
        }
        System.out.println("Öffne Serialport");
        enumComm = CommPortIdentifier.getPortIdentifiers();
        while (enumComm.hasMoreElements()) {
            serialPortId = (CommPortIdentifier) enumComm.nextElement();
            if (portName.contentEquals(serialPortId.getName())) {
                foundPort = true;
                break;
            }
        }
        if (foundPort != true) {
            System.out.println("Serialport nicht gefunden: " + portName);
            return false;
        }
        try {
            serialPort = (SerialPort) serialPortId.open("Öffnen und Senden", 500);
        } catch (PortInUseException e) {
            System.out.println("Port belegt");
        }
        try {
            outputStream = serialPort.getOutputStream();
        } catch (IOException e) {
            System.out.println("Keinen Zugriff auf OutputStream");
        }
        try {
            inputStream = serialPort.getInputStream();
        } catch (IOException e) {
            System.out.println("Keinen Zugriff auf InputStream");
        }
        try {
            serialPort.addEventListener(new serialPortEventListener());
        } catch (TooManyListenersException e) {
            System.out.println("TooManyListenersException für Serialport");
        }
        serialPort.notifyOnDataAvailable(true);
        try {
            serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parity);
        } catch (UnsupportedCommOperationException e) {
            System.out.println("Konnte Schnittstellen-Paramter nicht setzen");
        }

        serialPortGeoeffnet = true;
        return true;
    }

    void schliesseSerialPort() {
        if (serialPortGeoeffnet == true) {
            System.out.println("Schließe Serialport");
            serialPort.close();
            serialPortGeoeffnet = false;
        } else {
            System.out.println("Serialport bereits geschlossen");
        }
    }

    void aktualisiereSerialPort() {
        System.out.println("Akutalisiere Serialport-Liste");
        if (serialPortGeoeffnet != false) {
            System.out.println("Serialport ist geöffnet");
            return;
        }
        auswahl.removeAllItems();
        enumComm = CommPortIdentifier.getPortIdentifiers();
        while (enumComm.hasMoreElements()) {
            serialPortId = (CommPortIdentifier) enumComm.nextElement();
            if (serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                System.out.println("Found:" + serialPortId.getName());
                auswahl.addItem(serialPortId.getName());
            }
        }
    }

    void sendeSerialPort(String nachricht) {
        System.out.println("Sende: " + nachricht);
        if (serialPortGeoeffnet != true) {
            return;
        }
        try {
            // byte[] hexToByteArray = hexStringToByteArray();
            byte[] Sendstream = {(byte) 0x08, (byte) 0x03, (byte) 0x27, (byte) 0x18, (byte) 0x00, (byte) 0x01, (byte) 0x87, (byte) 0x66};

            // CRC32 crc32 = new CRC32();
            // crc32.update(hexToByteArray);
            // outputStream.write(Sendstream, 0, 8);
            outputStream.write(Sendstream);
            System.out.println("Hex gesendet");
            System.out.println(Arrays.toString(Sendstream));
            System.out.println(Sendstream);

        } catch (IOException e) {
            System.out.println("Fehler beim Senden");
        }
    }

    void serialPortDatenVerfuegbar() throws InterruptedException {
        try {
            byte[] data = new byte[60];
            int num;
            while (inputStream.available() > 0) {
                num = inputStream.read(data, 0, data.length);
                String byteArrayToHex = byteArrayToHexString(data);
                System.out.println("Empfange: " + byteArrayToHex);      //System.out.println("Empfange: " + new String(data, 0, num));

                if ("0f03".equals(byteArrayToHex.substring(0, 4)) || "0F03".equals(byteArrayToHex.substring(0, 4))) {
                    // Falls Antwort, Abfragen auslösen mit Device + CRC, gesplittet auf HR-Abfolgen
                    System.out.println("byteArrayToHex = 0F03");
                    System.out.println("PC-Bridge");
                    Thread.sleep(5);
                    try {
                        byte[] sendstream = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x32};
                        String sendstreammsg = byteArrayToHexString(sendstream);
                        System.out.println(sendstreammsg);
                        CRC16 crc = new CRC16();
                        crc.update(sendstream, 0, sendstream.length);
                        crc.getAll();
                        String crcmsg = byteArrayToHexString(crc.getAll());
                        System.out.println(crcmsg);
                        outputStream.write(crc.getAll());
                        Thread.sleep(5);

                        byte[] sendstream2 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x36};
                        String sendstreammsg2 = byteArrayToHexString(sendstream2);
                        System.out.println(sendstreammsg2);
                        CRC16 crc2 = new CRC16();
                        crc2.update(sendstream2, 0, sendstream2.length);
                        crc2.getAll();
                        String crcmsg2 = byteArrayToHexString(crc2.getAll());
                        System.out.println(crcmsg2);
                        outputStream.write(crc2.getAll());
                        Thread.sleep(5);

                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x05};
                        String sendstreammsg3 = byteArrayToHexString(sendstream3);
                        System.out.println(sendstreammsg3);
                        CRC16 crc3 = new CRC16();
                        crc3.update(sendstream3, 0, sendstream3.length);
                        crc3.getAll();
                        String crcmsg3 = byteArrayToHexString(crc3.getAll());
                        System.out.println(crcmsg3);
                        outputStream.write(crc3.getAll());

//                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01};
//                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x43};

                        System.out.println("0F03 senden erfolgreich");

                    } catch (IOException e) {
                        System.out.println("0F03 senden nicht möglich");
                    }
                }
                if ("100302".equals(byteArrayToHex.substring(0, 6))) {
                    System.out.println("byteArrayToHex = 100302");
                    System.out.println("Alone at Work 2.0");
                    System.out.println("Alone at Work 2.0");
                    System.out.println("Alone at Work 2.0");
                    System.out.println("Alone at Work 2.0");
                    System.out.println("Alone at Work 2.0");
                    System.out.println("Alone at Work 2.0");
                    System.out.println("Alone at Work 2.0");
                    System.out.println("Alone at Work 2.0");
                }
                if ("110302".equals(byteArrayToHex.substring(0, 6))) {
                    System.out.println("byteArrayToHex = 110302");
                    System.out.println("Panel left");
                    System.out.println("Panel left");
                    System.out.println("Panel left");
                    System.out.println("Panel left");
                    System.out.println("Panel left");
                    System.out.println("Panel left");
                    System.out.println("Panel left");
                    System.out.println("Panel left");
                }
                if ("120302".equals(byteArrayToHex.substring(0, 6))) {
                    System.out.println("byteArrayToHex = 120302");
                    System.out.println("Panel right");
                    System.out.println("Panel right");
                    System.out.println("Panel right");
                    System.out.println("Panel right");
                    System.out.println("Panel right");
                    System.out.println("Panel right");
                }
                if ("140302".equals(byteArrayToHex.substring(0, 6))) {
                    System.out.println("byteArrayToHex = 140302");
                    System.out.println("Connected Lighting");
                    System.out.println("Connected Lighting");
                    System.out.println("Connected Lighting");
                    System.out.println("Connected Lighting");
                    System.out.println("Connected Lighting");
                }
                if ("150302".equals(byteArrayToHex.substring(0, 6))) {
                    System.out.println("byteArrayToHex = 150302");
                    System.out.println("Connected Lighting");
                    System.out.println("Connected Lighting");
                    System.out.println("Connected Lighting");
                    System.out.println("Connected Lighting");
                    System.out.println("Connected Lighting");
                    System.out.println("Connected Lighting");
                }
                if ("170302".equals(byteArrayToHex.substring(0, 6))) {
                    System.out.println("byteArrayToHex = 170302");
                    System.out.println("Senslight Head 1");
                    System.out.println("Senslight Head 1");
                    System.out.println("Senslight Head 1");
                    System.out.println("Senslight Head 1");
                    System.out.println("Senslight Head 1");
                    System.out.println("Senslight Head 1");
                }
                if ("180302".equals(byteArrayToHex.substring(0, 6))) {
                    System.out.println("byteArrayToHex = 180302");
                    System.out.println("Senslight Head 2");
                    System.out.println("Senslight Head 2");
                    System.out.println("Senslight Head 2");
                    System.out.println("Senslight Head 2");
                    System.out.println("Senslight Head 2");
                    System.out.println("Senslight Head 2");
                    System.out.println("Senslight Head 2");
                    System.out.println("Senslight Head 2");
                }
                if ("190302".equals(byteArrayToHex.substring(0, 6))) {
                    System.out.println("byteArrayToHex = 190302");
                    System.out.println("Senslight Head 3");
                    System.out.println("Senslight Head 3");
                    System.out.println("Senslight Head 3");
                    System.out.println("Senslight Head 3");
                    System.out.println("Senslight Head 3");
                    System.out.println("Senslight Head 3");
                    System.out.println("Senslight Head 3");
                }
                if ("200302".equals(byteArrayToHex.substring(0, 6))) {
                    System.out.println("byteArrayToHex = 200302");
                    System.out.println("Senslight Head 4");
                    System.out.println("Senslight Head 4");
                    System.out.println("Senslight Head 4");
                    System.out.println("Senslight Head 4");
                    System.out.println("Senslight Head 4");
                    System.out.println("Senslight Head 4");
                }

                empfangen.append(byteArrayToHex);
            }

        } catch (IOException e) {
            System.out.println("Fehler beim Lesen empfangener Daten");
        }
    }

    public static String byteArrayToHexString(byte[] byteArray) {
        String hexString = "";

        for (int i = 0; i < byteArray.length; i++) {
            String thisByte = String.format("%02x", byteArray[i]);

            hexString += thisByte;
        }

        return hexString;
    }

    public static byte[] hexStringToByteArray(String hexString) {
        byte[] bytes = new byte[hexString.length() / 2];

        for (int i = 0; i < hexString.length(); i += 2) {
            String sub = hexString.substring(i, i + 2);
            Integer intVal = Integer.parseInt(sub, 16);
            bytes[i / 2] = intVal.byteValue();
            String hex = "".format("%02x", bytes[i / 2]);
            System.out.println(hex);
        }
        System.out.println(hexString);

        return bytes;

    }

    class WindowListener extends WindowAdapter {

        public void windowClosing(WindowEvent event) {
            schliesseSerialPort();
            System.out.println("Fenster wird geschlossen");
        }
    }

    class oeffnenActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            System.out.println("oeffnenActionListener");
            // TODO sperre Button Öffnen und Aktualisieren
            // TODO entsperre Nachricht senden und Schließen
            oeffneSerialPort((String) auswahl.getSelectedItem());
        }
    }

    class schliessenActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            System.out.println("schliessenActionListener");
            // TODO entsperre Button Öffnen und Aktualisieren
            // TODO sperre Nachricht senden und Schließen
            schliesseSerialPort();
        }
    }

    class aktualisierenActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            System.out.println("aktualisierenActionListener");
            aktualisiereSerialPort();
        }
    }

    class sendenActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            System.out.println("sendenActionListener");
            if (echo.isSelected() == true) {
                empfangen.append(nachricht.getText() + "\n");
            }
            sendeSerialPort(nachricht.getText() + "\n");
        }
    }

    /**
     *
     */
    class serialPortEventListener implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            System.out.println("serialPortEventlistener");
            switch (event.getEventType()) {
                case SerialPortEvent.DATA_AVAILABLE: {
                    try {
                        serialPortDatenVerfuegbar();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(OeffnenUndSenden.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
                case SerialPortEvent.BI:
                case SerialPortEvent.CD:
                case SerialPortEvent.CTS:
                case SerialPortEvent.DSR:
                case SerialPortEvent.FE:
                case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                case SerialPortEvent.PE:
                case SerialPortEvent.RI:
                default:
            }
        }
    }
}
