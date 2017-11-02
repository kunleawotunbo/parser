/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef;

import com.ef.util.Util;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Olakunle Awotunbo
 */
public class MainApp {

    public void runApp(Util fileAnalyzer, String[] array) {
        Util util = new Util();
        System.out.println("Your first argument is: " + array[0]);
        System.out.println("Your Second argument is: " + array[1]);
        
        
        String accesslog = decodeArgs(array).get("accesslog");
        String duration = decodeArgs(array).get("duration");
        String startString = decodeArgs(array).get("startDate");
        int threshold = Integer.parseInt(decodeArgs(array).get("threshold"));
        
        // Read file
        util.fileReader(accesslog);
        
         System.out.println(startString + " : " + duration + " : " + threshold);
         
         // 
        String dDuration = determineDuration(duration);
        
        // Perform operations
         util.performOperation(startString, duration, threshold);     
        
        
       

        // fileAnalyzer.mapIpAddressAgainstComment("2000-10-21.21:55:36","daily",1);
    }

    public HashMap<String, String> decodeArgs(String[] array) {
        return (HashMap<String, String>) Stream.of(array)
                .map(elem -> elem.split("\\="))
                .filter(elem -> elem.length == 2)
                .collect(Collectors.toMap(e -> e[0], e -> e[1]))
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(e -> e.getKey().split("\\-\\-")[1], e -> e.getValue()));

    }

  
    public String determineDuration(String durationArgs) {
        String duration;
        switch (durationArgs.toLowerCase()) {
            case "daily":
                duration = "daily";
                break;
            case "hourly":
                duration = "hourly";
                break;
            default:
                System.out.println(durationArgs + "Is not a valid value");
                System.out.println("Allowed values for durations are  \"daily\" and \"hourly\"");
                throw new IllegalArgumentException("Invaliduration : " + durationArgs);
        }
        return duration;
    }
}
