package com.fyp.mychat.mvvm;

import static com.fyp.mychat.services.FCMService.TYPE_ONLINE;
import static com.fyp.mychat.services.FCMService.TYPE_REQUEST;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fyp.mychat.FirebaseHelpers.FriendDB;
import com.fyp.mychat.FirebaseHelpers.InboxListFB;
import com.fyp.mychat.FirebaseHelpers.SignupFB;
import com.fyp.mychat.FirebaseHelpers.UsersFB;
import com.fyp.mychat.helpers.FCMNotificationSender;
import com.fyp.mychat.interfaces.FirebaseCallbacks;
import com.fyp.mychat.interfaces.FriendsInterface;
import com.fyp.mychat.interfaces.InboxInterface;
import com.fyp.mychat.interfaces.SignupInterface;
import com.fyp.mychat.interfaces.UserInterFace;
import com.fyp.mychat.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;


public class HomeMVVM extends ViewModel {

    FriendsInterface friendsInterface = new FriendDB();
    SignupInterface signupInterface = new SignupFB();
    UserInterFace userInterFace = new UsersFB();
    MutableLiveData<Boolean> sendNotification = new MutableLiveData<>();
    private final MutableLiveData<Boolean> myOnlineStatus = new MutableLiveData<>(false);

    public MutableLiveData<Boolean> getSendNotification() {
        return sendNotification;
    }

    public void fetchDeviceTokens(String friendId , FirebaseCallbacks<String> deviceTokens){
        signupInterface.fetchDeviceToken(friendId,onTokenList -> {
            if (onTokenList != null) deviceTokens.onComplete(onTokenList);
        });
    }
    public void fetchDeviceUser(String friendId , FirebaseCallbacks<UserModel> userModel){
        userInterFace.fetchUserName(friendId,onResult -> {
            if (onResult != null) userModel.onComplete(onResult);
        });
    }
    public void setSendNotification(String accessToken) {
        Log.d("FCM", "setSendNotification: Started"  );
        String userId = FirebaseAuth.getInstance().getUid();
        FCMNotificationSender fcmNotificationSender = new FCMNotificationSender() ;
        friendsInterface.fetchFriendIds(userId,onFriendList -> {
            if (onFriendList != null && !onFriendList.isEmpty()){
                Log.d("FCM", "setSendNotification: id Fetched" + onFriendList.get(0));
                for (String id : onFriendList){
                    signupInterface.fetchDeviceToken(id, deviceTokens -> {
                        if (deviceTokens != null) {
                        Log.d("FCM", "Device Token Fetched" + deviceTokens);
                        if (accessToken != null){
                            Log.d("FCM", "Access Token Fetched" + accessToken);
                            fetchDeviceUser(userId,user -> {
                                Log.d("FCM", "User Image Fetched" + user.getUserName());
                                if (user != null){
                                    fcmNotificationSender.sendNotification(accessToken,deviceTokens,
                                            user.getUserName(),"Your friend is online",user.getImgUrl(),TYPE_ONLINE);
                                    sendNotification.setValue(true);
                                }
                            });
                        }
                    }
                });
            }
        }
        });
    }


    public LiveData<Boolean> getMyOnlineStatus(){
        return myOnlineStatus;
    }
    public void setMyOnlineStatus(Boolean isOnline) {
        String uid = FirebaseAuth.getInstance().getUid();

        if (uid == null) {
            Log.w("InboxMVVM", "UID is null. Cannot set online status.");
            return;
        }

        userInterFace.setOnlineStatus(uid, isOnline, onResult -> {
            if (onResult != null && onResult) {
                myOnlineStatus.setValue(onResult);
            }
        });
    }
}
