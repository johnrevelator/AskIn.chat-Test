package com.unated.askincht_beta.Pojo.BusMessages;


public class NewInviteMessage {
    private String chat_id;
    private String chat_name;
    private String user_name;
    private String request_id;
    private String shop_id;
    private String guest_user_id;

    public NewInviteMessage(String chat_id, String chat_name,String user_name, String request_id,String shop_id,String guest_user_id) {
        this.request_id = request_id;
        this.shop_id = shop_id;
        this.chat_id = chat_id;
        this.guest_user_id = guest_user_id;
        this.user_name = user_name;
        this.chat_name = chat_name;
    }

    public String getChat_id() {
        return chat_id;
    }


    public String getChat_name() {
        return chat_name;
    }
    public String getUser_name() {
        return user_name;
    }
    public String getRequest_id() {
        return request_id;
    }
    public String getShop_id() {
        return shop_id;
    }
    public String getGuest_user_id() {
        return guest_user_id;
    }

}
