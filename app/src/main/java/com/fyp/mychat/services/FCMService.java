package com.fyp.mychat.services;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fyp.mychat.R;
import com.fyp.mychat.activity.HomeActivity;
import com.fyp.mychat.activity.RequestList;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
public class FCMService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "messages_channel_id";
    private static final String CHANNEL_NAME = "Messages and Chats";
    private static final String GROUP_CHAT_KEY = "com.fyp.mychat.CHAT_GROUP";

    public static final String TYPE_CHAT = "chat";
    public static final String TYPE_REQUEST = "friend_request";
    public static final String TYPE_ONLINE = "friend_online";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        if (message.getData().isEmpty()) return;

        // Don't show if app is foreground

        String type = message.getData().get("type");
        String title = message.getData().get("title");
        String body = message.getData().get("body");
        String imgUrl = message.getData().get("url");
        String senderId = message.getData().get("senderId");

        createNotificationChannel();

        switch (type) {
            case TYPE_CHAT:
                handleChatNotification(title, body, imgUrl, senderId);
                break;
            case TYPE_REQUEST:
                handleFriendRequestNotification(title, body);
                break;
            case TYPE_ONLINE:
                handleFriendOnlineNotification(title, body);
                break;
        }
    }

    // ✅ Chat
    private void handleChatNotification(String title, String body, String imgUrl, String senderId) {
//        if (isAppInForeground()) return;

        if (imgUrl != null && !imgUrl.isEmpty()) {
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(imgUrl)
                    .timeout(3000)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                            showNotification(title, body, false, bitmap, senderId);
                            showGroupSummaryNotification();
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            showNotification(title, body, false, null, senderId);
                            showGroupSummaryNotification();
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {}
                    });
        } else {
            showNotification(title, body, false, null, senderId);
            showGroupSummaryNotification();
        }
    }

    private void handleFriendRequestNotification(String title, String body) {
        if (isAppInForeground()) return;
        SharedPreferences.Editor editor = getSharedPreferences("AppTheme", MODE_PRIVATE).edit();
        editor.putBoolean("NewRequest", true);
        editor.apply();

        showNotification(title, body, true, null, "request"); // Unique ID for requests
    }

    private void handleFriendOnlineNotification(String title, String body) {
        if (isAppInForeground()) return;
        showNotification(title, body, false, null, "online");
    }

    private void showNotification(String title, String body, boolean isRequest, @Nullable Bitmap profilePic, String uniqueId) {
        Intent intent = new Intent(this, isRequest ? RequestList.class : HomeActivity.class);
        intent.putExtra("from_notification", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Avoid multiple launches

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.chat_24px)
                .setContentTitle(title)
                .setContentText(body)
                .setGroup(GROUP_CHAT_KEY)
                .setLargeIcon(profilePic)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        int notificationId = (uniqueId != null) ? uniqueId.hashCode() : (int) System.currentTimeMillis();
        manager.notify(notificationId, builder.build());
    }

    private void showGroupSummaryNotification() {
        NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.chat_24px)
                .setContentTitle("QuickChat")
                .setContentText("You have new messages")
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine("New messages received")
                        .setSummaryText("Multiple messages"))
                .setGroup(GROUP_CHAT_KEY)
                .setGroupSummary(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.notify(0, summaryBuilder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    // ✅ Suppress foreground notifications
    private boolean isAppInForeground() {
        ActivityManager.RunningAppProcessInfo info = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(info);
        return info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
    }
}
