package com.fyp.mychat.helpers;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            FirebaseDatabase.getInstance().getReference(".info/connected")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Boolean connected = snapshot.getValue(Boolean.class);
                            if (connected != null && connected) {
                                FirebaseDatabase.getInstance()
                                        .getReference("QuickChat")
                                        .child("Users")
                                        .child(uid)
                                        .child("online")
                                        .onDisconnect()
                                        .setValue(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }
    }
}

