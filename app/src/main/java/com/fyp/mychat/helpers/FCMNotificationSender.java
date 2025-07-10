package com.fyp.mychat.helpers;

import static com.fyp.mychat.services.FCMService.TYPE_REQUEST;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.fyp.mychat.interfaces.FCMApiService;
import com.fyp.mychat.model.FCMDataMessage;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FCMNotificationSender {

        private static final String BASE_URL = "https://fcm.googleapis.com/";
        private final FCMApiService apiService;

        private final String userId;
        public FCMNotificationSender() {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                apiService = retrofit.create(FCMApiService.class);

        }

        public void sendNotification(String accessToken,String targetToken, String title, String body
                ,String imgUrl,String requestType) {

                FCMDataMessage message = new FCMDataMessage(targetToken, title, body,imgUrl,requestType,userId);

                Call<ResponseBody> call = apiService.sendMessage("Bearer " + accessToken, message);
                call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {}

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {}
                });
        }
}

