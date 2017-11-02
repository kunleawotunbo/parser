/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.util;

import com.ef.bean.LogBean;
import com.ef.model.BlockedIPAddress;
import com.ef.model.Log;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Olakunle Awotunbo
 */
public class Util {

    private ArrayList<LogBean> entriesItems;
     private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss", Locale.US);


    /**
     * The accesslog file to read (Path).
     * It reads each line of the file into an arraylist.
     * Then the array list is being saved using the saveEntries method
     * @param fileName  Name of accesslog file
     */
    public void fileReader(String fileName) {

        this.entriesItems = new ArrayList<>();

        //read file into stream, try-with-resources
        // try-with-resources, it auto close the resources
        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
           
            lines.forEach(line -> {
                entriesItems.add(parseEntry(line));               
            });

            // Persist the records
            //System.out.println("entriesItems.size :: " + entriesItems.size());
            
            saveEntries(entriesItems);
           
          
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Parse each entry to LogBean Object
     *
     * @param line Line to parse
     * @return LogBean object
     */
    public LogBean parseEntry(String line) {
        // The log format:

        // 2017-01-01 00:00:11.763|192.168.234.82|"GET / HTTP/1.1"|200|"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0"       
        LogBean logBean = new LogBean();
        StringBuilder sb = new StringBuilder(line);

        String dateStr = matchTo(sb, "|");
        //Date date3 = parseDate(dateStr);
        Date date = parseDate2(dateStr);
        //System.out.println("date: " + date);

        String ip = matchTo(sb, "|");
        //System.out.println("IP: " + ip);

        matchTo(sb, "\""); // escape quote
        String request = matchTo(sb, "\"|"); //eat both
        //System.out.println("request: " + request);

        String statusStr = matchTo(sb, "|");
        //System.out.println("Status : "+statusStr);
        int status = Integer.parseInt(statusStr);

        matchTo(sb, "\""); // escape quote
        String userAgent = matchTo(sb, "\"");
        //System.out.println("userAgent: "+userAgent);

        // Date date, String ip, String request, int status, String userAgent     
        logBean.setDate(date);
        logBean.setIp(ip);
        logBean.setRequest(request);
        logBean.setStatus(status);
        logBean.setUserAgent(userAgent);

        return logBean;
    }
    
    /**
     * For string processing
     * @param sb
     * @param delimiter
     * @return 
     */
    public static String matchTo(StringBuilder sb, String delimiter) {
        int x = sb.indexOf(delimiter);
        if (x == -1) {
            x = sb.length();
        }
        String ans = sb.substring(0, x);
        sb.delete(0, x + delimiter.length());
        return ans;
    }

   /**
    * Parse dateString to date object
    * @param dateStr Date string to parse
    * @return parsed date
    */
    public static Date parseDate(String dateStr) {
        ParsePosition pp = new ParsePosition(0);
        return dateFormat.parse(dateStr, pp);
    }

     /**
    * Parse dateString to date object
    * @param dateStr Date string to parse
    * @return parsed date
    */
    public Date parseDate2(String dateStr) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
        Date date = null;

        try {
            date = formatter.parse(dateStr);
            //System.out.println("date :: " + date);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return date;
    }
  

    /**
     * To determine the end date based on startDate and duration
     * @param startDate The start date
     * @param duration Hourly or Daily
     * @return computed end date
     */
    public Date determineEndDate(String startDate, String duration) {

        if ("hourly".equalsIgnoreCase(duration)) {
            return datePlusHours(startDate, 1);
        } else if ("daily".equalsIgnoreCase(duration)) {
            return datePlusHours(startDate, 24);
        }
        return null;
    }

    /**
     * To add hours to date
     * @param date Date
     * @param hours numbers of hours to be added
     * @return  computed date
     */
    public Date datePlusHours(String date, int hours) {

        Date ddate = parseDate(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ddate);

        calendar.add(Calendar.HOUR, hours);

        return calendar.getTime();
    }

    

    /**
     * TO save log entries from the file.
     * It takes a list of LogBean object, iterate and persist 
     * @param entriesItems logBean list to persist
     */
    public void saveEntries(List<LogBean> entriesItems) {
        //Session session = sessionFactory.openSession();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Log log = null;

        int i = 0;
        int batchSize = 50; //Same as the JDBC batch size
        for (LogBean item : entriesItems) {
            log = new Log();
            log.setDate(item.getDate());
            log.setIp(item.getIp());
            log.setRequest(item.getRequest());
            log.setStatus(item.getStatus());
            log.setUserAgent(item.getUserAgent());

            session.save(log);
            if (i % batchSize == 0) // Same as the JDBC batch size
            {
                //flush a batch of inserts and release memory:
                session.flush();
                session.clear();
            }

            i++;
        }

        tx.commit();
        session.close();
    }

      /**
     * To save blocked ip.
     * It takes a  ArrayList<BlockedIPAddress> object, iterate and persist 
     * @param entriesItems itemss to persist
     */
    public void saveBulkComments(ArrayList<BlockedIPAddress> entriesItems) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        BlockedIPAddress blockedIPAddress = null;

        int i = 0;
        int batchSize = 50; //Same as the JDBC batch size
        for (BlockedIPAddress item : entriesItems) {
            blockedIPAddress = new BlockedIPAddress();

            blockedIPAddress.setBlockedDate(item.getBlockedDate());
            blockedIPAddress.setIp(item.getIp());
            blockedIPAddress.setComment(item.getComment());
            blockedIPAddress.setNoOfRequest(item.getNoOfRequest());

            session.save(blockedIPAddress);
            if (i % batchSize == 0) // Same as the JDBC batch size
            {
                //flush a batch of inserts and release memory:
                session.flush();
                session.clear();
            }

            i++;
        }

        tx.commit();
        session.close();
    }
    
