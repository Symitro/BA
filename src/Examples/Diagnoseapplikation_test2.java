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
import Bachelorarbeit_regent.Device;
import Bachelorarbeit_regent.data.Datacollection;
import Bachelorarbeit_regent.data.Livedatacollection;
import Bachelorarbeit_regent.data.Livedataentry;
import Bachelorarbeit_regent.misc.ConversionHelper;
import Bachelorarbeit_regent.misc.CRC16;
import Bachelorarbeit_regent.misc.CSVReader;
import Bachelorarbeit_regent.misc.AdressList;
import Bachelorarbeit_regent.misc.Request;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
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

//https://www.mikrocontroller.net/articles/Serielle_Schnittstelle_unter_Java
// TODO Dialog zur Konfiguration der Schnittstellenparameter
public class Diagnoseapplikation_test2 extends JFrame implements TableModelListener {

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
    byte changedDevice;
    int changedValue;
    String hexofchangedvalue;
    int selectedtab;

    // Abfragen für Control Unit M4, maximal 16 HR pro Abfrage für Performance
    byte[] sendstreamCUM4_1 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //32
    byte[] sendstreamCUM4_2 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamCUM4_3 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x01}; //37
    byte[] sendstreamCUM4_4 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamCUM4_5 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamCUM4_6 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x01}; //05
    byte[] sendstreamCUM4_7 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamCUM4_8 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x01}; //44
    byte[] sendstreamCUM4_9 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamCUM4_10 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x2C, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamCUM4_11 = {(byte) 0x08, (byte) 0x03, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //06
    byte[] sendstreamCUM4_12 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x01, (byte) 0x00, (byte) 0x01}; //79
    byte[] sendstreamCUM4_13 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x11, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamCUM4_14 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x21, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamCUM4_15 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x31, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamCUM4_16 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x41, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamCUM4_17 = {(byte) 0x08, (byte) 0x03, (byte) 0x27, (byte) 0x17, (byte) 0x00, (byte) 0x01}; //04
    byte[] sendstreamCUM4_18 = {(byte) 0x08, (byte) 0x03, (byte) 0x35, (byte) 0x05, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamCUM4_19 = {(byte) 0x08, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //19
    byte[] sendstreamCUM4_20 = {(byte) 0x08, (byte) 0x03, (byte) 0x40, (byte) 0x10, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamCUM4_21 = {(byte) 0x08, (byte) 0x03, (byte) 0x42, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //03
    byte[] sendstreamCUM4_22 = {(byte) 0x08, (byte) 0x03, (byte) 0x45, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //32
    byte[] sendstreamCUM4_23 = {(byte) 0x08, (byte) 0x03, (byte) 0x45, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCUM4_24 = {(byte) 0x08, (byte) 0x03, (byte) 0x60, (byte) 0x00, (byte) 0x00, (byte) 0x04}; //04
    byte[] sendstreamCUM4_25 = {(byte) 0x08, (byte) 0x03, (byte) 0x60, (byte) 0x06, (byte) 0x00, (byte) 0x04}; //04
    byte[] sendstreamCUM4_26 = {(byte) 0x08, (byte) 0x03, (byte) 0x61, (byte) 0x00, (byte) 0x00, (byte) 0x04}; //04
    byte[] sendstreamCUM4_27 = {(byte) 0x08, (byte) 0x03, (byte) 0x62, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //53
    byte[] sendstreamCUM4_28 = {(byte) 0x08, (byte) 0x03, (byte) 0x62, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCUM4_29 = {(byte) 0x08, (byte) 0x03, (byte) 0x62, (byte) 0x20, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCUM4_30 = {(byte) 0x08, (byte) 0x03, (byte) 0x62, (byte) 0x30, (byte) 0x00, (byte) 0x05}; //^
    byte[] sendstreamCUM4_31 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //69
    byte[] sendstreamCUM4_32 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCUM4_33 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x20, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCUM4_34 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x30, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCUM4_35 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x40, (byte) 0x00, (byte) 0x05}; //^

    byte[][] sendarraysCUM4 = {sendstreamCUM4_1, sendstreamCUM4_2, sendstreamCUM4_3, sendstreamCUM4_4, sendstreamCUM4_5, sendstreamCUM4_6, sendstreamCUM4_7, sendstreamCUM4_8, sendstreamCUM4_9, sendstreamCUM4_10, sendstreamCUM4_11, sendstreamCUM4_12, sendstreamCUM4_13, sendstreamCUM4_14, sendstreamCUM4_15, sendstreamCUM4_16, sendstreamCUM4_17, sendstreamCUM4_18, sendstreamCUM4_19, sendstreamCUM4_20, sendstreamCUM4_21, sendstreamCUM4_22, sendstreamCUM4_23, sendstreamCUM4_24, sendstreamCUM4_25, sendstreamCUM4_26, sendstreamCUM4_27, sendstreamCUM4_28, sendstreamCUM4_29, sendstreamCUM4_30, sendstreamCUM4_31, sendstreamCUM4_32, sendstreamCUM4_33, sendstreamCUM4_34, sendstreamCUM4_35};

    // Abfragen für Control Unit, maximal 16 HR pro Abfrage für Performance
    byte[] sendstreamCU_1 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //32
    byte[] sendstreamCU_2 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCU_3 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x10}; //37
    byte[] sendstreamCU_4 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCU_5 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x05}; //^
    byte[] sendstreamCU_6 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x05}; //05
    byte[] sendstreamCU_7 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamCU_8 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //44
    byte[] sendstreamCU_9 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCU_10 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x2C, (byte) 0x00, (byte) 0x0C}; //^
    byte[] sendstreamCU_11 = {(byte) 0x08, (byte) 0x03, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x04}; //04
    byte[] sendstreamCU_12 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x01, (byte) 0x00, (byte) 0x10}; //79
    byte[] sendstreamCU_13 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x11, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCU_14 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x21, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCU_15 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x31, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCU_16 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x41, (byte) 0x00, (byte) 0x0F}; //^
    byte[] sendstreamCU_17 = {(byte) 0x08, (byte) 0x03, (byte) 0x27, (byte) 0x17, (byte) 0x00, (byte) 0x04}; //04
    byte[] sendstreamCU_18 = {(byte) 0x08, (byte) 0x03, (byte) 0x35, (byte) 0x05, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamCU_19 = {(byte) 0x08, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //19
    byte[] sendstreamCU_20 = {(byte) 0x08, (byte) 0x03, (byte) 0x40, (byte) 0x10, (byte) 0x00, (byte) 0x03}; //^
    byte[] sendstreamCU_21 = {(byte) 0x08, (byte) 0x03, (byte) 0x42, (byte) 0x00, (byte) 0x00, (byte) 0x03}; //03
    byte[] sendstreamCU_22 = {(byte) 0x08, (byte) 0x03, (byte) 0x45, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //31
    byte[] sendstreamCU_23 = {(byte) 0x08, (byte) 0x03, (byte) 0x45, (byte) 0x10, (byte) 0x00, (byte) 0x0F}; //^
    byte[] sendstreamCU_24 = {(byte) 0x08, (byte) 0x03, (byte) 0x60, (byte) 0x06, (byte) 0x00, (byte) 0x04}; //04
    byte[] sendstreamCU_25 = {(byte) 0x08, (byte) 0x03, (byte) 0x62, (byte) 0x03, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamCU_26 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x03, (byte) 0x00, (byte) 0x01}; //01

    byte[][] sendarraysCU = {sendstreamCU_1, sendstreamCU_2, sendstreamCU_3, sendstreamCU_4, sendstreamCU_5, sendstreamCU_6, sendstreamCU_7, sendstreamCU_8, sendstreamCU_9, sendstreamCU_10, sendstreamCU_11, sendstreamCU_12, sendstreamCU_13, sendstreamCU_14, sendstreamCU_15, sendstreamCU_16, sendstreamCU_17, sendstreamCU_18, sendstreamCU_19, sendstreamCU_20, sendstreamCU_21, sendstreamCU_22, sendstreamCU_23, sendstreamCU_24, sendstreamCU_25, sendstreamCU_26};

    // Abfragen des Alone at Works 2.0
    byte[] sendstreamAAW_1 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //32
    byte[] sendstreamAAW_2 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamAAW_3 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x10}; //37
    byte[] sendstreamAAW_4 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamAAW_5 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x05}; //^
    byte[] sendstreamAAW_6 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x05}; //5
    byte[] sendstreamAAW_7 = {(byte) 0x10, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamAAW_8 = {(byte) 0x10, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //32
    byte[] sendstreamAAW_9 = {(byte) 0x10, (byte) 0x03, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamAAW_10 = {(byte) 0x10, (byte) 0x03, (byte) 0x10, (byte) 0x10, (byte) 0x00, (byte) 0x07}; //07
    byte[] sendstreamAAW_11 = {(byte) 0x10, (byte) 0x03, (byte) 0x10, (byte) 0x20, (byte) 0x00, (byte) 0x10}; //28
    byte[] sendstreamAAW_12 = {(byte) 0x10, (byte) 0x03, (byte) 0x10, (byte) 0x30, (byte) 0x00, (byte) 0x0C}; //^
    byte[] sendstreamAAW_13 = {(byte) 0x10, (byte) 0x03, (byte) 0x10, (byte) 0x68, (byte) 0x00, (byte) 0x09}; //09
    byte[] sendstreamAAW_14 = {(byte) 0x10, (byte) 0x03, (byte) 0x11, (byte) 0x00, (byte) 0x00, (byte) 0x07}; //07
    byte[] sendstreamAAW_15 = {(byte) 0x10, (byte) 0x03, (byte) 0x27, (byte) 0x18, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamAAW_16 = {(byte) 0x10, (byte) 0x03, (byte) 0x30, (byte) 0x00, (byte) 0x00, (byte) 0x09}; //09
    byte[] sendstreamAAW_17 = {(byte) 0x10, (byte) 0x03, (byte) 0x40, (byte) 0x01, (byte) 0x00, (byte) 0x05}; //05

    byte[][] sendarraysAAW = {sendstreamAAW_1, sendstreamAAW_2, sendstreamAAW_3, sendstreamAAW_4, sendstreamAAW_5, sendstreamAAW_6, sendstreamAAW_7, sendstreamAAW_8, sendstreamAAW_9, sendstreamAAW_10, sendstreamAAW_11, sendstreamAAW_12, sendstreamAAW_13, sendstreamAAW_14, sendstreamAAW_15, sendstreamAAW_16, sendstreamAAW_17};

    // Abfragen des linken Panel
    byte[] sendstreamPL_1 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //32
    byte[] sendstreamPL_2 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamPL_3 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x01}; //37
    byte[] sendstreamPL_4 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamPL_5 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamPL_6 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x01}; //05
    byte[] sendstreamPL_7 = {(byte) 0x11, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamPL_8 = {(byte) 0x11, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x01}; //32
    byte[] sendstreamPL_9 = {(byte) 0x11, (byte) 0x03, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamPL_10 = {(byte) 0x11, (byte) 0x03, (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamPL_11 = {(byte) 0x11, (byte) 0x03, (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //03
    byte[] sendstreamPL_12 = {(byte) 0x11, (byte) 0x03, (byte) 0x13, (byte) 0x07, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamPL_13 = {(byte) 0x11, (byte) 0x03, (byte) 0x14, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //13
    byte[] sendstreamPL_14 = {(byte) 0x11, (byte) 0x03, (byte) 0x21, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //07
    byte[] sendstreamPL_15 = {(byte) 0x11, (byte) 0x03, (byte) 0x27, (byte) 0x18, (byte) 0x00, (byte) 0x01}; //02
    byte[] sendstreamPL_16 = {(byte) 0x11, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //07

//        // Abfragen des linken Panel
//    byte[] sendstreamPL_1 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //32
//    byte[] sendstreamPL_2 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //^
//    byte[] sendstreamPL_3 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x10}; //37
//    byte[] sendstreamPL_4 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x05}; //^
//    byte[] sendstreamPL_5 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x05}; //^
//    byte[] sendstreamPL_6 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x05}; //05
//    byte[] sendstreamPL_7 = {(byte) 0x11, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
//    byte[] sendstreamPL_8 = {(byte) 0x11, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //32
//    byte[] sendstreamPL_9 = {(byte) 0x11, (byte) 0x03, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0x10}; //^
//    byte[] sendstreamPL_10 = {(byte) 0x11, (byte) 0x03, (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
//    byte[] sendstreamPL_11 = {(byte) 0x11, (byte) 0x03, (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x03}; //03
//    byte[] sendstreamPL_12 = {(byte) 0x11, (byte) 0x03, (byte) 0x13, (byte) 0x07, (byte) 0x00, (byte) 0x01}; //01
//    byte[] sendstreamPL_13 = {(byte) 0x11, (byte) 0x03, (byte) 0x14, (byte) 0x00, (byte) 0x00, (byte) 0x0D}; //13
//    byte[] sendstreamPL_14 = {(byte) 0x11, (byte) 0x03, (byte) 0x21, (byte) 0x00, (byte) 0x00, (byte) 0x07}; //07
//    byte[] sendstreamPL_15 = {(byte) 0x11, (byte) 0x03, (byte) 0x27, (byte) 0x18, (byte) 0x00, (byte) 0x02}; //02
//    byte[] sendstreamPL_16 = {(byte) 0x11, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x07}; //07
    byte[][] sendarraysPL = {sendstreamPL_1, sendstreamPL_2, sendstreamPL_3, sendstreamPL_4, sendstreamPL_5, sendstreamPL_6, sendstreamPL_7, sendstreamPL_8, sendstreamPL_9, sendstreamPL_10, sendstreamPL_11, sendstreamPL_12, sendstreamPL_13, sendstreamPL_14, sendstreamPL_15, sendstreamPL_16};

    // Abfrage des rechten Panel
    byte[] sendstreamPR_1 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //32
    byte[] sendstreamPR_2 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamPR_3 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x01}; //37
    byte[] sendstreamPR_4 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamPR_5 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamPR_6 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x01}; //05
    byte[] sendstreamPR_7 = {(byte) 0x12, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamPR_8 = {(byte) 0x12, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x01}; //32
    byte[] sendstreamPR_9 = {(byte) 0x12, (byte) 0x03, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0x01}; //^
    byte[] sendstreamPR_10 = {(byte) 0x12, (byte) 0x03, (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamPR_11 = {(byte) 0x12, (byte) 0x03, (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //03
    byte[] sendstreamPR_12 = {(byte) 0x12, (byte) 0x03, (byte) 0x13, (byte) 0x07, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamPR_13 = {(byte) 0x12, (byte) 0x03, (byte) 0x14, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //13
    byte[] sendstreamPR_14 = {(byte) 0x12, (byte) 0x03, (byte) 0x21, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //07
    byte[] sendstreamPR_15 = {(byte) 0x12, (byte) 0x03, (byte) 0x27, (byte) 0x18, (byte) 0x00, (byte) 0x01}; //02
    byte[] sendstreamPR_16 = {(byte) 0x12, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //07
//        // Abfrage des rechten Panel
//    byte[] sendstreamPR_1 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //32
//    byte[] sendstreamPR_2 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //^
//    byte[] sendstreamPR_3 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x10}; //37
//    byte[] sendstreamPR_4 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x10}; //^
//    byte[] sendstreamPR_5 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x05}; //^
//    byte[] sendstreamPR_6 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x05}; //05
//    byte[] sendstreamPR_7 = {(byte) 0x12, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
//    byte[] sendstreamPR_8 = {(byte) 0x12, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //32
//    byte[] sendstreamPR_9 = {(byte) 0x12, (byte) 0x03, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0x10}; //^
//    byte[] sendstreamPR_10 = {(byte) 0x12, (byte) 0x03, (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
//    byte[] sendstreamPR_11 = {(byte) 0x12, (byte) 0x03, (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x03}; //03
//    byte[] sendstreamPR_12 = {(byte) 0x12, (byte) 0x03, (byte) 0x13, (byte) 0x07, (byte) 0x00, (byte) 0x01}; //01
//    byte[] sendstreamPR_13 = {(byte) 0x12, (byte) 0x03, (byte) 0x14, (byte) 0x00, (byte) 0x00, (byte) 0x0D}; //13
//    byte[] sendstreamPR_14 = {(byte) 0x12, (byte) 0x03, (byte) 0x21, (byte) 0x00, (byte) 0x00, (byte) 0x07}; //07
//    byte[] sendstreamPR_15 = {(byte) 0x12, (byte) 0x03, (byte) 0x27, (byte) 0x18, (byte) 0x00, (byte) 0x02}; //02
//    byte[] sendstreamPR_16 = {(byte) 0x12, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x07}; //07

    byte[][] sendarraysPR = {sendstreamPR_1, sendstreamPR_2, sendstreamPR_3, sendstreamPR_4, sendstreamPR_5, sendstreamPR_6, sendstreamPR_7, sendstreamPR_8, sendstreamPR_9, sendstreamPR_10, sendstreamPR_11, sendstreamPR_12, sendstreamPR_13, sendstreamPR_14, sendstreamPR_15, sendstreamPR_16};

    // Abfrage des Senselight Head 1
    byte[] sendstreamSH1_1 = {(byte) 0x17, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //32
    byte[] sendstreamSH1_2 = {(byte) 0x17, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamSH1_3 = {(byte) 0x17, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x10}; //37
    byte[] sendstreamSH1_4 = {(byte) 0x17, (byte) 0x03, (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamSH1_5 = {(byte) 0x17, (byte) 0x03, (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x05}; //^
    byte[] sendstreamSH1_6 = {(byte) 0x17, (byte) 0x03, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x05}; //05
    byte[] sendstreamSH1_7 = {(byte) 0x17, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamSH1_8 = {(byte) 0x17, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //44
    byte[] sendstreamSH1_9 = {(byte) 0x17, (byte) 0x03, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamSH1_10 = {(byte) 0x17, (byte) 0x03, (byte) 0x02, (byte) 0x30, (byte) 0x00, (byte) 0x0C}; //^
    byte[] sendstreamSH1_11 = {(byte) 0x17, (byte) 0x03, (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamSH1_12 = {(byte) 0x17, (byte) 0x03, (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x0B}; //11
    byte[] sendstreamSH1_13 = {(byte) 0x17, (byte) 0x03, (byte) 0x14, (byte) 0x00, (byte) 0x00, (byte) 0x06}; //06
    byte[] sendstreamSH1_14 = {(byte) 0x17, (byte) 0x03, (byte) 0x15, (byte) 0x00, (byte) 0x00, (byte) 0x07}; //07
    byte[] sendstreamSH1_15 = {(byte) 0x17, (byte) 0x03, (byte) 0x16, (byte) 0x00, (byte) 0x00, (byte) 0x02}; //02
    byte[] sendstreamSH1_16 = {(byte) 0x17, (byte) 0x03, (byte) 0x27, (byte) 0x18, (byte) 0x00, (byte) 0x02}; //02
    byte[] sendstreamSH1_17 = {(byte) 0x17, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x09}; //09

    byte[][] sendarraysSH1 = {sendstreamSH1_1, sendstreamSH1_2, sendstreamSH1_3, sendstreamSH1_4, sendstreamSH1_5, sendstreamSH1_6, sendstreamSH1_7, sendstreamSH1_8, sendstreamSH1_9, sendstreamSH1_10, sendstreamSH1_11, sendstreamSH1_12, sendstreamSH1_13, sendstreamSH1_14, sendstreamSH1_15, sendstreamSH1_16, sendstreamSH1_17};

    // Abfrage des Senselight Head 2
    byte[] sendstreamSH2_1 = {(byte) 0x18, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //32
    byte[] sendstreamSH2_2 = {(byte) 0x18, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamSH2_3 = {(byte) 0x18, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x10}; //37
    byte[] sendstreamSH2_4 = {(byte) 0x18, (byte) 0x03, (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamSH2_5 = {(byte) 0x18, (byte) 0x03, (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x05}; //^
    byte[] sendstreamSH2_6 = {(byte) 0x18, (byte) 0x03, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x05}; //05
    byte[] sendstreamSH2_7 = {(byte) 0x18, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamSH2_8 = {(byte) 0x18, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //44
    byte[] sendstreamSH2_9 = {(byte) 0x18, (byte) 0x03, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamSH2_10 = {(byte) 0x18, (byte) 0x03, (byte) 0x02, (byte) 0x30, (byte) 0x00, (byte) 0x0C}; //^
    byte[] sendstreamSH2_11 = {(byte) 0x18, (byte) 0x03, (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamSH2_12 = {(byte) 0x18, (byte) 0x03, (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x0B}; //11
    byte[] sendstreamSH2_13 = {(byte) 0x18, (byte) 0x03, (byte) 0x14, (byte) 0x00, (byte) 0x00, (byte) 0x06}; //06
    byte[] sendstreamSH2_14 = {(byte) 0x18, (byte) 0x03, (byte) 0x15, (byte) 0x00, (byte) 0x00, (byte) 0x07}; //07
    byte[] sendstreamSH2_15 = {(byte) 0x18, (byte) 0x03, (byte) 0x16, (byte) 0x00, (byte) 0x00, (byte) 0x02}; //02
    byte[] sendstreamSH2_16 = {(byte) 0x18, (byte) 0x03, (byte) 0x27, (byte) 0x18, (byte) 0x00, (byte) 0x02}; //02
    byte[] sendstreamSH2_17 = {(byte) 0x18, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x09}; //09

    byte[][] sendarraysSH2 = {sendstreamSH2_1, sendstreamSH2_2, sendstreamSH2_3, sendstreamSH2_4, sendstreamSH2_5, sendstreamSH2_6, sendstreamSH2_7, sendstreamSH2_8, sendstreamSH2_9, sendstreamSH2_10, sendstreamSH2_11, sendstreamSH2_12, sendstreamSH2_13, sendstreamSH2_14, sendstreamSH2_15, sendstreamSH2_16, sendstreamSH2_17};

    // Abfrage des Senselight Head 3
    byte[] sendstreamSH3_1 = {(byte) 0x19, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //32
    byte[] sendstreamSH3_2 = {(byte) 0x19, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamSH3_3 = {(byte) 0x19, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x10}; //37
    byte[] sendstreamSH3_4 = {(byte) 0x19, (byte) 0x03, (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamSH3_5 = {(byte) 0x19, (byte) 0x03, (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x05}; //^
    byte[] sendstreamSH3_6 = {(byte) 0x19, (byte) 0x03, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x05}; //05
    byte[] sendstreamSH3_7 = {(byte) 0x19, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamSH3_8 = {(byte) 0x19, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //44
    byte[] sendstreamSH3_9 = {(byte) 0x19, (byte) 0x03, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamSH3_10 = {(byte) 0x19, (byte) 0x03, (byte) 0x02, (byte) 0x30, (byte) 0x00, (byte) 0x0C}; //^
    byte[] sendstreamSH3_11 = {(byte) 0x19, (byte) 0x03, (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamSH3_12 = {(byte) 0x19, (byte) 0x03, (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x0B}; //11
    byte[] sendstreamSH3_13 = {(byte) 0x19, (byte) 0x03, (byte) 0x14, (byte) 0x00, (byte) 0x00, (byte) 0x06}; //06
    byte[] sendstreamSH3_14 = {(byte) 0x19, (byte) 0x03, (byte) 0x15, (byte) 0x00, (byte) 0x00, (byte) 0x07}; //07
    byte[] sendstreamSH3_15 = {(byte) 0x19, (byte) 0x03, (byte) 0x16, (byte) 0x00, (byte) 0x00, (byte) 0x02}; //02
    byte[] sendstreamSH3_16 = {(byte) 0x19, (byte) 0x03, (byte) 0x27, (byte) 0x18, (byte) 0x00, (byte) 0x02}; //02
    byte[] sendstreamSH3_17 = {(byte) 0x19, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x09}; //09

    byte[][] sendarraysSH3 = {sendstreamSH3_1, sendstreamSH3_2, sendstreamSH3_3, sendstreamSH3_4, sendstreamSH3_5, sendstreamSH3_6, sendstreamSH3_7, sendstreamSH3_8, sendstreamSH3_9, sendstreamSH3_10, sendstreamSH3_11, sendstreamSH3_12, sendstreamSH3_13, sendstreamSH3_14, sendstreamSH3_15, sendstreamSH3_16, sendstreamSH3_17};

    // Abfrage des Senselight Head 4
    byte[] sendstreamSH4_1 = {(byte) 0x1A, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //32
    byte[] sendstreamSH4_2 = {(byte) 0x1A, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamSH4_3 = {(byte) 0x1A, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x10}; //37
    byte[] sendstreamSH4_4 = {(byte) 0x1A, (byte) 0x03, (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamSH4_5 = {(byte) 0x1A, (byte) 0x03, (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x05}; //^
    byte[] sendstreamSH4_6 = {(byte) 0x1A, (byte) 0x03, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x05}; //05
    byte[] sendstreamSH4_7 = {(byte) 0x1A, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamSH4_8 = {(byte) 0x1A, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //44
    byte[] sendstreamSH4_9 = {(byte) 0x1A, (byte) 0x03, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamSH4_10 = {(byte) 0x1A, (byte) 0x03, (byte) 0x02, (byte) 0x30, (byte) 0x00, (byte) 0x0C}; //^
    byte[] sendstreamSH4_11 = {(byte) 0x1A, (byte) 0x03, (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamSH4_12 = {(byte) 0x1A, (byte) 0x03, (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x0B}; //11
    byte[] sendstreamSH4_13 = {(byte) 0x1A, (byte) 0x03, (byte) 0x14, (byte) 0x00, (byte) 0x00, (byte) 0x06}; //06
    byte[] sendstreamSH4_14 = {(byte) 0x1A, (byte) 0x03, (byte) 0x15, (byte) 0x00, (byte) 0x00, (byte) 0x07}; //07
    byte[] sendstreamSH4_15 = {(byte) 0x1A, (byte) 0x03, (byte) 0x16, (byte) 0x00, (byte) 0x00, (byte) 0x02}; //02
    byte[] sendstreamSH4_16 = {(byte) 0x1A, (byte) 0x03, (byte) 0x27, (byte) 0x18, (byte) 0x00, (byte) 0x02}; //02
    byte[] sendstreamSH4_17 = {(byte) 0x1A, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x09}; //09

    byte[][] sendarraysSH4 = {sendstreamSH4_1, sendstreamSH4_2, sendstreamSH4_3, sendstreamSH4_4, sendstreamSH4_5, sendstreamSH4_6, sendstreamSH4_7, sendstreamSH4_8, sendstreamSH4_9, sendstreamSH4_10, sendstreamSH4_11, sendstreamSH4_12, sendstreamSH4_13, sendstreamSH4_14, sendstreamSH4_15, sendstreamSH4_16, sendstreamSH4_17};

//    byte[][][] sendarrays = {sendarraysCUM4, sendarraysCU, sendarraysAAW, sendarraysPL, sendarraysPR, sendarraysSH1, sendarraysSH2, sendarraysSH3, sendarraysSH4};
    // Datacollections werden in ein Array gefüllt
    Datacollection[] datacollectionArray;

    // Livedatacollections werden in ein Array gefüllt
    Livedatacollection[] livedatacollectionArray;

    Device[] deviceArray;

    int actualDeviceCounter = 0;

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
    JButton firmware = new JButton("Gesamte Lampen-Firmware aktualisieren");
    JButton modulfirmware = new JButton("Ausgewählte Modul-Firmware aktualisieren");
    JButton statusabfrage = new JButton("manuelle Statusabfrage");

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
        
//        Request.requestGenerate(CUM4Collection.toString());
        
        this.deviceArray = new Device[]{
            new Device(sendarraysCUM4, "Control Unit M4", "0803020006E447", (byte) 0x08),
            new Device(sendarraysCU, "Control Unit", "0803020002E584", (byte) 0x08),
            new Device(sendarraysAAW, "Alone at Work 2.0", "100302", (byte) 0x10),
            new Device(sendarraysPL, "Panel Links", "110302", (byte) 0x11),
            new Device(sendarraysPR, "Panel Rechts", "120302", (byte) 0x12),
            new Device(sendarraysSH1, "Senselighthead 1", "170304", (byte) 0x17),
            new Device(sendarraysSH2, "Senselighthead 2", "180304", (byte) 0x18),
            new Device(sendarraysSH3, "Senselighthead 3", "190304", (byte) 0x19),
            new Device(sendarraysSH4, "Senselighthead 4", "1A0304", (byte) 0x1A),
            new Device(null, "Connected Lighting", "150302", (byte) 0x15),
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

        empfangenJScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        empfangenJScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        empfangenJScrollPane.setViewportView(empfangen);

        jmenubar1.add(jmenu1);
        jmenubar1.add(jmenu2);
        jmenubar1.add(jmenu3);
        jmenu1.add(jmenuitem1);
        jmenu1.add(jmenuitem2);
        jmenu1.add(jmenuitem3);
        jmenu2.add(jmenuitem4);
        jmenu2.add(jmenuitem5);
        jmenu3.add(jmenuitem6);
        jmenu1.setText("Datei");
        jmenu2.setText("Firmware");
        jmenu3.setText("Hilfe");
        jmenuitem1.setText("Config laden");
        jmenuitem2.setText("Config speichern");
        jmenuitem3.setText("Beenden");
        jmenuitem4.setText("Ordner für Firmware auswählen");
        jmenuitem5.setText("Manuelle Firmwareauswahl");
        jmenuitem6.setText("Über");
        setJMenuBar(jmenubar1);
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
//                                    empfangen.append(deviceArray[i].deviceName + " angewählt\n");
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
//                                    empfangen.append(deviceArray[i].deviceName + " angewählt\n");
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
            try {
                Runtime.getRuntime().exec("cmd /c start " + fwFileChooser.getSelectedFile());
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
        if (deviceArray[actualDeviceCounter].deviceStatus) {
            if (!deviceArray[actualDeviceCounter].isRequestFinished()) {
                byte[] sendstream = deviceArray[actualDeviceCounter].getNextByteArray();
                CRC16 crc = new CRC16();
                crc.update(sendstream, 0, sendstream.length);
                crc.getAll();
                outputStream.write(crc.getAll());

                String sendstreamstring = ConversionHelper.byteArrayToHexString(sendstream);
                System.out.println("Aktuelle Abfrage: " + deviceArray[actualDeviceCounter].deviceName);
                System.out.println("Senden erfolgreich: " + sendstreamstring);

            } else {
                actualDeviceCounter++;
                byte[] sendstream = deviceArray[actualDeviceCounter].getNextByteArray();
                CRC16 crc = new CRC16();
                crc.update(sendstream, 0, sendstream.length);
                crc.getAll();
                outputStream.write(crc.getAll());

                String sendstreamstring = ConversionHelper.byteArrayToHexString(sendstream);
                System.out.println("Aktuelle Abfrage: " + deviceArray[actualDeviceCounter].deviceName);
                System.out.println("Senden der letzten Nachrichterfolgreich: " + sendstreamstring);
            }
        } else {
            actualDeviceCounter++;
        }
    }

    void writeSend() throws IOException {
        // Alte Daten mit neuen Daten vergleichen, falls Änderungen 
        // Bsp. 08 06 0000 0001 "CRC" senden
//        String gereat = geraeteListe.getSelectedValue().toString();

//        byte devicebyte = 0;
//
//        for (int i = 0; i < deviceArray.length; i++) {
//            if (gereat.equals(deviceArray[i].deviceName)) {
//                devicebyte = (byte) deviceArray[i].devicebyte;
//            }
//        }
//        StringBuilder sb = new StringBuilder();
////        sb.append(String.format("%02X ", devicebyte));
////        System.out.println(sb.toString());
//        System.out.println("Integer.toHexString: " + Integer.toHexString(devicebyte & 0xFF));
//        System.out.println("devicebyte" + devicebyte);
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
        empfangen.append("Änderungen übernommen");
        changedDevice = 0;
    }

    void deviceRequest() throws IOException {
        byte[] devicetype = {(byte) 0x08, (byte) 0x03, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //1
        CRC16 crc = new CRC16();
        crc.update(devicetype, 0, devicetype.length);
        crc.getAll();
        outputStream.write(crc.getAll());
    }

    void manualDeviceRequest() throws IOException {
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

    void serialPortDatenVerfuegbar() throws InterruptedException {
        try {
            System.out.println("Starttime" + getCurrentTimeStamp());
            byte[] data = new byte[170];
            byte[] requestbuffer = new byte[3];
            byte[] crcbuffer = new byte[2];
            int num;

            requestbuffer[0] = deviceArray[actualDeviceCounter].getSingleByteArray(0);
            requestbuffer[1] = deviceArray[actualDeviceCounter].getSingleByteArray(1);
            requestbuffer[2] = (byte) (2 * deviceArray[actualDeviceCounter].getSingleByteArray(5));
            String requestbufferstring = ConversionHelper.byteArrayToHexString(requestbuffer);

            ArrayList<String> adresslist = AdressList.adressArray(deviceArray[actualDeviceCounter].requestArray, deviceArray[actualDeviceCounter].lastmessageCounter);

            while (inputStream.available() > 0) {

                num = inputStream.read(data, 0, data.length);
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
//                Iterator<Integer> repeatIterator = repeatList.iterator();

                // Multiple Nachrichten finden
                for (int i = 0; i < deviceArray.length; i++) {
                    for (int z = 0; z < message.length() - 4; z++) {
                        if (deviceArray[i].shortHexRead.equals(message.substring(z, z + 4)) || deviceArray[i].shortHexWrite.equals(message.substring(z, z + 4))) {
                            if (!repeatList.contains(z)) {
                                repeatList.add(z);
                            }
                        }
                    }
                }
                // Sorting of arraylist using Collections.sort
                Collections.sort(repeatList);
                // Multiple Nachrichten aufteilen
                for (int i = 0; i < repeatList.size(); i++) {
                    int repeatstartposition;
                    int repeatendposition;

                    if (i + 1 == repeatList.size()) {
                        repeatstartposition = repeatList.get(i);
                        repeatendposition = message.length();
                    } else {
                        repeatstartposition = repeatList.get(i);
                        repeatendposition = repeatList.get(i + 1);
                    }
                    messageList.add(message.substring(repeatstartposition, repeatendposition));
                }
                byte[] singleMessageArray = new byte[messageList.size()];
                for (int z = 0; z < messageList.size(); z++) {
                    System.out.println("messageList: " + messageList.get(z));

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
                        String hexAdress = null;
                        String requestLength = null;
                        String dataValue = null;
                        ArrayList<String> dataValueArrayList = new ArrayList<>();
                        ArrayList<String> requestAdressList = new ArrayList<>();
                        if (messageList.get(z).length() == 16) {
                            hexAdress = singleMessageString.substring(4, 8);
                            System.out.println("hexadress: " + hexAdress);
                            requestLength = singleMessageString.substring(8, messageList.get(z).length() - 4);
                            System.out.println("requestLength: " + requestLength);
                            System.out.println("requestLength in int: " + Integer.parseInt(requestLength));
                            requestAdressList = AdressList.adressString(hexAdress, requestLength);
                        } else {
                            dataValue = singleMessageString.substring(6, messageList.get(z).length() - 4);
                            System.out.println("dataValue: " + dataValue);
                        }
                        if (1 < requestAdressList.size() && dataValue != null) {
                            dataValueArrayList.add(dataValue.substring(0, dataValue.length() / 2));
                            dataValueArrayList.add(dataValue.substring(dataValue.length() / 2, dataValue.length() / 2));
                        } else {
                            dataValueArrayList.add(dataValue);
                        }
                        // Gerätestatus auslesen
                        for (int i = 0; i < deviceArray.length; i++) {
                            if (i < 2 && deviceArray[i].hexIdentifier.equals(message.substring(0, 14))) {
                                if (!deviceArray[i].deviceStatus) {
                                    deviceArray[i].setDeviceStatus(true);
                                    if (i == 0) {
                                        geraeteListeModel.set(i, deviceArray[i].deviceName);
                                    }
                                }
                            } else if (i > 1 && deviceArray[i].hexIdentifier.equals(message.substring(0, 6))) {
                                if (!deviceArray[i].deviceStatus) {
                                    deviceArray[i].setDeviceStatus(true);
                                }
                            }
                        }
                        if (requestbufferstring.equals(message.substring(0, 6))) {
                            // Werte aus Anfrage in Gerätedatentabelle speichern
                            for (Map.Entry entry : datacollectionArray[actualDeviceCounter].dataEntryCollection.entrySet()) {
                                for (int j = 0; j < adresslist.size(); j++) {
                                    if (entry.getKey().toString().equals(adresslist.get(j))) {
                                        String dataentitystring1 = dataValue.substring(j * 2, j * 2 + 2);
                                        String dataentitystring2 = dataValue.substring(j * 2 + 2, j * 2 + 4);
                                        String dataentitystring = dataentitystring1 + dataentitystring2;
                                        datacollectionArray[actualDeviceCounter].setCurrentValue(entry.getKey().toString(), dataentitystring);
                                    }
                                }
                            }
                        } else {
//                            // Livewerte speichern
//                            for (int i = 0; i < deviceArray.length; i++) {
//                                for (int j = 0; j < requestAdressList.size(); j++) {
//                                    if (deviceArray[i].shortHexRead.equals(singleMessageShortString) || deviceArray[i].shortHexWrite.equals(singleMessageShortString)) {
//                                        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
//                                        Date date = new Date();
//                                        Livedataentry livedata = new Livedataentry();
//                                        livedata.id = messageList.get(z);
//                                        livedata.time = dateFormat.format(date);
//                                        livedata.deviceAdress = messageList.get(z).substring(0, 2);
//                                        livedata.functionCode = messageList.get(z).substring(2, 4);
//                                        livedata.hexAdress = requestAdressList.get(j);
//                                        livedata.value = dataValueArrayList.get(j);
//                                        livedatacollectionArray[i].addEntry(livedata.id, livedata);
//                                    }
//                                }
//                            }
                        }
                    } else {
                        System.err.println("CRC-Überprüfung fehlerhaft");
                    }

                }
                // Control Unit Typ abfragen, Abfragen auslösen, Änderungen senden
                if ("0F03".equals(byteArrayToHex.substring(0, 4))) {
                    if (abfragen) {
                        if (!deviceArray[0].deviceStatus && !deviceArray[1].deviceStatus) {
                            deviceRequest();
                        } else {
                            requestSend();
                        }
                    }
                    if (schreiben) {
                        writeSend();
                    }
                }
            }
            System.out.println("Endtime" + getCurrentTimeStamp());
            System.out.println("");
        } catch (IOException e) {
            System.out.println("Fehler beim Lesen empfangener Daten");
            empfangen.append("Fehler beim Lesen empfangener Daten\n");
        } catch (java.lang.NegativeArraySizeException e) {
            System.out.println("1. Nachricht nicht vollständig");
            empfangen.append("1. Nachricht nicht vollständig\n");
//        } catch (java.lang.StringIndexOutOfBoundsException e) {
//            System.out.println("StringIndexOutOfBoundsException: " + e);
//            empfangen.append("StringIndexOutOfBoundsException: " + e + "\n");
//        } catch (java.lang.NullPointerException e) {
//            System.out.println("Nullpointer Exception");
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

    class oeffnenActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            System.out.println("oeffnenActionListener");
            oeffneSerialPort((String) auswahl.getSelectedItem());
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
            if (abfragen == false) {
                System.out.println("abfragenActionListener true");
                empfangen.append("Abfragen gestartet \n");
                abfragen = true;
            } else {
                System.out.println("abfragenActionListener false");
                empfangen.append("Abfragen abgebrochen \n");
                abfragen = false;
            }
        }
    }

    class schreibenActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            if (schreiben == false) {
                System.out.println("schreibennActionListener true");
                empfangen.append("Änderungen gesendet \n");
                schreiben = true;
            } else {
                System.out.println("schreibennActionListener false");
                empfangen.append("Änderungen senden abgebrochen \n");
                schreiben = false;
            }
        }
    }

    class firmwareActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            Runtime runtime = Runtime.getRuntime();
            try {
//                File folder = fwPathChooser.getSelectedFile();
                String folder = "C:\\Users\\Julian\\Documents\\GitHub\\ba-gui\\04_Releases\\20180427-lightpad_slim_mda-v1.04\\20180427-lightpad_slim_mda-v1.04";
                String file = "Lightpad2.bat";
                String[] cmd = {"cmd", "/K", "start", folder + "\\" + file, "-o=3"};
//                String[] cmd = {"cmd", "/K", "cd", folder, "&", "start", folder + "\\" + file, "-o=3"};

                Process p1 = runtime.exec(cmd);
                InputStream is = p1.getInputStream();
                int i = 0;
                while ((i = is.read()) != -1) {
                    System.out.print((char) i);
                }
                p1.waitFor();
            } catch (IOException ioException) {
                System.out.println(ioException.getMessage());
            } catch (InterruptedException ex) {
                Logger.getLogger(Diagnoseapplikation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    class modulfirmwareActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            Runtime runtime = Runtime.getRuntime();
            File folder = fwPathChooser.getSelectedFile();
            String file = null;
            String gereat = geraeteListe.getSelectedValue().toString();

            for (int i = 0; i < deviceArray.length; i++) {
                if (gereat.equals(deviceArray[i].deviceName)) {
                    String devicename = deviceArray[i].deviceName;
                    file = devicename.replaceAll(" ", "_").toLowerCase();
                }
            }
            if (gereat != null) {
                try {
                    Process p1 = runtime.exec("cmd /c start " + folder + "\\" + file);
                    InputStream is = p1.getInputStream();
                    int i = 0;
                    while ((i = is.read()) != -1) {
                        System.out.print((char) i);
                    }
                    p1.waitFor();
                } catch (IOException ioException) {
                    System.out.println(ioException.getMessage());
                } catch (InterruptedException ex) {
                    Logger.getLogger(Diagnoseapplikation.class.getName()).log(Level.SEVERE, null, ex);
                }
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
                System.out.println("Kein Gerät angewählt");
            } catch (IOException ex) {
                Logger.getLogger(Diagnoseapplikation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private class DisabledItemListCellRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component comp = super.getListCellRendererComponent(list, value, index, false, false);
            if (deviceArray[index].deviceStatus) {
                if (isSelected) {
                    comp.setForeground(Color.white);
                    comp.setBackground(Color.gray);
                } else {
                    comp.setForeground(Color.black);
                    comp.setBackground(Color.green);
                }
            } else if (!deviceArray[index].deviceStatus) {
                if (isSelected) { //  & cellHasFocus
                    comp.setForeground(Color.white);
                    comp.setBackground(Color.gray);
                } else if (deviceArray[index].manualrequest) {
                    comp.setForeground(Color.black);
                    comp.setBackground(Color.red);
                }
            } else {
                comp.setForeground(Color.black);
                comp.setBackground(Color.white);
            }
//            geraeteListe.repaint();
            return comp;
        }
    }
//    @Override
//    public void itemStateChanged(ItemEvent event) {
//        JCheckBox checkBox = (JCheckBox) event.getSource();
//        int index = -1;
//        for (int i = 0; i < ITEMS.length; i++) {
//            if (ITEMS[i].equals(checkBox.getText())) {
//                index = i;
//                break;
//            }
//        }
//        if (index != -1) {
//            enabledFlags[index] = checkBox.isSelected();
//            jList.repaint();
//        }
//    }

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
