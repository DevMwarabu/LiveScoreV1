package com.laxco.livescorev1.Models;

import org.json.JSONObject;

public class MatchNext {
    private JSONObject teams,goals,league,fixture;

    public MatchNext(JSONObject teams, JSONObject goals, JSONObject league, JSONObject fixture) {
        this.teams = teams;
        this.goals = goals;
        this.league = league;
        this.fixture = fixture;
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
