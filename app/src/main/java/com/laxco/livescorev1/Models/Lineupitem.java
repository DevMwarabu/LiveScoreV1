package com.laxco.livescorev1.Models;

import org.json.JSONObject;

public class Lineupitem {
    private String team_id,type,details,season;
    private boolean isHome;
    private JSONObject time,player;

    public Lineupitem(String team_id, String type, String details, JSONObject time, JSONObject player,boolean isHome,String season) {
        this.team_id = team_id;
        this.type = type;
        this.details = details;
        this.time = time;
        this.player = player;
        this.isHome = isHome;
        this.season = season;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public boolean isHome() {
        return isHome;
    }

    public void setHome(boolean home) {
        isHome = home;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public JSONObject getTime() {
        return time;
    }

    public void setTime(JSONObject time) {
        this.time = time;
    }

    public JSONObject getPlayer() {
        return player;
    }

    public void setPlayer(JSONObject player) {
        this.player = player;
    }
}
