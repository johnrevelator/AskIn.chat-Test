package com.unated.askincht_beta.Pojo;


public class UserRequestItem {
    private int id;
    private int user_id;
    private int shop_id;
    private String city;
    private String text;
    private String last_message;
    private double lat;
    private double lon;
    private long last_message_time;
    private boolean readed;
    private int messages;

    public UserRequestItem(RequestItem item){
        this.id = item.getId();
        this.last_message=item. getLastMsg();
        this.user_id = item.getUser_id();
        this.shop_id = item.getShop_id();
        this.text = item.getText();
        this.lat = item.getLat();
        this.lon = item.getLon();
        this.last_message_time = item.getTime();
        this.readed = item.getReaded();
    }

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

    public int getMessages() {
        return messages;
    }

    public void setMessages(int messages) {
        this.messages = messages;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
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

    public void setTime(long time) {
        this.last_message_time = last_message_time;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }
}
