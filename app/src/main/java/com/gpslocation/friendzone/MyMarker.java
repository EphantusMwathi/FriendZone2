package com.gpslocation.friendzone;

/**
 * Created by mwathi on 3/18/2016.
 */
public class MyMarker{


    private String firstname;
    private String secondname;
    private String  mLabel;
    private String distance;
    private String mIcon;
    private double latitude;
    private double longitude;

    public MyMarker(String label,String firstname,String secondname,String distance,double latitude,double longitude){
        this.mLabel=label;
        this.firstname=firstname;
        this.secondname=secondname;
        this.distance=distance;
        this.latitude=latitude;
        this.longitude=longitude;

    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getmLabel()
    {
        return mLabel;
    }
    public String getmIcon()
    {
        return mLabel;
    }
    public String getFirstname(){

        return firstname;
    }
    public String getSecondname(){

        return secondname;
    }
    public String getDistance(){

        return distance;
    }
public void setmLabel(String label){


    this.mLabel=label;
}
    public void setFirstname(String firstname){


        this.firstname=firstname;
    }
    public void setSecondname(String secondname){


        this.secondname=secondname;
    }
    public void setDistance(String distance){


        this.distance=distance;
    }
    public void setmIcon(String icon){


        this.mIcon=icon;
    }
}
