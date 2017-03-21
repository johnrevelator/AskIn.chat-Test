package com.unated.askincht_beta.Pojo;

/**
 * Created by dmitryabramichev on 30.09.16.
 */
public class ProfileResponse {
    private int status;
    private Data data;
    private String error_msg;

    public int getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public String getError_msg() {
        return error_msg;
    }

    public class Data {
        private int userid;
        private String username;
        private String useremail;

        public int getUserid() {
            return userid;
        }

        public String getUsername() {
            return username;
        }

        public String getUseremail() {
            return useremail;
        }
    }
}