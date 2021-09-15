package com.hfad.travelx;

public class Videopostmodel {
    public String dateandtime;
    public String postdescription;
    public String profileimage;
    public String profilename;
    public String uid;
    public String addressline;
    public String posturl;

    public Videopostmodel(){}
    public Videopostmodel(String dateandtime, String postdescription, String profileimage, String profilename, String type, String uid, String addressline, String posturl) {
        this.dateandtime = dateandtime;
        this.postdescription = postdescription;
        this.profileimage = profileimage;
        this.profilename = profilename;
        this.uid = uid;
        this.addressline = addressline;
        this.posturl = posturl;
    }

    public String getDateandtime() {
        return dateandtime;
    }

    public void setDateandtime(String dateandtime) {
        this.dateandtime = dateandtime;
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


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
}
