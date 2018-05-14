/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bachelorarbeit_regent.data;

import Bachelorarbeit_regent.misc.ConversionHelper;
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

    public void setCurrentValue(String hexIdentifier, Object value) {
        if ("char".equals(dataEntryCollection.get(hexIdentifier).valueType)) {
            String unhexdataentity = ConversionHelper.unHex((String) value);
            value = unhexdataentity;
        } else {
            Long dataentitylong = Long.parseLong((String) value, 16);
            value = dataentitylong;
        }
        dataEntryCollection.get(hexIdentifier).currentValue = value;
    }

    public Object getDataByIdentifier(String hexIdentifier, String data) {
        if (data.equals("varName")) {
            return dataEntryCollection.get(hexIdentifier).varName;
        }
        if (data.equals("index")) {
            return dataEntryCollection.get(hexIdentifier).index;
        }
        if (data.equals("defaultValue")) {
            return dataEntryCollection.get(hexIdentifier).defaultValue;
        }
        if (data.equals("currentValue")) {
            return dataEntryCollection.get(hexIdentifier).currentValue;
        }
        if (data.equals("minValue")) {
            return dataEntryCollection.get(hexIdentifier).minValue;
        }
        if (data.equals("maxValue")) {
            return dataEntryCollection.get(hexIdentifier).maxValue;
        }
        return dataEntryCollection.get(hexIdentifier).valueType;
    }
}
