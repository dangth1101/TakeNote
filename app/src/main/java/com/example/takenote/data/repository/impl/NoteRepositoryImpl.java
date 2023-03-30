package com.example.takenote.data.repository.impl;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.takenote.data.model.Note;
import com.example.takenote.data.repository.NoteRepository;
import com.example.takenote.data.room.dao.NoteDao;
import com.example.takenote.data.room.database.NoteDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NoteRepositoryImpl implements NoteRepository {

    private LiveData<List<Note>> allNotes;

    @Override
    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    private final NoteDao noteDao;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore fireStore;
    private final FirebaseStorage fireStorage;


    public NoteRepositoryImpl(NoteDatabase noteDatabase, FirebaseAuth firebaseAuth,
                              FirebaseFirestore fireStore, FirebaseStorage fireStorage) {
        this.noteDao = noteDatabase.noteDao();
        this.firebaseAuth = firebaseAuth;
        this.fireStore = fireStore;
        this.fireStorage = fireStorage;

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

    public Boolean isEmpty() {
        IsEmptyAsyncTask task =  new IsEmptyAsyncTask(noteDao);
        task.execute();

        try {
            return task.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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

    private static class IsEmptyAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private NoteDao noteDao;

        private IsEmptyAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Boolean doInBackground(Void...voids) {
            return noteDao.nNote() == 0;
        }
    }

    public void loadData(Activity activity) {
        deleteAll();

        String uid = firebaseAuth.getCurrentUser().getUid();
        StorageReference ref =  fireStorage.getReference().child("notes/");

        ref.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                boolean fileExists = false;
                for (StorageReference item : listResult.getItems()) {
                    Log.w("FIRE STORAGE", "onSuccess: " + item.getName());
                    if (item.getName().equals(uid + ".txt")) {
                        item.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                String stringData = new String(bytes, StandardCharsets.UTF_8);

                                Gson gson = new Gson();
                                List<Note> notes = gson.fromJson(stringData,
                                        new TypeToken<List<Note>>(){}.getType());

                                for (Note note : notes) {
                                    insert(note);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.e("FIRE STORAGE", "Error downloading file:", exception);
                            }
                        });
                        return;
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("FIRE STORAGE", "Error checking for file:", exception);
            }
        });
    }
}
