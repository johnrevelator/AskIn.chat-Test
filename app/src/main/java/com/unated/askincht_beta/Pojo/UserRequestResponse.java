package com.unated.askincht_beta.Pojo;

import java.util.List;


public class UserRequestResponse {
    private int status;
    private Data data;

    public int getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public class Data {
        private List<RequestItem> requests;

        public List<RequestItem> getRequests() {
            return requests;
        }
    }
}
