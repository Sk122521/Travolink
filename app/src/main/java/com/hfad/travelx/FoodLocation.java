package com.hfad.travelx;

import java.io.Serializable;
import java.util.ArrayList;

public class FoodLocation {
    public  String name,type,mainimage;
   public FoodLocation(){
   }

    public FoodLocation(String name, String type, String mainimage) {
        this.name = name;
        this.type = type;
        this.mainimage = mainimage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMainimage() {
        return mainimage;
    }

    public void setMainimage(String mainimage) {
        this.mainimage = mainimage;
    }
}
