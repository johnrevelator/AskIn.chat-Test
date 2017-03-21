package com.unated.askincht_beta.Pojo;

import java.util.List;


public class ShopsResponse {

    private int status;
    public  int getStatus(){
        return status;
    }

    private List<Shop> data;

    public List<Shop> getData() {
        return data;
    }
}
