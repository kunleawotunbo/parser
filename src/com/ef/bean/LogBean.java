/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.bean;

import java.util.Date;

/**
 * Log helper bean
 * @author Olakunle Awotunbo
 */
public class LogBean {
    // 2017-01-01 00:00:11.763|192.168.234.82|"GET / HTTP/1.1"|200|"swcd (unknown version) CFNetwork/808.2.16 Darwin/15.6.0"
   // @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date date;
    private String ip;
    private String request;    
    private int status;
    private String userAgent;
    
    /*
    public LogBean() {
		
    }
    public LogBean(Date date, String ip, String request, int status, String userAgent) {
		super();
                this.date = date;
		this.ip = ip;
                this.request = request;		
		this.status = status;
                this.userAgent = userAgent;
	}
    
    */

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the request
     */
    public String getRequest() {
        return request;
    }

    /**
     * @param request the request to set
     */
    public void setRequest(String request) {
        this.request = request;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the userAgent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * @param userAgent the userAgent to set
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
