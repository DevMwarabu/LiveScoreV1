package com.laxco.livescorev1.Models;

public class Lineup {
    String formation,played;

    public Lineup(String formation, String played) {
        this.formation = formation;
        this.played = played;
    }

    public String getFormation() {
        return formation;
    }

    public void setFormation(String formation) {
        this.formation = formation;
    }

    public String getPlayed() {
        return played;
    }

    public void setPlayed(String played) {
        this.played = played;
    }
}
