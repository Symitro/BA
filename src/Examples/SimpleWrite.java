package Examples;

import java.io.*;
import java.util.*;
import javax.comm.*;

public class SimpleWrite {

    static Enumeration portList;
    static CommPortIdentifier portId;
    static String messageString = "150301000001";
    static SerialPort serialPort;
    static OutputStream outputStream;

    public static void main(String[] args) {
        portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (portId.getName().equals("COM3")) {
                    //if (portId.getName().equals("/dev/term/a")) {
                    try {
                        serialPort = (SerialPort) portId.open("SimpleWriteApp", 2000);

                    } catch (PortInUseException e) {
                    }
                    try {
                        outputStream = serialPort.getOutputStream();
                    } catch (IOException e) {
                    }
                    try {
                        serialPort.setSerialPortParams(115200,
                                SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_2,
                                SerialPort.PARITY_NONE);
                    } catch (UnsupportedCommOperationException e) {
                    }
                    try {
                        byte[] hexToByteArray = hexStringToByteArray(messageString);
                        outputStream.write(hexToByteArray);
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    public static byte[] hexStringToByteArray(String hexString) {
        byte[] bytes = new byte[hexString.length() / 2];

        for (int i = 0; i < hexString.length(); i += 2) {
            String sub = hexString.substring(i, i + 2);
            Integer intVal = Integer.parseInt(sub, 16);
            bytes[i / 2] = intVal.byteValue();
            String hex = "".format("0x%x", bytes[i / 2]);
        }

        return bytes;
    }
}
