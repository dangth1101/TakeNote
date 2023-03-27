package com.example.takenote.data.repository.impl;

import static com.example.takenote.data.constant.NOTE_PATH;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.takenote.data.model.Note;
import com.example.takenote.data.repository.NoteRepository;
import com.example.takenote.data.room.dao.NoteDao;
import com.example.takenote.data.room.database.NoteDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NoteRepositoryImpl implements NoteRepository {

    private LiveData<List<Note>> allNotes;

    @Override
    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    private final NoteDao noteDao;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore fireStore;


    public NoteRepositoryImpl(NoteDatabase noteDatabase, FirebaseAuth firebaseAuth,
                              FirebaseFirestore fireStore) {
        this.noteDao = noteDatabase.noteDao();
        this.firebaseAuth = firebaseAuth;
        this.fireStore = fireStore;

        allNotes = this.noteDao.getAllNotes();
    }

    public void insert(Note note) {
        new InsertNoteAsyncTask(noteDao).execute(note);
    }

    public void delete(Note note) {
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }

    public void update(Note note) {
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }

    public void deleteAll() {
        new DeleteAllNotesAsyncTask(noteDao).execute();
    }

    private static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        private InsertNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        private DeleteNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        private UpdateNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }

    private static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;

        private DeleteAllNotesAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void...voids) {
            noteDao.deleteAllNotes();
            return null;
        }
    }

    public void loadData() {
        deleteAll();

        String uid = firebaseAuth.getCurrentUser().getUid();
        CollectionReference collection =
                fireStore.collection(NOTE_PATH).document(uid).collection("notes");
        collection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Note> notesList = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    Note note = document.toObject(Note.class);
                    insert(note);
                }
            }
            else {
                Log.w("FIRE STORE", "loadData: failed");
            }
        });
    }
}
