package com.fyp.mychat.FirebaseHelpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fyp.mychat.interfaces.ChatsInterface;
import com.fyp.mychat.interfaces.FirebaseCallbacks;
import com.fyp.mychat.model.ChatsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsListFB implements ChatsInterface {
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("QuickChat")
            .child("Users Conversations");
    @Override
    public void addToChatList(String userId,ChatsModel chatsModel, FirebaseCallbacks<Boolean> result) {
        myRef.child(userId).child(chatsModel.getChatId_uId1_uId2()).push().setValue(chatsModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.onComplete(true);
                    }
                });
    }

    @Override
    public void fetchAllChats(String firstUID,String chatId,FirebaseCallbacks<List<ChatsModel>> result) {
        myRef.child(firstUID).child(chatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ChatsModel> chats = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    ChatsModel chat = snap.getValue(ChatsModel.class);
                    if (chat != null) {
                        chats.add(chat);
                    }
                }
                result.onComplete(chats); // ✅ callback when data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatsListFB", "Firebase fetch failed", error.toException());
            }
        });
    }

    @Override
    public void fetchMessageIdList(String chatId, FirebaseCallbacks<List<String>> result) {

    }

    @Override
    public void clearAllChat(String userId,String chatId, FirebaseCallbacks<Boolean> result) {
        myRef.child(userId).child(chatId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    result.onComplete(true);
                }
            }
        });
    }

    @Override
    public void deleteMessage(String userId,String chatId, String messageId, FirebaseCallbacks<Boolean> result) {
        myRef.child(userId).child(chatId).child(messageId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                result.onComplete(true);
            }
        });
    }



    private String generateFriendKey(String uid1, String uid2) {
        return uid1.compareTo(uid2) < 0 ? uid1 + "_" + uid2 : uid2 + "_" + uid1;
    }
}
