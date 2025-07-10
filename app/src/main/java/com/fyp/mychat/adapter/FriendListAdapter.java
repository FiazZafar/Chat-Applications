package com.fyp.mychat.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fyp.mychat.FirebaseHelpers.InboxListFB;
import com.fyp.mychat.FirebaseHelpers.UsersFB;
import com.fyp.mychat.R;
import com.fyp.mychat.activity.ChatScreen;
import com.fyp.mychat.helpers.MessageType;
import com.fyp.mychat.interfaces.InboxInterface;
import com.fyp.mychat.interfaces.UserInterFace;
import com.fyp.mychat.model.FriendListModel;
import com.fyp.mychat.model.InboxModel;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    UserInterFace myUser = new UsersFB();
    InboxInterface myInbox = new InboxListFB();
    List<FriendListModel> friendList;
    Context context;
    public FriendListAdapter(List<FriendListModel> friendList, Context context){
        this.friendList = friendList;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.friends_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FriendListModel currentFriend = friendList.get(position);
            String firstUID = currentFriend.getFirstUserId();
            String secondUID = currentFriend.getSecondUserId();

            String chatId;
            int result = firstUID.compareTo(secondUID);
            if (result < 0 || result == 0){
                chatId = firstUID + "_" + secondUID;
            }else {
                chatId = secondUID + "_" + firstUID;
            }

            myUser.fetchUserName(secondUID,userDetail -> {
                if (userDetail != null){
                    Log.d("UserName", "onBindViewHolder: " + userDetail);
                    holder.userName.setText(userDetail.getUserName());
                    Glide.with(context).load(userDetail.getImgUrl()).error(R.drawable.account_circle_24px).into(holder.profilePic);
                }
            });
            holder.chatBtn.setOnClickListener(view -> {
                InboxModel inboxUser = new InboxModel(null, secondUID,
                        MessageType.Text,System.currentTimeMillis(),2,chatId,firstUID,secondUID,false);
                myInbox.addToInbox(currentFriend.getFirstUserId(),currentFriend.getSecondUserId()
                        ,inboxUser, inboxList -> {
                    Intent intent = new Intent(context, ChatScreen.class);
                    intent.putExtra("UserId",inboxUser.getSecondUserId());
                    context.startActivity(intent);
                    if (context instanceof Activity){
                        ((Activity) context).finish();
                    }
                });
            });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public void update(List<FriendListModel> newList) {
        this.friendList.clear();
        this.friendList.addAll(newList);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView userName,chatBtn;
        ImageView profilePic;
        public ViewHolder(View itemView){
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            chatBtn = itemView.findViewById(R.id.startChatBtn);
            profilePic = itemView.findViewById(R.id.userProfile);
        }
    }
}
