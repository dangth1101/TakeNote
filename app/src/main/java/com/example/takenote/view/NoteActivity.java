package com.example.takenote.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

import com.example.takenote.data.dagger.component.DaggerUserRepositoryComponent;
import com.example.takenote.data.dagger.component.UserRepositoryComponent;
import com.example.takenote.data.model.Note;
import com.example.takenote.data.repository.UserRepository;
import com.example.takenote.databinding.ActivityNoteBinding;
import com.example.takenote.recyclerview.NoteAdapter;
import com.example.takenote.viewmodel.NoteViewModel;

import java.util.List;

import javax.inject.Inject;

public class NoteActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;
    @Inject
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNoteBinding binding = ActivityNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UserRepositoryComponent component = DaggerUserRepositoryComponent.create();
        component.inject(this);

        binding.btnLogout.setOnClickListener(v -> {
            userRepository.logOut(this);
        });

        binding.rViewNote.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rViewNote.setHasFixedSize(true);

        NoteAdapter adapter = new NoteAdapter();
        binding.rViewNote.setAdapter(adapter);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.setNotes(notes);
            }
        });
    }
}