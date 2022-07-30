package com.laxco.livescorev1.Models;

import org.json.JSONArray;
import org.json.JSONObject;

public class PlayeState {
    private JSONObject player;
    private JSONArray statistics;

    public PlayeState(JSONObject player, JSONArray statistics) {
        this.player = player;
        this.statistics = statistics;
    }

    public JSONObject getPlayer() {
        return player;
    }

    public void setPlayer(JSONObject player) {
        this.player = player;
    }

    public JSONArray getStatistics() {
        return statistics;
    }

    public void setStatistics(JSONArray statistics) {
        this.statistics = statistics;
    }
}
