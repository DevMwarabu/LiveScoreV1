package com.laxco.livescorev1.Models;

import org.json.JSONArray;
import org.json.JSONObject;

public class League {
    private JSONObject league,country;
    private JSONArray seasons;

    public League(JSONObject league, JSONObject country,JSONArray seasons) {
        this.league = league;
        this.country = country;
        this.seasons = seasons;
    }

    public JSONArray getSeasons() {
        return seasons;
    }

    public void setSeasons(JSONArray seasons) {
        this.seasons = seasons;
    }

    public JSONObject getLeague() {
        return league;
    }

    public void setLeague(JSONObject league) {
        this.league = league;
    }

    public JSONObject getCountry() {
        return country;
    }

    public void setCountry(JSONObject country) {
        this.country = country;
    }
}
