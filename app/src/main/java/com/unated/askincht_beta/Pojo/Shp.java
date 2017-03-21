package com.unated.askincht_beta.Pojo;

import java.io.Serializable;


public class Shp implements Serializable {
    private int id;
    private int user_id;
    private String name;
    private String email;
    private String address;
    private String phone;
    private double lat;
    private double lon;

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }


    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }


}
