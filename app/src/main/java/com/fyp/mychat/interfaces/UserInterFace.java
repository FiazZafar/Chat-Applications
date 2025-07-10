package com.fyp.mychat.interfaces;

import com.fyp.mychat.model.UserModel;

import java.util.List;

public interface UserInterFace {
    void addUser(UserModel userModel, String userId,FirebaseCallbacks<Boolean> result);
    void setProfilePic(String userId,String imageUrl, FirebaseCallbacks<Boolean> result);
    void setOnlineStatus(String userId,Boolean isOnline, FirebaseCallbacks<Boolean> result);
    void fetchUsersList(FirebaseCallbacks<List<UserModel>> result);
    void fetchUserName(String userId,FirebaseCallbacks<UserModel> result);
    void fetchUserProfile(String userId,FirebaseCallbacks<UserModel> result);
}
