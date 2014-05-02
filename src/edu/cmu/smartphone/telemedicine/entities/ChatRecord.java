package edu.cmu.smartphone.telemedicine.entities;

import java.sql.Date;

public class ChatRecord {
    private String message;
    private Boolean status;
    private Date date;
    private String chatUserID;
    private Boolean direction;
    private int messageType;
    
    public ChatRecord() {
        message = null;
        status = false;
        date = null;
        chatUserID = null;
        direction = true;
        messageType = 0;
    }
    
<<<<<<< HEAD
    
           
    public String getMessage() {
=======





	public void setStatus(Boolean status) {
		this.status = status;
	}






	public void setChatUserID(String chatUserID) {
		this.chatUserID = chatUserID;
	}






	public void setDirection(Boolean direction) {
		this.direction = direction;
	}






	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}



	public String getMessage() {
>>>>>>> FETCH_HEAD
        return message;
    }
    
    public boolean getDirection() {
        return direction;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Boolean getStatus() {
        return status;
    }
    
    public void setStatus(boolean status) {
        this.status = status;
    }
    
    public Date getDate() {
        //return null;
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public String getChatUserID() {
        return chatUserID;
    }
    
    
    public int getMessageType() {
        return messageType;
    }
}
