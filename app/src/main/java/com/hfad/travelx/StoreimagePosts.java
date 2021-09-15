package com.hfad.travelx;

import java.util.ArrayList;
import java.util.Map;

public class StoreimagePosts {
    private String date,time,type,profilename,profileimage,uid,dateandtime,addressline,posturl,photodescription ;
    private ArrayList<String> images;
    private Map<String,String> timestamp;

    public StoreimagePosts() {
    }
    public StoreimagePosts(String date, String time, String type, String profilename, String profileimage, String uid, String dateandtime, String addressline, String posturl, String photodescription, ArrayList<String> images, Map<String, String> timestamp) {
        this.date = date;
        this.time = time;
        this.type = type;
        this.profilename = profilename;
        this.profileimage = profileimage;
        this.uid = uid;
        this.dateandtime = dateandtime;
        this.addressline = addressline;
        this.posturl = posturl;
        this.photodescription = photodescription;
        this.images = images;
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProfilename() {
        return profilename;
    }

    public void setProfilename(String profilename) {
        this.profilename = profilename;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDateandtime() {
        return dateandtime;
    }

    public void setDateandtime(String dateandtime) {
        this.dateandtime = dateandtime;
    }

    public String getAddressline() {
        return addressline;
    }

    public void setAddressline(String addressline) {
        this.addressline = addressline;
    }

    public String getPosturl() {
        return posturl;
    }

    public void setPosturl(String posturl) {
        this.posturl = posturl;
    }

    public String getPhotodescription() {
        return photodescription;
    }

    public void setPhotodescription(String photodescription) {
        this.photodescription = photodescription;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public Map<String, String> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Map<String, String> timestamp) {
        this.timestamp = timestamp;
    }
}
