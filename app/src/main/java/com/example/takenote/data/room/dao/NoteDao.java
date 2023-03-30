package com.example.takenote.data.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.takenote.data.model.Note;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    void insert(Note note);

    @Delete
    void delete(Note note);

    @Update
    void update(Note note);

    @Query("DELETE FROM Note")
    void deleteAllNotes();

    @Query("SELECT * FROM Note ORDER BY priority DESC, updated_at DESC")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT COUNT(*) FROM Note")
    int nNote();
}
