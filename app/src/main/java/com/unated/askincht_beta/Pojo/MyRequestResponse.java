package com.unated.askincht_beta.Pojo;

import java.util.List;


public class MyRequestResponse {

    private int status;
    private Data data;

    public int getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public class Data {

        private List<MyRequestItem> requests;

        public List<MyRequestItem> getRequests() {
            return requests;
        }
    }
}
