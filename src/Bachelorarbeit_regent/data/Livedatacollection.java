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
public class Livedatacollection {

    public Map<String, Livedataentry> liveDataEntryCollection;

    public Livedatacollection() {
        liveDataEntryCollection = new HashMap<>();
    }

    public void addEntry(String id, Livedataentry obj) {
        liveDataEntryCollection.put(id, obj);
    }

    public void setCurrentValue(String id, Object value) {
//        if () {
//        }
//        liveDataEntryCollection.get(time).ad = value;
    }

    public Object getDataByIdentifier(String id, String data) {
        if (data.equals("time")) {
            return liveDataEntryCollection.get(id).time;
        }
        if (data.equals("deviceAdress")) {
            return liveDataEntryCollection.get(id).deviceAdress;
        }
        if (data.equals("functionCode")) {
            return liveDataEntryCollection.get(id).functionCode;
        }
        if (data.equals("hexAdress")) {
            return liveDataEntryCollection.get(id).hexAdress;
        }
        if (data.equals("value")) {
            return liveDataEntryCollection.get(id).value;
        }
        return liveDataEntryCollection.get(id).id;
    }
}
