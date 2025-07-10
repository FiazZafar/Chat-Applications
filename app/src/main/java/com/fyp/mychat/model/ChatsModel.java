package com.fyp.mychat.model;

import com.fyp.mychat.helpers.MessageType;

public class ChatsModel {
    String ChatId_uId1_uId2;
    String message;
    Long timeStamp;
    String senderId;
    Boolean read;
    MessageType type;

    public ChatsModel(String chatId_uId1_uId2, String senderId, String message, Long timeStamp, MessageType type, Boolean read) {
        ChatId_uId1_uId2 = chatId_uId1_uId2;
        this.senderId = senderId;
        this.message = message;
        this.timeStamp = timeStamp;
        this.type = type;
        this.read = read;
    }

    public ChatsModel() {
    }

    public String getChatId_uId1_uId2() {
        return ChatId_uId1_uId2;
    }

    public void setChatId_uId1_uId2(String chatId_uId1_uId2) {
        ChatId_uId1_uId2 = chatId_uId1_uId2;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
