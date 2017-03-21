package com.unated.askincht_beta.Pojo.BusMessages;


public class InviteMessage {
    private String chatId;
    private String friendPhone;

    public InviteMessage(String chatId, String shopId){
        this.chatId = chatId;
        this.friendPhone =friendPhone;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getFriendPhone() {
        return friendPhone;
    }

    public void setFriendPhone(String friendPhone) {
        this.friendPhone= friendPhone;
    }
}
