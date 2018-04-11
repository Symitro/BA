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
public class CSVReader {

    public static void main(String[] args) {
        Datacollection mainCollection = null;
//        Datacollection CUCollection;
//        Datacollection AAWCollection;
//        Datacollection PLCollection;
//        Datacollection PRCollection;
//        Datacollection SH1Collection;
//        Datacollection SH2Collection;
//        Datacollection SH3Collection;
//        Datacollection SH4Collection;

        String csvFile = "C:/Users/Julian/Documents/GitHub/ba-gui/05_Modbus/controlunit_m4/modbus.CSV";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            String headerLine = br.readLine();
            System.out.println("headerLine: " + headerLine);
            while ((line = br.readLine()) != null) {
                String[] cell = line.split(cvsSplitBy);
                //New data object
                Dataentry data = new Dataentry();
//                data.varName = cell[0];
                data.hexIdentifier = cell[5];
                data.valueType = cell[7];
                data.min = Integer.parseInt(cell[9]);
                data.max = Integer.parseInt(cell[10]);
                data.defaultValue = cell[11];
                data.currentValue = 1;

                System.out.println("data.varName= " + data.varName);
                System.out.println("data.hexIdentifier= " + data.hexIdentifier);
                System.out.println("data.valueType= " + data.valueType);
                System.out.println("data.min= " + data.min);
                System.out.println("data.max= " + data.max);
                System.out.println("data.defaultValue= " + data.defaultValue);
                System.out.println("data.currentValue= " + data.currentValue);
                mainCollection.addEntry("hex", data);

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

    }

}
