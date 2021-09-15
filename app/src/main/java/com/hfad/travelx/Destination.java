package com.hfad.travelx;

public class Destination {
    String image,name,location,city;
    public Destination(){}
    public Destination(String image, String name,  String location, String video,String city) {
        this.image = image;
        this.name = name;
        this.location = location;
        this.city = city;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
