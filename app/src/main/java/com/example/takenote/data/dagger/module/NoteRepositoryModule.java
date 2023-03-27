package com.example.takenote.data.dagger.module;

import com.example.takenote.data.repository.NoteRepository;
import com.example.takenote.data.repository.impl.NoteRepositoryImpl;
import com.example.takenote.data.room.database.NoteDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {FirebaseModule.class, RoomModule.class})
public class NoteRepositoryModule {
    @Singleton
    @Provides
    public NoteRepository provideNoteRepository(NoteDatabase noteDatabase, FirebaseAuth firebaseAuth,
                                                FirebaseFirestore fireStore) {
        return new NoteRepositoryImpl(noteDatabase, firebaseAuth, fireStore);
    }
}
