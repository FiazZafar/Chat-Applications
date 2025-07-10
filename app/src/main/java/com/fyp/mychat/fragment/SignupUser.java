package com.fyp.mychat.fragment;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.fyp.mychat.R;
import com.fyp.mychat.activity.HomeActivity;
import com.fyp.mychat.databinding.FragmentSignupUserBinding;
import com.fyp.mychat.mvvm.SignupMVVM;
import com.google.firebase.auth.FirebaseAuth;
/** @noinspection ALL*/
public class SignupUser extends Fragment {

    private FragmentSignupUserBinding binding;

    private String userName,userEmail,passwords,confirmPass;
    private FirebaseAuth mAuth;
    private static final int REQUEST_CODE = 200;
    String userImage = null;
    SignupMVVM signupMVVM;
    String userId;

    public SignupUser() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignupUserBinding.inflate(inflater,container,false);
        mAuth = FirebaseAuth.getInstance();

        userImage = "https://surl.lt/vzacfw";
        signupMVVM = new ViewModelProvider(this).get(SignupMVVM.class);

        binding.moveToLoginBtn.setOnClickListener(view -> getParentFragmentManager().
                beginTransaction().
                replace(R.id.auth_container,new LoginUser()).
                addToBackStack(null).
                commit());
        binding.nextBtn.setOnClickListener(view -> {
            // Get all input values
            userName = binding.firstNameEdt.getText().toString().trim();
            userEmail = binding.emailEdt.getText().toString().trim();
            passwords = binding.passwordEdt.getText().toString().trim();
            confirmPass = binding.confirmPasswordEdt.getText().toString().trim();

            setErrorsOnViews();
            signupMVVM.registerUser(userName,userEmail,passwords,userImage)
                    .observe(getViewLifecycleOwner(),isDone -> {
                        startActivity(new Intent(getContext(),HomeActivity.class));
                        Toast.makeText(getContext(), "Signed Successfully...", Toast.LENGTH_SHORT).show();
                    });
        });

        return  binding.getRoot();
    }
    private Boolean validateInputs() {
        // Validate empty fields
        if (userName.isEmpty()) {
            binding.firstNameEdt.setError("First name is required");
            binding.firstNameEdt.requestFocus();
            return false;
        }


        // Email validation
        if (userEmail.isEmpty()) {
            binding.emailEdt.setError("Email is required");
            binding.emailEdt.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            binding.emailEdt.setError("Please enter a valid email");
            binding.emailEdt.requestFocus();
            return false;
        }

        // Password validation
        if (passwords.isEmpty()) {
            binding.passwordEdt.setError("Password is required");
            binding.passwordEdt.requestFocus();
            return false;
        } else if (passwords.length() < 8) {
            binding.passwordEdt.setError("Password must be at least 8 characters");
            binding.passwordEdt.requestFocus();
            return false;
        }

        // Confirm password validation
        if (confirmPass.isEmpty()) {
            binding.confirmPasswordEdt.setError("Please confirm your password");
            binding.confirmPasswordEdt.requestFocus();
            return false;
        } else if (!confirmPass.equals(passwords)) {
            binding.confirmPasswordEdt.setError("Passwords don't match");
            binding.confirmPasswordEdt.requestFocus();
            return false;
        }
        return true;
    }
    private void setErrorsOnViews() {
        // Reset all error indicators
        binding.firstNameEdt.setError(null);
        binding.emailEdt.setError(null);
        binding.passwordEdt.setError(null);
        binding.confirmPasswordEdt.setError(null);
    }

}