package com.unated.askincht_beta.Pojo;

import java.io.Serializable;


public class Recall implements Serializable {
    private String user_name;
    private String rating;
    private String text;
    private String date;


    public String getUser_name() {
        return user_name;
    }

    public String getText() {
        return text;
    }

    public String getRating() {
        return rating;
    }

    public String getDate() {
        return date;
    }


}
