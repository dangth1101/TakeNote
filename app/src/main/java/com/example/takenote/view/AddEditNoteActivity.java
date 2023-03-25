package com.example.takenote.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.takenote.R;
import com.example.takenote.databinding.ActivityAddNoteBinding;


public class AddEditNoteActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.example.takenote.view.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.takenote.view.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.takenote.view.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY = "com.example.takenote.view.EXTRA_PRIORITY";


    private ActivityAddNoteBinding binding;
    private boolean isClick = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.priority.setOnClickListener(v -> {
            if (isClick) {
                v.setBackgroundResource(R.drawable.star_outline);
            }
            else {
                v.setBackgroundResource(R.drawable.star);
            }

            isClick = !isClick;
        });

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit Note");
            binding.etTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            binding.etDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            isClick = intent.getBooleanExtra(EXTRA_PRIORITY, false);

            if (isClick) {
                binding.priority.setBackgroundResource(R.drawable.star);
            }
            else {
                binding.priority.setBackgroundResource(R.drawable.star_outline);
            }
        } else {
            setTitle("Add Note");
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void syncNote() {
    }

    private void saveNote() {
        String title = binding.etTitle.getText().toString();
        String description = binding.etDescription.getText().toString();
        boolean priority = isClick;
        
        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_PRIORITY, priority);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);

        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }
}