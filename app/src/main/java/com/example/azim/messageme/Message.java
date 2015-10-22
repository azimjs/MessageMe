package com.example.azim.messageme;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Azim on 10/3/2015.
 */
public class Message implements Serializable {

    String objectId, sender, receiver, senderName, message, beaconId;
    Boolean isRead;
    Date createdAt, updatedAt;

    public Message(String objectId, String sender, String receiver, String senderName, String message, String beaconId, Boolean isRead, Date createdAt, Date updatedAt) {
        this.objectId = objectId;
        this.sender = sender;
        this.receiver = receiver;
        this.senderName = senderName;
        this.message = message;
        this.beaconId = beaconId;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public  Message(){
        //null constructor
    }

    public String getObjectId() { return objectId; }

    public void setObjectId(String objectId) { this.objectId = objectId; }

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "obejectId='" + objectId + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", senderName='" + senderName + '\'' +
                ", message='" + message + '\'' +
                ", beaconId='" + beaconId + '\'' +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
