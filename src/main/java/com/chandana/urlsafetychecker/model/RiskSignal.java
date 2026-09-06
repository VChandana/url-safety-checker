package com.chandana.urlsafetychecker.model;

public class RiskSignal {
    private final String name;
    private final int score;

    public RiskSignal(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
