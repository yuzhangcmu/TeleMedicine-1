package edu.cmu.smartphone.telemedicine.entities;

import java.sql.Date;

public class RecentChat {
    private String userID;
    private Date updateTime;
    
    public RecentChat(String userID, Date updateTime) {
        this.userID = userID;
        this.updateTime = updateTime;
    }
    
    public String getUserID() {
        return userID;
    }
    
    public Date getDate() {
        return updateTime;
    }
    
    public void setDate(Date updateTime) {
        this.updateTime = updateTime;
    }
}
