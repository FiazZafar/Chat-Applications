package com.fyp.mychat.mvvm;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fyp.mychat.FirebaseHelpers.FriendDB;
import com.fyp.mychat.interfaces.FirebaseCallbacks;
import com.fyp.mychat.interfaces.FriendsInterface;
import com.fyp.mychat.model.FriendListModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FriendsMVVM extends ViewModel {

    FriendsInterface friendsInterface = new FriendDB();
    MutableLiveData<List<FriendListModel>> friendList = new MutableLiveData<>();


    public LiveData<List<FriendListModel>> getFriendList() {
        return friendList;
    }

    public void setFriendList() {
        String myId = FirebaseAuth.getInstance().getUid();
        setFriendId(myId, friendIds -> {
            if (friendIds == null || friendIds.isEmpty()){
                friendList.setValue(new ArrayList<>());
                return;
            }
            List<FriendListModel> allFriends = new ArrayList<>();
            AtomicInteger counter = new AtomicInteger(friendIds.size());
            for (String id : friendIds) {
                friendsInterface.fetchFriends(myId, id, list -> {
                    if (list != null) {
                        allFriends.addAll(list);
                    }
                    if (counter.decrementAndGet() == 0) {
                        if (allFriends != null)
                            friendList.setValue(allFriends);
                        else
                            friendList.setValue(new ArrayList<>());
                    }
                });
            }
        });
    }

    public void setFriendId(String userId, FirebaseCallbacks<List<String>> friendIdCallback) {

        friendsInterface.fetchFriendIds(userId, result -> {
            friendIdCallback.onComplete(result);
        });
    }
}
