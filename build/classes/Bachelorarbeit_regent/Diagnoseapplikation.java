/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bachelorarbeit_regent;

/**
 *
 * @author Julian
 */
import Bachelorarbeit_regent.data.Datacollection;
import Bachelorarbeit_regent.data.Livedatacollection;
import Bachelorarbeit_regent.data.Livedataentry;
import Bachelorarbeit_regent.misc.ConversionHelper;
import Bachelorarbeit_regent.misc.CRC16;
import Bachelorarbeit_regent.misc.CSVReader;
import Bachelorarbeit_regent.misc.AddressList;
import javax.comm.*;
import java.util.Enumeration;
import java.io.*;
import java.util.TooManyListenersException;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultCaret;

//https://www.mikrocontroller.net/articles/Serielle_Schnittstelle_unter_Java
// TODO Dialog zur Konfiguration der Schnittstellenparameter
public class Diagnoseapplikation extends JFrame implements TableModelListener {

    /**
     * Variable declaration
     */
    public Datacollection CUM4Collection;
    public Datacollection CUCollection;
    public Datacollection AAWCollection;
    public Datacollection PLCollection;
    public Datacollection PRCollection;
    public Datacollection SH1Collection;
    public Datacollection SH2Collection;
    public Datacollection SH3Collection;
    public Datacollection SH4Collection;

    public Livedatacollection liveCUM4Collection = new Livedatacollection();
    public Livedatacollection liveCUCollection = new Livedatacollection();
    public Livedatacollection liveAAWCollection = new Livedatacollection();
    public Livedatacollection livePLCollection = new Livedatacollection();
    public Livedatacollection livePRCollection = new Livedatacollection();
    public Livedatacollection liveSH1Collection = new Livedatacollection();
    public Livedatacollection liveSH2Collection = new Livedatacollection();
    public Livedatacollection liveSH3Collection = new Livedatacollection();
    public Livedatacollection liveSH4Collection = new Livedatacollection();
    public Livedatacollection liveCLCollection = new Livedatacollection();
    public Livedatacollection livePCBCollection = new Livedatacollection();

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

    Boolean abfragen = false;
    Boolean schreiben = false;
    Boolean abfrageabgeschlossen = false;
    Boolean manualrequest = false;
    Boolean slaveStatus = false;
    Boolean errorRead = false;
    Boolean lastWasRequest = false;
    Boolean requestSuccess = true;
    byte changedDevice;
    int changedValue;
    String hexofchangedvalue;
    int selectedtab;
    ArrayList<String> messageListCache;

    // Datacollections werden in ein Array gefüllt
    Datacollection[] datacollectionArray;

    // Livedatacollections werden in ein Array gefüllt
    Livedatacollection[] livedatacollectionArray;

    public Device[] deviceArray;

    int actualDeviceCounter = 0;

//    HashMap<String, String> liveRequest = new HashMap<String, String>();
    /**
     * Fenster
     */
    JPanel panel = new JPanel(new GridBagLayout());
    JFileChooser fwPathChooser = new javax.swing.JFileChooser();
    JFileChooser fwFileChooser = new javax.swing.JFileChooser();

    JMenuBar jmenubar1 = new JMenuBar();
    JMenu jmenu1 = new JMenu();
    JMenu jmenu2 = new JMenu();
    JMenu jmenu3 = new JMenu();
    JMenuItem jmenuitem1 = new JMenuItem();
    JMenuItem jmenuitem2 = new JMenuItem();
    JMenuItem jmenuitem3 = new JMenuItem();
    JMenuItem jmenuitem4 = new JMenuItem();
    JMenuItem jmenuitem5 = new JMenuItem();
    JMenuItem jmenuitem6 = new JMenuItem();

    JPanel panelGeraeteDaten = new JPanel(new GridBagLayout());
    JPanel panelGeraeteListe = new JPanel(new GridBagLayout());
    JPanel panelGeraete = new JPanel(new GridBagLayout());
    JPanel panelSetup = new JPanel(new GridBagLayout());
    JPanel panelFirmware = new JPanel(new GridBagLayout());
    JPanel panelEmpfangen = new JPanel(new GridBagLayout());

    JComboBox auswahl = new JComboBox();
    JButton oeffnen = new JButton("Öffnen");
    JButton schliessen = new JButton("Schließen");
    JButton aktualisieren = new JButton("Aktualisiere Portliste");

    JButton abfragenbtn = new JButton("Abfragen starten");
    JButton uebernehmen = new JButton("Änderungen übernehmen");
    JButton firmware = new JButton("Gesamte Leuchten-Firmware aktualisieren");
    JButton modulfirmware = new JButton("Ausgewählte Modul-Firmware aktualisieren");
    JButton statusabfrage = new JButton("Manuelle Statusabfrage");
    JButton errorRequest = new JButton("Fehlermeldung auslesen");
    JButton masterMode = new JButton("Mastermode");
    JButton slaveMode = new JButton("Slavemode");

    JTextArea empfangen = new JTextArea();

    JScrollPane empfangenJScrollPane = new JScrollPane();
    JLabel console = new JLabel("Konsole");

    JList geraeteListe = new JList<>();
    JLabel geraeteNamen = new JLabel("Geräteliste");
    DefaultListModel<String> geraeteListeModel = new DefaultListModel<>();

    Object[][] data = {null, null, null, null, null, null, null};
    Object[] columns = {"Index", "Variablenname", "HEX-Adresse", "Aktueller Wert", "Min-Wert", "Max-Wert", "Default-Wert"};
    DefaultTableModel model = new DefaultTableModel(data, columns) {
        @Override
        public Class getColumnClass(int column) {
            switch (column) {
                case 0:
                    return Integer.class;
                case 1:
                    return String.class;
                case 2:
                    return String.class;
                case 3:
                    return String.class;
                case 4:
                    return Integer.class;
                case 5:
                    return Integer.class;
                case 6:
                    return Integer.class;
                default:
                    return Integer.class;
            }
        }

    };
    JTabbedPane jTabbedPane1 = new JTabbedPane();

    JTable geraeteDatenTabelle = new JTable(model) {
        public boolean isCellEditable(int row, int column) {
            int modelColumn = convertColumnIndexToModel(column);
            return (modelColumn != 3) ? false : true;
        }
    };

