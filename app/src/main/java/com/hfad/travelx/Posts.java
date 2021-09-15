package com.hfad.travelx;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Posts {
    public String addressline,dateandtime,destname,foodcult,postdescription,profileimage,profilename,type,uid,your_experience,filter;
    public ArrayList<String> images = new ArrayList<>();

    public Posts() {
    }

    public Posts(String addressline, String dateandtime, String destname, String foodcult, String postdescription, String profileimage, String profilename, String type, String uid, String your_experience, ArrayList<String> images,String filter) {
        this.addressline = addressline;
        this.dateandtime = dateandtime;
        this.destname = destname;
        this.foodcult = foodcult;
        this.postdescription = postdescription;
        this.profileimage = profileimage;
        this.profilename = profilename;
        this.type = type;
        this.uid = uid;
        this.your_experience = your_experience;
        this.images = images;
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getAddressline() {
        return addressline;
    }

    public void setAddressline(String addressline) {
        this.addressline = addressline;
    }

    public String getDateandtime() {
        return dateandtime;
    }

    public void setDateandtime(String dateandtime) {
        this.dateandtime = dateandtime;
    }

    public String getDestname() {
        return destname;
    }

    public void setDestname(String destname) {
        this.destname = destname;
    }

    public String getFoodcult() {
        return foodcult;
    }

    public void setFoodcult(String foodcult) {
        this.foodcult = foodcult;
    }

    public String getPostdescription() {
        return postdescription;
    }

    public void setPostdescription(String postdescription) {
        this.postdescription = postdescription;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getProfilename() {
        return profilename;
    }

    public void setProfilename(String profilename) {
        this.profilename = profilename;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getYour_experience() {
        return your_experience;
    }

    public void setYour_experience(String your_experience) {
        this.your_experience = your_experience;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
