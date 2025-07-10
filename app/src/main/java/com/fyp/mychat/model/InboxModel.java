package com.fyp.mychat.model;

import com.fyp.mychat.helpers.MessageType;

public class InboxModel {
    String lastMessage;
    String senderIdLM;
    MessageType messageType;
    Long TimeStamp;
    int unreadCount;
    String chatId;

    Boolean onlineStatus;
    String firstUserId;
    String secondUserId;

    public InboxModel(String lastMessage, String senderIdLM,
                      MessageType messageType, Long timeStamp, int unreadCount,
                      String chatId, String firstUserId, String secondUserId,Boolean onlineStatus) {
        this.lastMessage = lastMessage;
        this.senderIdLM = senderIdLM;
        this.messageType = messageType;
        TimeStamp = timeStamp;
        this.unreadCount = unreadCount;
        this.chatId = chatId;
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
        this.onlineStatus = onlineStatus;
    }

    public InboxModel() {
    }

    public Boolean getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getSenderIdLM() {
        return senderIdLM;
    }

    public void setSenderIdLM(String senderIdLM) {
        this.senderIdLM = senderIdLM;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getFirstUserId() {
        return firstUserId;
    }

    public void setFirstUserId(String firstUserId) {
        this.firstUserId = firstUserId;
    }


    public String getSecondUserId() {
        return secondUserId;
    }

    public void setSecondUserId(String secondUserId) {
        this.secondUserId = secondUserId;
    }
}
