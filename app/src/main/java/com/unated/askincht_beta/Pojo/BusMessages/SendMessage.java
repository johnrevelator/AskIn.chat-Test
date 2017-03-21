package com.unated.askincht_beta.Pojo.BusMessages;


public class SendMessage {

    private String text;
    private int request_id;
    private int shop_id;
    private String uniq;

    public SendMessage(){

    }

    public String getText() {
        return text;
    }

    public int getRequest_id() {
        return request_id;
    }

    public int getShop_id() {
        return shop_id;
    }

    public String getUniq() {
        return uniq;
    }

    public void setUniq(String uniq) {
        this.uniq = uniq;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public void setRequest_id(int request_id) {
        this.request_id = request_id;
    }

    public void setText(String text) {
        this.text = text;
    }
}
