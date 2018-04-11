/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bachelorarbeit_regent.data;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Julian
 */
public class Datacollection {
    
    Map<String, Dataentry> dataEntryCollection;

    public Datacollection() {
        dataEntryCollection = new HashMap<String, Dataentry>();
    }

    public void addEntry(String hexIdentifier, Dataentry obj) {
        dataEntryCollection.put(hexIdentifier, obj);
    }

    public Object getValueByHex(String hexIdentifier, String type) {
        if (type.equals("live")) {
            return dataEntryCollection.get(hexIdentifier).currentValue;
        }
        return dataEntryCollection.get(hexIdentifier).defaultValue;
    }

    public void addLiveValue() {

    }
}
