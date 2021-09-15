package com.hfad.travelx;

public class comment {
    private String comment;
    private String date;
    private String name_of_commentee;
    private String profileimage_of_commentee;
    private String time;
    private String uid;

    public comment() {
    }

    public comment(String comment2, String date2, String name_of_commentee2, String profileimage_of_commentee2, String time2, String uid2) {
        this.comment = comment2;
        this.date = date2;
        this.name_of_commentee = name_of_commentee2;
        this.profileimage_of_commentee = profileimage_of_commentee2;
        this.time = time2;
        this.uid = uid2;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment2) {
        this.comment = comment2;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date2) {
        this.date = date2;
    }

    public String getName_of_commentee() {
        return this.name_of_commentee;
    }

    public void setName_of_commentee(String name_of_commentee2) {
        this.name_of_commentee = name_of_commentee2;
    }

    public String getProfileimage_of_commentee() {
        return this.profileimage_of_commentee;
    }

    public void setProfileimage_of_commentee(String profileimage_of_commentee2) {
        this.profileimage_of_commentee = profileimage_of_commentee2;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time2) {
        this.time = time2;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid2) {
        this.uid = uid2;
    }
}
