package com.unated.askincht_beta.Pojo.BusMessages;


import java.util.ArrayList;

public class ExactReq {
    private int status;

    private Data data;

    public int getStatus() {
        return status;
    }
    public Data getData() {
        return data;
    }
    public class Data {
        private ArrayList<String> requests;

        public ArrayList<String> getText() {
            return requests;
        }

    }

}
