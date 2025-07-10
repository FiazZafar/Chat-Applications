package com.fyp.mychat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.fyp.mychat.adapter.RequestListAdapter;
import com.fyp.mychat.databinding.ActivityRequestListBinding;
import com.fyp.mychat.mvvm.RequestMVVM;
import java.util.ArrayList;

public class RequestList extends AppCompatActivity {
    private RequestListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.fyp.mychat.databinding.ActivityRequestListBinding binding = ActivityRequestListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RequestMVVM requestMVVM = new ViewModelProvider(this).get(RequestMVVM.class);
        requestMVVM.setFriendList();

        adapter = new RequestListAdapter(new ArrayList<>(),this);
        binding.requestRec.setLayoutManager(new LinearLayoutManager(this));
        binding.requestRec.setAdapter(adapter);

        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.shimmerLayout.startShimmer();

        binding.requestRec.setVisibility(View.GONE);
        binding.noListFoundView.setVisibility(View.GONE);

        requestMVVM.getFriendList().observe(this, list -> {
            binding.shimmerLayout.setVisibility(View.GONE);
            binding.shimmerLayout.stopShimmer();
            if (list == null) {
                binding.noListFoundView.setVisibility(View.VISIBLE);
                binding.requestRec.setVisibility(View.GONE);
            }else if (list.isEmpty()){
                binding.noListFoundView.setVisibility(View.VISIBLE);
                binding.requestRec.setVisibility(View.GONE);
            }else {
                binding.noListFoundView.setVisibility(View.GONE);
                binding.requestRec.setVisibility(View.VISIBLE);
                adapter.updateList(list);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }
}