    /**
     * Main operation that does the 
     * @param startDateString
     * @param duration
     * @param threshold 
     */
    public void performOperation(String startDateString, String duration, int threshold) {

        Date startDate = parseDate(startDateString);        
        Date endDate = determineEndDate(startDateString, duration);

        ArrayList<BlockedIPAddress> ipRequestResult = findIPRequests(startDate, endDate, threshold);

        // Print blocked IPs to console     
        String title = "IP with  more than " + threshold + " requests between " + startDate.toString() + " and " + endDate.toString();
       printToConsole(ipRequestResult, title);       

        // Save blocked ips
        saveBulkComments(ipRequestResult);
    }

    /**
     * Gets a list of IPs that exceeds the threshold between start and end dte
     * @param dstartDate Start date
     * @param dendDate End date
     * @param threshold threshold
     * @return List of IPs that exceeds threshold
     */
    public ArrayList<BlockedIPAddress> findIPRequests(Date dstartDate, Date dendDate, int threshold) {

        Session session = HibernateUtil.getSessionFactory().openSession();

        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDate = formatter.format(dstartDate);
        String endDate = formatter.format(dendDate);

        ArrayList<BlockedIPAddress> list = null;
        BlockedIPAddress blockedIPAddress = null;

        String sql = "SELECT b.ip , COUNT(ip) as requests "
                + " FROM "
                + "logs AS b WHERE "
                + " b.date BETWEEN  '" + startDate + "' AND '" + endDate + "' "
                + " GROUP BY ip HAVING requests > " + threshold;

        //System.out.println("sql :: " + sql);
        SQLQuery query = session.createSQLQuery(sql);
        Iterator<Object[]> results = query.list().iterator();
        int i = 0;
        String comment = "Exceeded more than " + threshold + " requests between " + startDate + " and " + endDate;
        Date date = new Date();
        while (results.hasNext()) {
            i = 0;
            if (list == null) {
                list = new ArrayList<BlockedIPAddress>();
            }
            blockedIPAddress = new BlockedIPAddress();

            Object[] row = results.next();

            blockedIPAddress.setIp((String) row[i++]);
            //Integer noOfRequest = ((BigInteger) row[i++]).intValueExact();            
            blockedIPAddress.setNoOfRequest(((BigInteger) row[i++]).intValueExact());
            blockedIPAddress.setComment(comment);
            blockedIPAddress.setBlockedDate(date);

            list.add(blockedIPAddress);
        }
        //System.out.println("list.size() :: " + list.size());
        
        return list;
    }

   
/**
 * This prints the list of blocked items to the console
 * @param blockedIPAddresses Arraylist of items to be blocked
 * @param title  Title of table printed in the console
 */
    public void printToConsole(ArrayList<BlockedIPAddress> blockedIPAddresses, String title) {             
        
        
        System.out.println("\n------------------------------------------------------------------------------------------------------");
	 System.out.println("" + title);
        System.out.println("------------------------------------------------------------------------------------------------------");
	System.out.println("  IP             :                               COMMENT  ");
	 System.out.println("------------------------------------------------------------------------------------------------------");
	blockedIPAddresses.stream().forEachOrdered(item->System.out.println(
                item.getIp() + " : " + item.getComment()
						));
			
	 System.out.println("------------------------------------------------------------------------------------------------------");
    }


    
    
    
    /*
    
    public List<Log> findIp(Date startDate, Date endDate, int threshold) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        System.out.println("startDate :: " + startDate);
        System.out.println("endDate :: " + endDate);

        Criteria crit = session.createCriteria(Log.class);

        crit.add(Restrictions.eq("ip", "192.168.234.82"));

        crit.add(Restrictions.ge("date", startDate));
        crit.add(Restrictions.le("date", endDate));

        // System.out.println("crit.toString() :: " + crit.toString());
        System.out.println("crit.list().size() :: " + crit.list().size());
        return crit.list();
    }

    public List<Log> findByIp(Date startDate, Date endDate, int threshold) {

        Session session = HibernateUtil.getSessionFactory().openSession();

        System.out.println("startDate :: " + startDate);
        System.out.println("endDate :: " + endDate);

        Criteria crit = session.createCriteria(Log.class);

        //crit.add(Restrictions.eq("ip", "192.168.234.82"));        
        crit.add(Restrictions.ge("date", startDate));
        crit.add(Restrictions.le("date", endDate));

        // System.out.println("crit.toString() :: " + crit.toString());
        System.out.println("crit.list().size() :: " + crit.list().size());

        List<Log> logList = crit.list();
        
        return logList;
    }

    
    
    public void persistLog(List<LogBean> entriesItems) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            //session.beginTransaction();
            Log log = new Log();

            transaction = session.beginTransaction();
            int i = 0;
            for (LogBean item : entriesItems) {

                log.setDate(item.getDate());
                log.setIp(item.getIp());
                log.setRequest(item.getRequest());
                log.setStatus(item.getStatus());
                log.setUserAgent(item.getUserAgent());

                session.save(log);
                int batchSize = 50;
                if (i % batchSize == 0) { //20, same as the JDBC batch size
                    //flush a batch of inserts and release memory:
                    session.flush();
                    session.clear();
                }

                i++;
            }
            transaction.commit();

        } catch (HibernateException e) {
            transaction.rollback();

            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }

    }
    
    public HashMap<String, List<Log>> groupRecordsByIpAddress(List<Log> logList) {
        return (HashMap<String, List<Log>>) logList.stream()
                .collect(Collectors.groupingBy(Log::getIp));
    }

    public HashMap<String, List<Log>> groupRecordsByIpAddress(List<Log> logList, int threshhold) {
        return (HashMap<String, List<Log>>) groupRecordsByIpAddress(logList)
                .entrySet()
                .stream()
                .filter(a -> a.getValue().size() > threshhold)
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

      public LocalDateTime convertDateToLocalTime(String date) {
        return LocalDateTime.of(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss")),
                LocalDateTime.now().toLocalTime()
        );
    }
      
    public void performOperation3(String dateString, String duration, int threshold) {

        LocalDateTime startDate = convertDateToLocalTime(dateString);
        LocalDateTime endDate = calEndDate(dateString, duration);

        List<Log> logList = findByStartAndEndDate(convertedLocalDateTimeToDate(startDate), convertedLocalDateTimeToDate(endDate));

        int i = 1;
        groupRecordsByIpAddress(logList, threshold).
                entrySet().stream().
                forEach(e -> {

                    Session session = HibernateUtil.getSessionFactory().openSession();
                    Transaction transaction = null;
                    //System.out.println("i :: " + i);
                    try {
                        Log comment = new Log();
                        System.out.println(e.getValue());
                        e.getValue().stream().distinct()
                                .collect(Collectors.groupingBy(Log::getStatus))
                                .entrySet().stream().forEach(leSet -> {
                                   
                                });
                        transaction.commit();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        session.close();
                    }
                    // i++;
                });

    }
    
    
    public List<Log> findByStartAndEndDate(Date startDate, Date endDate) {

        Session session = HibernateUtil.getSessionFactory().openSession();

        Criteria crit = session.createCriteria(Log.class);

        crit.add(Restrictions.ge("date", startDate));
        crit.add(Restrictions.le("date", endDate));

        // System.out.println("crit.toString() :: " + crit.toString());
        //System.out.println("crit.list().size() :: " + crit.list().size());        
        return crit.list();
    }
    
    
     public Date convertedLocalDateTimeToDate(LocalDateTime localDateTime) {

        Date convertedDatetime = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        return convertedDatetime;
    }
    
    */


    
}
