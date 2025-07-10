package com.fyp.mychat.model;

public class FriendListModel {
    String firstUserId;
    String secondUserId;
    Long timeStamp;

    public FriendListModel() {
    }

    public FriendListModel(String firstUserId, String secondUserId, Long timeStamp) {
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
        this.timeStamp = timeStamp;
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

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
