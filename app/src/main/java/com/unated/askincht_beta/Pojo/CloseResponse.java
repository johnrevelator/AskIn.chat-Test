package com.unated.askincht_beta.Pojo;


import java.util.List;

public class CloseResponse {
    private int status;

    private String error_msg;
    private Data data;
    public int getStatus() {
        return status;
    }


    public Data getData() {
        return data;
    }
    public class Data {
        private String shop_id;
        private String rating;
        public String getShop_id(){
            return shop_id;
        }
        public String getShop_rating(){
            return rating;
        }


    }



}
