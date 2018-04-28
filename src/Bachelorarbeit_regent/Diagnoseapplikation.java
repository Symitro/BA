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
import Bachelorarbeit_regent.misc.adressList;
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
    String device;
    Byte devicebyte;
    int changedvalue;
    String hexofchangedvalue;
    int selectedtab;

//    boolean controlunitm4_status;
//    boolean controlunit_status;
//    boolean aloneatwork_status;
//    boolean panelleft_status;
//    boolean panelright_status;
//    boolean connectedlighting_status;
//    boolean senslighthead1_status;
//    boolean senslighthead2_status;
//    boolean senslighthead3_status;
//    boolean senslighthead4_status;
//
//    boolean controlunitm4_msgsend;
//    boolean controlunit_msgsend;
//    boolean aloneatwork_msgsend;
//    boolean panelleft_msgsend;
//    boolean panelright_msgsend;
//    boolean connectedlighting_msgsend;
//    boolean senslighthead1_msgsend;
//    boolean senslighthead2_msgsend;
//    boolean senslighthead3_msgsend;
//    boolean senslighthead4_msgsend;
    // Abfragen für Control Unit M4, maximal 16 HR pro Abfrage für Performance
    byte[] sendstreamCUM4_1 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //32
    byte[] sendstreamCUM4_2 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCUM4_3 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x10}; //37
    byte[] sendstreamCUM4_4 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCUM4_5 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x05}; //^
    byte[] sendstreamCUM4_6 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x05}; //05
    byte[] sendstreamCUM4_7 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamCUM4_8 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //44
    byte[] sendstreamCUM4_9 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCUM4_10 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x2C, (byte) 0x00, (byte) 0x0C}; //^
    byte[] sendstreamCUM4_11 = {(byte) 0x08, (byte) 0x03, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x06}; //06
    byte[] sendstreamCUM4_12 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x01, (byte) 0x00, (byte) 0x10}; //79
    byte[] sendstreamCUM4_13 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x11, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCUM4_14 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x21, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCUM4_15 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x31, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamCUM4_16 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x41, (byte) 0x00, (byte) 0x0F}; //^
    byte[] sendstreamCUM4_17 = {(byte) 0x08, (byte) 0x03, (byte) 0x27, (byte) 0x17, (byte) 0x00, (byte) 0x04}; //04
    byte[] sendstreamCUM4_18 = {(byte) 0x08, (byte) 0x03, (byte) 0x35, (byte) 0x05, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamCUM4_19 = {(byte) 0x08, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //19
    byte[] sendstreamCUM4_20 = {(byte) 0x08, (byte) 0x03, (byte) 0x40, (byte) 0x10, (byte) 0x00, (byte) 0x03}; //^
    byte[] sendstreamCUM4_21 = {(byte) 0x08, (byte) 0x03, (byte) 0x42, (byte) 0x00, (byte) 0x00, (byte) 0x03}; //03
    byte[] sendstreamCUM4_22 = {(byte) 0x08, (byte) 0x03, (byte) 0x45, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //32
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

//    int messageLengthCUM4 = sendarraysCUM4.length;
//    static int currentMessageCUM4 = 0;
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

//    int messageLengthCU = sendarraysCU.length;
//    int currentMessageCU = 0;
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

//    int messageLengthAAW = sendarraysAAW.length;
//    int currentMessageAAW = 0;
    // Abfragen des linken Panel
    byte[] sendstreamPL_1 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //32
    byte[] sendstreamPL_2 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamPL_3 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x10}; //37
    byte[] sendstreamPL_4 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamPL_5 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x05}; //^
    byte[] sendstreamPL_6 = {(byte) 0x11, (byte) 0x03, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x05}; //05
    byte[] sendstreamPL_7 = {(byte) 0x11, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamPL_8 = {(byte) 0x11, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //32
    byte[] sendstreamPL_9 = {(byte) 0x11, (byte) 0x03, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamPL_10 = {(byte) 0x11, (byte) 0x03, (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamPL_11 = {(byte) 0x11, (byte) 0x03, (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x03}; //03
    byte[] sendstreamPL_12 = {(byte) 0x11, (byte) 0x03, (byte) 0x13, (byte) 0x07, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamPL_13 = {(byte) 0x11, (byte) 0x03, (byte) 0x14, (byte) 0x00, (byte) 0x00, (byte) 0x0D}; //13
    byte[] sendstreamPL_14 = {(byte) 0x11, (byte) 0x03, (byte) 0x21, (byte) 0x00, (byte) 0x00, (byte) 0x07}; //07
    byte[] sendstreamPL_15 = {(byte) 0x11, (byte) 0x03, (byte) 0x27, (byte) 0x18, (byte) 0x00, (byte) 0x02}; //02
    byte[] sendstreamPL_16 = {(byte) 0x11, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x07}; //07

    byte[][] sendarraysPL = {sendstreamPL_1, sendstreamPL_2, sendstreamPL_3, sendstreamPL_4, sendstreamPL_5, sendstreamPL_6, sendstreamPL_7, sendstreamPL_8, sendstreamPL_9, sendstreamPL_10, sendstreamPL_11, sendstreamPL_12, sendstreamPL_13, sendstreamPL_14, sendstreamPL_15, sendstreamPL_16};

//    int messageLengthPL = sendarraysPL.length;
//    int currentMessagePL = 0;
    // Abfrage des rechten Panel
    byte[] sendstreamPR_1 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //32
    byte[] sendstreamPR_2 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamPR_3 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x10}; //37
    byte[] sendstreamPR_4 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamPR_5 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x05}; //^
    byte[] sendstreamPR_6 = {(byte) 0x12, (byte) 0x03, (byte) 0x00, (byte) 0xFF, (byte) 0x00, (byte) 0x05}; //05
    byte[] sendstreamPR_7 = {(byte) 0x12, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamPR_8 = {(byte) 0x12, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //32
    byte[] sendstreamPR_9 = {(byte) 0x12, (byte) 0x03, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0x10}; //^
    byte[] sendstreamPR_10 = {(byte) 0x12, (byte) 0x03, (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamPR_11 = {(byte) 0x12, (byte) 0x03, (byte) 0x13, (byte) 0x00, (byte) 0x00, (byte) 0x03}; //03
    byte[] sendstreamPR_12 = {(byte) 0x12, (byte) 0x03, (byte) 0x13, (byte) 0x07, (byte) 0x00, (byte) 0x01}; //01
    byte[] sendstreamPR_13 = {(byte) 0x12, (byte) 0x03, (byte) 0x14, (byte) 0x00, (byte) 0x00, (byte) 0x0D}; //13
    byte[] sendstreamPR_14 = {(byte) 0x12, (byte) 0x03, (byte) 0x21, (byte) 0x00, (byte) 0x00, (byte) 0x07}; //07
    byte[] sendstreamPR_15 = {(byte) 0x12, (byte) 0x03, (byte) 0x27, (byte) 0x18, (byte) 0x00, (byte) 0x02}; //02
    byte[] sendstreamPR_16 = {(byte) 0x12, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x07}; //07

    byte[][] sendarraysPR = {sendstreamPR_1, sendstreamPR_2, sendstreamPR_3, sendstreamPR_4, sendstreamPR_5, sendstreamPR_6, sendstreamPR_7, sendstreamPR_8, sendstreamPR_9, sendstreamPR_10, sendstreamPR_11, sendstreamPR_12, sendstreamPR_13, sendstreamPR_14, sendstreamPR_15, sendstreamPR_16};

//    int messageLengthPR = sendarraysPR.length;
//    int currentMessagePR = 0;
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

//    int messageLengthSH1 = sendarraysSH1.length;
//    int currentMessageSH1 = 0;
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

//    int messageLengthSH2 = sendarraysSH2.length;
//    int currentMessageSH2 = 0;
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

//    int messageLengthSH3 = sendarraysSH3.length;
//    int currentMessageSH3 = 0;
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
//    int messageLengthSH4 = sendarraysSH4.length;
//    int currentMessageSH4 = 0;
    int totalLength;
    int totalMessageCount;
    long starttime;
    long endtime;
    long loopstarttime;

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
    JButton modulfirmware = new JButton("einzelne Modul-Firmware aktualisieren");
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
    Object[] livecolumns = {"Nachrichteneingang", "Geräteadresse", "Funktionscode", "Nachricht"};
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
            new Device(sendarraysCUM4, "Control Unit M4", "0803020006E447", (byte) 0x08),
            new Device(sendarraysCU, "Control Unit", "0803020002E584", (byte) 0x08),
            new Device(sendarraysAAW, "Alone at Work 2.0", "100302", (byte) 0x10),
            new Device(sendarraysPL, "Panel Links", "110302", (byte) 0x11),
            new Device(sendarraysPR, "Panel Rechts", "120302", (byte) 0x12),
            new Device(sendarraysSH1, "Senselighthead 1", "170304", (byte) 0x17),
            new Device(sendarraysSH2, "Senselighthead 2", "180304", (byte) 0x18),
            new Device(sendarraysSH3, "Senselighthead 3", "190304", (byte) 0x19),
            new Device(sendarraysSH4, "Senselighthead 4", "1A0304", (byte) 0x1A)
        };

        this.livedatacollectionArray = new Livedatacollection[]{
            liveCUM4Collection, liveCUCollection, liveAAWCollection,
            livePLCollection, livePRCollection, liveSH1Collection,
            liveSH2Collection, liveSH3Collection, liveSH4Collection};
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
        jmenu3.add(jmenuitem5);
        jmenu3.add(jmenuitem6);
        jmenu1.setText("Datei");
        jmenu2.setText("Firmware");
        jmenu3.setText("Hilfe");
        jmenuitem1.setText("Config laden");
        jmenuitem2.setText("Config speichern");
        jmenuitem3.setText("Beenden");
        jmenuitem4.setText("Firmware aktualisieren");
        jmenuitem5.setText("Über");
        jmenuitem6.setText("?");
        setJMenuBar(jmenubar1);

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
            geraeteListeModel.addElement(deviceArray[i].deviceName);
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
//                                model.addRow(new Object[]{"dataview", deviceArray[i].deviceStatus});
//                                devicebyte = deviceArray[i].devicebyte;
                                    for (Map.Entry entry : datacollectionArray[i].dataEntryCollection.entrySet()) {
                                        model.addRow(new Object[]{
                                            datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "index"),
                                            datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "varName"),
                                            entry.getKey(),
                                            datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "currentValue"),
                                            datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "currentValue"),
                                            datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "defaultValue"),
                                            datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "minValue"),
                                            datacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "maxValue")});
                                    }
                                    empfangen.append(deviceArray[i].deviceName + " angewählt\n");
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
//                                model.addRow(new Object[]{"liveview"});
//                                devicebyte = deviceArray[i].devicebyte;

                                    for (Map.Entry entry : livedatacollectionArray[i].liveDataEntryCollection.entrySet()) {
                                        model.addRow(new Object[]{
                                            livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "time"),
                                            livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "deviceadress"),
                                            livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "functioncode"),
                                            livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "message")
                                        });
                                    }
                                    empfangen.append(deviceArray[i].deviceName + " angewählt\n");
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
//        setSize(1200, 800);

        setVisible(true);
        setLocationRelativeTo(null);
        System.out.println("Fenster erzeugt");
    }

    // https://stackoverflow.com/questions/40192911/tablemodellistener-and-addrow-interference-in-jtable-defaulttablemodel
    // https://www.programcreek.com/java-api-examples/javax.swing.event.TableModelEvent
    public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE) {
            int row = e.getFirstRow();
            int column = e.getColumn();

            if (column == 3) {
                TableModel model = (TableModel) e.getSource();
                int changedvalue = Integer.parseInt((String) model.getValueAt(row, 3));
                String hexofchangedvalue = ((String) model.getValueAt(row, 2));
                System.out.println("changedvalue: " + changedvalue);
                System.out.println("hexofchangedvalue: " + hexofchangedvalue);
                System.out.println("Device: " + device);
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
        boolean valuedifferent = false;
        byte devicetype = 0;

//        switch (device) {
//            case deviceArray[0].deviceName:
//                devicetype = 0x08;
//                break;
//            case "cu":
//                devicetype = 0x08;
//                break;
//            case "aaw":
//                devicetype = 0x10;
//                break;
//            case "pl":
//                devicetype = 0x11;
//                break;
//            case "pr":
//                devicetype = 0x12;
//                break;
//            case "sh1":
//                devicetype = 0x17;
//                break;
//            case "sh2":
//                devicetype = 0x18;
//                break;
//            case "sh3":
//                devicetype = 0x19;
//                break;
//            case "sh4":
//                devicetype = 0x1A;
//                break;
//        }
        // Wert in List zwischenspeichern, dannach mit Collection vergleichen und falls anderst, neue Abfrage starten
//        if (SH4Collection.getDataByIdentifier(entry.getKey().toString(), "hexIdentifier"){
//            valuedifferent = true;
//        }
        byte[] hexbuffer = ConversionHelper.hexStringToByteArray(hexofchangedvalue);
        byte[] changedvaluearray = new byte[2];
        changedvaluearray[0] = (byte) (changedvalue & 0xFF);
        changedvaluearray[1] = (byte) ((changedvalue >> 8) & 0xFF);

        byte[] sendstream = {(byte) devicebyte, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01};
        System.arraycopy(hexbuffer, 0, sendstream, 2, 2);
        System.arraycopy(changedvaluearray, 0, sendstream, 4, 2);

        String sendstreamstring = ConversionHelper.byteArrayToHexString(sendstream);
        System.out.println("sendstreamstring: " + sendstreamstring);

        CRC16 crc = new CRC16();
        crc.update(sendstream, 0, sendstream.length);
        crc.getAll();
        outputStream.write(crc.getAll());
        return;

    }

    void deviceRequest() throws IOException {
        byte[] devicetype = {(byte) 0x08, (byte) 0x03, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x01}; //1
        CRC16 crc = new CRC16();
        crc.update(devicetype, 0, devicetype.length);
        crc.getAll();
        outputStream.write(crc.getAll());
    }

    void serialPortDatenVerfuegbar() throws InterruptedException {
        try {
            byte[] data = new byte[170];
            byte[] requestbuffer = new byte[3];
            byte[] responsebuffer = new byte[3];
            byte[] crcbuffer = new byte[2];
            int num;
            boolean responsetrue = false;

            requestbuffer[0] = deviceArray[actualDeviceCounter].getSingleByteArray(0);
            requestbuffer[1] = deviceArray[actualDeviceCounter].getSingleByteArray(1);
            requestbuffer[2] = (byte) (2 * deviceArray[actualDeviceCounter].getSingleByteArray(5));
            String requestbufferstring = ConversionHelper.byteArrayToHexString(requestbuffer);

            ArrayList<String> adresslist = adressList.adress(deviceArray[actualDeviceCounter].requestArray, deviceArray[actualDeviceCounter].lastmessageCounter);

            while (inputStream.available() > 0) {

                num = inputStream.read(data, 0, data.length);
                String byteArrayToHex = ConversionHelper.byteArrayToHexString(data);
                System.out.println("Dateninput unbearbeitet: " + byteArrayToHex);

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
                            repeatList.add(z);
//                            System.out.println("Wiederholung gefunden bei: " + z);
//                            System.out.println("Device: " + deviceArray[i].deviceName);
                        }
                    }
                }
//                // Rückwärtsüberpüfung
//                if (0 < repeatList.size()) {
//                    for (int i = 0; i < deviceArray.length; i++) {
//                        for (int y = message.length() - 4; y > 4; y--) {
//                            if (deviceArray[i].shortHexRead.equals(message.substring(y, y + 4)) || deviceArray[i].shortHexWrite.equals(message.substring(y, y + 4))) {
//                                if (!repeatList.contains(y)) {
//                                    repeatList.add(y);
//                                }
//                                System.out.println("Nachricht rückwärts gefunden bei: " + y);
//                            }
//                        }
//                    }
//                }

                /* Sorting of arraylist using Collections.sort*/
                Collections.sort(repeatList);

                for (int i = 0; i < repeatList.size(); i++) {
                    System.out.println("Startpunkt: " + repeatList.get(i));
                }

                // Multiple Nachrichten aufteilen
                for (int i = 0; i < repeatList.size(); i++) {
                    int repeatstartposition;
                    int repeatendposition;
                    if (repeatList.size() == 1) {
                        repeatstartposition = repeatList.get(i);
                        repeatendposition = message.length();
                    }
                    if (i + 1 == repeatList.size()) {
                        repeatstartposition = repeatList.get(i);
                        repeatendposition = message.length();
                    } else {
                        repeatstartposition = repeatList.get(i);
                        repeatendposition = repeatList.get(i + 1);
                    }
                    messageList.add(message.substring(repeatstartposition, repeatendposition));
                }
                for (int i = 0; i < messageList.size(); i++) {
                    System.out.println("messageList: " + messageList.get(i));
                }

                // Unvollständige Nachrichten werden gefiltert
                if (datalength >= 6) {
                    // Array mit ersten 3 Byte für die Überprüfung der Antwort
                    System.arraycopy(data, 0, responsebuffer, 0, 3);

                    // Array für CRC-Teil der empfangenen Nachricht, zur Gegenprüfung des CRC
                    System.arraycopy(data, (datalength / 2) - 2, crcbuffer, 0, 2);

                    // Array für Nachricht ohne CRC, zur Gegenprüfung des CRC
                    byte[] msgbuffer = new byte[(datalength / 2) - 2];
                    System.arraycopy(data, 0, msgbuffer, 0, (datalength / 2) - 2);

                    // Überprüfung des CRCs, Empfangene Nachricht und CRC werden verglichen
                    CRC16 crcreverse = new CRC16();
                    if (crcreverse.check(msgbuffer, crcbuffer)) {
                        System.out.println("crcreverse.check = true");
                    }

                    int dataentrylength = datalength - 10;

                    byte[] dataentry = new byte[dataentrylength / 2];
                    System.arraycopy(data, 3, dataentry, 0, dataentrylength / 2);

                    // Gerätestatus auslesen
                    for (int i = 0; i < deviceArray.length; i++) {
                        if (i < 2 && deviceArray[i].hexIdentifier.equals(byteArrayToHex.substring(0, 14))) {
                            if (!deviceArray[i].deviceStatus) {
                                deviceArray[i].setDeviceStatus(true);
                            }
                        } else if (i > 1 && deviceArray[i].hexIdentifier.equals(byteArrayToHex.substring(0, 6))) {
                            if (!deviceArray[i].deviceStatus) {
                                deviceArray[i].setDeviceStatus(true);
                            }
                        }
//                        System.out.println("Device: " + deviceArray[i].hexIdentifier + "\nDevicestatus: " + deviceArray[i].deviceStatus);
//                        System.out.println("shorthexread " + i + ": " + deviceArray[i].shortHexRead + "\nshorthexwrite " + i + ": " + deviceArray[i].shortHexWrite);
                    }

                    if (requestbufferstring.equals(message.substring(0, 6))) {
                        responsetrue = true;
                        System.err.println("response: " + responsetrue);
                    } else {
                        responsetrue = false;
                    }
                    // Werte aus Anfrage speichern
                    if (responsetrue) {
                        for (Map.Entry entry : datacollectionArray[actualDeviceCounter].dataEntryCollection.entrySet()) {
                            for (int j = 0; j < adresslist.size(); j++) {
                                if (entry.getKey().toString().equals(adresslist.get(j))) {
                                    byte[] dataentity = new byte[2];
                                    System.arraycopy(dataentry, j * 2, dataentity, 0, 2);
                                    String dataentitystring = ConversionHelper.byteArrayToHexString(dataentity);
                                    datacollectionArray[actualDeviceCounter].setCurrentValue(entry.getKey().toString(), dataentitystring);
                                }
                            }
                        }
                    }
//                    if (datacollectionArray[actualDeviceCounter].getDataByIdentifier("0", "currentValue") != null) {
//                        System.out.println("Current Value" + datacollectionArray[actualDeviceCounter].getDataByIdentifier("0", "currentValue"));
//                    }
//                    for (int i = 0; i < deviceArray.length; i++) {
//                        System.out.println("deviceArray: " + deviceArray[i].toString());
//                        System.out.println("deviceArray.requestArray: " + deviceArray[i].requestArray.toString());
//                        System.out.println("deviceArray.hexIdentifier: " + deviceArray[i].hexIdentifier);
//                    }
//
//                    if (deviceArray[0].shortHexRead.equals(byteArrayToHex.substring(0, 4))) {
//                        System.err.println("klappt");
//                    }
                    // Livewerte speichern
//                    for (int i = 0; i < deviceArray.length; i++) {
//                        if (!responsetrue && deviceArray[i].shortHexRead.equals(byteArrayToHex.substring(0, 4)) || !responsetrue && deviceArray[i].shortHexWrite.equals(byteArrayToHex.substring(0, 4))) {
                    if (!responsetrue) {
                        for (int i = 0; i < deviceArray.length; i++) {
                            for (int z = 0; z < messageList.size(); z++) {
                                String currentmessage = messageList.get(z);
                                String currentmessagesub = messageList.get(z).substring(0, 4);
//                                System.out.println("currentmessage: " + currentmessage);
//                                System.out.println("currentmessagesub: " + currentmessagesub);
//                                System.out.println("shorthexread: " + deviceArray[i].shortHexRead);

                                if (deviceArray[i].shortHexRead.equals(currentmessagesub) || deviceArray[i].shortHexWrite.equals(currentmessagesub)) {
//                                    String currentmessage = messageList.get(z);
                                    System.out.println("currentmessage: " + currentmessage);

                                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                                    Date date = new Date();

                                    Livedataentry livedata = new Livedataentry();
                                    livedata.id = currentmessage;
                                    livedata.time = dateFormat.format(date);
                                    livedata.deviceadress = currentmessage.substring(0, 2);
                                    livedata.functioncode = currentmessage.substring(2, 4);
                                    livedata.message = currentmessage.substring(4, currentmessage.length());
                                    livedatacollectionArray[i].addEntry(livedata.id, livedata);
                                }
                            }
                        }
                    }
//                    for (int i = 0; i < livedatacollectionArray.length; i++) {
//                        for (Map.Entry entry : livedatacollectionArray[i].liveDataEntryCollection.entrySet()) {
//                            System.out.println("livedatacollection time [" + i + "]: " + livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "time"));
//                            System.out.println("livedatacollection deviceadress [" + i + "]: " + livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "deviceadress"));
//                            System.out.println("livedatacollection functioncode [" + i + "]: " + livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "functioncode"));
//                            System.out.println("livedatacollection message [" + i + "]: " + livedatacollectionArray[i].getDataByIdentifier(entry.getKey().toString(), "message"));
//                        }
//                    }
                    // Control Unit Typ abfragen, Abfragen auslösen, Änderungen senden
                    if ("0F03".equals(byteArrayToHex.substring(0, 4))) {
                        if (!deviceArray[0].deviceStatus && !deviceArray[1].deviceStatus) {
                            deviceRequest();
                        }
                        if (abfragen) {
                            requestSend();
                        }
                        if (schreiben) {
                            writeSend();
                        }
                    }
                } else {
                    System.err.println("Empfangene Nachricht kleiner als 6");
                }
            }
