package com.gpslocation.friendzone.model;

import java.io.Serializable;

/**
 * Created by mwathi on 4/26/2016.
 */
public class Invite implements Serializable {

    String name,email,created_at;
    int invite_id;



    public Invite(){}

    public Invite(String name,String created_at,String email,int invite_id){

        this.name=name;
        this.email=email;
        this.created_at=created_at;

    }




public void setInvite_id(int invite_id){

    this.invite_id=invite_id;
}
    public int getInvite_id(){

        return invite_id;
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


}
