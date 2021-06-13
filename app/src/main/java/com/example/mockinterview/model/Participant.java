package com.example.mockinterview.model;

public class Participant {
    private String lname;
    private String fname;
    private String email;
    private String id;

    public Participant(String fname, String lname, String id, String email) {
        this.lname = lname;
        this.fname = fname;
        this.email = email;
        this.id = id;
    }
    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
