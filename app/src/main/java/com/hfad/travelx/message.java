package com.hfad.travelx;

public class message {
    private String from;
    private String message;
    private String messageid;
    private String name;
    private String seen;
    private String time;
    private String to;
    private String type;
    private String filename;

    public message() {
    }

    public message(String message2, String type2, String from2, String to2, String messageid2, String time2, String name2, String seen2,String filename) {
        this.message = message2;
        this.type = type2;
        this.from = from2;
        this.to = to2;
        this.messageid = messageid2;
        this.time = time2;
        this.name = name2;
        this.seen = seen2;
        this.filename = filename;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message2) {
        this.message = message2;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type2) {
        this.type = type2;
    }

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from2) {
        this.from = from2;
    }

    public String getTo() {
        return this.to;
    }

    public void setTo(String to2) {
        this.to = to2;
    }

    public String getMessageid() {
        return this.messageid;
    }

    public void setMessageid(String messageid2) {
        this.messageid = messageid2;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time2) {
        this.time = time2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getSeen() {
        return this.seen;
    }

    public void setSeen(String seen2) {
        this.seen = seen2;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
