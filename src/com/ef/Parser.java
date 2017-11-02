/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef;

import com.ef.util.Util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Olakunle Awotunbo
 */
public class Parser {

    public static void main(String[] args) {

        Util util = new Util();
        MainApp mainApp = new MainApp();
        
        // For testing purpose
        //String fileName = "C:/logs/access.log";
        //String fileName = "C:/logs/access_small.log";

        //String startString = "2017-01-01.00:00:00";
        //String startDateStr = "2017-01-01.13:00:00";
        //String endDateStr = "2017-01-01.14:00:00";

        // For Question 1
        // String duration = "hourly";
        // int threshold = 100;
        
        // Ready log
        // util.fileReader(fileName);
        //util.performOperation(startDateStr, duration, threshold);
        
        if (args.length > 0) {

            mainApp.runApp(util, args);
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
