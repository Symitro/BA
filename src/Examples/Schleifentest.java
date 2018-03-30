package Examples;

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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Julian
 */
public class Schleifentest extends JFrame {

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
                new Schleifentest();
            }
        });
        System.out.println("Main durchlaufen");
    }

    /**
     * Konstruktor
     */
    public Schleifentest() {
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

    void sendeSerialPort(String nachricht) throws InterruptedException {
        System.out.println("Sende: " + nachricht);
        if (serialPortGeoeffnet != true) {
            return;
        }
        try {
            byte[] data = new byte[60];
            int num;
            num = inputStream.read(data, 0, data.length);
            String byteArrayToHex = byteArrayToHexString(data);
            System.out.println("Empfange: " + byteArrayToHex);
            if ("0f03".equals(byteArrayToHex.substring(0, 4)) || "0F03".equals(byteArrayToHex.substring(0, 4))) {

                byte[] sendstreamCUM4_1 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20};
                byte[] sendstreamCUM4_2 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x24};
                //            byte[] sendstreamCUM4_3 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x05};
                //            byte[] sendstreamCUM4_4 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01};
                //            byte[] sendstreamCUM4_5 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x43};
                //            byte[] sendstreamCUM4_6 = {(byte) 0x08, (byte) 0x03, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x06};
                //            byte[] sendstreamCUM4_7 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x01, (byte) 0x00, (byte) 0x78};
                //            byte[] sendstreamCUM4_8 = {(byte) 0x08, (byte) 0x03, (byte) 0x27, (byte) 0x17, (byte) 0x00, (byte) 0x04};
                //            byte[] sendstreamCUM4_9 = {(byte) 0x08, (byte) 0x03, (byte) 0x35, (byte) 0x05, (byte) 0x00, (byte) 0x01};
                //            byte[] sendstreamCUM4_10 = {(byte) 0x08, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x18};
                //            byte[] sendstreamCUM4_11 = {(byte) 0x08, (byte) 0x03, (byte) 0x42, (byte) 0x00, (byte) 0x00, (byte) 0x03};
                //            byte[] sendstreamCUM4_12 = {(byte) 0x08, (byte) 0x03, (byte) 0x45, (byte) 0x00, (byte) 0x00, (byte) 0x31};
                //            byte[] sendstreamCUM4_13 = {(byte) 0x08, (byte) 0x03, (byte) 0x60, (byte) 0x00, (byte) 0x00, (byte) 0x04};
                //            byte[] sendstreamCUM4_14 = {(byte) 0x08, (byte) 0x03, (byte) 0x60, (byte) 0x06, (byte) 0x00, (byte) 0x04};
                //            byte[] sendstreamCUM4_15 = {(byte) 0x08, (byte) 0x03, (byte) 0x61, (byte) 0x00, (byte) 0x00, (byte) 0x04};
                //            byte[] sendstreamCUM4_16 = {(byte) 0x08, (byte) 0x03, (byte) 0x62, (byte) 0x00, (byte) 0x00, (byte) 0x52};
                //            byte[] sendstreamCUM4_17 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x00, (byte) 0x00, (byte) 0x68};

                byte[][] sendarrays = {sendstreamCUM4_1, sendstreamCUM4_2};
                CRC16 crc = new CRC16();
//                crc.update(sendarrays[0][1], 0, sendarrays.length
//                );
                crc.getAll();
                String crcmsg = byteArrayToHexString(crc.getAll());
                System.out.println(crcmsg);
                outputStream.write(crc.getAll());
                Thread.sleep(3);

                outputStream.write(sendstreamCUM4_1);
                System.out.println("Hex gesendet");
                System.out.println(Arrays.toString(sendstreamCUM4_1));
                System.out.println(sendstreamCUM4_1);
            }
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
                System.out.println("Empfange: " + byteArrayToHex);

//                if ("0f03".equals(byteArrayToHex.substring(0, 4)) || "0F03".equals(byteArrayToHex.substring(0, 4))) {
//                    // Falls Antwort, Abfragen auslösen mit Device + CRC, gesplittet auf HR-Abfolgen
//                    System.out.println("byteArrayToHex = 0F03");
//                    System.out.println("PC-Bridge");
//                    Thread.sleep(3);
//                    try {
//                        byte[] sendstream = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20};
//                        CRC16 crc = new CRC16();
//                        crc.update(sendstream, 0, sendstream.length);
//                        crc.getAll();
//                        String crcmsg = byteArrayToHexString(crc.getAll());
//                        System.out.println(crcmsg);
//                        outputStream.write(crc.getAll());
//                        Thread.sleep(3);
//
////                        byte[] sendstream2 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x24};
////                        CRC16 crc2 = new CRC16();
////                        crc2.update(sendstream2, 0, sendstream2.length);
////                        crc2.getAll();
////                        String crcmsg2 = byteArrayToHexString(crc2.getAll());
////                        System.out.println(crcmsg2);
////                        outputStream.write(crc2.getAll());
////                        Thread.sleep(3);
////
////                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x05};
////                        CRC16 crc3 = new CRC16();
////                        crc3.update(sendstream3, 0, sendstream3.length);
////                        crc3.getAll();
////                        String crcmsg3 = byteArrayToHexString(crc3.getAll());
////                        System.out.println(crcmsg3);
////                        outputStream.write(crc3.getAll());
////                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01};
////                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x43};
////                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x06};
////                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x01, (byte) 0x00, (byte) 0x78};
////                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x27, (byte) 0x17, (byte) 0x00, (byte) 0x04};
////                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x35, (byte) 0x05, (byte) 0x00, (byte) 0x01};
////                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x18};
////                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x42, (byte) 0x00, (byte) 0x00, (byte) 0x03};
////                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x45, (byte) 0x00, (byte) 0x00, (byte) 0x31};
////                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x60, (byte) 0x00, (byte) 0x00, (byte) 0x04};
////                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x60, (byte) 0x06, (byte) 0x00, (byte) 0x04};
////                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x61, (byte) 0x00, (byte) 0x00, (byte) 0x04};
////                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x62, (byte) 0x00, (byte) 0x00, (byte) 0x52};
////                        byte[] sendstream3 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x00, (byte) 0x00, (byte) 0x68};
//                        System.out.println("0F03 senden erfolgreich");
//
//                    } catch (IOException e) {
//                        System.out.println("0F03 senden nicht möglich");
//                    }
//                }
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
                    System.out.println("Panel left aktiv");
                    System.out.println("Panel left aktiv");
                    System.out.println("Panel left aktiv");
                    System.out.println("Panel left aktiv");
                    System.out.println("Panel left aktiv");
                    System.out.println("Panel left aktiv");
                }
                if ("120302".equals(byteArrayToHex.substring(0, 6))) {
                    System.out.println("byteArrayToHex = 120302");
                    System.out.println("Panel right aktiv");
                    System.out.println("Panel right aktiv");
                    System.out.println("Panel right aktiv");
                    System.out.println("Panel right aktiv");
                    System.out.println("Panel right aktiv");
                    System.out.println("Panel right aktiv");
                }
                if ("140302".equals(byteArrayToHex.substring(0, 6))) {
                    System.out.println("byteArrayToHex = 140302");
                    System.out.println("Connected Lighting aktiv");
                    System.out.println("Connected Lighting aktiv");
                    System.out.println("Connected Lighting aktiv");
                    System.out.println("Connected Lighting aktiv");
                    System.out.println("Connected Lighting aktiv");
                }
                if ("150302".equals(byteArrayToHex.substring(0, 6))) {
                    System.out.println("byteArrayToHex = 150302");
                    System.out.println("Connected Lighting aktiv");
                    System.out.println("Connected Lighting aktiv");
                    System.out.println("Connected Lighting aktiv");
                    System.out.println("Connected Lighting aktiv");
                    System.out.println("Connected Lighting aktiv");

                }
                if ("170302".equals(byteArrayToHex.substring(0, 6))) {
                    System.out.println("byteArrayToHex = 170302");
                    System.out.println("Senslight Head 1 aktiv");
                    System.out.println("Senslight Head 1 aktiv");
                    System.out.println("Senslight Head 1 aktiv");
                    System.out.println("Senslight Head 1 aktiv");
                    System.out.println("Senslight Head 1 aktiv");
                }
                if ("180302".equals(byteArrayToHex.substring(0, 6))) {
                    System.out.println("byteArrayToHex = 180302");
                    System.out.println("Senslight Head 2 aktiv");
                    System.out.println("Senslight Head 2 aktiv");
                    System.out.println("Senslight Head 2 aktiv");
                    System.out.println("Senslight Head 2 aktiv");
                    System.out.println("Senslight Head 2 aktiv");
                    System.out.println("Senslight Head 2 aktiv");
                }
                if ("190302".equals(byteArrayToHex.substring(0, 6))) {
                    System.out.println("byteArrayToHex = 190302");
                    System.out.println("Senslight Head 3 aktiv");
                    System.out.println("Senslight Head 3 aktiv");
                    System.out.println("Senslight Head 3 aktiv");
                    System.out.println("Senslight Head 3 aktiv");
                    System.out.println("Senslight Head 3 aktiv");
                    System.out.println("Senslight Head 3 aktiv");
                }
                if ("200302".equals(byteArrayToHex.substring(0, 6))) {
                    System.out.println("byteArrayToHex = 200302");
                    System.out.println("Senslight Head 4 aktiv");
                    System.out.println("Senslight Head 4 aktiv");
                    System.out.println("Senslight Head 4 aktiv");
                    System.out.println("Senslight Head 4 aktiv");
                    System.out.println("Senslight Head 4 aktiv");
                    System.out.println("Senslight Head 4 aktiv");
                    System.out.println("Senslight Head 4 aktiv");
                }

                empfangen.append(byteArrayToHex + "\n");
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
