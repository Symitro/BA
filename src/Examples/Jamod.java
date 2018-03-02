/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Examples;

/**
 *
 * @author Julian
 */
public class Jamod {
    
    public static void Main()
    static SerialConnection con = null;
  static int freqMsec = 1000;  // number of milliseconds between
                               // controller network queries
  static boolean DEBUG = false;
  static boolean finished = false;

  public static void main(String args) throws IOException
  {
    String portname = "/dev/ttyS0";
    int    baud = 57600;

    try {
      //check params
      if (args.length > 0) {
        try {
          if (args.length == 2) {
            portname = args;
            baud = Integer.parseInt(args);
          } else {
            printUsage();
          }
        } catch (Exception ex) {
          ex.printStackTrace();
          printUsage();
          System.exit(1);
        }
      }
      // open the serial communications port
      SerialParameters params=new SerialParameters();
      params.setPortName(portname);
      System.out.println("Baud set to: " + baud);
      params.setBaudRate(baud);
      params.setDatabits(8);
      params.setParity("None");
      params.setStopbits(1);
      params.setEncoding(Modbus.SERIAL_ENCODING_RTU);
      params.setEcho(true);
      con = new SerialConnection(params);
      con.open(); 
}
