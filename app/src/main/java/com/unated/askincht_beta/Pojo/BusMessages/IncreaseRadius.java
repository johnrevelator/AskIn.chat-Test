package com.unated.askincht_beta.Pojo.BusMessages;

/**
 * Created by dmitryabramichev on 09.10.16.
 */
public class IncreaseRadius {
    private int request_id;
    private int radius;

    public IncreaseRadius(int request_id, int radius) {
        this.request_id = request_id;
        this.radius = radius;
    }

    public int getReqId() {
        return request_id;
    }

    public void setReqId(String reqId) {
        this.request_id = request_id;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(String shopId) {
        this.radius = radius;
    }
}
