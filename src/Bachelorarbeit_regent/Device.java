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
    public boolean deviceStatus = false;
    public byte[][] requestArray;
    public String hexIdentifier;
    
    public Device(byte[][] requestArray, String hexIdentifier){
        this.requestArray = requestArray;
    }
    
    public boolean isRequestFinished(){
        return this.requestArray.length == this.messageCounter + 1 ? true : false;
    }
    
    public byte[] getNextByteArray(){
        byte[] out = this.requestArray[this.messageCounter];
        this.messageCounter++;
        return out;
    }
      
}
