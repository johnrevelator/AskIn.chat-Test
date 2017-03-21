package com.unated.askincht_beta.Pojo.BusMessages;


public class SearchMessage {
    private String text;
    private String uniq;
    private int radius;
    private double lat;
    private double lon;

    public SearchMessage(){

    }

    public int getRadius() {
        return radius;
    }

    public String getText() {
        return text;
    }

    public String getUniq() {
        return uniq;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setUniq(String uniq) {
        this.uniq = uniq;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
