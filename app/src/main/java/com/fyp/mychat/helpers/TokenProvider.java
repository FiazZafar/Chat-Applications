package com.fyp.mychat.helpers;

import android.content.Context;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;

public class TokenProvider {
    private static String cachedToken;
    private static AccessToken lastAccessToken;

    public static String getToken(Context context) {
        if (cachedToken != null && lastAccessToken != null && lastAccessToken.getExpirationTime().after(new Date())) {
            return cachedToken; // still valid
        }

        try {
            InputStream inputStream = context.getAssets().open("your-service-account.json");
            GoogleCredentials creds = GoogleCredentials
                    .fromStream(inputStream)
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/firebase.messaging"));

            creds.refreshIfExpired();
            AccessToken accessToken = creds.getAccessToken();
            cachedToken = accessToken.getTokenValue();
            lastAccessToken = accessToken;

            return cachedToken;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}


