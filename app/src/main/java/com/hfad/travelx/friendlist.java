package com.hfad.travelx;

public class friendlist {
    private String address;
    private String image;
    private String name;
    private String uid;

    public friendlist() {
    }

    public friendlist(String name2, String address2, String image2, String uid2) {
        this.name = name2;
        this.address = address2;
        this.image = image2;
        this.uid = uid2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address2) {
        this.address = address2;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image2) {
        this.image = image2;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid2) {
        this.uid = uid2;
    }
}
