package com.example.takenote.view;

import static com.example.takenote.data.constant.RC_SIGN_IN;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.takenote.data.dagger.component.DaggerUserComponent;
import com.example.takenote.data.dagger.component.UserComponent;
import com.example.takenote.data.repository.UserRepository;
import com.example.takenote.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

public class LoginActivity extends AppCompatActivity {
    @Inject
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UserComponent component = DaggerUserComponent.create();
        component.inject(this);

        if (userRepository.isLogin()) {
            userRepository.navigateHomeScreen(this);
        }

        binding.btnLogin.setOnClickListener(v -> {
            userRepository.signIn(this);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                userRepository.firebaseAuthWithGoogle(this, account.getIdToken());
            } catch (ApiException e) {
                Log.w("GOOGLE SIGN IN", "Google sign in failed", e);
            }
        }
    }
}