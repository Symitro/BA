/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bachelorarbeit_regent.misc;

import Bachelorarbeit_regent.data.Datacollection;
import Bachelorarbeit_regent.data.Dataentry;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Julian
 */
public class Request {

    public Datacollection CUM4Collection;
    public Datacollection CUCollection;
    public Datacollection AAWCollection;
    public Datacollection PLCollection;
    public Datacollection PRCollection;
    public Datacollection SH1Collection;
    public Datacollection SH2Collection;
    public Datacollection SH3Collection;
    public Datacollection SH4Collection;
    Datacollection[] datacollectionArray;

    public Request() {
        // Daten werden aus CSV gelesen
        CUM4Collection = CSVReader.getDataFromCsv("controlunit_m4");
        CUCollection = CSVReader.getDataFromCsv("controlunit");
        AAWCollection = CSVReader.getDataFromCsv("aloneatwork");
        PLCollection = CSVReader.getDataFromCsv("panel");;
        PRCollection = CSVReader.getDataFromCsv("panel");
        SH1Collection = CSVReader.getDataFromCsv("sensormodule");
        SH2Collection = CSVReader.getDataFromCsv("sensormodule");
        SH3Collection = CSVReader.getDataFromCsv("sensormodule");
        SH4Collection = CSVReader.getDataFromCsv("sensormodule");

        datacollectionArray = new Datacollection[]{
            CUM4Collection, CUCollection, AAWCollection, PLCollection, PRCollection,
            SH1Collection, SH2Collection, SH3Collection, SH4Collection};
    }

    public byte[] requestGenerate(String collectionName) {
        byte[] requestArray = new byte[4];

        for (int i = 0; i < datacollectionArray.length; i++) {
            if (collectionName.equals(datacollectionArray[i].dataEntryCollection.toString())) {
                for (Map.Entry entry : datacollectionArray[i].dataEntryCollection.entrySet()) {
                    byte[] hexAdressbuffer = ConversionHelper.hexStringToByteArray(entry.getValue().toString());
                    requestArray[0] = (byte) hexAdressbuffer[0];
                    requestArray[1] = (byte) hexAdressbuffer[1];
                    requestArray[2] = (byte) 0x00;
                    requestArray[3] = (byte) 0x01;

                }
            }
        }
        return requestArray;
    }
}

// Methode mit Iterator
//                Iterator<Map.Entry<String, Dataentry>> iterator = datacollectionArray[i].dataEntryCollection.entrySet().iterator();
//                while (iterator.hasNext()) {
//                    Map.Entry<String, Dataentry> entry = iterator.next();
//                    System.out.println("Key : " + entry.getKey() + " Value :" + entry.getValue());
