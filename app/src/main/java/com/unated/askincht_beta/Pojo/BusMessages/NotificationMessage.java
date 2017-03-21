package com.unated.askincht_beta.Pojo.BusMessages;


public class NotificationMessage {
    private String mMessage;
    private String requestId;
    private String shopId;
    private boolean isRequest;

    public NotificationMessage(String msg, boolean type) {
        this.mMessage = msg;
        this.isRequest = type;
    }

    public String getMessage() {
        return mMessage;
    }

    public boolean isRequest() {
        return isRequest;
    }
}
