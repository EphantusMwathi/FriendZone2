package com.gpslocation.friendzone.model;

import java.io.Serializable;

/**
 * Created by mwathi on 5/14/2016.
 */
public class Close implements Serializable {



    String name,email,created_at,friend_id;
    double distance;


    public Close(){}

    public Close(String name,String created_at,String email,double distance,String friend_id){

        this.name=name;
        this.email=email;
        this.created_at=created_at;
        this.distance=distance;
        this.friend_id=friend_id;


    }




    public void setFriend_id(String friend_id){

        this.friend_id=friend_id;
    }
    public String getFriend_id(){

        return friend_id;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getName(){

        return name;
    }
    public void setEmail(String email){

        this.email=email;
    }
    public String getEmail(){

        return email;
    }
    public void setCreated_at(String created_at){

        this.created_at=created_at;
    }

    public String getCreated_at(){

        return created_at;
    }




public void setDistance(double distance){
    this.distance=distance;
}
    public double getDistance(){


        return distance;
    }



}
