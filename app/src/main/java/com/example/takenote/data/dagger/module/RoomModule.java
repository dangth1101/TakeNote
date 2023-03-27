package com.example.takenote.data.dagger.module;

import android.content.Context;

import androidx.room.Room;

import com.example.takenote.data.room.database.NoteDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {
    private final Context context;

    public RoomModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public NoteDatabase provideNoteDatabase() {
        return Room.databaseBuilder(context, NoteDatabase.class, "notes_database")
                .fallbackToDestructiveMigration()
                .build();
    }
}
