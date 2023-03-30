package com.example.takenote.data.repository;

import android.app.Activity;

import com.example.takenote.data.model.Note;

import java.util.List;

public interface UserRepository {
    void signIn(Activity activity);
    void logOut(Activity activity, List<Note> notes);

    void firebaseAuthWithGoogle(Activity activity,String idToken);
    void sync(Activity activity, List<Note> notes);

    String getUID();

    boolean isLogin();

    void navigateNoteScreen(Activity activity);
    void navigateLoginScreen(Activity activity);

}
