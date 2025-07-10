package com.fyp.mychat.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fyp.mychat.FirebaseHelpers.UsersFB;
import com.fyp.mychat.R;
import com.fyp.mychat.activity.ChatScreen;
import com.fyp.mychat.interfaces.UserInterFace;
import com.fyp.mychat.model.InboxModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InboxListAdapter extends RecyclerView.Adapter<InboxListAdapter.ViewHolder> {
    private List<InboxModel> inboxModels;
    UserInterFace myUser = new UsersFB();
    Fragment fragment;
    public InboxListAdapter(List<InboxModel> inboxModels, Fragment fragment){
        this.inboxModels = inboxModels;
        this.fragment = fragment;
    }
    @NonNull
    @Override
    public InboxListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_list, parent, false));

    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull InboxListAdapter.ViewHolder holder, int position) {
            InboxModel currentUser = inboxModels.get(position);

            if (currentUser.getOnlineStatus()){
                holder.onlineAlert.setVisibility(View.VISIBLE);
            }else {
                holder.onlineAlert.setVisibility(View.GONE);
            }
        if (!currentUser.getFirstUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            myUser.fetchUserName(currentUser.getFirstUserId(), userName -> {
                if (userName != null){
                    holder.userName.setText(userName.getUserName());
                    Log.d("InboxListAdapter", "onBindViewHolder: image is " + userName.getImgUrl());
                }
            });
        }else {
            myUser.fetchUserName(currentUser.getSecondUserId(), userDetail -> {
                if (userDetail != null){
                    holder.userName.setText(userDetail.getUserName());
                    if (fragment.isAdded() && fragment.getContext() != null) {
                        Log.d("InboxListAdapter", "onBindViewHolder: image is " + userDetail.getImgUrl());
                        Glide.with(fragment).load(userDetail.getImgUrl())
                                .error(R.drawable.chat_error_24px).into(holder.profilePic);
                    }
                }
            });
        }

        String lastMsg = currentUser.getLastMessage();

        if (lastMsg != null && !lastMsg.trim().isEmpty()) {
            if (lastMsg.length() > 30) {
                holder.lastMessage.setText(lastMsg.substring(0, 30) + "...");
            } else {
                holder.lastMessage.setText(lastMsg);
            }
        } else {
            holder.lastMessage.setText("No message yet");
        }


        holder.timestamp.setText(formatTimeStamp(currentUser.getTimeStamp()));
        if (currentUser.getUnreadCount() != 0){
            holder.isMessage.setVisibility(View.VISIBLE);
        }else {
            holder.isMessage.setVisibility(View.GONE);

        }


        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), ChatScreen.class);
            Log.d("UserId", "onBindViewHolder: " + currentUser.getSecondUserId());
            intent.putExtra("UserId",currentUser.getSecondUserId());
            fragment.startActivity(intent);

        });
    }

    private String formatTimeStamp(Long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        return format.format(new Date(timeStamp));
    }


    @Override
    public int getItemCount() {
        return inboxModels.size();
    }

    public void update(List<InboxModel> inboxModel) {
        this.inboxModels.clear();
        notifyDataSetChanged();
        this.inboxModels.addAll(inboxModel);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView userName,lastMessage,timestamp;
        private ImageView profilePic;
        private View isMessage,onlineAlert;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastChat);
            timestamp = itemView.findViewById(R.id.timeStamp);
            isMessage = itemView.findViewById(R.id.messageCounter);
            profilePic = itemView.findViewById(R.id.userProfile);
            onlineAlert = itemView.findViewById(R.id.onlineAlert);
        }

    }
}
