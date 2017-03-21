package com.unated.askincht_beta.Pojo.BusMessages;

/**
 * Created by dmitryabramichev on 06.10.16.
 */
public class PostListenWritingMessage {
    private String requestId;
    private String shopId;

    public PostListenWritingMessage(String requestId, String shopId) {
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
