package com.unated.askincht_beta.Pojo;

import com.unated.askincht_beta.Pojo.BusMessages.MessageItem;

import java.util.List;


public class MessagesResponse {
    private int status;
    private Data data;

    public int getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public class Data {
        private List<MessageItem> messages;
        private long last_online;
        private boolean online;
        public boolean getOnline() {
            return online;
        }

        public List<MessageItem> getMessages() {
            return messages;
        }
        public long getLast() {
            return last_online;

        }
    }
}
