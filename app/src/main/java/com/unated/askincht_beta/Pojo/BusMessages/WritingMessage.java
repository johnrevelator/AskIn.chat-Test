package com.unated.askincht_beta.Pojo.BusMessages;

public class WritingMessage {
    private String requestId;
    private String shopId;

    public WritingMessage(String requestId, String shopId){
        this.requestId = requestId;
        this.shopId = shopId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}
