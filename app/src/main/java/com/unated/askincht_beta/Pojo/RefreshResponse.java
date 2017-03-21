package com.unated.askincht_beta.Pojo;


public class RefreshResponse {
    private int status;
private Data data;
    public int getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }


    public class Data {

        private String token;

        public String getToken() {
            return token;
        }


    }


}
