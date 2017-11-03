/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef;

/**
 *
 * @author Olakunle Awotunbo
 */
public class Parser {

    public static void main(String[] args) {

        //Util util = new Util();
        MainApp mainApp = new MainApp();

        // Method to test tool
       //util.testTool();

        if (args.length > 0) {
            // Pass command args to main app
            mainApp.runApp(args);
        } else {
            System.out.println("Please check the arguments passed..");
            System.out.println("The command should be similar to: ");
            System.out.println("java -cp \"parser.jar\" com.ef.Parser --accesslog=/path/to/file --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100");
            System.out.println("in your OS's console.");
            System.out.println("NB: check that you are running java 8 ++");
            System.out.println("Also Ensure that your MySQL server is running");
            System.out.println("on USER = root and PASSWORD = ");
            System.out.println("and has a database named: 'log_analyzer'");

            System.out.println("Thanks");
        }

    }
}
