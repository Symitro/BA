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
// https://stackoverflow.com/questions/12663889/hashmapstring-object-how-to-put-object-itself-as-in-place-of-string
public class Datacollection {

    public Map<String, Dataentry> dataEntryCollection;

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

    public void addLiveValue(String hexIdentifier, Object value) {
        dataEntryCollection.get(hexIdentifier).currentValue = value;
    }
//    
//    public void getDataByIdentifier(String hexIdentifier) {
//        dataEntryCollection.get(hexIdentifier).currentValue = value;
//    }
}
