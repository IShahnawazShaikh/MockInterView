package com.example.mockinterview.model;

public class Participant {
    private String lname;
    private String fname;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Participant(String fname, String lname,String id) {
        this.fname = fname;
        this.lname = lname;
        this.id=id;
    }


    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }




}
