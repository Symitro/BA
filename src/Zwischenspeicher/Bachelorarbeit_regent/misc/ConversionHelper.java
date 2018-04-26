/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Zwischenspeicher.Bachelorarbeit_regent.misc;

import java.nio.ByteBuffer;

/**
 *
 * @author Julian
 */
public class ConversionHelper {

    public static byte[] decode(String hex) {

        String[] list = hex.split("(?<=\\G.{2})");
        ByteBuffer buffer = ByteBuffer.allocate(list.length);
        System.out.println(list.length);
        for (String str : list) {
            buffer.put(Byte.parseByte(str, 16));
        }

        return buffer.array();

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

    public static String unHex(String arg) {

        String str = "";
        for (int i = 0; i < arg.length(); i += 2) {
            String s = arg.substring(i, (i + 2));
            int decimal = Integer.parseInt(s, 16);
            str = str + (char) decimal;
        }
        return str;
    }

    //https://www.mkyong.com/java/how-to-convert-hex-to-ascii-in-java/
    public String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }
        System.out.println("Decimal : " + temp.toString());

        return sb.toString();
    }

}
