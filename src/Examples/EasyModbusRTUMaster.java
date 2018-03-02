/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Examples;

import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;
import java.io.Console;
import java.io.IOException;
import java.net.SocketException;

/**
 *
 * @author Julian
 */
public class EasyModbusRTUMaster {

    public static void Main(string[] args) throws ModbusException, SocketException, IOException {
        ModbusClient modbusClient = new ModbusClient("COM3");
        //modbusClient.UnitIdentifier = 1; Not necessary since default slaveID = 1;
        //modbusClient.Baudrate = 9600;	// Not necessary since default baudrate = 9600
        //modbusClient.Parity = System.IO.Ports.Parity.None;
        //modbusClient.StopBits = System.IO.Ports.StopBits.Two;
        //modbusClient.ConnectionTimeout = 500;			
        modbusClient.Connect();

        Console.WriteLine("Value of Discr. Input #1: " + modbusClient.ReadDiscreteInputs(0, 1)[0].ToString());	//Reads Discrete Input #1
        Console.WriteLine("Value of Input Reg. #10: " + modbusClient.ReadInputRegisters(9, 1)[0].ToString());	//Reads Inp. Reg. #10

        modbusClient.WriteSingleCoil(4, true);		//Writes Coil #5
        modbusClient.WriteSingleRegister(19, 4711);	//Writes Holding Reg. #20

        Console.WriteLine("Value of Coil #5: " + modbusClient.ReadCoils(4, 1)[0].ToString());	//Reads Discrete Input #1
        Console.WriteLine("Value of Holding Reg.. #20: " + modbusClient.ReadHoldingRegisters(19, 1)[0].ToString());	//Reads Inp. Reg. #10
        modbusClient.WriteMultipleRegisters(49, new int[10] {1
        ,2,3,4,5,6,7,8,9,10});
        modbusClient.WriteMultipleCoils(29, new bool[10] {true
        ,true,true,true,true,true,true,true,true,true,});

        Console.Write("Press any key to continue . . . ");
        Console.ReadKey(true);
    }
}
