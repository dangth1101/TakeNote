package com.example.takenote.data.room.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.takenote.data.model.Note;
import com.example.takenote.data.room.dao.NoteDao;

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();
}
