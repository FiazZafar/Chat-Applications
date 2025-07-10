package com.fyp.mychat.interfaces;

import com.fyp.mychat.model.InboxModel;

import java.util.List;

public interface InboxInterface {
    void addToInbox(String userId, String friendId, InboxModel myChats, FirebaseCallbacks<Boolean> result);
    void fetchAllInbox(String userId,String friendId,FirebaseCallbacks<InboxModel> inboxLists);
    void  fetchFriendsId(String userId, FirebaseCallbacks<List<String>> friendId);
    void removeFromInbox(String userId,String friendId,FirebaseCallbacks<Boolean> removeInbox);
    void removeAllInbox(String userId,FirebaseCallbacks<Boolean> removeAllInbox);
}
