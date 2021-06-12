package com.example.mockinterview.model;

public class Meeting {
   private String start_time;
   private String end_time;
   private String subject;
   private String meet_id;


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Meeting(String start, String end, String Subject, String id) {
        this.start_time = start;
        this.end_time = end;
        this.subject = Subject;
        this.meet_id=id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }


    public String getMeet_id() {
        return meet_id;
    }

    public void setMeet_id(String meet_id) {
        this.meet_id = meet_id;
    }
}
