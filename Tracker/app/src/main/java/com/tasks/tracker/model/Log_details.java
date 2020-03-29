package com.tasks.tracker.model;

import android.location.Location;
import android.location.LocationManager;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Log_details {
    private String name_user_crossed;
    private String phone_number;
    private String username;
    private GeoPoint location;
    private com.google.firebase.Timestamp date_crossed;


    public Log_details() {
    }

    public Log_details(String name, String phone_number, String username, com.google.firebase.Timestamp date_crossed, GeoPoint location) {
        this.name_user_crossed = name;
        this.phone_number = phone_number;
        this.username = username;
        this.date_crossed = date_crossed;
        this.location = location;
    }

    public String getName() {
        return name_user_crossed;
    }

    public void setName(String name) {
        this.name_user_crossed = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public Timestamp getDate_crossed() {
        return date_crossed;
    }

    public void setDate_crossed(Timestamp date_crossed) {
        this.date_crossed = date_crossed;
    }
}
