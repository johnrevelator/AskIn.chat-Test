package com.unated.askincht_beta.Pojo;

import java.util.List;


public class RecallsResponse {

    private int status;
    private Data data;

    public int getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public class Data {
        private String shop_id;
        private String shop_rating;
        public String getShop_id(){
            return shop_id;
        }
        public String getShop_rating(){
            return shop_rating;
        }


        private List<Recall> reviews;

        public List<Recall> getRecalls() {
            return reviews;
        }
    }
}
