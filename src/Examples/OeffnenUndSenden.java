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
import javax.comm.*;
import java.util.Enumeration;
import java.io.*;
import java.util.TooManyListenersException;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
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

    boolean controlunitm4_status;
    boolean controlunit_status;
    boolean aloneatwork_status;
    boolean panelleft_status;
    boolean panelright_status;
    boolean connectedlighting_status;
    boolean senslighthead1_status;
    boolean senslighthead2_status;
    boolean senslighthead3_status;
    boolean senslighthead4_status;

    boolean controlunitm4_msgsend;
    boolean controlunit_msgsend;
    boolean aloneatwork_msgsend;
    boolean panelleft_msgsend;
    boolean panelright_msgsend;
    boolean connectedlighting_msgsend;
    boolean senslighthead1_msgsend;
    boolean senslighthead2_msgsend;
    boolean senslighthead3_msgsend;
    boolean senslighthead4_msgsend;

    // Abfragen für Control Unit M4, maximal 16 HR pro Abfrage für Performance
    byte[] sendstreamCUM4_1 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //32
    byte[] sendstreamCUM4_2 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCUM4_3 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x10}; //37
    byte[] sendstreamCUM4_4 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCUM4_5 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x05};
    byte[] sendstreamCUM4_6 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamCUM4_7 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //44
    byte[] sendstreamCUM4_8 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCUM4_9 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x2C, (byte) 0x00, (byte) 0x0C};
    byte[] sendstreamCUM4_10 = {(byte) 0x08, (byte) 0x03, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x06};
    byte[] sendstreamCUM4_11 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x01, (byte) 0x00, (byte) 0x10}; //79
    byte[] sendstreamCUM4_12 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x11, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCUM4_13 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x21, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCUM4_14 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x31, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCUM4_15 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x41, (byte) 0x00, (byte) 0x0F};
    byte[] sendstreamCUM4_16 = {(byte) 0x08, (byte) 0x03, (byte) 0x27, (byte) 0x17, (byte) 0x00, (byte) 0x04};
    byte[] sendstreamCUM4_17 = {(byte) 0x08, (byte) 0x03, (byte) 0x35, (byte) 0x05, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamCUM4_18 = {(byte) 0x08, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //19
    byte[] sendstreamCUM4_19 = {(byte) 0x08, (byte) 0x03, (byte) 0x40, (byte) 0x10, (byte) 0x00, (byte) 0x03};
    byte[] sendstreamCUM4_20 = {(byte) 0x08, (byte) 0x03, (byte) 0x42, (byte) 0x00, (byte) 0x00, (byte) 0x03};
    byte[] sendstreamCUM4_21 = {(byte) 0x08, (byte) 0x03, (byte) 0x45, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //32
    byte[] sendstreamCUM4_22 = {(byte) 0x08, (byte) 0x03, (byte) 0x45, (byte) 0x10, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCUM4_23 = {(byte) 0x08, (byte) 0x03, (byte) 0x60, (byte) 0x00, (byte) 0x00, (byte) 0x04};
    byte[] sendstreamCUM4_24 = {(byte) 0x08, (byte) 0x03, (byte) 0x60, (byte) 0x06, (byte) 0x00, (byte) 0x04};
    byte[] sendstreamCUM4_25 = {(byte) 0x08, (byte) 0x03, (byte) 0x61, (byte) 0x00, (byte) 0x00, (byte) 0x04};
    byte[] sendstreamCUM4_26 = {(byte) 0x08, (byte) 0x03, (byte) 0x62, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //53
    byte[] sendstreamCUM4_27 = {(byte) 0x08, (byte) 0x03, (byte) 0x62, (byte) 0x10, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCUM4_28 = {(byte) 0x08, (byte) 0x03, (byte) 0x62, (byte) 0x20, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCUM4_29 = {(byte) 0x08, (byte) 0x03, (byte) 0x62, (byte) 0x30, (byte) 0x00, (byte) 0x05};
    byte[] sendstreamCUM4_30 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //69
    byte[] sendstreamCUM4_31 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x10, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCUM4_32 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x20, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCUM4_33 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x30, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCUM4_34 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x40, (byte) 0x00, (byte) 0x05};

    byte[][] sendarraysCUM4 = {sendstreamCUM4_1, sendstreamCUM4_2, sendstreamCUM4_3, sendstreamCUM4_4, sendstreamCUM4_5, sendstreamCUM4_6, sendstreamCUM4_7, sendstreamCUM4_8, sendstreamCUM4_9, sendstreamCUM4_10, sendstreamCUM4_11, sendstreamCUM4_12, sendstreamCUM4_13, sendstreamCUM4_14, sendstreamCUM4_15, sendstreamCUM4_16, sendstreamCUM4_17, sendstreamCUM4_18, sendstreamCUM4_19, sendstreamCUM4_20, sendstreamCUM4_21, sendstreamCUM4_22, sendstreamCUM4_23, sendstreamCUM4_24, sendstreamCUM4_25, sendstreamCUM4_26, sendstreamCUM4_27, sendstreamCUM4_28, sendstreamCUM4_29, sendstreamCUM4_30, sendstreamCUM4_31, sendstreamCUM4_32, sendstreamCUM4_33, sendstreamCUM4_34};

    int messageLengthCUM4 = sendarraysCUM4.length;
    int currentMessageCUM4 = 0;

    // Abfragen für Control Unit, maximal 16 HR pro Abfrage für Performance
    byte[] sendstreamCU_1 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //32
    byte[] sendstreamCU_2 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCU_3 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x30, (byte) 0x00, (byte) 0x10}; //37
    byte[] sendstreamCU_4 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x40, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCU_5 = {(byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x45, (byte) 0x00, (byte) 0x05};
    byte[] sendstreamCU_6 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamCU_7 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x10, (byte) 0x00, (byte) 0x10}; //44
    byte[] sendstreamCU_8 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x20, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCU_9 = {(byte) 0x08, (byte) 0x03, (byte) 0x02, (byte) 0x2C, (byte) 0x00, (byte) 0x0C};
    byte[] sendstreamCU_10 = {(byte) 0x08, (byte) 0x03, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x06};
    byte[] sendstreamCU_11 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x01, (byte) 0x00, (byte) 0x10}; //79
    byte[] sendstreamCU_12 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x11, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCU_13 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x21, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCU_14 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x31, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCU_15 = {(byte) 0x08, (byte) 0x03, (byte) 0x20, (byte) 0x41, (byte) 0x00, (byte) 0x0F};
    byte[] sendstreamCU_16 = {(byte) 0x08, (byte) 0x03, (byte) 0x27, (byte) 0x17, (byte) 0x00, (byte) 0x04};
    byte[] sendstreamCU_17 = {(byte) 0x08, (byte) 0x03, (byte) 0x35, (byte) 0x05, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamCU_18 = {(byte) 0x08, (byte) 0x03, (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //19
    byte[] sendstreamCU_19 = {(byte) 0x08, (byte) 0x03, (byte) 0x40, (byte) 0x10, (byte) 0x00, (byte) 0x03};
    byte[] sendstreamCU_20 = {(byte) 0x08, (byte) 0x03, (byte) 0x42, (byte) 0x00, (byte) 0x00, (byte) 0x03};
    byte[] sendstreamCU_21 = {(byte) 0x08, (byte) 0x03, (byte) 0x45, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //32
    byte[] sendstreamCU_22 = {(byte) 0x08, (byte) 0x03, (byte) 0x45, (byte) 0x10, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCU_23 = {(byte) 0x08, (byte) 0x03, (byte) 0x60, (byte) 0x00, (byte) 0x00, (byte) 0x04};
    byte[] sendstreamCU_24 = {(byte) 0x08, (byte) 0x03, (byte) 0x60, (byte) 0x06, (byte) 0x00, (byte) 0x04};
    byte[] sendstreamCU_25 = {(byte) 0x08, (byte) 0x03, (byte) 0x61, (byte) 0x00, (byte) 0x00, (byte) 0x04};
    byte[] sendstreamCU_26 = {(byte) 0x08, (byte) 0x03, (byte) 0x62, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //53
    byte[] sendstreamCU_27 = {(byte) 0x08, (byte) 0x03, (byte) 0x62, (byte) 0x10, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCU_28 = {(byte) 0x08, (byte) 0x03, (byte) 0x62, (byte) 0x20, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCU_29 = {(byte) 0x08, (byte) 0x03, (byte) 0x62, (byte) 0x30, (byte) 0x00, (byte) 0x05};
    byte[] sendstreamCU_30 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x00, (byte) 0x00, (byte) 0x10}; //69
    byte[] sendstreamCU_31 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x10, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCU_32 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x20, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCU_33 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x30, (byte) 0x00, (byte) 0x10};
    byte[] sendstreamCU_34 = {(byte) 0x08, (byte) 0x03, (byte) 0x63, (byte) 0x40, (byte) 0x00, (byte) 0x05};

    byte[][] sendarraysCU = {sendstreamCUM4_1, sendstreamCUM4_2, sendstreamCUM4_3, sendstreamCUM4_4, sendstreamCUM4_5, sendstreamCUM4_6, sendstreamCUM4_7, sendstreamCUM4_8, sendstreamCUM4_9, sendstreamCUM4_10, sendstreamCUM4_11, sendstreamCUM4_12, sendstreamCUM4_13, sendstreamCUM4_14, sendstreamCUM4_15, sendstreamCUM4_16, sendstreamCUM4_17, sendstreamCUM4_18, sendstreamCUM4_19, sendstreamCUM4_20, sendstreamCUM4_21, sendstreamCUM4_22, sendstreamCUM4_23, sendstreamCUM4_24, sendstreamCUM4_25, sendstreamCUM4_26, sendstreamCUM4_27, sendstreamCUM4_28, sendstreamCUM4_29, sendstreamCUM4_30, sendstreamCUM4_31, sendstreamCUM4_32, sendstreamCUM4_33, sendstreamCUM4_34};

    int messageLengthCU = sendarraysCUM4.length;
    int currentMessageCU = 0;

    // Abfragen des Alone at Works 2.0
    byte[] sendstreamAAW_1 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamAAW_2 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamAAW_3 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamAAW_4 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamAAW_5 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamAAW_6 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamAAW_7 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamAAW_8 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x01};

    byte[][] sendarraysAAW = {sendstreamAAW_1, sendstreamAAW_2, sendstreamAAW_3, sendstreamAAW_4, sendstreamAAW_5, sendstreamAAW_6, sendstreamAAW_7, sendstreamAAW_8};

    int messageLengthAAW = sendarraysAAW.length;
    int currentMessageAAW = 0;

    // Abfragen des linken Panel
    byte[] sendstreamPL_1 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPL_2 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPL_3 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPL_4 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPL_5 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPL_6 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPL_7 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPL_8 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x01};

    byte[][] sendarraysPL = {sendstreamPL_1, sendstreamPL_2, sendstreamPL_3, sendstreamPL_4, sendstreamPL_5, sendstreamPL_6, sendstreamPL_7, sendstreamPL_8};

    int messageLengthPL = sendarraysPL.length;
    int currentMessagePL = 0;

    // Abfrage des rechten Panel
    byte[] sendstreamPR_1 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPR_2 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPR_3 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPR_4 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPR_5 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPR_6 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPR_7 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPR_8 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x01};

    byte[][] sendarraysPR = {sendstreamPR_1, sendstreamPR_2, sendstreamPR_3, sendstreamPR_4, sendstreamPR_5, sendstreamPR_6, sendstreamPR_7, sendstreamPR_8};

    int messageLengthPR = sendarraysPR.length;
    int currentMessagePR = 0;

    // Abfrage des rechten Panel
    byte[] sendstreamPR_1 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPR_2 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPR_3 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPR_4 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPR_5 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPR_6 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPR_7 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x01};
    byte[] sendstreamPR_8 = {(byte) 0x10, (byte) 0x03, (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x01};

    byte[][] sendarraysPR = {sendstreamPR_1, sendstreamPR_2, sendstreamPR_3, sendstreamPR_4, sendstreamPR_5, sendstreamPR_6, sendstreamPR_7, sendstreamPR_8};

    int messageLengthPR = sendarraysPR.length;
    int currentMessagePR = 0;

    int totalLength = sendarraysCUM4.length + sendarraysAAW.length + sendarraysPL.length + messageLengthPR;
    int totalMessageCount = currentMessageCUM4 + currentMessageAAW + currentMessagePL + currentMessagePR;

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

    public byte[] requestSend() throws IOException {
        // Abfrage der Control Unit M4
        if (controlunitm4_status == true && controlunitm4_msgsend == false) {
            byte[] sendstream = sendarraysCUM4[currentMessageCUM4];
            currentMessageCUM4++;
            if (currentMessageCUM4 == messageLengthCUM4) {
                currentMessageCUM4 = 0;
                controlunitm4_msgsend = true;
                System.out.println("currentMessageCUM4 = 0");
            }
            CRC16 crc = new CRC16();
            crc.update(sendstream, 0, sendstream.length);
            crc.getAll();
            outputStream.write(crc.getAll());
            System.out.println("Control Unit M4 gesendet: " + currentMessageCUM4);
        }
        // Abfrage des Alone at Work-Modules
        if (aloneatwork_status == true && controlunitm4_msgsend == true && aloneatwork_msgsend == false) {
            if (currentMessageAAW == messageLengthAAW) {
                currentMessageAAW = 0;
                aloneatwork_msgsend = true;
            }
            byte[] sendstream = sendarraysAAW[currentMessageAAW];
            currentMessageAAW++;
            CRC16 crc = new CRC16();
            crc.update(sendstream, 0, sendstream.length);
            crc.getAll();
            outputStream.write(crc.getAll());
        }
        // Abfrage des linken Panels
        if (panelleft_status == true && aloneatwork_msgsend == true && panelleft_msgsend == false || panelleft_status == true && aloneatwork_status == false && controlunitm4_msgsend == true && panelleft_msgsend == false) {
            if (currentMessagePL == messageLengthPL) {
                currentMessagePL = 0;
                panelleft_msgsend = true;
                System.out.println("currentMessagePL = 0");
            }
            byte[] sendstream = sendarraysPL[currentMessagePL];
            currentMessagePL++;
            CRC16 crc = new CRC16();
            crc.update(sendstream, 0, sendstream.length);
            crc.getAll();
            outputStream.write(crc.getAll());
            System.out.println("Panel left gesendet: " + currentMessagePL);
        }
        // Abfrage des rechten Panels
        if (panelright_status == true && panelleft_msgsend == true && panelright_msgsend == false || panelright_status == true && panelleft_status == false && aloneatwork_msgsend == true && panelright_msgsend == false) {
            if (currentMessagePR == messageLengthPR) {
                currentMessagePR = 0;
                panelright_msgsend = true;
                System.out.println("currentMessagePR = 0");
            }
            byte[] sendstream = sendarraysPR[currentMessagePR];
            currentMessagePR++;
            CRC16 crc = new CRC16();
            crc.update(sendstream, 0, sendstream.length);
            crc.getAll();
            outputStream.write(crc.getAll());
            System.out.println("Panel right gesendet: " + currentMessagePR);
        }
        // Abfrage des rechten Panels
        if (senslighthead1_status == true && panelright_msgsend == true && panelright_msgsend == false || senslighthead1_status == true && panelright_status == false && aloneatwork_msgsend == true && panelright_msgsend == false) {
            if (currentMessagePR == messageLengthPR) {
                currentMessagePR = 0;
                senslighthead1_msgsend = true;
                System.out.println("currentMessagePR = 0");
            }
            byte[] sendstream = sendarraysPR[currentMessagePR];
            currentMessagePR++;
            CRC16 crc = new CRC16();
            crc.update(sendstream, 0, sendstream.length);
            crc.getAll();
            outputStream.write(crc.getAll());
            System.out.println("Panel right gesendet: " + currentMessagePR);
        }
//        if (totalMessageCount == totalLength) {
//            currentMessageCUM4 = 0;
//            currentMessageAAW = 0;
//            currentMessagePL = 0;
//            controlunitm4_msgsend = false;
//            aloneatwork_msgsend = false;
//            panelleft_msgsend = false;
//            panelright_msgsend = false;
//
//        }
        return null;
    }

    void sendeSerialPort(String nachricht) {
        System.out.println("Sende: " + nachricht);

        if (serialPortGeoeffnet != true) {
            return;
        }
    }

    void serialPortDatenVerfuegbar() throws InterruptedException {
        try {
            byte[] data = new byte[270];
            int num;

            while (inputStream.available() > 0) {
                System.out.println(System.currentTimeMillis());
                num = inputStream.read(data, 0, data.length);
                String byteArrayToHex = byteArrayToHexString(data);
                System.out.println("Empfange: " + byteArrayToHex);

                if ("0f03".equals(byteArrayToHex.substring(0, 4)) || "0F03".equals(byteArrayToHex.substring(0, 4))) {
                    // Falls Antwort, Abfragen auslösen mit Device + CRC, gesplittet auf HR-Abfolgen
                    controlunitm4_status = true;
                    System.out.println("byteArrayToHex = 0F03");
                    System.out.println("PC-Bridge initialisiert: " + System.currentTimeMillis());
                    Thread.sleep(3);
                    requestSend();
                }
                if ("100302".equals(byteArrayToHex.substring(0, 6))) {
                    aloneatwork_status = true;
                    System.out.println("byteArrayToHex = 100302");
                    System.out.println("Alone at Work 2.0");
                    System.out.println(System.currentTimeMillis());
                }
                if ("110302".equals(byteArrayToHex.substring(0, 6))) {
                    panelleft_status = true;
                    System.out.println("byteArrayToHex = 110302");
                    System.out.println("Panel left aktiv");
                }
                if ("120302".equals(byteArrayToHex.substring(0, 6))) {
                    panelright_status = true;
                    System.out.println("byteArrayToHex = 120302");
                    System.out.println("Panel right aktiv");
                }
                if ("140302".equals(byteArrayToHex.substring(0, 6))) {
                    connectedlighting_status = true;
                    System.out.println("byteArrayToHex = 140302");
                    System.out.println("Connected Lighting aktiv");
                }
                if ("150302".equals(byteArrayToHex.substring(0, 6))) {
                    connectedlighting_status = true;
                    System.out.println("byteArrayToHex = 150302");
                    System.out.println("Connected Lighting aktiv");
                }
                if ("170302".equals(byteArrayToHex.substring(0, 6))) {
                    senslighthead1_status = true;
                    System.out.println("byteArrayToHex = 170302");
                    System.out.println("Senslight Head 1 aktiv");
                }
                if ("180302".equals(byteArrayToHex.substring(0, 6))) {
                    senslighthead2_status = true;
                    System.out.println("byteArrayToHex = 180302");
                    System.out.println("Senslight Head 2 aktiv");
                }
                if ("190302".equals(byteArrayToHex.substring(0, 6))) {
                    senslighthead3_status = true;
                    System.out.println("byteArrayToHex = 190302");
                    System.out.println("Senslight Head 3 aktiv");
                }
                if ("200302".equals(byteArrayToHex.substring(0, 6))) {
                    senslighthead4_status = true;
                    System.out.println("byteArrayToHex = 200302");
                    System.out.println("Senslight Head 4 aktiv");
                }
//
//                empfangen.append(byteArrayToHex + "\n");
            }
            System.out.println("while-Schleife durchlaufen: " + System.currentTimeMillis());
            System.out.println("");

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
