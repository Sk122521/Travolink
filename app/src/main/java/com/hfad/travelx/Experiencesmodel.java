package com.hfad.travelx;

public class Experiencesmodel {
    private String exp,uid;
   public Experiencesmodel(){}

    public Experiencesmodel(String exp, String uid) {
        this.exp = exp;
        this.uid = uid;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
