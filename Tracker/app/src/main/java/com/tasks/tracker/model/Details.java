package com.tasks.tracker.model;

import com.google.firebase.Timestamp;

public class Details {
    private String phone_number;
    private String name_user;
    private Timestamp date_Added;
    private String username;

    public Details() {
    }

    public Details(String phone_number, String name_user, Timestamp date_Added, String username) {
        this.phone_number = phone_number;
        this.name_user = name_user;
        this.date_Added = date_Added;
        this.username = username;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getName_user() {
        return name_user;
    }

    public void setName_user(String name_user) {
        this.name_user = name_user;
    }

    public Timestamp getDate_Added() {
        return date_Added;
    }

    public void setDate_Added(Timestamp date_Added) {
        this.date_Added = date_Added;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
