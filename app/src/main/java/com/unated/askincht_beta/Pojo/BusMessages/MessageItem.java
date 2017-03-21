package com.unated.askincht_beta.Pojo.BusMessages;

import com.unated.askincht_beta.Pojo.ChatInterface;
import com.unated.askincht_beta.Pojo.FileChat;

import java.util.List;


public class MessageItem implements ChatInterface{
    private int id;
    private int request_id;
    private int shop_id;
    private String text;
    private String file_name;
    private long time;
    private long last_online;
    private String incoming;
    private String readed;
    private boolean online;
    private boolean is_file;
    private int delay;
    private String user_id;
    private int shopuserid;
    private int clientuserid;
    private int sign;
    private String uniq;
    private FileChat file;
    private List<String> users;
    public int getId() {
        return id;
    }


    public int getRequest_id() {
        return request_id;
    }

    public int getShop_id() {
        return shop_id;
    }

    public String getText() {
        return text;
    }

    public long getTime() {
        return time;
    }
    public long getLast() {
        return last_online;
    }

    public String getUserId() {
        return user_id;
    }

    public String getReaded() {
        return readed;
    }
    public String getFile_name() {
        return file_name;
    }

    public int getDelay() {
        return delay;
    }
    public boolean isIs_file() {
        return is_file;
    }

    public int getShopuserid() {
        return shopuserid;
    }

    public int getClientuserid() {
        return clientuserid;
    }

    public int getSign() {
        return sign;
    }

    public String getUniq() {
        return uniq;
    }
    public FileChat getFileChat() {
        return file;
    }

    public List<String> getUsers() {
        return users;
    }

    @Override
    public String getMessageType() {
        return "message";
    }
}
