package com.fyp.mychat.activity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.fyp.mychat.R;
import com.fyp.mychat.adapter.ChatsAdapter;
import com.fyp.mychat.databinding.ActivityChatScreenBinding;
import com.fyp.mychat.helpers.TokenProvider;
import com.fyp.mychat.model.ChatsModel;
import com.fyp.mychat.mvvm.ChatsMVVM;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatScreen extends AppCompatActivity {
    ActivityChatScreenBinding binding;
    List<ChatsModel> chatsModelList = new ArrayList<>();
    ChatsAdapter adapter;
    String accessToken;
    String userName, userProfile;
    ChatsMVVM chatsMVVM;
    String firstUID, secondUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatScreenBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        binding.noListFoundView.setVisibility(View.GONE);
        chatsMVVM = new ViewModelProvider(this).get(ChatsMVVM.class);


        firstUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        secondUID = getIntent().getStringExtra("UserId");

        initViews();
        initObservers();
        initListeners();

        if (secondUID != null) {
            chatsMVVM.setChatsDetail(firstUID, secondUID);
            chatsMVVM.setFriendDetail(secondUID);

            fetchAccessToken();
        }
    }

    private void initViews() {
        adapter = new ChatsAdapter(chatsModelList, this);
        binding.chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRecycler.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initObservers() {

        chatsMVVM.getClearChat().observe(this,chatCleared -> {
            adapter.update(chatCleared);
        });
        chatsMVVM.getFriendDetail().observe(this, userDetail -> {
            Toast.makeText(this, "Name is " + userDetail.getUserName(), Toast.LENGTH_SHORT).show();
            Log.d("ProfileCheck", "initObservers: " + userDetail.getImgUrl());
            userName = userDetail.getUserName();
            userProfile = userDetail.getImgUrl();
            if (userDetail.getOnline()){
                binding.lastSeen.setText("Online");
            }else {
                binding.lastSeen.setText("Offline");
            }
            binding.friendUserName.setText(userName);
            Glide.with(this).load(userProfile).into(binding.friendProfile);
        });

        chatsMVVM.getChats().observe(this, myChats -> {
            if(myChats != null){
                chatsModelList.clear();
                chatsModelList.addAll(myChats);
                adapter.notifyDataSetChanged();
                binding.chatRecycler.scrollToPosition(myChats.size()-1);
            }else {
                binding.noListFoundView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initListeners() {
        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });

        binding.sendBtn.setOnClickListener(view -> {

            String message = binding.messageEdt.getText().toString().trim();
            if (message.isEmpty()) return;
            binding.messageEdt.setText("");
            chatsMVVM.registerMessage(message, accessToken, firstUID, secondUID)
                    .observe(this, isSent -> {
                        if (isSent) {
                            Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
                            binding.chatRecycler.scrollToPosition(chatsModelList.size()-1);
                        }
                    });
        });

        binding.moreListBtn.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(ChatScreen.this,view);
            popupMenu.inflate(R.menu.chat_options_menu);

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.clearAll){
                    chatsMVVM.setClearStatus(firstUID,secondUID);
                }
                return false;
            });
            popupMenu.show();
        });
    }

    private void fetchAccessToken() {
        new Thread(() -> accessToken = TokenProvider.getToken(this)).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
