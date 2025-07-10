package com.fyp.mychat.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fyp.mychat.FirebaseHelpers.SignupFB;
import com.fyp.mychat.R;
import com.fyp.mychat.activity.HomeActivity;
import com.fyp.mychat.databinding.FragmentLoginUserBinding;
import com.fyp.mychat.interfaces.SignupInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginUser extends Fragment {

    private FragmentLoginUserBinding binding;
    private String userEmail,userName,userPassword;
    private FirebaseAuth mAuth;
    SignupInterface signupInterface;
    public LoginUser() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginUserBinding.inflate(inflater,container,false);

        signupInterface = new SignupFB();
        mAuth = FirebaseAuth.getInstance();

        binding.moveToSignUpBtn.setOnClickListener(view -> getParentFragmentManager().beginTransaction().
                replace(R.id.auth_container,new SignupUser()).
                addToBackStack(null).
                commit());
        binding.nextBtn.setOnClickListener(view -> {
            userEmail = binding.emailEdt.getText().toString().trim();
            userPassword = binding.passwordEdt.getText().toString().trim();

            // Email validation
            if (userEmail.isEmpty()) {
                binding.emailEdt.setError("Email is required");
                binding.emailEdt.requestFocus();
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                binding.emailEdt.setError("Please enter a valid email");
                binding.emailEdt.requestFocus();
                return;
            }

            // Password validation
            if (userPassword.isEmpty()) {
                binding.passwordEdt.setError("Password is required");
                binding.passwordEdt.requestFocus();
                return;
            } else if (userPassword.length() < 8) {
                binding.passwordEdt.setError("Password must be at least 8 characters");
                binding.passwordEdt.requestFocus();
                return;
            }
            loginWithCreds(userEmail,userPassword);
        });


        return binding.getRoot();
    }

    private void loginWithCreds(String userEmail, String userPassword) {
        mAuth.signInWithEmailAndPassword(userEmail,userPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(token ->{
                            if (token.isSuccessful()){

                                signupInterface.saveDeviceToken(user.getUid(),token.getResult(),result -> {
                                    Toast.makeText(this.getContext(), "Login Successful: " + user.getPhoneNumber(), Toast.LENGTH_LONG).show();
                                    Log.d("AUTH", "Authentication Successfull...");
                                    startActivity(new Intent(getContext(), HomeActivity.class));
                                    // Redirect to main activity
                                });
                            }
                        });

                    } else {
                        Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_LONG).show();
                    }
                });
    }

}