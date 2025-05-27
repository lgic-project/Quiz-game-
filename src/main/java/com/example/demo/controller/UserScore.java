package com.example.demo.controller;

public class UserScore {
    private String username;
    private int score;
    private String timestamp;

    public UserScore(String username, int score, String timestamp) {
        this.username = username;
        this.score = score;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
