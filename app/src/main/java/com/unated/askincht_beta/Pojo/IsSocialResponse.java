package com.unated.askincht_beta.Pojo;


public class IsSocialResponse {
    private int status;
    private Data data;

    public int getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public class Data {
        private boolean isBind;
        public boolean isBind(){return isBind;}

    }
}
