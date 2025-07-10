package com.fyp.mychat.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
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
import com.fyp.mychat.mvvm.SignupMVVM;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginUser extends Fragment {

    private FragmentLoginUserBinding binding;
    private String userEmail,userPassword;
    public LoginUser() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginUserBinding.inflate(inflater,container,false);
        SignupMVVM signupMVVM = new ViewModelProvider(this).get(SignupMVVM.class);
        binding.moveToSignUpBtn.setOnClickListener(view -> getParentFragmentManager().beginTransaction().
                replace(R.id.auth_container,new SignupUser()).
                addToBackStack(null).
                commit());

        binding.eyeVisiblePasBTn.setOnClickListener(view -> {
            if (binding.passwordEdt.getInputType() == (InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD)){
                if (!binding.passwordEdt.getText().toString().equals("")){
                    binding.passwordEdt.setInputType(InputType.TYPE_CLASS_TEXT
                            |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    binding.eyeVisiblePasBTn.setImageResource(R.drawable.visibility_off_24px);
                }
            }else {
                if (!binding.passwordEdt.getText().toString().equals("")) {
                    binding.passwordEdt.setInputType(InputType.TYPE_CLASS_TEXT|
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.eyeVisiblePasBTn.setImageResource(R.drawable.visibility_24px);
                }
            }
//            binding.passwordEdt.setSelection(binding.passwordEdt.length());
        });
        
        binding.nextBtn.setOnClickListener(view -> {
            userEmail = binding.emailEdt.getText().toString().trim();
            userPassword = binding.passwordEdt.getText().toString().trim();
            Boolean isValid = validateInputs();
            setErrorsOnViews();
            if (isValid){
                signupMVVM.loginUser(userEmail,userPassword)
                        .observe(getViewLifecycleOwner(),onLogin->{
                    if (onLogin){
                        Toast.makeText(this.getActivity(), "Login Successful...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this.getActivity(), HomeActivity.class));
                        this.getActivity().finish();
                    }else{
                        Toast.makeText(this.getActivity(), "Login Failed...", Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(this.getActivity(), "Invalid inputs...", Toast.LENGTH_SHORT).show();
            }

        });



        return binding.getRoot();
    }

    private Boolean validateInputs(){

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
        if (userPassword.isEmpty()) {
            binding.passwordEdt.setError("Password is required");
            binding.passwordEdt.requestFocus();
            return false;
        } else if (userPassword.length() < 8) {
            binding.passwordEdt.setError("Password must be at least 8 characters");
            binding.passwordEdt.requestFocus();
            return false;
        }

        return true;
    }
    private void setErrorsOnViews() {
        binding.emailEdt.setError(null);
        binding.passwordEdt.setError(null);
    }
}