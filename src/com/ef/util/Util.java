/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.util;

import com.ef.bean.LogBean;
import com.ef.model.Log;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Olakunle Awotunbo
 */
public class Util {

    private ArrayList<LogBean> entriesItems;

    public void fileReader(String fileName) {

        this.entriesItems = new ArrayList<>();

        //read file into stream, try-with-resources
        // try-with-resources, it auto close the resources
        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
            //lines.forEach(System.out::println);

            lines.forEach(line -> {

                entriesItems.add(parseEntry(line));
                // System.out.println(line);
            });

            // Persist the records
            System.out.println("entriesItems.size :: " + entriesItems.size());
            //persistLog(entriesItems);
            saveEntries(entriesItems);
            //testBulkSave(entriesItems);

            // 
            /*
            for (String line : (Iterable<String>) lines::iterator) {
                System.out.println(line);
             records.add(parseEntry(line));
            }
            
             */
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Read parse each entry
     *
     * @param line
     * @return
     */
    public LogBean parseEntry(String line) {
        // The log format:

        // 2017-01-01 00:00:11.763|192.168.234.82|"GET / HTTP/1.1"|200|"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0"       
        LogBean logBean = new LogBean();
        StringBuilder sb = new StringBuilder(line);

        String dateStr = matchTo(sb, "|");
        //Date date3 = parseDate(dateStr);
        Date date = parseDate2(dateStr);
        //LocalDateTime date = parseDate3(dateStr);
        // System.out.println("date3: " + date3);
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
        Date today = new Date();

        LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

        // logBean.setDate(ldt);
        logBean.setDate(date);
        logBean.setIp(ip);
        logBean.setRequest(request);
        logBean.setStatus(status);
        logBean.setUserAgent(userAgent);

        //System.out.println("logBean.getDate before :: " + logBean.getDate());
        return logBean;
    }

    public static String matchTo(StringBuilder sb, String delimiter) {
        int x = sb.indexOf(delimiter);
        if (x == -1) {
            x = sb.length();
        }
        String ans = sb.substring(0, x);
        sb.delete(0, x + delimiter.length());
        return ans;
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss", Locale.US);

    public static Date parseDate(String dateStr) {
        ParsePosition pp = new ParsePosition(0);
        return dateFormat.parse(dateStr, pp);
    }

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

    public Date parseDate3(String dateStr) {
//  String startString = "2017-01-01.13:00:00";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
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

    public LocalDateTime convertDateToLocalTime(String date) {
        return LocalDateTime.of(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss")),
                LocalDateTime.now().toLocalTime()
        );
    }

    /**
     * This calculate end date 
     * @param startDate The start date 
     * @param duration the duration
     * @return the calculated date
     */
    public LocalDateTime calEndDate(String startDate, String duration) {
        
        if ("hourly".equalsIgnoreCase(duration)) {
            return convertDateToLocalTime(startDate).plusHours(1);
        } else if ("daily".equalsIgnoreCase(duration)) {
            return convertDateToLocalTime(startDate).plusDays(1);
        }
        return null;
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

        /*
        Map<String, List<Log>> logMap = logList.stream()
                .collect(Collectors.groupingBy(Log::getIp));
         */
      /*
        logList.stream()
                .collect(Collectors.groupingBy(Log::getIp));
        */
        /*
        Stream<Student> students = persons.stream()
                .filter(p -> p.getAge() > 18)
                .map(person -> new Student(person));
        */
        //System.out.println("productsByArticle :: " + logMap.size());

        return logList;
    }

   

    public HashMap<String, List<Log>> groupRecordsByIpAddress(List<Log> logList) {
        return (HashMap<String, List<Log>>) logList.stream()
                .collect(Collectors.groupingBy(Log::getIp));
    }
    
    /*
    public HashMap<String, List<Log>> groupRecordsByIpAddress(String startDate, String duration, int threshhold) {
        return (HashMap<String, List<Log>>) groupRecordsByIpAddress(startDate, duration)
                .entrySet()
                .stream()
                .filter(a -> a.getValue().size() > threshhold)
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }
    */

}