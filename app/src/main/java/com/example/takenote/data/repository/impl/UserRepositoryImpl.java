package com.example.takenote.data.repository.impl;

import static com.example.takenote.data.constant.RC_SIGN_IN;
import static com.example.takenote.data.constant.USER_PATH;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.example.takenote.R;
import com.example.takenote.data.model.User;
import com.example.takenote.data.repository.UserRepository;
import com.example.takenote.view.NoteActivity;
import com.example.takenote.view.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import javax.inject.Inject;

public class UserRepositoryImpl implements UserRepository {
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firesStore;

    @Inject
    public UserRepositoryImpl(FirebaseAuth firebaseAuth, FirebaseFirestore firesStore) {
        this.firebaseAuth = firebaseAuth;
        this.firesStore = firesStore;
    }

    @Override
    public void signIn(Activity activity) {
        if (firebaseAuth.getCurrentUser() != null) {
            navigateHomeScreen(activity);
        } else {
            signInWithGoogle(activity);
        }
    }

    private void navigateHomeScreen(Activity activity) {
        activity.startActivity(new Intent(activity, NoteActivity.class));
        activity.finish();
    }

    private void navigateLoginScreen(Activity activity) {
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }

    @Override
    public void logOut(Activity activity) {
        firebaseAuth.signOut();
        navigateLoginScreen(activity);
    }

    private void signInWithGoogle(Activity activity) {
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(activity.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
        GoogleSignInClient gsc = GoogleSignIn.getClient(activity, gso);

        Intent signInIntent = gsc.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void firebaseAuthWithGoogle(Activity activity, String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        Log.d("FIRE AUTH", "signInWithCredential:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        assert user != null;
                        saveUserData(activity, user);
                    } else {
                        Log.w("FIRE AUTH", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    @Override
    public void sync() {

    }

    private void saveUserData(Activity activity, FirebaseUser fuser) {
        User user = new User(fuser.getUid(),
                fuser.getDisplayName(),
                fuser.getEmail(),
                Objects.requireNonNull(fuser.getPhotoUrl()).toString());

        firesStore.collection(USER_PATH)
                .document(user.getUid())
                .set(user)
                .addOnSuccessListener(v-> {
                    Log.d("FIRE STORE", "saveUserData:success");
                    navigateHomeScreen(activity);
                })
                .addOnFailureListener(e -> Log.w("FIRE STORE", "saveUserData:failure" + e.getMessage()));
    }
}
