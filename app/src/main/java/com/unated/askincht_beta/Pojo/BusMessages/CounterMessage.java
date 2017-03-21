package com.unated.askincht_beta.Pojo.BusMessages;


public class CounterMessage {

    private boolean isShow;

    public CounterMessage(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
