package com.fyp.mychat.mvvm;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fyp.mychat.FirebaseHelpers.FriendDB;
import com.fyp.mychat.FirebaseHelpers.UsersFB;
import com.fyp.mychat.adapter.UserListAdapter;
import com.fyp.mychat.helpers.StatusEnum;
import com.fyp.mychat.interfaces.FirebaseCallbacks;
import com.fyp.mychat.interfaces.FriendsInterface;
import com.fyp.mychat.interfaces.UserInterFace;
import com.fyp.mychat.model.FriendListModel;
import com.fyp.mychat.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class UsersMVVM extends ViewModel {
    UserInterFace userInterFace = new UsersFB();
    FriendsInterface friendsInterface = new FriendDB();
    MutableLiveData<List<UserModel>> userList = new MutableLiveData<>();


    private void fetchFriendsList(String userId , FirebaseCallbacks<List<String>> friendList){
        friendsInterface.fetchFriendIds(userId,onResult -> {
            if(onResult != null){
                friendList.onComplete(onResult);
            }
        });
    }
    public LiveData<List<UserModel>> getUsersList(){return userList;}

    public void setUserList() {
        String userId = FirebaseAuth.getInstance().getUid();
        List<UserModel> list = new ArrayList<>();

        userInterFace.fetchUsersList(result -> {
            fetchFriendsList(userId, onList -> {
                list.clear();
                for (UserModel userModel : result) {
                    if (onList.contains(userModel.getuId())) {
                        userModel.setFriendShipStatus(StatusEnum.Accepted);
                    }
                    list.add(userModel);
                }
                userList.setValue(list);
            });
        });
    }

}
