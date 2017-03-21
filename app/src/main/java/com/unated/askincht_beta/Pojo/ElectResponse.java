package com.unated.askincht_beta.Pojo;

import java.util.List;


public class ElectResponse {
    private Data data;


    public Data getData() {
        return data;
    }

    public class Data {
        private List<Shop> shops;

        public List<Shop> getRequests() {
            return  shops;
        }
    }
}
