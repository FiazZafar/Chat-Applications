package com.fyp.mychat.FirebaseHelpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fyp.mychat.interfaces.FirebaseCallbacks;
import com.fyp.mychat.interfaces.FriendsInterface;
import com.fyp.mychat.model.FriendListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendDB implements FriendsInterface {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference myRef = FirebaseDatabase.getInstance().
            getReference("QuickChat").child("Friend List");
    @Override
    public void addFriend(FriendListModel friendListModel, FirebaseCallbacks<Boolean> result) {
        String userId = friendListModel.getFirstUserId();
        String friendId = friendListModel.getSecondUserId();


        myRef.child(userId).child(friendId)
                .setValue(friendListModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    result.onComplete(true);
                }
            }
        });
    }

    @Override
    public void fetchFriends(String userId,String friendId,FirebaseCallbacks<List<FriendListModel>> result) {
        List<FriendListModel> myFriends = new ArrayList<>();
        myRef.child(userId).child(friendId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                        FriendListModel currentFriend = snapshot.getValue(FriendListModel.class);
                        if (currentFriend != null){
                        myFriends.add(currentFriend);
                        }
                    result.onComplete(myFriends);
                }else {
                    result.onComplete(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void removeFriend(String userId,String friendId, FirebaseCallbacks<Boolean> result) {
            myRef.child(userId).child(friendId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        result.onComplete(true);
                    }
                }
            });
    }

    @Override
    public void fetchFriendIds(String userId, FirebaseCallbacks<List<String>> result) {
        List<String> userIds = new ArrayList<>();
        Log.d("FriendsDB", "fetchFriendIds: " + userId);
        myRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userIds.clear();
                if (snapshot.exists()){
                    Log.d("FriendsDB", "fetchFriendIds: snap exists");
                    for (DataSnapshot friendShot:snapshot.getChildren()) {
                        String Id = friendShot.getKey().toString();
                        Log.d("FriendsDB", "fetchFriendIds: friend " + Id);
                        if (Id != null){
                            userIds.add(Id);
                        }
                    }
                        result.onComplete(userIds);

                }else {
                    Log.d("FriendsDB", "fetchFriendIds: snap not exists");

                    result.onComplete(new ArrayList<>());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

}
