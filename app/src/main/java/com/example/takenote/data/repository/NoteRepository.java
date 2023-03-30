package com.example.takenote.data.repository;

import android.app.Activity;

import androidx.lifecycle.LiveData;

import com.example.takenote.data.model.Note;

import java.util.List;

public interface NoteRepository {
    LiveData<List<Note>> getAllNotes();
    void insert(Note note);
    void delete(Note note);
    void update(Note note);
    void loadData(Activity activity);
    void deleteAll();
    Boolean isEmpty();
}
