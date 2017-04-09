package com.unated.askincht_beta.Pojo;


public class Unread {
    private int status;

    private Data data;

    public int getStatus() {
        return status;
    }
    public Data getData() {
        return data;
    }
    public class Data {
        private int messages_from_shops_new;
        private int messages_from_clients_new;
        private int messages_from_support_new;
        private int requests_from_clients;
        public int getMessageClients() {
            return messages_from_clients_new;
        }
        public int getMessageSupport() {
            return messages_from_support_new;
        }
        public int getMessageShops() {
            return messages_from_shops_new;
        }
        public int getRequest() {
            return requests_from_clients;
        }
    }

}
