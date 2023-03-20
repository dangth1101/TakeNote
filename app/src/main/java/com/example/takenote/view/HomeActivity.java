package com.example.takenote.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.example.takenote.R;
import com.example.takenote.data.dagger.component.DaggerUserRepositoryComponent;
import com.example.takenote.data.dagger.component.UserRepositoryComponent;
import com.example.takenote.data.repository.UserRepository;
import com.example.takenote.databinding.ActivityHomeBinding;

import javax.inject.Inject;

public class HomeActivity extends AppCompatActivity {

    @Inject
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UserRepositoryComponent component = DaggerUserRepositoryComponent.create();
        component.inject(this);

        binding.btnLogout.setOnClickListener(v -> {
            userRepository.logOut(this);
        });
    }
}