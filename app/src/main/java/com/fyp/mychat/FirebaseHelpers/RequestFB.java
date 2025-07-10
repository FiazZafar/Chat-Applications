package com.fyp.mychat.FirebaseHelpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fyp.mychat.helpers.StatusEnum;
import com.fyp.mychat.interfaces.FirebaseCallbacks;
import com.fyp.mychat.interfaces.FriendsManagerInterface;
import com.fyp.mychat.model.RequestModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RequestFB implements FriendsManagerInterface {
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("QuickChat")
            .child("Requests");
    @Override
    public void addRequest(RequestModel requestModel, FirebaseCallbacks<Boolean> result) {
        String friendKey = generateFriendKey(requestModel.getSenderId(), requestModel.getReceiverUid());
        myRef.child(friendKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){

                    myRef.child(friendKey).setValue(requestModel)
                            .addOnSuccessListener(aVoid -> result.onComplete(true));
                }else {
                    result.onComplete(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void fetchRequestList(String currentUID, FirebaseCallbacks<List<RequestModel>> result) {

        if (currentUID == null || currentUID.isEmpty()) {
            result.onComplete(Collections.emptyList());
            return;
        }
        List<RequestModel> requests = new ArrayList<>();

        myRef.orderByChild("receiverUid").equalTo(currentUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        requests.clear();
                        if (snapshot.exists()){
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                RequestModel request = snap.getValue(RequestModel.class);
                                if (request != null && request.getStatus().equals(StatusEnum.Pending)){
                                    requests.add(request);
                                }
                            }
                            result.onComplete(requests);
                        }else {
                            result.onComplete(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FetchError", "Failed to read: " + error.getMessage());
                        result.onComplete(Collections.emptyList());
                    }
                });
    }
    @Override
    public void updateRequestStatus(String senderId, String receiverId, StatusEnum status, FirebaseCallbacks<Boolean> result) {
        String friendKey = generateFriendKey(senderId,receiverId);
        myRef.child(friendKey).child("status").setValue(status).addOnCompleteListener(
                setStatus -> result.onComplete(true)
        );
    }

    @Override
    public void fetchUserIdsList(String currentUID,FirebaseCallbacks<List<String>> result) {
        List<String> userIds = new ArrayList<>();
        myRef.child(currentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userIds.clear();
                if (snapshot.exists()){
                    for (DataSnapshot requestShot:snapshot.getChildren()) {
                       String userId = requestShot.getKey().toString();
                        if (userId != null){
                            userIds.add(userId);
                        }
                    }
                    result.onComplete(userIds);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void fetchRequestByUserName(String userName, FirebaseCallbacks<RequestModel> result) {

    }

    @Override
    public void deleteRequest(String firstUser,String secondUser, FirebaseCallbacks<Boolean> result) {
        String freindsKey = generateFriendKey(firstUser,secondUser);
        myRef.child(freindsKey).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                result.onComplete(true);
            }
        });

    }

    private String generateFriendKey(String uid1, String uid2) {
        return uid1.compareTo(uid2) < 0 ? uid1 + "_" + uid2 : uid2 + "_" + uid1;
    }
}
