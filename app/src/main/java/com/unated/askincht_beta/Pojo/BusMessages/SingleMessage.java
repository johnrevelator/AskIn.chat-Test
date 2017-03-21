package com.unated.askincht_beta.Pojo.BusMessages;


public class SingleMessage {

    private String request_id;
    private String msg;
    public SingleMessage(String request_id,String msg) {
        this.msg=msg;
        this.request_id=request_id;

    }
    public String getRequest_id(){
        return request_id;
    }
    public String getMsg(){
        return msg;
    }

}
