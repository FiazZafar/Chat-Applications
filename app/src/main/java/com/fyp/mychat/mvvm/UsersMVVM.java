package com.fyp.mychat.mvvm;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fyp.mychat.FirebaseHelpers.UsersFB;
import com.fyp.mychat.adapter.UserListAdapter;
import com.fyp.mychat.interfaces.UserInterFace;
import com.fyp.mychat.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UsersMVVM extends ViewModel {
    UserInterFace userInterFace = new UsersFB();
    MutableLiveData<List<UserModel>> userList = new MutableLiveData<>();


    public LiveData<List<UserModel>> getUsersList(){
        return userList;
    }
    public void setUserList(){
        userInterFace.fetchUsersList(result -> {
                userList.setValue(result);
        });
    }
}
