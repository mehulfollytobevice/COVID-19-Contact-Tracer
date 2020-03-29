package com.tasks.tracker.util;

import android.app.Activity;
import android.app.Application;
import android.widget.TableRow;

public class TrackerApi extends Application {
     private String username;
     private String userid;
     private static TrackerApi instance;

    public TrackerApi() {
    }

    public static  TrackerApi getInstance(){
        if (instance==null){
            instance=new TrackerApi();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
