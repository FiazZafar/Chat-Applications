package com.fyp.mychat.interfaces;

import com.fyp.mychat.model.FCMDataMessage;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FCMApiService {
    @POST("v1/projects/messenger-lite-f51d4/messages:send")
    Call<ResponseBody> sendMessage(
            @Header("Authorization") String authHeader,
            @Body FCMDataMessage message
    );
}

