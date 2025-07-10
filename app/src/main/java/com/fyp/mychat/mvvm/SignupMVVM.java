package com.fyp.mychat.mvvm;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.fyp.mychat.FirebaseHelpers.SignupFB;
import com.fyp.mychat.FirebaseHelpers.UsersFB;
import com.fyp.mychat.helpers.StatusEnum;
import com.fyp.mychat.interfaces.SignupInterface;
import com.fyp.mychat.interfaces.UserInterFace;
import com.fyp.mychat.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;


public class SignupMVVM extends ViewModel {
    SignupInterface signupInterface = new SignupFB();
    UserInterFace myUser = new UsersFB();
    MutableLiveData<Boolean> isSigned = new MutableLiveData<>();
    MutableLiveData<Boolean> isSaved = new MutableLiveData<>();
    MutableLiveData<Boolean> isTokenSaved = new MutableLiveData<>();

    public LiveData<Boolean> getSavedToken(){
        return isTokenSaved;
    }
    public void setSavedToken(String userId){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(token ->{
            if (token.isSuccessful()){
                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    signupInterface.saveDeviceToken(userId,token.getResult(),result1 -> {
                        if (result1) isTokenSaved.setValue(true);
                    });
                }
            }
        });
    }
    public LiveData<Boolean> getSavedUser(){
        return isSaved;
    }
    public void setSavedUser(UserModel userModel){
        myUser.addUser(userModel,userModel.getuId(),onSaved -> isSaved.setValue(true));
    }
    public LiveData<Boolean> getSignedStatus(){
        return  isSigned;
    }
    public void setSignedStatus(String email,String password){
        signupInterface.signupUser(email,password,onSuccess -> {
            if (onSuccess)
                isSigned.setValue(true);
            else
                isSigned.setValue(false);
        });
    }
    public LiveData<Boolean> registerUser(String userName, String email, String password, String imageUrl) {
        MutableLiveData<Boolean> registrationStatus = new MutableLiveData<>();
        setSignedStatus(email, password);
        getSignedStatus().observeForever(isSigned -> {
            if (isSigned != null && isSigned) {
                String userId = FirebaseAuth.getInstance().getUid();
                UserModel userModel = new UserModel(userId, userName, email, imageUrl,
                        System.currentTimeMillis(), "Hey there! I am using QuickChat", false, StatusEnum.Pending);
                setSavedUser(userModel);
            }else {
                registrationStatus.setValue(false);
            }
        });

        getSavedUser().observeForever(isSavedUser -> {
            if (isSavedUser != null && isSavedUser) {
                setSavedToken(FirebaseAuth.getInstance().getUid());
            }
        });

        getSavedToken().observeForever(isTokenSaved -> {
            if (isTokenSaved != null && isTokenSaved) {
                registrationStatus.setValue(true);
            }
        });

        return registrationStatus;
    }
    public LiveData<Boolean> loginUser(String email, String password){
        MutableLiveData<Boolean> loginStatus = new MutableLiveData<>(false);
        Log.d("SignupMVVM", "loginUser: start");
        signupInterface.loginUser(email,password,onResult ->{
            Log.d("SignupMVVM", "loginUser: here");
            if (onResult){
                Log.d("SignupMVVM", "loginUser: true");
                setSavedToken(FirebaseAuth.getInstance().getUid());
            }else {
                Log.d("SignupMVVM", "loginUser: false");
                loginStatus.setValue(false);
            }
        });

        getSavedToken().observeForever(isTokenSaved -> {
            if (isTokenSaved != null && isTokenSaved) {
                loginStatus.setValue(true);
            }
        });
        return loginStatus;
    }
}
