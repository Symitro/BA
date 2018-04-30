/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bachelorarbeit_regent;

/**
 *
 * @author jherr
 */
public class Device {

    public int messageCounter = 0;
    public int lastmessageCounter = 0;
    public boolean deviceStatus = false;
    public boolean manualrequest = false;
    public byte[][] requestArray;
    public String hexIdentifier;
    public String deviceName;
    public String shortHexRead;
    public String shortHexWrite;
    public String Read = "03";
    public String Write = "06";
    public byte devicebyte;

    public Device(byte[][] requestArray, String deviceName, String hexIdentifier, byte deviceByte) {
        this.requestArray = requestArray;
        this.deviceName = deviceName;
        this.hexIdentifier = hexIdentifier;
        this.devicebyte = (byte) deviceByte;
        this.shortHexRead = hexIdentifier.substring(0, 2) + Read;
        this.shortHexWrite = hexIdentifier.substring(0, 2) + Write;
    }

    public boolean isRequestFinished() {
        return this.requestArray.length == this.messageCounter + 1 ? true : false;
    }

    public void setDeviceStatus(boolean deviceStatus) {
        this.deviceStatus = deviceStatus;
//        Diagnoseapplikation.geraeteListe.repaint();
    }

    public byte[] getNextByteArray() {
        byte[] out = this.requestArray[this.messageCounter];
        this.messageCounter++;
        return out;
    }

    public byte getSingleByteArray(int i) {
        if (this.messageCounter > 0) {
            this.lastmessageCounter = this.messageCounter - 1;
        }
        byte byteout = this.requestArray[this.lastmessageCounter][i];
        return byteout;
    }
}
