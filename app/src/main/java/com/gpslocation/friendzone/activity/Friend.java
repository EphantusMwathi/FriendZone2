package com.gpslocation.friendzone.activity;

/**
 * Created by mwathi on 3/7/2016.
 */
public class Friend {

    public int id;
    public String name;
    public String email;

    public String distance;


    public Friend(){



    }

    public Friend(int id,String name,String distance,String email){

       this.distance=distance;
        this.name=name;
        this.email=email;
        this.id=id;



    }
}
