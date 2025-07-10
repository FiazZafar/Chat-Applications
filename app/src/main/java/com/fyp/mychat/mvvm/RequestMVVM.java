package com.fyp.mychat.mvvm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fyp.mychat.FirebaseHelpers.RequestFB;
import com.fyp.mychat.interfaces.FriendsManagerInterface;
import com.fyp.mychat.model.RequestModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class RequestMVVM extends ViewModel {
    private FriendsManagerInterface friendsInterface = new RequestFB();
    MutableLiveData<List<RequestModel>> friendRequestList = new MutableLiveData<>();

    public LiveData<List<RequestModel>> getFriendList(){
        return friendRequestList;
    }
    public void setFriendList(){
        friendsInterface.fetchRequestList(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                resultList -> {
                    friendRequestList.setValue(resultList);
                });
    }
}
