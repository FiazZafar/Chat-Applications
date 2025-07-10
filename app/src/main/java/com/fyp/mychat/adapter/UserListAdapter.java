package com.fyp.mychat.adapter;

import static com.fyp.mychat.services.FCMService.TYPE_REQUEST;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fyp.mychat.FirebaseHelpers.RequestFB;
import com.fyp.mychat.FirebaseHelpers.SignupFB;
import com.fyp.mychat.FirebaseHelpers.UsersFB;
import com.fyp.mychat.R;
import com.fyp.mychat.helpers.FCMNotificationSender;
import com.fyp.mychat.helpers.StatusEnum;
import com.fyp.mychat.helpers.TokenProvider;
import com.fyp.mychat.interfaces.FirebaseCallbacks;
import com.fyp.mychat.interfaces.FriendsManagerInterface;
import com.fyp.mychat.interfaces.SignupInterface;
import com.fyp.mychat.interfaces.UserInterFace;
import com.fyp.mychat.model.RequestModel;
import com.fyp.mychat.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private List<UserModel> usersList;
    UserInterFace userInterFace = new UsersFB();
    FriendsManagerInterface friendsManagerInterface = new RequestFB();
    private Fragment fragment;
    String accessToken;
    SignupInterface signupInterface = new SignupFB();
    public UserListAdapter(List<UserModel> usersList,Fragment fragment){
        this.usersList = usersList;
        this.fragment = fragment;
    }
    @NonNull
    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull UserListAdapter.ViewHolder holder, int position) {
        UserModel currentUser = usersList.get(position);
        holder.userEmail.setText(currentUser.getUserEmail());

        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverId = currentUser.getuId();

            userInterFace.fetchUserName(receiverId,result -> {
                holder.userName.setText(result.getUserName());
                if (fragment.isAdded() && fragment.getContext() != null) {
                    Glide.with(fragment).load(currentUser.getImgUrl())
                            .error(R.drawable.chat_error_24px).into(holder.profilePic);
                }
            });

        int result = senderId.compareTo(receiverId);
        String requestId;
        if (result < 0 || result == 0){
            requestId = senderId + "_" + receiverId;
        }else {
            requestId = receiverId + "_" + senderId;
        }

        // Don't display current logged-in user
        if (receiverId.equals(senderId)) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            return;
        }


        holder.connectBtn.setOnClickListener(view -> {

            if (holder.connectBtn.getText().toString().equalsIgnoreCase("Connect")) {
                RequestModel request = new RequestModel(receiverId,senderId , System.currentTimeMillis(), StatusEnum.Pending);
                friendsManagerInterface.addRequest(request,  result1 -> {
                    if (result1) {
                        sendRequest(receiverId);
                        holder.connectBtn.setText("Cancel");
                    } else {
                        holder.connectBtn.setText("Connect");
                    }
                });
            }else {
                friendsManagerInterface.deleteRequest(senderId,requestId, result2 -> {
                    if (result2) {
                        holder.connectBtn.setText("Connect");
                    } else {
                        holder.connectBtn.setText("Cancel");
                    }
                });

            }
        });
    }

    private void sendRequest(String receiverId) {
        signupInterface.fetchDeviceToken(receiverId, deviceTokens -> {
            if (deviceTokens != null) {
                FCMNotificationSender fcmNotification = new FCMNotificationSender();
                getAccessToken(onAccessToken -> {
                    if (onAccessToken != null){
                        android.util.Log.d("NOTIFICATION", "sendRequest: sended");
                        fcmNotification.sendNotification(onAccessToken,deviceTokens,"QuickChat's",
                                "You've new friend request",null,TYPE_REQUEST);
                    }
                });
            }
        });

    }

    private void getAccessToken(FirebaseCallbacks<String> accessTokens){
        new Thread(() -> {
            accessToken = TokenProvider.getToken(fragment.requireActivity());
            accessTokens.onComplete(accessToken);
        }).start();
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public void update(List<UserModel> list) {
        this.usersList.clear();
        this.usersList.addAll(list);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView userName,userEmail,connectBtn;
        private ImageView profilePic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userEmail = itemView.findViewById(R.id.userEmail);
            connectBtn = itemView.findViewById(R.id.acceptBtn);
            profilePic = itemView.findViewById(R.id.userProfile);
        }

    }
}
