package com.fyp.mychat.fragment;

import static android.content.Intent.getIntent;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fyp.mychat.adapter.InboxListAdapter;
import com.fyp.mychat.databinding.FragmentInboxScreenBinding;
import com.fyp.mychat.mvvm.InboxMVVM;
import java.util.ArrayList;

public class InboxScreen extends Fragment {
    private FragmentInboxScreenBinding binding;
    InboxListAdapter adapter;
    Context context;
    InboxMVVM inboxMVVM;

    public InboxScreen() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInboxScreenBinding.inflate(inflater, container, false);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().show();

        inboxMVVM = new ViewModelProvider(this).get(InboxMVVM.class);

        // Initialize adapter with empty list
        adapter = new InboxListAdapter(new ArrayList<>(), this);
        binding.chatListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.chatListRecycler.setAdapter(adapter);

        binding.shimmerLayout.setVisibility(VISIBLE);
        binding.shimmerLayout.startShimmer();
        // Set initial UI state
        binding.noListFoundView.setVisibility(GONE);
        binding.chatListRecycler.setVisibility(GONE);


        inboxMVVM.setInboxList();
        inboxMVVM.getInboxList().observe(getViewLifecycleOwner(), inboxLists -> {
            // Hide progress bar regardless of result
            binding.shimmerLayout.stopShimmer();
            binding.shimmerLayout.setVisibility(GONE);
            if (inboxLists == null) {
                // Error case
                binding.noListFoundView.setVisibility(View.VISIBLE);
                binding.chatListRecycler.setVisibility(GONE);
            } else if (inboxLists.isEmpty()) {
                // Empty case
                binding.noListFoundView.setVisibility(View.VISIBLE);
                binding.chatListRecycler.setVisibility(GONE);
            } else {
                // Data loaded case
                binding.noListFoundView.setVisibility(GONE);
                binding.chatListRecycler.setVisibility(View.VISIBLE);

                adapter.update(inboxLists);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}

