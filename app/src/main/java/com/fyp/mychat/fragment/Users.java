package com.fyp.mychat.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fyp.mychat.FirebaseHelpers.UsersFB;
import com.fyp.mychat.adapter.UserListAdapter;
import com.fyp.mychat.databinding.FragmentUsersBinding;
import com.fyp.mychat.interfaces.UserInterFace;
import com.fyp.mychat.mvvm.UsersMVVM;

import java.util.ArrayList;

public class Users extends Fragment {
    FragmentUsersBinding binding;

    public Users() {}
    UsersMVVM usersMVVM;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater, container, false);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().show();

        UserListAdapter adapter = new UserListAdapter(new ArrayList<>(),this);
        usersMVVM = new ViewModelProvider(this).get(UsersMVVM.class);
        usersMVVM.setUserList();

        binding.usersRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.usersRecycler.setAdapter(adapter);

        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.shimmerLayout.startShimmer();

        binding.usersRecycler.setVisibility(View.GONE);
        binding.noListFoundView.setVisibility(View.GONE);

        usersMVVM.getUsersList().observe(getViewLifecycleOwner(),list -> {
            binding.shimmerLayout.setVisibility(View.GONE);
            binding.shimmerLayout.stopShimmer();

            if (list == null) {
                binding.noListFoundView.setVisibility(View.VISIBLE);
                binding.usersRecycler.setVisibility(View.GONE);
            }else if (list.isEmpty()){
                binding.noListFoundView.setVisibility(View.VISIBLE);
                binding.usersRecycler.setVisibility(View.GONE);
            }else {
                Toast.makeText(this.getContext(), "list is empty", Toast.LENGTH_SHORT).show();
                binding.usersRecycler.setVisibility(View.VISIBLE);
                binding.noListFoundView.setVisibility(View.GONE);
                adapter.update(list);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
