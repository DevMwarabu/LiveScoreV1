package com.laxco.livescorev1.Models;

import org.json.JSONObject;

public class Standing {
    private JSONObject team,all,goals,home,away;
    private  String rank,points,gd;

    public Standing(JSONObject team, JSONObject all, JSONObject goals, String rank, String points, String gd,JSONObject home,JSONObject away) {
        this.team = team;
        this.all = all;
        this.goals = goals;
        this.rank = rank;
        this.points = points;
        this.gd = gd;
        this.home = home;
        this.away = away;
    }

    public JSONObject getHome() {
        return home;
    }

    public void setHome(JSONObject home) {
        this.home = home;
    }

    public JSONObject getAway() {
        return away;
    }

    public void setAway(JSONObject away) {
        this.away = away;
    }

    public JSONObject getTeam() {
        return team;
    }

    public void setTeam(JSONObject team) {
        this.team = team;
    }

    public JSONObject getAll() {
        return all;
    }

    public void setAll(JSONObject all) {
        this.all = all;
    }

    public JSONObject getGoals() {
        return goals;
    }

    public void setGoals(JSONObject goals) {
        this.goals = goals;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getGd() {
        return gd;
    }

    public void setGd(String gd) {
        this.gd = gd;
    }
}
