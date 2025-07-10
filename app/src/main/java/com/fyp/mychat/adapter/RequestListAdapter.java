package com.fyp.mychat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fyp.mychat.FirebaseHelpers.RequestFB;
import com.fyp.mychat.FirebaseHelpers.FriendDB;
import com.fyp.mychat.FirebaseHelpers.SignupFB;
import com.fyp.mychat.FirebaseHelpers.UsersFB;
import com.fyp.mychat.R;
import com.fyp.mychat.helpers.StatusEnum;
import com.fyp.mychat.interfaces.FriendsInterface;
import com.fyp.mychat.interfaces.FriendsManagerInterface;
import com.fyp.mychat.interfaces.SignupInterface;
import com.fyp.mychat.interfaces.UserInterFace;
import com.fyp.mychat.model.RequestModel;
import com.fyp.mychat.model.FriendListModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.ViewHolder> {

    List<RequestModel> requestList;
    UserInterFace userInterFace = new UsersFB();
    FriendsInterface friendsInterface = new FriendDB();
    FriendsManagerInterface friendsManagerInterface = new RequestFB();
    Context context;
    String accessToken,deviceToken;
    SignupInterface signupInterface = new SignupFB();
    public RequestListAdapter(List<RequestModel> requestList, Context context){
        this.context = context;
        this.requestList = requestList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.request_list,parent,false));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void update(List<RequestModel> newList) {
        this.requestList.clear();
        this.requestList.addAll(newList);
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestModel currentReq = requestList.get(position);

        String myId = FirebaseAuth.getInstance().getUid();
        String secondUserId = currentReq.getSenderId();
        if (secondUserId != null){
            userInterFace.fetchUserName(secondUserId,result -> {
                if (result != null){
                    holder.userName.setText(result.getUserName());
                    Glide.with(context).load(result.getImgUrl()).error(R.drawable.account_circle_24px).into(holder.profilePic);
                }
            });

        }
        holder.confirmBtnTxt.setOnClickListener(view -> {

            creatingFriendsListOnFB(myId,secondUserId);
            requestList.remove(position);
            notifyDataSetChanged();
    });
    }
    @SuppressLint("NotifyDataSetChanged")
    private void creatingFriendsListOnFB(String receiverId,
                                         String senderId) {
        FriendListModel friendData1 = new FriendListModel(receiverId,senderId,System.currentTimeMillis());
        friendsInterface.addFriend(friendData1,friendSuccess -> {
            friendsManagerInterface.updateRequestStatus(senderId,receiverId,
                    StatusEnum.Accepted,result -> {
            });
        });

        FriendListModel friendData2 = new FriendListModel(senderId,receiverId,System.currentTimeMillis());
        friendsInterface.addFriend(friendData2,friendSuccess -> {
            friendsManagerInterface.updateRequestStatus(senderId,receiverId,
                    StatusEnum.Accepted,result -> {
//                notifyDataSetChanged();
            });
        });

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<RequestModel> list) {
        this.requestList.clear();
        this.requestList.addAll(list);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView userName,confirmBtnTxt;
        ImageButton rejectionBtn;
        ImageView profilePic;
        ViewHolder(View itemView){
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            confirmBtnTxt = itemView.findViewById(R.id.acceptBtnReq);
            rejectionBtn = itemView.findViewById(R.id.rejectBtnReq);
            profilePic = itemView.findViewById(R.id.userProfile);
        }
    }
}
