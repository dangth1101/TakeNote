package com.example.takenote.data.repository;

import android.app.Activity;

import androidx.lifecycle.LiveData;

import com.example.takenote.data.model.Note;

import java.util.List;

public interface UserRepository {
    void signIn(Activity activity);
    void logOut(Activity activity);

    void firebaseAuthWithGoogle(Activity activity,String idToken);
    void sync(List<Note> notes);

    String getUID();

    boolean isLogin();

    void navigateHomeScreen(Activity activity);
    void navigateLoginScreen(Activity activity);

}