    Object[][] livedata = {null, null, null, null, null, null, null};
    Object[] livecolumns = {"Nachrichteneingang", "ID", "Geräteadresse", "Funktionscode", "HEX-Adresse", "Aktueller Wert"};
    DefaultTableModel livemodel = new DefaultTableModel(livedata, livecolumns) {
        @Override
        public Class getColumnClass(int column) {
            switch (column) {
                case 0:
                    return Long.class;
                case 1:
                    return String.class;
                case 2:
                    return String.class;
                case 3:
                    return String.class;
                default:
                    return String.class;
            }
        }

    };
    JTable liveDatenTabelle = new JTable(livemodel) {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    JScrollPane geraeteDatenJScrollPane = new JScrollPane(geraeteDatenTabelle);
    JScrollPane liveDatenJScrollPane = new JScrollPane(liveDatenTabelle);

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Programm gestartet");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Diagnoseapplikation();
            }
        });

        System.out.println("Main durchlaufen");
    }

    /**
     * Konstruktor
     */
    public Diagnoseapplikation() {
        // Daten werden aus CSV gelesen
        CUM4Collection = CSVReader.getDataFromCsv("controlunit_m4");
        CUCollection = CSVReader.getDataFromCsv("controlunit");
        AAWCollection = CSVReader.getDataFromCsv("aloneatwork");
        PLCollection = CSVReader.getDataFromCsv("panel");
        PRCollection = CSVReader.getDataFromCsv("panel");
        SH1Collection = CSVReader.getDataFromCsv("sensormodule");
        SH2Collection = CSVReader.getDataFromCsv("sensormodule");
        SH3Collection = CSVReader.getDataFromCsv("sensormodule");
        SH4Collection = CSVReader.getDataFromCsv("sensormodule");

        this.deviceArray = new Device[]{
            new Device(CUM4Collection, "Control Unit M4", "0803020006E447", (byte) 0x08),
            new Device(CUCollection, "Control Unit", "0803020002E584", (byte) 0x08),
            new Device(AAWCollection, "AloneAtWork", "100302", (byte) 0x10),
            new Device(PLCollection, "Panel left", "110302", (byte) 0x11),
            new Device(PRCollection, "Panel right", "120302", (byte) 0x12),
            new Device(SH1Collection, "Sensormodule 1", "170304", (byte) 0x17),
            new Device(SH2Collection, "Sensormodule 2", "180304", (byte) 0x18),
            new Device(SH3Collection, "Sensormodule 3", "190304", (byte) 0x19),
            new Device(SH4Collection, "Sensormodule 4", "1A0304", (byte) 0x1A),
            new Device(null, "CLM", "150302", (byte) 0x15),
            new Device(null, "PC-Bridge", "0F0302", (byte) 0x0F)
        };

        this.livedatacollectionArray = new Livedatacollection[]{
            liveCUM4Collection, liveCUCollection, liveAAWCollection,
            livePLCollection, livePRCollection, liveSH1Collection,
            liveSH2Collection, liveSH3Collection, liveSH4Collection,
            liveCLCollection, livePCBCollection};

        this.datacollectionArray = new Datacollection[]{
            CUM4Collection, CUCollection, AAWCollection, PLCollection, PRCollection,
            SH1Collection, SH2Collection, SH3Collection, SH4Collection};

        System.out.println("Konstruktor aufgerufen");

        // TableModelListener wird dem model hinzugefügt
        model.addTableModelListener(this);

        initComponents();

    }

    protected void finalize() {
        System.out.println("Destruktor aufgerufen");
    }

    void initComponents() {
        GridBagConstraints constraints = new GridBagConstraints();

        setTitle("Diagnoseapplikation");
        addWindowListener(new WindowListener());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        oeffnen.addActionListener(new oeffnenActionListener());
        schliessen.addActionListener(new schliessenActionListener());
        aktualisieren.addActionListener(new aktualisierenActionListener());
        abfragenbtn.addActionListener(new abfragenActionListener());
        uebernehmen.addActionListener(new schreibenActionListener());
        firmware.addActionListener(new firmwareActionListener());
        modulfirmware.addActionListener(new modulfirmwareActionListener());
        statusabfrage.addActionListener(new manualRequestActionListener());
        errorRequest.addActionListener(new errorRequestActionListener());
        masterMode.addActionListener(new masterModeActionListener());
        slaveMode.addActionListener(new slaveModeActionListener());

        empfangenJScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        empfangenJScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        empfangenJScrollPane.setViewportView(empfangen);
        DefaultCaret caret = (DefaultCaret) empfangen.getCaret();
        caret.setUpdatePolicy(DefaultCaret.UPDATE_WHEN_ON_EDT);

        jmenubar1.add(jmenu1);
        jmenubar1.add(jmenu2);
        jmenubar1.add(jmenu3);
//        jmenu1.add(jmenuitem1);
//        jmenu1.add(jmenuitem2);
        jmenu1.add(jmenuitem3);
        jmenu2.add(jmenuitem4);
        jmenu2.add(jmenuitem5);
//        jmenu3.add(jmenuitem6);
        jmenu1.setText("Datei");
        jmenu2.setText("Firmware");
        jmenu3.setText("Hilfe");
        jmenuitem1.setText("Config laden");
        jmenuitem2.setText("Config speichern");
        jmenuitem3.setText("Beenden");
        jmenuitem4.setText("Ordner für Firmware auswählen");
        jmenuitem5.setText("Manuelle Firmwareauswahl");
//        jmenuitem6.setText("Über");
        setJMenuBar(jmenubar1);
        jmenuitem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });
        jmenuitem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fwPathActionPerformed(evt);
            }
        });
        jmenuitem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fwFileActionPerformed(evt);
            }
        });
        fwFileChooser.setFileFilter(new FirmwareFilter());

        // erste Zeile wird generiert, Auswahlbutton für COM-Port
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;
        constraints.insets = new Insets(5, 5, 5, 5);
        panelSetup.add(auswahl, constraints);

        // COM-Port wird geöffnet
        constraints.gridx = 1;
        constraints.weightx = 1;
        panelSetup.add(oeffnen, constraints);

        // COM-Port wird geschlossen
        constraints.gridx = 2;
        panelSetup.add(schliessen, constraints);

        // COM-Portliste wird aktualisiert
        constraints.gridx = 3;
        panelSetup.add(aktualisieren, constraints);

        // einzelne Elemente werden dem Panel hinzugefügt
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 10;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        panel.add(panelSetup, constraints);

        // Label für Geräteliste
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;

        panelGeraeteListe.add(geraeteNamen, constraints);
        // Geräteliste
        for (int i = 0; i < deviceArray.length; i++) {
            if (i != 0) {
                geraeteListeModel.addElement(deviceArray[i].deviceName);
            }
        }
        geraeteListe.setModel(geraeteListeModel);
        geraeteListe.setCellRenderer(new DisabledItemListCellRenderer());
        geraeteListe.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting()) {
                    String gereat = geraeteListe.getSelectedValue().toString();
                    if (selectedtab == 1) {
                        DefaultTableModel model = (DefaultTableModel) geraeteDatenTabelle.getModel();
                        model.setRowCount(0);
                        for (int i = 0; i < deviceArray.length; i++) {
                            if (deviceArray[i].deviceName.equals(gereat)) {
                                if (deviceArray[i].deviceStatus) {
                                    for (Map.Entry entry : datacollectionArray[i].dataEntryCollection.entrySet()) {
                                        model.addRow(new Object[]{
                                            datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "index"),
                                            datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "varName"),
                                            entry.getKey(),
                                            datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "currentValue"),
                                            datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "defaultValue"),
                                            datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "minValue"),
                                            datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "maxValue")});
                                    }
                                } else {
                                    model.setRowCount(0);
                                }
                            }
                        }
                    } else {
                        DefaultTableModel model = (DefaultTableModel) liveDatenTabelle.getModel();
                        model.setRowCount(0);
                        for (int i = 0; i < deviceArray.length; i++) {
                            if (deviceArray[i].deviceName.equals(gereat)) {
                                if (deviceArray[i].deviceStatus) {
                                    for (Map.Entry entry : livedatacollectionArray[i].liveDataEntryCollection.entrySet()) {
                                        model.addRow(new Object[]{
                                            livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "time"),
                                            entry.getKey(),
                                            livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "deviceAdress"),
                                            livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "functionCode"),
                                            livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "hexAdress"),
                                            livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "value")});
                                    }
                                } else {
                                    model.setRowCount(0);
                                }
                            }
                        }
                    }
                }
            }
        }
        );

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;

        panelGeraeteListe.add(geraeteListe, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.NONE;

        panel.add(panelGeraeteListe, constraints);

        geraeteDatenTabelle.setAutoCreateRowSorter(true);
        geraeteDatenTabelle.getRowSorter().toggleSortOrder(0);
        liveDatenTabelle.setAutoCreateRowSorter(true);
        liveDatenTabelle.getRowSorter().toggleSortOrder(0);
        model.setRowCount(0);
        livemodel.setRowCount(0);

        jTabbedPane1.addTab("Liveview", liveDatenJScrollPane);
        jTabbedPane1.addTab("Gerätedaten", geraeteDatenJScrollPane);

        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 0.5;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.fill = GridBagConstraints.BOTH;

        panel.add(jTabbedPane1, constraints);

        jTabbedPane1.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (e.getSource() instanceof JTabbedPane) {
                    JTabbedPane pane = (JTabbedPane) e.getSource();
                    selectedtab = pane.getSelectedIndex();
                    if (geraeteListe.getSelectedValue() != null) {
                        String gereat = geraeteListe.getSelectedValue().toString();
                        if (selectedtab == 1) {
                            DefaultTableModel model = (DefaultTableModel) geraeteDatenTabelle.getModel();
                            model.setRowCount(0);
                            for (int i = 0; i < deviceArray.length; i++) {
                                if (deviceArray[i].deviceName.equals(gereat)) {
                                    if (deviceArray[i].deviceStatus) {
                                        for (Map.Entry entry : datacollectionArray[i].dataEntryCollection.entrySet()) {
                                            model.addRow(new Object[]{
                                                datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "index"),
                                                datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "varName"),
                                                entry.getKey(),
                                                datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "currentValue"),
                                                datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "defaultValue"),
                                                datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "minValue"),
                                                datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "maxValue")});
                                        }
                                    } else {
                                        model.setRowCount(0);
                                    }
                                }
                            }
                        } else {
                            DefaultTableModel model = (DefaultTableModel) liveDatenTabelle.getModel();
                            model.setRowCount(0);
                            for (int i = 0; i < deviceArray.length; i++) {
                                if (deviceArray[i].deviceName.equals(gereat)) {
                                    if (deviceArray[i].deviceStatus) {
                                        for (Map.Entry entry : livedatacollectionArray[i].liveDataEntryCollection.entrySet()) {
                                            model.addRow(new Object[]{
                                                livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "time"),
                                                entry.getKey(),
                                                livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "deviceAdress"),
                                                livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "functionCode"),
                                                livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "hexAdress"),
                                                livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "value")});
                                        }
                                    } else {
                                        model.setRowCount(0);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        );
        // Abfragen werden gestartet
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.7;
        constraints.weighty = 0.7;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;

        panelFirmware.add(abfragenbtn, constraints);

        // Änderungen an aktiven Werten werden übernommen
        constraints.gridy = 1;

        panelFirmware.add(uebernehmen, constraints);

        // Änderungen an aktiven Werten werden übernommen
        constraints.gridy = 2;

        panelFirmware.add(firmware, constraints);

        // Änderungen an aktiven Werten werden übernommen
        constraints.gridy = 3;

        panelFirmware.add(modulfirmware, constraints);

        // Änderungen an aktiven Werten werden übernommen
        constraints.gridy = 4;

        panelFirmware.add(statusabfrage, constraints);

        // Änderungen an aktiven Werten werden übernommen
        constraints.gridy = 5;

        panelFirmware.add(errorRequest, constraints);

        // Mastermodus
        constraints.gridy = 6;

        panelFirmware.add(masterMode, constraints);

        // Slavemodus
        constraints.gridy = 7;

        panelFirmware.add(slaveMode, constraints);

        // Firmarepanel wird dem Panel hinzugefügt
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.fill = GridBagConstraints.NONE;

        panel.add(panelFirmware, constraints);

        // Label für Konsole
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.1;
        constraints.weighty = 0.1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.NONE;

        panelEmpfangen.add(console, constraints);

        // Input-Konsole
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0.9;
        constraints.weighty = 0.9;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;

        panelEmpfangen.add(empfangenJScrollPane, constraints);

        // Konsole wird dem Panel hinzugefügt
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridwidth = 10;
        constraints.gridheight = 5;
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.fill = GridBagConstraints.BOTH;

        panel.add(panelEmpfangen, constraints);

        aktualisiereSerialPort();

        add(panel);

        pack();

        setVisible(true);
        setLocationRelativeTo(null);
        System.out.println("Fenster erzeugt");
    }

    private void fwPathActionPerformed(java.awt.event.ActionEvent evt) {
        fwPathChooser = new JFileChooser();
        fwPathChooser.setCurrentDirectory(new java.io.File("."));
        fwPathChooser.setDialogTitle("Auswahl des Firmwareordners");
        fwPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fwPathChooser.setAcceptAllFileFilterUsed(true);
        if (fwPathChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            System.out.println("Firmwareordner: " + fwPathChooser.getCurrentDirectory());
//            System.out.println("getSelectedFile() : " + fwPathChooser.getSelectedFile());
        } else {
            System.out.println("No Selection ");
        }
    }

    private void fwFileActionPerformed(java.awt.event.ActionEvent evt) {
        int returnVal = fwFileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            File file = fwFileChooser.getSelectedFile();
            System.out.println(fwFileChooser.getSelectedFile());
            schliesseSerialPort();

            Runtime runtime = Runtime.getRuntime();

            try {
                String port = (String) auswahl.getSelectedItem();
                String portNr = port.substring(3, 4);
                File folder = fwPathChooser.getSelectedFile();
//                String folderpath = folder.toString();
                String fileString = file.toString();

//                Runtime.getRuntime().exec("cmd /c start " + fwFileChooser.getSelectedFile());
                String[] cmd = {"cmd", "/K", "start", fileString, portNr};

                Process p1 = runtime.exec(cmd);

            } catch (IOException ioException) {
                System.out.println(ioException.getMessage());
            }
        } else {
            System.out.println("File access cancelled by user.");
        }
    }

    class FirmwareFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            // Allow only directories, or files with ".txt" extension
            return file.isDirectory() || file.getAbsolutePath().endsWith(".bat");
        }

        @Override
        public String getDescription() {
            return "Firmware-Datei (*.bat)";
        }
    }

    // https://stackoverflow.com/questions/40192911/tablemodellistener-and-addrow-interference-in-jtable-defaulttablemodel
    // https://www.programcreek.com/java-api-examples/javax.swing.event.TableModelEvent
    public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE) {
            int row = e.getFirstRow();
            int column = e.getColumn();

            if (column == 3) {
                TableModel model = (TableModel) e.getSource();
                changedValue = Integer.parseInt((String) model.getValueAt(row, 3));
                hexofchangedvalue = ((String) model.getValueAt(row, 2));
                String gereat = geraeteListe.getSelectedValue().toString();
                for (int i = 0; i < deviceArray.length; i++) {
                    if (gereat.equals(deviceArray[i].deviceName)) {
                        changedDevice = (byte) deviceArray[i].devicebyte;
                    }
                }
                empfangen.append("Änderungsanfrage für Device: " + changedDevice + " | HEX-Adresse: " + hexofchangedvalue + " | Neuer Wert: " + changedValue + "\n");
                System.out.println("changedValue: " + changedValue);
                System.out.println("hexofchangedvalue: " + hexofchangedvalue);
                System.out.println("changedDevice: " + changedDevice);
            }
        }
    }

    boolean oeffneSerialPort(String portName) {
        Boolean foundPort = false;
        if (serialPortGeoeffnet != false) {
            System.out.println("Serialport bereits geöffnet");
            empfangen.append("Serialport bereits geöffnet\n");

            return false;
        }
        System.out.println("Öffne Serialport");
        empfangen.append("Öffne Serialport\n");
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
            empfangen.append("Serialport nicht gefunden: " + portName + "\n");
            return false;
        }
        try {
            serialPort = (SerialPort) serialPortId.open("Öffnen und Senden", 500);
        } catch (PortInUseException e) {
            System.out.println("Port belegt");
            empfangen.append("Port belegt\n");
        }
        try {
            outputStream = serialPort.getOutputStream();
        } catch (IOException e) {
            System.out.println("Keinen Zugriff auf OutputStream");
            empfangen.append("Keinen Zugriff auf OutputStream\n");
        }
        try {
            inputStream = serialPort.getInputStream();
        } catch (IOException e) {
            System.out.println("Keinen Zugriff auf InputStream");
            empfangen.append("Keinen Zugriff auf InputStream\n");
        }
        try {
            serialPort.addEventListener(new serialPortEventListener());
        } catch (TooManyListenersException e) {
            System.out.println("TooManyListenersException für Serialport");
            empfangen.append("TooManyListenersException für Serialport\n");
        }
        serialPort.notifyOnDataAvailable(true);
        try {
            serialPort.setSerialPortParams(baudrate, dataBits, stopBits, parity);
        } catch (UnsupportedCommOperationException e) {
            System.out.println("Konnte Schnittstellen-Paramter nicht setzen");
            empfangen.append("Konnte Schnittstellen-Paramter nicht setzen\n");
        }

        serialPortGeoeffnet = true;
        return true;
    }

    void schliesseSerialPort() {
        if (serialPortGeoeffnet == true) {
            System.out.println("Schließe Serialport");
            empfangen.append("Schließe Serialport" + "\n");

            serialPort.close();
            serialPortGeoeffnet = false;
        } else {
            System.out.println("Serialport bereits geschlossen");
            empfangen.append("Serialport bereits geschlossen\n");

        }
    }

    void aktualisiereSerialPort() {
        System.out.println("Akutalisiere Serialport-Liste");
        if (serialPortGeoeffnet != false) {
            System.out.println("Serialport ist geöffnet");
            empfangen.append("Serialport ist geöffnet\n");

            return;
        }
        auswahl.removeAllItems();
        enumComm = CommPortIdentifier.getPortIdentifiers();
        while (enumComm.hasMoreElements()) {
            serialPortId = (CommPortIdentifier) enumComm.nextElement();
            if (serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                System.out.println("Erkannter COM-Port:" + serialPortId.getName());
                auswahl.addItem(serialPortId.getName());
            }
        }
        empfangen.append("COM-Portliste aktualisiert\n");
    }

    public void requestSend() throws IOException {
        ArrayList<Integer> deviceOnlineCount = new ArrayList<>();
        for (int i = 0; i < deviceArray.length; i++) {
            if (deviceArray[i].deviceStatus) {
                deviceOnlineCount.add(i);
            }
        }
        if (!deviceArray[actualDeviceCounter].deviceStatus) {
            actualDeviceCounter++;
        }

        if (actualDeviceCounter <= deviceOnlineCount.size()) {
            if (deviceArray[actualDeviceCounter].deviceStatus) {
                if (!deviceArray[actualDeviceCounter].isRequestFinished()) {
                    if (!requestSuccess) {
                        byte[] sendstream = deviceArray[actualDeviceCounter].getLastByteArray();
                        CRC16 crc = new CRC16();
                        crc.update(sendstream, 0, sendstream.length);
                        crc.getAll();
                        outputStream.write(crc.getAll());

                        String sendstreamstring = ConversionHelper.byteArrayToHexString(sendstream);
//                        System.out.println("Aktuelle Abfrage: " + deviceArray[actualDeviceCounter].deviceName);
//                        System.out.println("Senden erfolgreich: " + sendstreamstring);
//                        System.err.println("Senden wiederholt: " + sendstreamstring);
                        lastWasRequest = true;
                        empfangen.append(".");
                    } else {
                        byte[] sendstream = deviceArray[actualDeviceCounter].getNextByteArray();
                        CRC16 crc = new CRC16();
                        crc.update(sendstream, 0, sendstream.length);
                        crc.getAll();
                        outputStream.write(crc.getAll());

                        String sendstreamstring = ConversionHelper.byteArrayToHexString(sendstream);
//                        System.out.println("Aktuelle Abfrage: " + deviceArray[actualDeviceCounter].deviceName);
//                        System.out.println("Senden erfolgreich: " + sendstreamstring);
//                        System.err.println("Senden: " + sendstreamstring);
                        lastWasRequest = true;
                        empfangen.append(".");
                    }
                } else {
                    empfangen.append("\nDeviceabfrage abgeschlossen: " + deviceArray[actualDeviceCounter].deviceName + "\n");
//                    System.out.println("\nDeviceabfrage abgeschlossen: " + deviceArray[actualDeviceCounter].deviceName);
                    firmwareVersion();
                    actualDeviceCounter++;
                    if (slaveStatus) {
                        requestSend();
                    }
                }

            }
        } else {
//            System.out.println("Alle Abfragen abgeschlossen");
            empfangen.append("Alle Abfragen abgeschlossen\n");
            abfragen = false;
        }
    }

    public void firmwareVersion() {
        try {
            String startAdressFWVersion = "0000";
            String fWVersionLength = "20";
            ArrayList<String> adressListFW = AddressList.adressString(startAdressFWVersion, fWVersionLength);
            StringBuilder fwsb = new StringBuilder();

            String startAdressHWVersion = "0030";
            String hWVersionLength = "05";
            ArrayList<String> adresslistHW = AddressList.adressString(startAdressHWVersion, hWVersionLength);
            StringBuilder hwsb = new StringBuilder();

            String startAdressFWBLVersion = "0035";
            String fWBLVersionLength = "14";
            ArrayList<String> adresslistFWBL = AddressList.adressString(startAdressFWBLVersion, fWBLVersionLength);
            StringBuilder fwblsb = new StringBuilder();

            if (adressListFW != null) {
                for (int i = 0; i < adressListFW.size(); i++) {
                    String fWVersion = null;
                    fWVersion = (String) datacollectionArray[actualDeviceCounter].getDataByIdentifier(adressListFW.get(i), "currentValue");
                    if (fWVersion != null && 1 < fWVersion.length()) {
                        String fWVersionSwapped = ConversionHelper.swapChars(fWVersion, 0, 1);
                        fwsb.append(fWVersionSwapped);
                    } else {
                        fwsb.append(fWVersion);
                    }
                }
                empfangen.append("Firmwareversion: " + fwsb.toString() + "\n");
            }
            if (adresslistHW != null) {
                for (int i = 0; i < adresslistHW.size(); i++) {
                    String hWVersion = null;
                    hWVersion = (String) datacollectionArray[actualDeviceCounter].getDataByIdentifier(adresslistHW.get(i), "currentValue");
                    if (hWVersion != null && 1 < hWVersion.length()) {
                        String hWVersionSwapped = ConversionHelper.swapChars(hWVersion, 0, 1);
                        hwsb.append(hWVersionSwapped);
                    } else {
                        hwsb.append(hWVersion);
                    }
                }
                empfangen.append("Hardwareversion: " + hwsb.toString() + "\n");
            }
            if (adresslistFWBL != null) {
                for (int i = 0; i < adresslistFWBL.size(); i++) {
                    String fWBLVersion = null;
                    fWBLVersion = (String) datacollectionArray[actualDeviceCounter].getDataByIdentifier(adresslistFWBL.get(i), "currentValue");
                    if (fWBLVersion != null && 1 < fWBLVersion.length()) {
                        String fWBLVersionSwapped = ConversionHelper.swapChars(fWBLVersion, 0, 1);
                        fwblsb.append(fWBLVersionSwapped);
                    } else {
                        fwblsb.append(fWBLVersion);
                    }
                }
                empfangen.append("Firmwareversion Bootloader: " + fwblsb.toString() + "\n\n");
            }
        } catch (java.lang.NullPointerException e) {
            empfangen.append("Versionierung nicht vollständig empfangen");
        } catch (java.lang.StringIndexOutOfBoundsException o) {
            empfangen.append("Versionierung nicht vollständig empfangen");
        }
    }
    // https://stackoverflow.com/questions/1735840/how-do-i-split-an-integer-into-2-byte-binary

    public void writeSend() throws IOException {
        // Alte Daten mit neuen Daten vergleichen, falls Änderungen 
        // Bsp. 08 06 0000 0001 "CRC" senden
        if (changedDevice != 0) {
            byte[] hexbuffer = ConversionHelper.hexStringToByteArray(hexofchangedvalue);
            byte[] changedvaluearray = new byte[2];
            changedvaluearray[1] = (byte) (changedValue & 0xFF);
            changedvaluearray[0] = (byte) ((changedValue >> 8) & 0xFF);

            byte[] sendstream = {(byte) changedDevice, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
            System.arraycopy(hexbuffer, 0, sendstream, 2, 2);
            System.arraycopy(changedvaluearray, 0, sendstream, 4, 2);
            String sendstreamstring = ConversionHelper.byteArrayToHexString(sendstream);
            System.out.println("sendstreamstring: " + sendstreamstring);

            CRC16 crc = new CRC16();
            crc.update(sendstream, 0, sendstream.length);
            crc.getAll();
            outputStream.write(crc.getAll());
        }
        schreiben = false;
        System.out.println("Änderungen übernommen");
        empfangen.append("Änderungen übernommen\n");
        changedDevice = 0;
    }

    public void deviceRequest() throws IOException {
        byte[] devicetype = {(byte) 0x08, (byte) 0x03, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //1
        CRC16 crc = new CRC16();
        crc.update(devicetype, 0, devicetype.length);
        crc.getAll();
        outputStream.write(crc.getAll());
    }

    public void masterMode() throws IOException {
        if (serialPortGeoeffnet) {
            byte[] devicetype = {(byte) 0x08, (byte) 0x06, (byte) 0x27, (byte) 0x18, (byte) 0x00, (byte) 0x02}; //1
            CRC16 crc = new CRC16();
            crc.update(devicetype, 0, devicetype.length);
            crc.getAll();
            outputStream.write(crc.getAll());
            empfangen.append("Mastermode wird gestartet, Neustart der Lampe initiiert\n");
        } else {
            empfangen.append("Port geschlossen\n");
            System.out.println("Port geschlossen");
        }
    }

    public void slaveMode() throws IOException {
        if (serialPortGeoeffnet) {
            byte[] devicetype = {(byte) 0x08, (byte) 0x06, (byte) 0x27, (byte) 0x18, (byte) 0x00, (byte) 0x04}; //1
            CRC16 crc = new CRC16();
            crc.update(devicetype, 0, devicetype.length);
            crc.getAll();
            outputStream.write(crc.getAll());
            empfangen.append("Slavemode wird gestartet\n");
        } else {
            empfangen.append("Port geschlossen\n");
            System.out.println("Port geschlossen");
        }
    }

    public void errorRequest2718() throws IOException {
        if (serialPortGeoeffnet) {
            byte[] devicetype = {(byte) 0x08, (byte) 0x03, (byte) 0x27, (byte) 0x18, (byte) 0x00, (byte) 0x01}; //1
            CRC16 crc = new CRC16();
            crc.update(devicetype, 0, devicetype.length);
            crc.getAll();
            outputStream.write(crc.getAll());
        } else {
            empfangen.append("Port geschlossen\n");
            System.out.println("Port geschlossen");
        }
    }

    public void errorRequest2719() throws IOException {
        if (serialPortGeoeffnet) {
            byte[] devicetype = {(byte) 0x08, (byte) 0x03, (byte) 0x27, (byte) 0x19, (byte) 0x00, (byte) 0x01}; //1
            CRC16 crc = new CRC16();
            crc.update(devicetype, 0, devicetype.length);
            crc.getAll();
            outputStream.write(crc.getAll());
        } else {
            empfangen.append("Port geschlossen\n");
            System.out.println("Port geschlossen");
        }
    }

    public void manualDeviceRequest() throws IOException {
        if (serialPortGeoeffnet) {
            String gereat = geraeteListe.getSelectedValue().toString();
            byte devicebyte = 0x00;
            byte length = 0x00;
            for (int i = 0; i < deviceArray.length - 6; i++) {
                if (gereat.equals(deviceArray[i].deviceName)) {
                    devicebyte = deviceArray[i].devicebyte;
                    length = 0x01;
                }
            }
            for (int i = 5; i < deviceArray.length - 2; i++) {
                if (gereat.equals(deviceArray[i].deviceName)) {
                    devicebyte = deviceArray[i].devicebyte;
                    length = 0x02;
                }
            }
            byte[] devicetype = {(byte) devicebyte, (byte) 0x03, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) length}; //1
            CRC16 crc = new CRC16();
            crc.update(devicetype, 0, devicetype.length);
            crc.getAll();
            outputStream.write(crc.getAll());
        } else {
            empfangen.append("Port geschlossen\n");
            System.out.println("Port geschlossen");
        }
    }

    // https://www.javacodegeeks.com/2011/05/avoid-concurrentmodificationexception.html
    void serialPortDatenVerfuegbar() throws InterruptedException {
        try {
            System.out.println("Starttime: " + getCurrentTimeStamp());
            byte[] data = new byte[170];
            byte[] requestbuffer = new byte[3];
            byte[] crcbuffer = new byte[2];
            int num;

            requestbuffer[0] = deviceArray[actualDeviceCounter].getSingleByteArray(0);
            requestbuffer[1] = deviceArray[actualDeviceCounter].getSingleByteArray(1);
            requestbuffer[2] = (byte) (2 * deviceArray[actualDeviceCounter].getSingleByteArray(5));
            String requestbufferstring = ConversionHelper.byteArrayToHexString(requestbuffer);
            String adress = deviceArray[actualDeviceCounter].getCurrentByteAdress();
//            System.err.println("adress: " + adress);

            while (inputStream.available() > 0) {

                num = inputStream.read(data, 0, data.length);
//                System.out.println(Arrays.toString(data));
                String byteArrayToHex = ConversionHelper.byteArrayToHexString(data);
//                System.out.println("Dateninput unbearbeitet: " + byteArrayToHex);
                int datalength = byteArrayToHex.length() / 2;
                while ("00".equals(byteArrayToHex.substring(datalength - 2, datalength))) {
                    datalength -= 2;
                }
                byte[] messagearray = new byte[datalength / 2];
                System.arraycopy(data, 0, messagearray, 0, messagearray.length);
                String message = ConversionHelper.byteArrayToHexString(messagearray);
                System.out.println("Empfangene Nachricht: " + message);

                ArrayList<Integer> repeatList = new ArrayList<>();
                ArrayList<String> messageList = new ArrayList<>();

                // Multiple Nachrichten finden
                for (int i = 0; i < deviceArray.length; i++) {
                    for (int z = 0; z < message.length() - 4; z++) {
                        if (deviceArray[i].shortHexRead.equals(message.substring(z, z + 4)) || deviceArray[i].shortHexWrite.equals(message.substring(z, z + 4)) || deviceArray[i].shortHexMultipleWrite.equals(message.substring(z, z + 4))) {
                            if (!repeatList.contains(z)) {
                                repeatList.add(z);
                            }
                        }
                    }
                }
                System.out.println("Anzahl erkannter Wiederholungen: " + repeatList.size());
                // Sorting of arraylist using Collections.sort
                Collections.sort(repeatList);
                // Multiple Nachrichten aufteilen
                for (int i = 0; i < repeatList.size(); i++) {
                    int repeatstartposition = repeatList.get(i);
                    int repeatendposition = 0;
                    int next = i + 1;
                    int nextnext = i + 2;
                    int last = repeatList.size();
                    boolean isLast = false;
                    boolean delta = false;
                    boolean isEven = false;
                    if (1 < repeatList.size()) {
                        if (next != last) {
                            delta = 13 < (repeatList.get(next) - repeatList.get(i));
                            isEven = ((repeatList.get(next) - repeatList.get(i)) % 2) == 0;
                        } else if (next == last) {
                            isLast = true;
                            delta = 13 < (message.length() - repeatList.get(i));
                            isEven = ((message.length() - repeatList.get(i)) % 2) == 0;
                        }
                    } else {
                        isEven = (message.length() % 2) == 0;
                        repeatendposition = message.length();
                        messageList.add(message.substring(repeatstartposition, repeatendposition));
                    }
                    if (isEven) {
                        if (isLast && delta) {
                            repeatendposition = message.length();
                            messageList.add(message.substring(repeatstartposition, repeatendposition));

                        } else if (!isLast && delta) {
                            repeatendposition = repeatList.get(next);
                            messageList.add(message.substring(repeatstartposition, repeatendposition));
//                        } else if (!isLast && !delta) {
//                            repeatendposition = repeatList.get(next);
//                            messageList.add(message.substring(repeatstartposition, repeatendposition));
                        }
                    } else if (!isEven && !isLast && !delta) {
                        if (nextnext != last) {
                            repeatendposition = repeatList.get((nextnext));
                            i++;
                        } else {
                            repeatendposition = message.length();
                            i++;
                        }
                        messageList.add(message.substring(repeatstartposition, repeatendposition));
                        System.err.println("Anzahl erkannter Nachrichten: " + messageList.size());
                    }
                }
                System.out.println("Anzahl erkannter Nachrichten: " + messageList.size());
                for (int i = 0; i < messageList.size(); i++) {
                    System.out.println("messageList: " + messageList.get(i));
                }
                if (0 < messageList.size()) {
                    byte[] singleMessageArray = new byte[messageList.size()];
                    for (int z = 0; z < messageList.size(); z++) {
//                        System.out.println("messageList.z: " + messageList.get(z));

                        singleMessageArray = ConversionHelper.hexStringToByteArray(messageList.get(z));
                        int arraylength = messageList.get(z).length() / 2;
                        byte[] msgbuffer = new byte[arraylength - 2];
                        System.arraycopy(singleMessageArray, 0, msgbuffer, 0, msgbuffer.length);
                        System.arraycopy(singleMessageArray, msgbuffer.length, crcbuffer, 0, 2);

                        // Überprüfung des CRCs, Empfangene Nachricht und CRC werden verglichen
                        CRC16 crcreverse = new CRC16();
                        boolean msgcrctrue = crcreverse.check(msgbuffer, crcbuffer);
                        if (msgcrctrue) {
                            String singleMessageString = messageList.get(z);

                            String singleMessageShortString = messageList.get(z).substring(0, 4);

                            Boolean isRequest = false;
                            String deviceAdress = null;
                            String functionCode = null;
                            String hexAdress = null;
                            String requestLength = null;
                            String dataValue = null;
                            String liveValue = null;
                            ArrayList<String> liveDataValueArrayList = new ArrayList<>();
                            ArrayList<String> requestAdressList = new ArrayList<>();

                            // Gerätestatus auslesen
                            for (int i = 0; i < deviceArray.length; i++) {
                                if (i < 2 && 13 < singleMessageString.length()) {
                                    if (deviceArray[i].hexIdentifier.equals(singleMessageString.substring(0, 14))) {
                                        if (i == 0 && !deviceArray[i].deviceStatus && !deviceArray[i + 1].deviceStatus) {
                                            deviceArray[i].setDeviceStatus(true);
                                            geraeteListeModel.set(i, deviceArray[i].deviceName);
                                        } else if (i == 1 && !deviceArray[i].deviceStatus && !deviceArray[i - 1].deviceStatus) {
                                            deviceArray[i].setDeviceStatus(true);
                                        }
                                    }
                                } else if (i > 1) {
                                    if (deviceArray[i].hexIdentifier.equals(singleMessageString.substring(0, 6))) {
                                        if (!deviceArray[i].deviceStatus) {
                                            deviceArray[i].setDeviceStatus(true);
                                        }
                                    }
                                }
                            }
                            // Anfragenlänge = 8 Byte, Antwort dynamisch
                            if (singleMessageString.length() == 16) {
                                deviceAdress = singleMessageString.substring(0, 2);
                                functionCode = singleMessageString.substring(2, 4);
                                hexAdress = singleMessageString.substring(4, 8);
                                isRequest = true;
                            } else if ("10".equals(singleMessageString.substring(2, 4)) && singleMessageString.length() != 16) {
                                deviceAdress = singleMessageString.substring(0, 2);
                                functionCode = "10";
                                hexAdress = singleMessageString.substring(4, 8);
                                requestLength = singleMessageString.substring(8, 12);
                                requestAdressList = AddressList.adressString(hexAdress, requestLength);
                                liveValue = singleMessageString.substring(14, singleMessageString.length() - 4);
                            } else if (singleMessageString.length() != 16) {
                                deviceAdress = singleMessageString.substring(0, 2);
                                functionCode = singleMessageString.substring(2, 4);
                            }
                            // Lesebefehl erkannt
                            if ("03".equals(functionCode)) {
                                if (isRequest) {
                                    requestLength = singleMessageString.substring(8, messageList.get(z).length() - 4);
                                    requestAdressList = AddressList.adressString(hexAdress, requestLength);
                                } else {
                                    requestLength = "0001";
                                    liveValue = singleMessageString.substring(6, 10);
                                }
                            }
                            // Schreibbefehl erkannt
                            if ("06".equals(functionCode)) {
                                requestLength = "0001";
                                liveValue = singleMessageString.substring(8, singleMessageString.length() - 4);
                            }
                            if (errorRead && "0803".equals(singleMessageString.substring(0, 4))) {
                                if ("01".equals(singleMessageString.substring(10, 12))) {
                                    empfangen.append("Fehlercode: 1 = Number Of LED Drivers Wrong");
                                } else if ("02".equals(singleMessageString.substring(10, 12))) {
                                    empfangen.append("Fehlercode: 2 = Number Of Sensors Wrong");
                                } else if ("03".equals(singleMessageString.substring(10, 12))) {
                                    empfangen.append("Fehlercode: 3 = Number Of Panels Wrong");
                                } else if ("04".equals(singleMessageString.substring(10, 12))) {
                                    empfangen.append("Fehlercode: 4 = falsche LED Drivers gefunden -> Adressierung falsch");
                                } else if ("11".equals(singleMessageString.substring(10, 12))) {
                                    empfangen.append("Fehlercode: 11 = Relais Error");
                                } else if ("13".equals(singleMessageString.substring(10, 12))) {
                                    empfangen.append("Fehlercode: 13 = Modbus Short");
                                } else if ("14".equals(singleMessageString.substring(10, 12))) {
                                    empfangen.append("Fehlercode: 14 = DC Detected – 0-Durchgang defect");
                                } else if ("15".equals(singleMessageString.substring(10, 12))) {
                                    empfangen.append("Fehlercode: 15 = Wrong Head detected");
                                }
                            }

                            // MultipleWrite erkannt
                            if ("10".equals(functionCode)) {
                                requestLength = singleMessageString.substring(8, 12);
                                requestAdressList = AddressList.adressString(hexAdress, requestLength);
                                if (singleMessageString.length() != 16) {
                                    liveValue = singleMessageString.substring(14, singleMessageString.length() - 4);
                                }
                            }
                            // Antwort auf Anfrage in gleicher Zeile
                            if (z + 1 < messageList.size() && messageList.get(z + 1).length() != 16) {
                                liveValue = messageList.get(z + 1).substring(6, messageList.get(z + 1).length() - 4);
                            }

                            if (liveValue != null && liveValue.length() == requestAdressList.size() * 4) {
                                for (int y = 0; y < requestAdressList.size(); y++) {
                                    liveDataValueArrayList.add(liveValue.substring(y * 4, y * 4 + 4));
                                }
                            }
                            // Werte aus Anfrage in Gerätedatentabelle speichern
                            if (requestbufferstring.equals(singleMessageString.substring(0, 6)) && abfragen && lastWasRequest && singleMessageString.length() == 14) {
                                for (Map.Entry entry : datacollectionArray[actualDeviceCounter].dataEntryCollection.entrySet()) {
                                    if (entry.getKey().toString().equals(adress)) {
                                        dataValue = singleMessageString.substring(6, 10);
                                        datacollectionArray[actualDeviceCounter].setCurrentValue(entry.getKey().toString(), dataValue);
                                        System.err.println("DataValue: " + dataValue);
                                        System.out.println("singleMessageString: " + singleMessageString);
                                        System.out.println("Speicherzeitpunkt: " + getCurrentTimeStamp());
                                    }
                                }
                                lastWasRequest = false;
                                requestSuccess = true;
                            } else {
                                if (lastWasRequest) {
                                    lastWasRequest = false;
                                    requestSuccess = false;
                                }
                                // Livewerte speichern
                                for (int i = 0; i < deviceArray.length; i++) {
                                    if (deviceArray[i].shortHexRead.equals(singleMessageShortString) || deviceArray[i].shortHexWrite.equals(singleMessageShortString) || deviceArray[i].shortHexMultipleWrite.equals(singleMessageShortString)) {
                                        if (requestLength == "0001") {
                                            if (liveValue != null && hexAdress != null) {
                                                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS"); // https://www.mkyong.com/java/java-how-to-get-current-date-time-date-and-calender/
                                                Date date = new Date();
                                                Livedataentry liveDataEntry = new Livedataentry();
                                                liveDataEntry.id = deviceAdress + functionCode + hexAdress;
                                                liveDataEntry.time = dateFormat.format(date);
                                                liveDataEntry.deviceAdress = deviceAdress;
                                                liveDataEntry.functionCode = functionCode;
                                                liveDataEntry.hexAdress = hexAdress;
                                                liveDataEntry.value = liveValue;
                                                livedatacollectionArray[i].addEntry(liveDataEntry.id, liveDataEntry);
//                                            } else if (liveValue != null) {
//                                                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS"); 
//                                                Date date = new Date();
//                                                Livedataentry liveDataEntry = new Livedataentry();
//                                                liveDataEntry.id = deviceAdress + functionCode + liveValue;
//                                                liveDataEntry.time = dateFormat.format(date);
//                                                liveDataEntry.deviceAdress = deviceAdress;
//                                                liveDataEntry.functionCode = functionCode;
//                                                liveDataEntry.hexAdress = "-";
//                                                liveDataEntry.value = liveValue;
//                                                livedatacollectionArray[i].addEntry(liveDataEntry.id, liveDataEntry);
                                            }
                                        } else {
                                            if (!requestAdressList.isEmpty() && !liveDataValueArrayList.isEmpty()) {
                                                for (int j = 0; j < requestAdressList.size(); j++) {
                                                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
                                                    Date date = new Date();
                                                    Livedataentry liveDataEntry = new Livedataentry();
                                                    liveDataEntry.id = deviceAdress + functionCode + requestAdressList.get(j);
                                                    liveDataEntry.time = dateFormat.format(date);
                                                    liveDataEntry.deviceAdress = deviceAdress;
                                                    liveDataEntry.functionCode = functionCode;
                                                    liveDataEntry.hexAdress = requestAdressList.get(j);
                                                    liveDataEntry.value = liveDataValueArrayList.get(j);
                                                    livedatacollectionArray[i].addEntry(liveDataEntry.id, liveDataEntry);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        } else {
                            // CRC falsch    
                            System.err.println("Zeitstempel: " + getCurrentTimeStamp());
                            System.err.println("CRC-Überprüfung fehlerhaft");
                            System.out.println("Zeitstempel: " + getCurrentTimeStamp());
                        }
                        // Control Unit Typ abfragen, Abfragen auslösen, Änderungen senden
                        if ("0F03".equals(messageList.get(z).substring(0, 4))) {
                            if (abfragen && !schreiben) {
                                if (!deviceArray[0].deviceStatus && !deviceArray[1].deviceStatus) {
                                    Thread.sleep(5);
                                    deviceRequest();
                                } else {
                                    Thread.sleep(5);
                                    requestSend();
                                }
                            }
                            if (schreiben && !abfragen) {
                                writeSend();
                            }
                        }
                        // Abfragen in Slavemodus
                        if (slaveStatus && abfragen) {
                            Thread.sleep(20);
                            requestSend();
                        }
                    }
                }
            }
//            System.out.println("Endtime: " + getCurrentTimeStamp());
            System.out.println("");
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen empfangener Daten");
            empfangen.append("Fehler beim Lesen empfangener Daten\n");
//        } catch (java.lang.NegativeArraySizeException e) {
//            System.out.println("1. Nachricht nicht vollständig");
//            empfangen.append("1. Nachricht nicht vollständig\n");
        } catch (java.lang.StringIndexOutOfBoundsException e) {
            System.out.println("StringIndexOutOfBoundsException: " + e);
//            empfangen.append("StringIndexOutOfBoundsException: " + e + "\n");
//        } catch (java.lang.NullPointerException e) {
//            System.out.println("Nullpointer Exception");
//            empfangen.append("Nullpointer Exception\n");
        }
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        return strDate;

    }

    class WindowListener extends WindowAdapter {

        public void windowClosing(WindowEvent event) {
            schliesseSerialPort();
            System.out.println("Fenster wird geschlossen");
        }
    }

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        schliesseSerialPort();
        System.out.println("Fenster wird geschlossen");
        System.exit(0);

    }

    class oeffnenActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            System.out.println("oeffnenActionListener");
            oeffneSerialPort((String) auswahl.getSelectedItem());
            JavaTimer();
        }
    }

    class schliessenActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            System.out.println("schliessenActionListener");
            schliesseSerialPort();
        }
    }

    class aktualisierenActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            System.out.println("aktualisierenActionListener");
            aktualisiereSerialPort();
        }
    }

    class abfragenActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            if (!slaveStatus) {
                if (abfragen == false) {
                    System.out.println("abfragenActionListener true");
                    empfangen.append("Abfragen gestartet \n");
                    abfragen = true;
                } else {
                    System.out.println("abfragenActionListener false");
                    empfangen.append("Abfragen abgebrochen \n");
                    abfragen = false;
                }
            } else {
                try {
                    if (abfragen == false) {
                        Thread.sleep(30);
                        requestSend();
                        empfangen.append("Abfragen gestartet \n");
                        abfragen = true;
                    } else {
                        empfangen.append("Abfragen abgebrochen \n");
                        abfragen = false;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Diagnoseapplikation.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Diagnoseapplikation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    class schreibenActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            if (!slaveStatus) {
                if (schreiben == false) {
                    System.out.println("schreibennActionListener true");
                    empfangen.append("Änderungen werden gesendet \n");
                    schreiben = true;
                } else {
                    System.out.println("schreibennActionListener false");
                    empfangen.append("Änderungen senden abgebrochen \n");
                    schreiben = false;
                }
            } else {
                try {
                    Thread.sleep(30);
                    writeSend();
                } catch (IOException ex) {
                    Logger.getLogger(Diagnoseapplikation.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Diagnoseapplikation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    class firmwareActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            schliesseSerialPort();
            Runtime runtime = Runtime.getRuntime();
            try {
                String port = (String) auswahl.getSelectedItem();
                String portNr = port.substring(3, 4);
                File folder = fwPathChooser.getSelectedFile();
                String folderpath = folder.toString();
                String file = "Lightpad.bat";
                String[] cmd = {"cmd", "/K", "cd", folderpath, "&", "start", file, portNr};

                Process p1 = runtime.exec(cmd);

            } catch (IOException ioException) {
                System.out.println(ioException.getMessage());
            }
        }
    }

    class modulfirmwareActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            schliesseSerialPort();
            Runtime runtime = Runtime.getRuntime();
            try {
                String port = (String) auswahl.getSelectedItem();
                String portNr = port.substring(3, 4);
                File folder = fwPathChooser.getSelectedFile();
                String folderpath = folder.toString();
                String file = null;
                String geraet = geraeteListe.getSelectedValue().toString();

                for (int i = 0; i < deviceArray.length; i++) {
                    if (geraet.equals(deviceArray[i].deviceName)) {
                        String devicename = deviceArray[i].deviceName;
                        file = devicename.replaceAll(" ", "_").toLowerCase();
                    }
                }
                if (geraet != null) {
                    empfangen.append("Firmwareupdate für " + geraet + " gestartet...");
                    try {
                        String[] cmd = {"cmd", "/K", "cd", folderpath, "&", "start", file, portNr};
                        Process p1 = runtime.exec(cmd);
                    } catch (IOException ioException) {
                        System.out.println(ioException.getMessage());
                    }
                }
            } catch (java.lang.NullPointerException e) {
                empfangen.append("Kein Gerät ausgewählt oder Pfad angegeben");
//                System.out.println("Kein Gerät ausgewählt oder Pfad angegeben");
            }
        }
    }

    class manualRequestActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            try {
                String gereat = geraeteListe.getSelectedValue().toString();
                for (int i = 0; i < deviceArray.length; i++) {
                    if (gereat.equals(deviceArray[i].deviceName)) {
                        deviceArray[i].manualrequest = true;
                        empfangen.append("Manuelle Abfrage von " + deviceArray[i].deviceName + " gestartet\n");
                        manualDeviceRequest();
                    }
                }
            } catch (java.lang.NullPointerException e) {
                empfangen.append("Kein Gerät angewählt\n");
            } catch (IOException ex) {
                Logger.getLogger(Diagnoseapplikation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    class errorRequestActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            if (slaveStatus) {
                try {
                    Thread.sleep(30);
                    errorRequest2718();
                    Thread.sleep(30);
                    errorRequest2719();
                    errorRead = true;
                } catch (InterruptedException | IOException ex) {
                    Logger.getLogger(Diagnoseapplikation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    class masterModeActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            try {
                Thread.sleep(30);
                masterMode();
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(Diagnoseapplikation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    class slaveModeActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            try {
                Thread.sleep(30);
                slaveMode();
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(Diagnoseapplikation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //https://stackoverflow.com/questions/9413656/how-to-use-timer-class-to-call-a-method-do-something-reset-timer-repeat
    public void JavaTimer() {
        // Timer startet nach 2 Sekunden Slavemodus, sofern kein Device online ist
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                boolean completed = true;
                for (int i = 0; i < deviceArray.length; i++) {
                    if (deviceArray[i].deviceStatus) {
                        completed = false;
                        break;
                    }
                }
                if (!completed) {
                    slaveStatus = false;
                    System.out.println("Slavestatus false gesetzt" + System.currentTimeMillis());
                } else {
                    slaveStatus = true;
                    empfangen.append("Keine Kommunikation erkannt\nManuelle Abfragen auslösen\n");
                }
            }
        };
        timer.schedule(task, 2000);
    }

    //https://coderanch.com/t/335943/java/Changing-background-color-JList
    private class DisabledItemListCellRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component comp = super.getListCellRendererComponent(list, value, index, false, false);
            // 0. und 1. Device anderst, da nur eins dargestellt wird
            if (0 < index) {
                if (deviceArray[index + 1].deviceStatus) {
                    if (isSelected) {
                        comp.setForeground(Color.green);
                        comp.setBackground(Color.gray);
                    } else {
                        comp.setForeground(Color.black);
                        comp.setBackground(Color.green);
                    }
                } else if (!deviceArray[index + 1].deviceStatus) {
                    if (isSelected) {
                        comp.setForeground(Color.white);
                        comp.setBackground(Color.gray);
                    } else if (deviceArray[index + 1].manualrequest) {
                        comp.setForeground(Color.black);
                        comp.setBackground(Color.red);
                    }
                    if (isSelected && deviceArray[index + 1].manualrequest) {
                        comp.setForeground(Color.red);
                        comp.setBackground(Color.black);
                    }
                } else {
                    comp.setForeground(Color.black);
                    comp.setBackground(Color.white);
                }
            } else {
                if (deviceArray[index].deviceStatus) {
                    if (isSelected) {
                        comp.setForeground(Color.green);
                        comp.setBackground(Color.gray);
                    } else {
                        comp.setForeground(Color.black);
                        comp.setBackground(Color.green);
                    }
                } else if (!deviceArray[index].deviceStatus) {
                    if (isSelected) {
                        comp.setForeground(Color.white);
                        comp.setBackground(Color.gray);
                    } else if (deviceArray[index].manualrequest) {
                        comp.setForeground(Color.black);
                        comp.setBackground(Color.red);
                    }
                    if (isSelected && deviceArray[index + 1].manualrequest) {
                        comp.setForeground(Color.red);
                        comp.setBackground(Color.black);
                    }
                } else {
                    comp.setForeground(Color.black);
                    comp.setBackground(Color.white);
                }
            }
            SwingWorker worker = new SwingWorker() {
                @Override
                public Object doInBackground() {
                    return null;
                }

                @Override
                public void done() {
                    list.repaint();
                }
            };
            worker.execute();
            return comp;
        }
    }

    class serialPortEventListener implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            System.out.println("serialPortEventlistener");

            switch (event.getEventType()) {
                case SerialPortEvent.DATA_AVAILABLE: {
                    try {
                        serialPortDatenVerfuegbar();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Diagnoseapplikation.class.getName()).log(Level.SEVERE, null, ex);
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
