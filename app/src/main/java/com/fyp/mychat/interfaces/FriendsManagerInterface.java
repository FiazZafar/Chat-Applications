package com.fyp.mychat.interfaces;

import com.fyp.mychat.helpers.StatusEnum;
import com.fyp.mychat.model.RequestModel;

import java.util.List;

public interface FriendsManagerInterface {
    void addRequest(RequestModel requestModel, FirebaseCallbacks<Boolean> result);
    void fetchRequestList(String currenUserId,FirebaseCallbacks<List<RequestModel>> result);
    void updateRequestStatus(String senderId, String receiverId, StatusEnum status, FirebaseCallbacks<Boolean> result);
    void fetchUserIdsList(String currentUID,FirebaseCallbacks<List<String>> result);
    void fetchRequestByUserName(String userName,FirebaseCallbacks<RequestModel> result);
    void deleteRequest(String userId,String requestId,FirebaseCallbacks<Boolean> result);

}
