package com.example.takenote.data.repository;

import androidx.lifecycle.LiveData;

import com.example.takenote.data.model.Note;

import java.util.List;

public interface NoteRepository {
    LiveData<List<Note>> getAllNotes();
    public void insert(Note note);
    public void delete(Note note);
    public void update(Note note);
    public void deleteAll();
}
