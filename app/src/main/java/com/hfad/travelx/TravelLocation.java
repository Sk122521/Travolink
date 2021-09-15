package com.hfad.travelx;

public class TravelLocation {
    String name,mainimage;
   public TravelLocation(){}

    public TravelLocation(String name, String mainimage) {
        this.name = name;
        this.mainimage = mainimage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainimage() {
        return mainimage;
    }

    public void setMainimage(String mainimage) {
        this.mainimage = mainimage;
    }
}
