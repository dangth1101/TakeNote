package com.example.takenote.data.repository;

import androidx.lifecycle.LiveData;

import com.example.takenote.data.model.Note;

import java.util.List;

public interface NoteRepository {
    LiveData<List<Note>> getAllNotes();
    void insert(Note note);
    void delete(Note note);
    void update(Note note);
    void deleteAll();
}
