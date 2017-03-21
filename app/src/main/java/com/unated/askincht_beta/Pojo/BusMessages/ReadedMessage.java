package com.unated.askincht_beta.Pojo.BusMessages;

/**
 * Created by dmitryabramichev on 09.10.16.
 */
public class ReadedMessage {
    private String reqId;
    private String shopId;

    public ReadedMessage(String reqId, String shopId) {
        this.reqId = reqId;
        this.shopId = shopId;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}
