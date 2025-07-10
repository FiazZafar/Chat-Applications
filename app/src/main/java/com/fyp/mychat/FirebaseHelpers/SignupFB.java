package com.fyp.mychat.FirebaseHelpers;

import androidx.annotation.NonNull;

import com.fyp.mychat.interfaces.FirebaseCallbacks;
import com.fyp.mychat.interfaces.SignupInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupFB implements SignupInterface {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("QuickChat")
            .child("Users");
    DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("QuickChat")
            .child("Device-Tokens");
    @Override
    public void signupUser(String email, String passwords,FirebaseCallbacks<Boolean> result) {
        mAuth.createUserWithEmailAndPassword(email,passwords).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    result.onComplete(true);
                }else {
                    result.onComplete(false);
                }
            }
        });
    }
    @Override
    public void loginUser(String email, String passwords,FirebaseCallbacks<Boolean> result) {
        mAuth.signInWithEmailAndPassword(email,passwords).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                result.onComplete(true);
            }else {
                result.onComplete(false);
            }
        });
    }

    @Override
    public void saveDeviceToken(String userId,String deviceToken,
                                FirebaseCallbacks<Boolean> result) {
        tokenRef.child(userId).setValue(deviceToken).addOnCompleteListener(task ->{
            if (task.isSuccessful()){
                result.onComplete(true);
            }
        });
    }

    @Override
    public void fetchDeviceToken(String userId, FirebaseCallbacks<String> deviceToken) {
        tokenRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String token = snapshot.getValue(String.class);
                    deviceToken.onComplete(token);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
