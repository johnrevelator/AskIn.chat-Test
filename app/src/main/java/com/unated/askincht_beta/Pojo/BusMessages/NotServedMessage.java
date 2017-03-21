package com.unated.askincht_beta.Pojo.BusMessages;


public class NotServedMessage {

    public NotServedMessage(String msg,String city) {
        this.message=msg;
        this.city=city;

    }
    private String message;
    private String city;
    public String getText() {
        return message;
    }
    public String getCity() {
        return city;
    }


}
