package com.example.takenote.data.repository;

import android.app.Activity;

public interface UserRepository {
    void signIn(Activity activity);
    void logOut(Activity activity);

    void firebaseAuthWithGoogle(Activity activity,String idToken);
    void sync();

}
