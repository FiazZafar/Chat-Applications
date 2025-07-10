package com.fyp.mychat.model;

import com.fyp.mychat.helpers.StatusEnum;

public class RequestModel {
    String receiverUid;
    String senderId;
    Long timeStamp;
    StatusEnum status;

    public RequestModel() {
    }

    public RequestModel(String receiverUid, String senderId, Long timeStamp, StatusEnum status) {
        this.receiverUid = receiverUid;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
        this.status = status;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }
}