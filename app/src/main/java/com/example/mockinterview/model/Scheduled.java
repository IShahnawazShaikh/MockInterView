package com.example.mockinterview.model;

public class Scheduled {
    private String user_id;
    private String meet_id;

    public Scheduled(String user_id, String meet_id) {
        this.user_id = user_id;
        this.meet_id = meet_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMeet_id() {
        return meet_id;
    }

    public void setMeet_id(String meet_id) {
        this.meet_id = meet_id;
    }
}
