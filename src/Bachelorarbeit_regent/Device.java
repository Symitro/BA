/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bachelorarbeit_regent;

import Bachelorarbeit_regent.data.*;
import Bachelorarbeit_regent.misc.ConversionHelper;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jherr
 */
public class Device {

    public int messageCounter = 0;
    public int lastmessageCounter = 0;
    public boolean deviceStatus = false;
    public boolean manualrequest = false;
    public byte[] requestArray;
    public Datacollection dataCollection;
    public String hexIdentifier;
    public String deviceName;
    public String shortHexRead;
    public String shortHexWrite;
    public String shortHexMultipleWrite;
    public String Read = "03";
    public String Write = "06";
    public String multipleWrite = "10";
    public byte devicebyte;

    public Device(Datacollection dataCollection, String deviceName, String hexIdentifier, byte deviceByte) {
        this.deviceName = deviceName;
        this.hexIdentifier = hexIdentifier;
        this.devicebyte = (byte) deviceByte;
        this.shortHexRead = hexIdentifier.substring(0, 2) + Read;
        this.shortHexWrite = hexIdentifier.substring(0, 2) + Write;
        this.shortHexMultipleWrite = hexIdentifier.substring(0, 2) + multipleWrite;
        this.dataCollection = dataCollection;
    }

    public byte[] requestGenerate(int idxOfMessage) {
        byte[] requestArray = new byte[6];
        List valueList = new ArrayList(this.dataCollection.dataEntryCollection.values());
        Dataentry data = (Dataentry) valueList.get(idxOfMessage);

        //Create byte array for request
        byte[] hexAdressBuffer = ConversionHelper.hexIdentifierToByteArray(data.hexIdentifier);
        requestArray[0] = this.devicebyte;
        requestArray[1] = (byte) 0x03;
        requestArray[2] = hexAdressBuffer[0];
        requestArray[3] = hexAdressBuffer[1];
        requestArray[4] = (byte) 0x00;
        requestArray[5] = (byte) 0x01;

        return requestArray;
    }

    public boolean isRequestFinished() {
        return this.dataCollection.dataEntryCollection.size() == this.messageCounter + 1;
    }

    public void setDeviceStatus(boolean deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getCurrentByteAdress() {
        if (this.messageCounter > 0) {
            this.lastmessageCounter = this.messageCounter - 1;
        }
        byte[] out = requestGenerate(this.lastmessageCounter);
        String adress = ConversionHelper.byteArrayToHexString(out).substring(4, 8);

        return adress;
    }

    public String getByteAdress(int hex) {
        byte[] out = requestGenerate(hex);
        String adress = ConversionHelper.byteArrayToHexString(out).substring(4, 8);

        return adress;
    }

    public byte[] getNextByteArray() {
        byte[] out = requestGenerate(this.messageCounter);
        this.messageCounter++;

        return out;
    }

    public byte getSingleByteArray(int i) {
        if (this.messageCounter > 0) {
            this.lastmessageCounter = this.messageCounter - 1;
        }
        byte byteout = this.requestGenerate(this.lastmessageCounter)[i];

        return byteout;
    }
}
