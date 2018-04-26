/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Zwischenspeicher.Bachelorarbeit_regent.misc;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Julian
 */
public class adressList {

    public static ArrayList<String> adress(byte[][] request, int lastMessage) {
        byte[] requestbuffer = new byte[1];
        requestbuffer[0] = (byte) request[lastMessage][5];
        String requeststring = ConversionHelper.byteArrayToHexString(requestbuffer);
        Long requestlong = Long.parseLong(requeststring, 16);

        byte[] startadress = new byte[2];
        startadress[0] = request[lastMessage][2];
        startadress[1] = request[lastMessage][3];
        String adressstring = ConversionHelper.byteArrayToHexString(startadress);
        Long adresslong = Long.parseLong(adressstring, 16);

        ArrayList<String> responseadresses = new ArrayList<String>();

        for (int i = 0; i < requestlong; i++) {
            Long adress = adresslong;
            adresslong++;
            String nextadress = Long.toHexString(adress).toUpperCase();
            responseadresses.add(nextadress);
        }

//        String adress = Long.toHexString(Float.floatToIntBits(requestfloat));
        return responseadresses;
    }
}
