package com.fyp.mychat.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.cloudinary.android.MediaManager;
import com.fyp.mychat.R;
import com.fyp.mychat.databinding.ActivityHomeBinding;
import com.fyp.mychat.fragment.FriendsScreen;
import com.fyp.mychat.fragment.InboxScreen;
import com.fyp.mychat.fragment.ProfileFragment;
import com.fyp.mychat.fragment.Users;
import com.fyp.mychat.helpers.AppLifecycleObserver;
import com.fyp.mychat.helpers.TokenProvider;
import com.fyp.mychat.model.FCMDataMessage;
import com.fyp.mychat.mvvm.HomeMVVM;
import com.fyp.mychat.mvvm.InboxMVVM;
import com.fyp.mychat.services.FCMService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    SharedPreferences.Editor editor;
    SharedPreferences appPref;
    Boolean newRequest;
    HomeMVVM homeMVVM;
    String accessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        homeMVVM = new ViewModelProvider(this).get(HomeMVVM.class);

        Boolean fromNotify = getIntent().getBooleanExtra("from_notification",false);
        if (fromNotify){
            AppLifecycleObserver observer = new AppLifecycleObserver(homeMVVM,this,true);
            ProcessLifecycleOwner.get().getLifecycle().addObserver(observer);
        }else {
            AppLifecycleObserver observer = new AppLifecycleObserver(homeMVVM,this,false);
            ProcessLifecycleOwner.get().getLifecycle().addObserver(observer);
        }

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dpdvhy4fx");
        config.put("api_key", "184645256189384");
        config.put("api_secret", "1t6EsEAX623sS0ZU0u1ln4cjRd8");


        appPref = getSharedPreferences("AppTheme",MODE_PRIVATE);
        editor = appPref.edit();
        newRequest = appPref.getBoolean("NewRequest",false);
        if (savedInstanceState == null){
            loadFragment(new InboxScreen(), false);
        }

        setToolbar("QuickChat", "Chat's");

//        homeMVVM.getSendNotification().observe(this,onNotify -> {
//        if (onNotify) Toast.makeText(this, "User is notified", Toast.LENGTH_SHORT).show();
//
//        });

        try {
            MediaManager.get();
        } catch (IllegalStateException e) {
            MediaManager.init(this, config);
        }

        setSupportActionBar(binding.toolbar);
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        binding.chatsBtn.setOnClickListener(v -> loadFragment(new InboxScreen(), false));
        binding.friendsBtn.setOnClickListener(v -> loadFragment(new FriendsScreen(), false));
        binding.contactsBtn.setOnClickListener(v -> loadFragment(new Users(), false));
        binding.profileBtn.setOnClickListener(v -> loadFragment(new ProfileFragment(), false)); // Update later if needed
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container, fragment, fragment.getClass().getSimpleName());
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        transaction.commit();
    }

    public void setToolbar(String title, String subtitle) {
        View customView = LayoutInflater.from(this)
                .inflate(R.layout.custom_toolbar, binding.toolbar, false);
        TextView titleTv = customView.findViewById(R.id.action_bar_title);
        TextView subtitleTv = customView.findViewById(R.id.action_bar_subtitle);
        ImageView moreBtn = customView.findViewById(R.id.more_list_btn);
        View alertView = customView.findViewById(R.id.alertSignal);

        if (newRequest){
            alertView.setVisibility(View.VISIBLE);
        }else {
            alertView.setVisibility(View.GONE);
        }

        moreBtn.setOnClickListener(view -> {

            PopupMenu popupMenu = new PopupMenu(this, view);
            popupMenu.inflate(R.menu.list_menu);
            Boolean isDarkMode = appPref.getBoolean("ThemeSetting",false);

            if (!isDarkMode){
                popupMenu.getMenu().getItem(2).setTitle("Night Mode").setIcon(R.drawable.mode_night_24px);
            }else {
                popupMenu.getMenu().getItem(2).setTitle("Light Mode").setIcon(R.drawable.light_mode_24px);
            }
            if (newRequest){
                popupMenu.getMenu().getItem(0).setTitle("New Request's");
            }
            try {
                Field[] fields = popupMenu.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(popupMenu);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        Method setForceShowIcon = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceShowIcon.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.request_list_id) {
                    startActivity(new Intent(this, RequestList.class));
                    finish();
                    editor.putBoolean("NewRequest",false);
                    editor.apply();
                } else if (itemId == R.id.notification_id) {
                    setupNotification();
                } else if (itemId == R.id.theme_mode) {
                     applyThemeSetting();
                }
                return true;
            });

            popupMenu.show();
        });

        titleTv.setText(title);
        subtitleTv.setText(subtitle);

        Toolbar.LayoutParams params = new Toolbar.LayoutParams(
                Toolbar.LayoutParams.MATCH_PARENT,
                Toolbar.LayoutParams.MATCH_PARENT
        );
        customView.setLayoutParams(params);

        binding.toolbar.setTitle("");
        binding.toolbar.setSubtitle("");
        binding.toolbar.addView(customView);

    }

    private Boolean applyThemeSetting() {
        Boolean isDarkMode = appPref.getBoolean("ThemeSetting",false);
        Boolean newMode = !isDarkMode;

        editor.putBoolean("ThemeSetting",newMode);
        editor.apply();
        AppCompatDelegate.setDefaultNightMode(newMode ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);

        return newMode;
    }
    private Boolean setupNotification() {
        Boolean notificationEnabled = appPref.getBoolean("notifications_enabled",true);
        if (notificationEnabled){
            editor.putBoolean("notifications_enabled",false);
            editor.apply();
            FirebaseMessaging.getInstance().setAutoInitEnabled(false);
            FirebaseMessaging.getInstance().subscribeToTopic("general")
                    .addOnCompleteListener(view -> {
                        Toast.makeText(this, "Notification Disabled", Toast.LENGTH_SHORT).show();
            });
        }else {
            editor.putBoolean("notifications_enabled",true);
            editor.apply();
            FirebaseMessaging.getInstance().setAutoInitEnabled(true);
            FirebaseMessaging.getInstance().subscribeToTopic("general")
                    .addOnCompleteListener(view -> {
                        Toast.makeText(this, "Notification Enabled", Toast.LENGTH_SHORT).show();
                    });
        }
        return notificationEnabled;
    }

}
