/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bachelorarbeit_regent.misc;

import Bachelorarbeit_regent.data.Datacollection;
import Bachelorarbeit_regent.data.Dataentry;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Julian
 */
// https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
public class CSVReader {

    public static Datacollection getDataFromCsv(String deviceType) {
        Datacollection mainCollection = new Datacollection();

        String csvFile = "";
        switch (deviceType) {
            case "controlunit_m4":
                csvFile = "C:/Users/Julian/Documents/GitHub/ba-gui/05_Modbus/controlunit_m4/modbus.CSV";
                break;
            case "controlunit":
                csvFile = "C:/Users/Julian/Documents/GitHub/ba-gui/05_Modbus/controlunit/modbus.CSV";
                break;
            case "aloneatwork":
                csvFile = "C:/Users/Julian/Documents/GitHub/ba-gui/05_Modbus/aloneatwork/modbus.CSV";
                break;
            case "panel":
                csvFile = "C:/Users/Julian/Documents/GitHub/ba-gui/05_Modbus/panel/modbus.CSV";
                break;
            case "sensormodule":
                csvFile = "C:/Users/Julian/Documents/GitHub/ba-gui/05_Modbus/sensormodule/modbus.CSV";
                break;
        }

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        try {
            br = new BufferedReader(new FileReader(csvFile));
            // 1. Zeile wird separat verwaltet, https://stackoverflow.com/questions/18306270/skip-first-line-while-reading-csv-file-in-java
            String headerLine = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] cell = line.split(cvsSplitBy);
                //New data object
                Dataentry data = new Dataentry();
                data.varName = cell[0];
                data.hexIdentifier = cell[5];
                data.valueType = cell[7];
                data.min = Integer.parseInt(cell[9]);
                data.max = Integer.parseInt(cell[10]);
                data.defaultValue = cell[11];
                mainCollection.addEntry(data.hexIdentifier, data);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return mainCollection;
    }

}
