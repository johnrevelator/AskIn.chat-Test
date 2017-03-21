package com.unated.askincht_beta.Pojo;


public class RequestResponse {
    private int status;

    private Data data;


    public Data getData() {
        return data;
    }
    public int getStatus() {
        return status;
    }

    public class Data {

        private RequestItem request;

        public  RequestItem getRequests() {
            return request;
        }
    }
}
