package com.example.takenote.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.takenote.R;
import com.example.takenote.databinding.ActivityAddNoteBinding;

public class AddNoteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAddNoteBinding binding = ActivityAddNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}