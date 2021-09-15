package com.hfad.travelx;

public class nmodel {
    public String senderid;
    public String notification;
    public String type;
    public nmodel(String senderid, String notification,String type) {
        this.senderid = senderid;
        this.notification = notification;
        this.type = type;
    }

    public nmodel() {
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }
}
