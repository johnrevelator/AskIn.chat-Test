package com.unated.askincht_beta.Pojo;


public class RequestSupResponse {
    private int status;

    private Data data;


    public Data getData() {
        return data;
    }
    public int getStatus() {
        return status;
    }

    public class Data {


            private int request_id;
            private int shop_id;



            public int getRequest_id() {
                return request_id;
            }


            public int getShop_id() {
                return shop_id;
            }


    }
}
