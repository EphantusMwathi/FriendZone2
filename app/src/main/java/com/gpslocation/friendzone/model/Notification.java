package com.gpslocation.friendzone.model;

import java.io.Serializable;

/**
 * Created by mwathi on 4/20/2016.
 */
public class Notification implements Serializable {


    String title,notification,created_at,friend_id;
    int notification_id;
    User user;

    public Notification(){}
    public Notification(String title,int notification_id, String notification, String created_at,String friend_id) {
        this.title=title;
        this.notification_id=notification_id;
        this.notification=notification;
        this.created_at=created_at;
        this.friend_id=friend_id;
        //this.user=user;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public String getTitle(){
        return title;
    }
    public void setNotification_id(int notification_id){


        this.notification_id=notification_id;
    }
    public int getNotification_id(){
        return notification_id;
    }
    public void setNotification(String notification){

        this.notification=notification;
    }
    public String getNotification(){
        return notification;
    }
    public void setCreated_at(String created_at){
        this.created_at=created_at;
    }

    public String getCreated_at(){
        return created_at;
    }

    public void setUser(User user){
        this.user=user;
    }
    public User getUser(){
        return user;
    }

    public void setFriend_id(String friend_id){
        this.friend_id=friend_id;
    }


    public String getFriend_id() {
        return friend_id;
    }
}
