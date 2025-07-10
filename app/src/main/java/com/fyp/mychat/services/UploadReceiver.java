package com.fyp.mychat.services;

import static java.security.AccessController.getContext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class UploadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String uploadedUrl = intent.getStringExtra("url");
        Toast.makeText(context, "Image uploaded" + uploadedUrl, Toast.LENGTH_SHORT).show();
    }

}
