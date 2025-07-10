package com.fyp.mychat.helpers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.fyp.mychat.mvvm.HomeMVVM;

public class AppLifecycleObserver implements LifecycleObserver {
        private final HomeMVVM homeMVVM;
        private final Context context;
        private final boolean launchedFromNotification;
        private boolean hasSentOnlineNotification = false;

        public AppLifecycleObserver(HomeMVVM homeMVVM, Context context, boolean launchedFromNotification) {
            this.homeMVVM = homeMVVM;
            this.context = context.getApplicationContext();
            this.launchedFromNotification = launchedFromNotification;
        }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground(){
        homeMVVM.setMyOnlineStatus(true);

       /*

       Commenting this all because i think its very disturbing to have
         notification on every online friend

       Log.d("AppLifecycleObserver", "onEnterForeground: AppLifecycleObserver" + hasSentOnlineNotification);

        if (launchedFromNotification){
            return;
        }
        if (!hasSentOnlineNotification){
            hasSentOnlineNotification = true;

            new Thread(() ->{
                String accessToken = TokenProvider.getToken(context);
                if (accessToken != null){
                    new Handler(Looper.getMainLooper()).post(() -> homeMVVM.setSendNotification(accessToken));
                }
            }).start();
*/
        }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground(){
        homeMVVM.setMyOnlineStatus(false);
        }
}
