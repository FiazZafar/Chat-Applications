package com.fyp.mychat.interfaces;

import com.fyp.mychat.model.ChatsModel;

import java.util.List;

public interface ChatsInterface {
    void addToChatList(String userId,ChatsModel chatsModel,FirebaseCallbacks<Boolean> result);
    void fetchAllChats(String userId,String chatId,FirebaseCallbacks<List<ChatsModel>> result);
    void fetchMessageIdList(String chatId,FirebaseCallbacks<List<String>> result);
    void clearAllChat(String userId,String chatId,FirebaseCallbacks<Boolean> result);
    void deleteMessage(String userId,String chatId,String messageId,FirebaseCallbacks<Boolean> result);

}