//            System.out.println("while-Schleife durchlaufen: " + System.currentTimeMillis());
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
        } catch (java.lang.NullPointerException e) {
            System.out.println("Nullpointer Exception");

        }
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
            empfangen.append("Port geöffnet");

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
//                Process p1 = runtime.exec("cmd /c start C:\\Users\\Julian\\Desktop\\test.bat");
                Process p1 = runtime.exec("cmd /c start \"\" C:\\Users\\Julian\\Desktop\\test.bat");
                InputStream is = p1.getInputStream();
                int i = 0;
                while ((i = is.read()) != -1) {
                    System.out.print((char) i);
                }
            } catch (IOException ioException) {
                System.out.println(ioException.getMessage());
            }
        }
    }

    class modulfirmwareActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

        }
    }

    class manualRequestActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            if (manualrequest == false) {
                System.out.println("Manuelle Abfrage gestartet");
                empfangen.append("Manuelle Abfrage gestartet \n");
                manualrequest = true;
            } else {
                System.out.println("Manuelle Abfrage abgebrochen");
                empfangen.append("Manuelle Abfrage gestartet \n");
                manualrequest = false;
            }
        }
    }

    private class DisabledItemListCellRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component comp = super.getListCellRendererComponent(list, value, index, false, false);
//            JComponent jc = (JComponent) comp;
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
                } else if (manualrequest) {
                    comp.setForeground(Color.black);
                    comp.setBackground(Color.red);
                }
            } else {
                comp.setForeground(Color.black);
                comp.setBackground(Color.white);
            }
//            comp.setEnabled(false);
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
