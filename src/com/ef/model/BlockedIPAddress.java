/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.model;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Olakunle Awotunbo
 */

@Entity
@Table(name = "blocked_ipaddresses")
public class BlockedIPAddress {
    
    private static long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
     @Column(name = "ip")
    private String ip;
    
    @Column(name = "no_of_request")
    private Integer noOfRequest;
    
     @Column(name = "comment")
    private String comment;
     
     @Column(name = "blocked_date")
    private Date blockedDate;

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @param aSerialVersionUID the serialVersionUID to set
     */
    public static void setSerialVersionUID(long aSerialVersionUID) {
        serialVersionUID = aSerialVersionUID;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the noOfRequest
     */
    public Integer getNoOfRequest() {
        return noOfRequest;
    }

    /**
     * @param noOfRequest the noOfRequest to set
     */
    public void setNoOfRequest(Integer noOfRequest) {
        this.noOfRequest = noOfRequest;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
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
     * @return the blockedDate
     */
    public Date getBlockedDate() {
        return blockedDate;
    }

    /**
     * @param blockedDate the blockedDate to set
     */
    public void setBlockedDate(Date blockedDate) {
        this.blockedDate = blockedDate;
    }

  
}
