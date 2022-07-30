package com.laxco.livescorev1.Models;

import org.w3c.dom.Comment;

import java.util.List;

public class LeagueMain {
    private String id,country,name,logo,date;

    public LeagueMain(String id, String country, String name,String logo,String date) {
        this.id = id;
        this.country = country;
        this.name = name;
        this.logo = logo;
        this.date =date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //for checking
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof LeagueMain)) {
            return false;
        }else {
            return true;
        }
    }
    @Override
    public String toString() {
        return "[" + this.id + "," + this.name + "]";
    }
}
