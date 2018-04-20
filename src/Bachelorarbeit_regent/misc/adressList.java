/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bachelorarbeit_regent.misc;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Julian
 */
public class adressList {

    public static ArrayList<String> adress(byte[][] request, int lastMessage) {
        byte[] requestbuffer = new byte[1];
//        requestbuffer[0] = request[lastMessage][0];
//        requestbuffer[1] = request[lastMessage][1];
        requestbuffer[0] = (byte) (2 * request[lastMessage][5]);
        String requeststring = ConversionHelper.byteArrayToHexString(requestbuffer);
        Long requestlong = Long.parseLong(requeststring, 16);
//        Float requestfloat = Float.intBitsToFloat(requestlong.intValue());
//        System.out.println("Long: " + Long.toHexString(requestlong));
//
//        System.out.println("Long über Float: " + Long.toHexString(Float.floatToIntBits(requestfloat)));

        byte[] startadress = new byte[2];
        startadress[0] = request[lastMessage][2];
        startadress[1] = request[lastMessage][3];
        String adressstring = ConversionHelper.byteArrayToHexString(startadress);
        Long adresslong = Long.parseLong(adressstring, 16);
//        Float adressfloat = Float.intBitsToFloat(adresslong.intValue());
//        System.out.println("Long: " + Long.toHexString(Float.floatToIntBits(adressfloat)));

        ArrayList<String> responseadresses = new ArrayList<String>();

        for (int i = 0; i < requestlong; i++) {
            Long adress = adresslong;
            adresslong++;
            String nextadress = Long.toHexString(adress);
            responseadresses.add(nextadress);
        }

//        String adress = Long.toHexString(Float.floatToIntBits(requestfloat));

        return responseadresses;
    }

}
