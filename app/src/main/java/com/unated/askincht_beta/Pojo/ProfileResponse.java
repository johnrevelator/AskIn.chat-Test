package com.unated.askincht_beta.Pojo;


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
        private String avatar;
        private String phone;
        private String balance;

        public int getUserid() {
            return userid;
        }

        public String getUsername() {
            return username;
        }

        public String getUserAvatar() {
            return avatar;
        }

        public String getBalance() {
            return balance;
        }

        public String getPhone() {
            return phone;
        }
    }
}
