package com.laxco.livescorev1.Models;

import org.json.JSONObject;

public class Stat {
    private String type,home_val,away_val;
    private boolean isToCalculate;

    public Stat(String type, String home_val, String away_val,boolean isToCalculate) {
        this.type = type;
        this.home_val = home_val;
        this.away_val = away_val;
        this.isToCalculate = isToCalculate;
    }

    public boolean isToCalculate() {
        return isToCalculate;
    }

    public void setToCalculate(boolean toCalculate) {
        isToCalculate = toCalculate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHome_val() {
        return home_val;
    }

    public void setHome_val(String home_val) {
        this.home_val = home_val;
    }

    public String getAway_val() {
        return away_val;
    }

    public void setAway_val(String away_val) {
        this.away_val = away_val;
    }
}
