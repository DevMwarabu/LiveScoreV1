package com.laxco.livescorev1.Models;

import org.json.JSONObject;

public class FixtureHome {
    private JSONObject teams,goals,league,fixture;
    private String elapsed,home,away,leaguename;

    public FixtureHome(JSONObject teams, JSONObject goals, JSONObject league, JSONObject fixture, String elapsed, String home, String away, String leaguename) {
        this.teams = teams;
        this.goals = goals;
        this.league = league;
        this.fixture = fixture;
        this.elapsed = elapsed;
        this.home = home;
        this.away = away;
        this.leaguename = leaguename;
    }

    public String getElapsed() {
        return elapsed;
    }

    public void setElapsed(String elapsed) {
        this.elapsed = elapsed;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getAway() {
        return away;
    }

    public void setAway(String away) {
        this.away = away;
    }

    public String getLeaguename() {
        return leaguename;
    }

    public void setLeaguename(String leaguename) {
        this.leaguename = leaguename;
    }

    public JSONObject getTeams() {
        return teams;
    }

    public void setTeams(JSONObject teams) {
        this.teams = teams;
    }

    public JSONObject getGoals() {
        return goals;
    }

    public void setGoals(JSONObject goals) {
        this.goals = goals;
    }

    public JSONObject getLeague() {
        return league;
    }

    public void setLeague(JSONObject league) {
        this.league = league;
    }

    public JSONObject getFixture() {
        return fixture;
    }

    public void setFixture(JSONObject fixture) {
        this.fixture = fixture;
    }
}
