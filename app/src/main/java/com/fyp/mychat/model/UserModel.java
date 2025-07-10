package com.fyp.mychat.model;

public class UserModel {
    String uId;
    String userName;
    String status;
    Long lastSeen;
    Boolean online;
    String imgUrl;
    String userEmail;
    public UserModel( String uId,
                      String uName,
                      String userEmail,
                      String imgUrl,
                      Long lastSeen,
                      String status,
                      Boolean online) {
        this.userName = uName;
        this.uId = uId;
        this.userEmail = userEmail;
        this.imgUrl = imgUrl;
        this.lastSeen = lastSeen;
        this.status = status;
        this.online = online;
    }

    public UserModel(String uName, String imgUrl, Boolean onlineStatus) {
        this.userName = uName;
        this.imgUrl = imgUrl;
        this.online = onlineStatus;
    }
    public UserModel() {
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }
}
