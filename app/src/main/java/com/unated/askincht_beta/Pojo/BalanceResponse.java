package com.unated.askincht_beta.Pojo;


public class BalanceResponse {

    private int status;
    private Data data;


    public Data getData() {
        return data;
    }
    public int getStatus(){
        return status;
    }

    public class Data {

        private String balance;

        public String getBalance() {
            return balance;
        }
    }
}
