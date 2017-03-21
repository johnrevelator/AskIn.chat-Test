package com.unated.askincht_beta.Pojo.BusMessages;

/**
 * Created by dmitryabramichev on 20.09.16.
 */
public class RequestCounter {
    private int requestCount;
    private boolean isMy;

    public RequestCounter(int count, boolean isMy) {
        this.requestCount = count;
        this.isMy = isMy;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public boolean isMy() {
        return isMy;
    }
}
