package com.unated.askincht_beta.Pojo;


public class RequestItem {
    private int id;
    private int user_id;
    private int shop_id;
    private String text;
    private String last_message;
    private String message;
    private String city;
    private boolean closed;
    private int closed_shop_id;
    private double lat;
    private double lon;
    private long last_message_time;
    private boolean readed;
    private int cnt_messages;
    private int new_messages;

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getCity() {
        return city;
    }

    public int getShop_id() {
        return shop_id;
    }

    public String getText() {
        return text;
    }

    public String getLastMsg() {
        return last_message;
    }
    public String getMessage() {
        return message;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public long getTime() {
        return last_message_time;
    }

    public boolean getReaded() {
        return readed;
    }
    public boolean isClosed() {
        return closed;
    }
    public int getClosedShopId(){
        return closed_shop_id;
    }
    public int getCnt_messages() {
        return cnt_messages;
    }

    public int getNew_messages() {
        return new_messages;
    }
}
