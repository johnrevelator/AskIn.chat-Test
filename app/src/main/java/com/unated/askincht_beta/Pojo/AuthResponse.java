package com.unated.askincht_beta.Pojo;


public class AuthResponse {
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
        private String sid;
        private int user_id;
        private int id;
        private String token;

        public String getSid() {
            return sid;
        }
        public String getToken() {
            return token;
        }

        public int getUser_id() {
            return user_id;
        }

    }
}
