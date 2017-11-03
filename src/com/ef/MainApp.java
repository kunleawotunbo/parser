/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef;

import com.ef.util.Util;

/**
 * Main App reads the arguments parsed from the command
 * to start the tool. It passes the values of the arguments to functions 
 * that does appropriate checks.
 * @author Olakunle Awotunbo
 */
public class MainApp {

    public void runApp(String[] array) {
        
        Util util = new Util();        
       
        String accesslogArg = "accesslog";
        String durationArg = "duration";
        String startDateArg = "startDate";
        String thresholdArg = "threshold";       
      
        // Get each arguments
        String accesslog = util.decodeArgs(array).get(accesslogArg);
        String duration = util.decodeArgs(array).get(durationArg);
        String startString = util.decodeArgs(array).get(startDateArg);
        String thresholdString = util.decodeArgs(array).get(thresholdArg);
        
        // Check if threshold is integer 
         util.isInteger(thresholdArg, thresholdString);
        int threshold = Integer.parseInt(thresholdString);
        
        // Check if duration is correct. Only accepted hourly and daily
        util.determineDuration(durationArg, duration);
        
        // check is start date format is correct
        String dateFormat = "yyyy-MM-dd.HH:mm:ss";
        util.checkDateFormat(startDateArg, dateFormat, startString);
        
        // Read file
        util.fileReader(accesslogArg, accesslog);
        
        // System.out.println(startString + " : " + duration + " : " + threshold);
         
         
        
        
        // Perform operations
        util.performOperation(startString, duration, threshold);         
        

    } 
  
}
