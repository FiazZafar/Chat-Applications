package com.fyp.mychat.model;

public class LastMessage {
    String lastMessage;
    Long timeStamp;
    Boolean read;

    public LastMessage() {
    }

    public LastMessage(String lastMessage, Long timeStamp, Boolean read) {
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
        this.read = read;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
