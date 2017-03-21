package com.unated.askincht_beta.Pojo.BusMessages;

public class PayToCall {
    private String requestId;
    private String sid;

    public PayToCall(String sid, String requestId){
        this.requestId = requestId;
        this.sid = sid;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
