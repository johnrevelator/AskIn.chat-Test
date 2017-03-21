package com.unated.askincht_beta.Pojo;

import java.io.Serializable;
import java.util.List;

public class MyRequestItem implements Serializable{

    private int id;
    private int radius;
    private int user_id;
    private String text;
    private double lat;
    private double lon;
    private long time;
    private long edit_radius_time;
    private long avg_time;
    private String message;
    private List<Shop> shops;
    private List<String> users;

    public int getId() {
        return id;
    }



    public int getUser_id() {
        return user_id;
    }
    public int getRadius() {
        return radius;
    }

    public String getText() {
        return text;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public long getTime() {
        return time;
    }
    public long getAvgTime() {
        return avg_time;
    }
    public long getRadTime() {
        return edit_radius_time;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public List<Shop> getShops() {
        return shops;
    }

    public List<String> getUsers() {
        return users;
    }

    public String getMessage() {
        return message;
    }
    public double getAVG() {
        return  avg_time;
    }
}
