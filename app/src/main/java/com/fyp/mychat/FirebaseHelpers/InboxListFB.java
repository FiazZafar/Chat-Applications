package com.fyp.mychat.FirebaseHelpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fyp.mychat.interfaces.FirebaseCallbacks;
import com.fyp.mychat.interfaces.InboxInterface;
import com.fyp.mychat.model.InboxModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InboxListFB implements InboxInterface {

    DatabaseReference myRef = FirebaseDatabase.getInstance()
            .getReference("QuickChat").child("Inbox List");

    @Override
    public void addToInbox(String userId, String friendId, InboxModel myChats,
                           FirebaseCallbacks<Boolean> result) {
        String inboxKey = generateFriendKey(userId,friendId);
        myRef.child(userId).child(inboxKey).setValue(myChats).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                result.onComplete(true);
            }
        });
    }
    @Override
    public void fetchAllInbox(String userId, String friendId,
                              FirebaseCallbacks<InboxModel> chatsLists) {
        myRef.child(userId).child(friendId).limitToFirst(50).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    InboxModel myInbox = snapshot.getValue(InboxModel.class);
                    chatsLists.onComplete(myInbox);
                    Log.d("FirebaseSnapshot", "Key: Not Null" );

                }else {
                    Log.d("FirebaseSnapshot", "Key: null" );

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void fetchFriendsId(String userId, FirebaseCallbacks<List<String>> friendId) {

        List<String> friendsIdList = new ArrayList<>();
            myRef.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot idSnapshot: snapshot.getChildren()){
                            String myId = idSnapshot.getKey();
                                friendsIdList.add(myId);
                        }
                        if (friendsIdList != null){
                            friendId.onComplete(friendsIdList);
                        }else {
                            friendId.onComplete(new ArrayList<>());
                        }
                    }else {
                        friendId.onComplete(new ArrayList<>());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }
    @Override
    public void removeFromInbox(String userId, String friendId, FirebaseCallbacks<Boolean> removeInbox) {
            myRef.child(userId).child(friendId).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    removeInbox.onComplete(true);
                }
            });
    }
    @Override
    public void removeAllInbox(String userId, FirebaseCallbacks<Boolean> removeAllInbox) {
            myRef.child(userId).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    removeAllInbox.onComplete(true);
                }
            });
    }

    private String generateFriendKey(String uid1, String uid2) {
        return uid1.compareTo(uid2) < 0 ? uid1 + "_" + uid2 : uid2 + "_" + uid1;
    }
}
