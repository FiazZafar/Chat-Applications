package com.fyp.mychat.interfaces;

import com.fyp.mychat.model.FriendListModel;

import java.util.List;

public interface FriendsInterface {
    void addFriend(FriendListModel friendListModel,FirebaseCallbacks<Boolean> result);
    void fetchFriends(String userId,String friendId,FirebaseCallbacks<List<FriendListModel>> result);
    void removeFriend(String userId,String friendId,FirebaseCallbacks<Boolean> result);
    void fetchFriendIds(String userId,FirebaseCallbacks<List<String>> result);

}
