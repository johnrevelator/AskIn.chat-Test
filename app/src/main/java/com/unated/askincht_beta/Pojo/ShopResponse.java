package com.unated.askincht_beta.Pojo;


public class ShopResponse {

    private int status;

    private Data data;


    public Data getData() {
        return data;
    }
    public int getStatus() {
        return status;
    }

    public class Data {

        private Shop shop;

        public Shop getShop() {
            return shop;
        }
    }
}
