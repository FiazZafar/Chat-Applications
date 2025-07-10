package com.fyp.mychat.mvvm;

import static com.fyp.mychat.services.FCMService.TYPE_CHAT;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fyp.mychat.FirebaseHelpers.ChatsListFB;
import com.fyp.mychat.FirebaseHelpers.InboxListFB;
import com.fyp.mychat.FirebaseHelpers.SignupFB;
import com.fyp.mychat.FirebaseHelpers.UsersFB;
import com.fyp.mychat.helpers.FCMNotificationSender;
import com.fyp.mychat.helpers.MessageType;
import com.fyp.mychat.interfaces.ChatsInterface;
import com.fyp.mychat.interfaces.FirebaseCallbacks;
import com.fyp.mychat.interfaces.InboxInterface;
import com.fyp.mychat.interfaces.SignupInterface;
import com.fyp.mychat.interfaces.UserInterFace;
import com.fyp.mychat.model.ChatsModel;
import com.fyp.mychat.model.InboxModel;
import com.fyp.mychat.model.UserModel;

import java.util.List;

public class ChatsMVVM extends ViewModel {
    UserInterFace userInterFace = new UsersFB();
    ChatsInterface chatsInterface = new ChatsListFB();

    InboxInterface inboxInterface = new InboxListFB();
    SignupInterface signupInterface = new SignupFB();


    MutableLiveData<UserModel> friendDetail = new MutableLiveData<>();
    MutableLiveData<List<ChatsModel>> chatsDetail = new MutableLiveData<>();
    MutableLiveData<Boolean> clearChats = new MutableLiveData<>();
    public LiveData<Boolean> getClearChat(){
        return clearChats;
    }
    public void setClearStatus(String firstUser,String secondUser){
        String chatId = generateFriendKey(firstUser,secondUser);
        chatsInterface.clearAllChat(firstUser,chatId , onResult -> {
            if (onResult) clearChats.setValue(onResult);
        });
    }
    public void setDeviceToken(String secondUID, FirebaseCallbacks<String> onCallback) {
        signupInterface.fetchDeviceToken(secondUID, result1 -> {
            if (result1 != null) {
                onCallback.onComplete(result1);
            }
        });
    }
    public void setNotification(String accessToken,String userName,String deviceToken,String message,String senderImage, FirebaseCallbacks<Boolean> onCallback){
        if (accessToken != null){
            FCMNotificationSender notify = new FCMNotificationSender();
            notify.sendNotification(accessToken,deviceToken,userName,message,senderImage,TYPE_CHAT);
            onCallback.onComplete(true);
        }
    }
    public void setInboxesStatus(String message, String firstUID, String secondUID, long timestamp, FirebaseCallbacks<Boolean> onCallback){
        String chatId = generateFriendKey(firstUID, secondUID);
        final boolean[] inboxStatus = {false, false};

        Log.d("ChatsMVVM", "setInboxesStatus: " + message);
        inboxInterface.addToInbox(firstUID, secondUID, new InboxModel(message, firstUID, MessageType.Text,
                timestamp, 0, chatId, firstUID, secondUID,false), result -> {
            inboxStatus[0] = true;
            if (inboxStatus[0] && inboxStatus[1]) {
                onCallback.onComplete(true);
            }
        });

        inboxInterface.addToInbox(secondUID, firstUID, new InboxModel(message, firstUID, MessageType.Text,
                timestamp, 1, chatId, secondUID, firstUID,false), result -> {
            inboxStatus[1] = true;
            if (inboxStatus[0] && inboxStatus[1]) {
                onCallback.onComplete(true);
            }
        });
    }
    public void setMessageStatus(String message, String firstUID, String secondUID, FirebaseCallbacks<Boolean> onMessage){
       String chatId = generateFriendKey(firstUID,secondUID);
        final boolean[] setMessage = {false, false};
       chatsInterface.addToChatList(firstUID,new ChatsModel(chatId,
               secondUID, message, System.currentTimeMillis(), MessageType.Text, false),result -> {
           setMessage[0] = true;
           if (setMessage[0] && setMessage[1]) {
               onMessage.onComplete(true);
           }
       });

       chatsInterface.addToChatList(secondUID,new ChatsModel(chatId,
               secondUID, message, System.currentTimeMillis(), MessageType.Text, false),result -> {
               onMessage.onComplete(true);
           setMessage[1] = true;
           if (setMessage[0] && setMessage[1]) {
               onMessage.onComplete(true);
           }
           });
    }
    public LiveData<List<ChatsModel>> getChats(){
        return chatsDetail;
    }
    public void setChatsDetail(String myId,String friendId){
        String chatId = generateFriendKey(myId,friendId);
        chatsInterface.fetchAllChats(myId,chatId,result -> {
            chatsDetail.postValue(result);
        });
    }
    public LiveData<UserModel> getFriendDetail(){return friendDetail;}
    public void setFriendDetail(String userId){
        userInterFace.fetchUserProfile(userId, result ->{
            if (result != null) {
                friendDetail.setValue(result);
            }
        });
    }
    public void friendDetail(String userId,FirebaseCallbacks<UserModel> onCallBack){
        userInterFace.fetchUserName(userId, result ->{
            if (result != null) {
                onCallBack.onComplete(result);
            }
        });
    }
    private String generateFriendKey(String uid1, String uid2) {
        return uid1.compareTo(uid2) < 0 ? uid1 + "_" + uid2 : uid2 + "_" + uid1;
    }
    public LiveData<Boolean> registerMessage(String message,String accessToken,
                                             String firstUID,String secondUID){
            MutableLiveData<Boolean> sendMessage = new MutableLiveData<>();

            setMessageStatus(message,firstUID,secondUID,onMessage -> {
                if (onMessage){
                    setInboxesStatus(message,firstUID,secondUID,System.currentTimeMillis(),onInbox ->{
                        if (onInbox){
                            friendDetail(firstUID,onUserName -> {
                                if (onUserName != null){
                                    setDeviceToken(secondUID,onToken -> {
                                        if (onToken != null){
                                            setNotification(accessToken,onUserName.getUserName(),onToken,message,onUserName.getImgUrl(),onNotification ->{
                                               sendMessage.postValue(true);
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
            return sendMessage;
    }
}
