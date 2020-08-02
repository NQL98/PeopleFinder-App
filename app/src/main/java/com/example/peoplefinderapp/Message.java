package com.example.peoplefinderapp;

import java.util.Date;

public class Message {
    public String userName;
    public String textMessage;
    private long messagaTime;

    public Message(){}
    public Message(String userName, String textMessage){
        this.userName = userName;
        this.textMessage = textMessage;
        this.messagaTime = new Date().getTime();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public long getMessagaTime() {
        return messagaTime;
    }

    public void setMessagaTime(long messagaTime) {
        this.messagaTime = messagaTime;
    }
}
