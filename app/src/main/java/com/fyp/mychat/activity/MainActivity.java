package com.fyp.mychat.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.fyp.mychat.databinding.ActivityMainBinding;
import com.fyp.mychat.fragment.SignupUser;
import com.fyp.mychat.helpers.AppLifecycleObserver;
import com.fyp.mychat.mvvm.HomeMVVM;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        setUpUserPrefs();


        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isFinishing() || isDestroyed()) return;


            if (user != null) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return;
            }
            loadFragment(new SignupUser(), 1);
            binding.constraint.setVisibility(View.GONE);

        }, 3000);

    }

    private void setUpUserPrefs() {
        SharedPreferences prefs = getSharedPreferences("AppTheme", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("ThemeSetting",false);
        boolean isNotification = prefs.getBoolean("notifications_enabled",true);

        setNotificationSetting(isNotification);

        AppCompatDelegate.setDefaultNightMode(isDark ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);

    }

    private void setNotificationSetting(Boolean isChecked) {
        if (isChecked){
            FirebaseMessaging.getInstance().setAutoInitEnabled(true);

            FirebaseMessaging.getInstance().subscribeToTopic("general")
                    .addOnCompleteListener(task -> {});
        }else {
            FirebaseMessaging.getInstance().setAutoInitEnabled(false);
            FirebaseMessaging.getInstance().subscribeToTopic("general")
                    .addOnCompleteListener(task -> {});
        }
    }


    private void loadFragment(Fragment fragment, int flag) {
        if (isFinishing() || isDestroyed()) return;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

        int containerId = binding.authContainer.getId();
        String tag = fragment.getClass().getSimpleName();

        if (flag == 1) {
            ft.add(containerId, fragment, tag);
        } else if (flag == 2) {
            ft.replace(containerId, fragment, tag);
            ft.addToBackStack(tag);
        } else if (flag == 3) {
            ft.add(containerId, fragment, tag);
            ft.addToBackStack(tag);
        }

        ft.commitAllowingStateLoss();
    }

}
