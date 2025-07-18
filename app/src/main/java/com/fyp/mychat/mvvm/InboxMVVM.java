package com.fyp.mychat.mvvm;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fyp.mychat.FirebaseHelpers.InboxListFB;
import com.fyp.mychat.FirebaseHelpers.UsersFB;
import com.fyp.mychat.interfaces.FirebaseCallbacks;
import com.fyp.mychat.interfaces.InboxInterface;
import com.fyp.mychat.interfaces.UserInterFace;
import com.fyp.mychat.model.InboxModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class InboxMVVM extends ViewModel {

    InboxInterface inboxInterface = new InboxListFB();
    UserInterFace userInterFace = new UsersFB();
    private final MutableLiveData<List<InboxModel>> inboxList = new MutableLiveData<>();

    public void fetchFriendsStatus(String id , FirebaseCallbacks<Boolean> fetchStatus){
        userInterFace.fetchUserName(id,result -> {
            if (result != null) {
                fetchStatus.onComplete(result.getOnline());
            }
        });
    }


    public LiveData<List<InboxModel>> getInboxList() {
        return inboxList;
    }



    private void setFriendsId(String userId, FirebaseCallbacks<List<String>> friendId) {
        inboxInterface.fetchFriendsId(userId, id -> friendId.onComplete(Objects.requireNonNullElseGet(id, ArrayList::new)));
    }

    public void setInboxList() {

        String userId = FirebaseAuth.getInstance().getUid();

        setFriendsId(userId, friendIdList -> {

            List<InboxModel> allInboxes = new ArrayList<>();
            Set<String> uniqueInboxKeys = new HashSet<>();
            AtomicInteger counter = new AtomicInteger(friendIdList.size());

            if (friendIdList.isEmpty()) {
                inboxList.setValue(allInboxes);
                return;
            }

            for (String id : friendIdList) {
                inboxInterface.fetchAllInbox(userId, id, inbox -> {
                    String key = inbox.getFirstUserId() + "_" + inbox.getSecondUserId();

                    synchronized (uniqueInboxKeys) {
                        if (uniqueInboxKeys.contains(key)) {
                            if (counter.decrementAndGet() == 0) {
                                inboxList.setValue(new ArrayList<>(allInboxes));
                            }
                            return;
                        }

                        uniqueInboxKeys.add(key);
                    }

                    fetchFriendsStatus(inbox.getSecondUserId(), onStatus -> {
                        inbox.setOnlineStatus(onStatus);
                        allInboxes.add(inbox);

                        if (counter.decrementAndGet() == 0) {
                            inboxList.setValue(new ArrayList<>(allInboxes));
                        }
                    });
                });
            }
        });
    }
}

