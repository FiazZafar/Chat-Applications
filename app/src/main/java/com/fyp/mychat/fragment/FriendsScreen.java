package com.fyp.mychat.fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fyp.mychat.adapter.FriendListAdapter;
import com.fyp.mychat.databinding.FragmentFriendsScreenBinding;
import com.fyp.mychat.mvvm.FriendsMVVM;

import java.util.ArrayList;

public class FriendsScreen extends Fragment {

    FragmentFriendsScreenBinding binding;
    FriendListAdapter adapter;
    FriendsMVVM friendsMVVM;

    public FriendsScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendsScreenBinding.inflate(inflater, container, false);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().show();
        friendsMVVM = new ViewModelProvider(this).get(FriendsMVVM.class);
        friendsMVVM.setFriendList();

        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.shimmerLayout.startShimmer();

        binding.noListFoundView.setVisibility(View.GONE);
        binding.friendsRec.setVisibility(View.GONE);


        adapter = new FriendListAdapter(new ArrayList<>(),getContext());
        binding.friendsRec.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.friendsRec.setAdapter(adapter);

        friendsMVVM.getFriendList().observe(getViewLifecycleOwner(),onFriendList -> {
            binding.shimmerLayout.setVisibility(View.GONE);
            binding.shimmerLayout.stopShimmer();

            if (onFriendList == null){
                binding.noListFoundView.setVisibility(View.VISIBLE);
                binding.friendsRec.setVisibility(View.GONE);
            } else if (onFriendList.isEmpty()) {
                binding.noListFoundView.setVisibility(View.VISIBLE);
                binding.friendsRec.setVisibility(View.GONE);
            }else {
                binding.noListFoundView.setVisibility(View.GONE);
                binding.friendsRec.setVisibility(View.VISIBLE);

                adapter.update(onFriendList);
            }
        });

        return binding.getRoot();
    }

}