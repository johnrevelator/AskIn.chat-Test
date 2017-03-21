package com.unated.askincht_beta.Pojo;

import java.io.Serializable;
import java.util.List;


public class Shop implements Serializable {
    private int id;
    private int user_id;
    private String name;
    private String email;
    private String address;
    private String avatar;
    private String phone;
    private String rating;
    private List<String> friends;
    private String time;
    private String last_message;
    private long last_message_time;
    private double lat;
    private int cnt_messages;
    private int new_messages;
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
    public long getLastRad() {
        return last_message_time;
    }
    public String getMsg() {
        return last_message;
    }

    public String getEmail() {
        return email;
    }
    public List<String> getFriends() {
        return friends;
    }

    public String getAddress() {
        return address;
    }
    public String getTime() {
        return time;
    }
    public String getRating() {
        return rating;
    }

    public String getPhone() {
        return phone;
    }
    public String getAvatar() {
        return avatar;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getCnt_messages() {
        return cnt_messages;
    }

    public int getNew_messages() {
        return new_messages;
    }
}
