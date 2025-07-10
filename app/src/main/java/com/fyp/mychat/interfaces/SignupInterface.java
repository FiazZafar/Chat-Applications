package com.fyp.mychat.interfaces;

import com.fyp.mychat.model.UserModel;

import java.util.List;

public interface SignupInterface {
    void signupUser(String email, String passwords,FirebaseCallbacks<Boolean> result);
    void loginUser(String email, String passwords,FirebaseCallbacks<Boolean> result);
    void saveDeviceToken(String userId, String deviceToken,FirebaseCallbacks<Boolean> result);
    void fetchDeviceToken(String userId,FirebaseCallbacks<String> deviceToken);
}
