package com.example.takenote.data.repository.impl;

import static com.example.takenote.data.constant.RC_SIGN_IN;
import static com.example.takenote.data.constant.USER_PATH;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.takenote.R;
import com.example.takenote.data.model.Note;
import com.example.takenote.data.model.User;
import com.example.takenote.data.repository.UserRepository;
import com.example.takenote.view.LoginActivity;
import com.example.takenote.view.NoteActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

public class UserRepositoryImpl implements UserRepository {
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private final FirebaseAuth fireAuth;
    private final FirebaseFirestore firesStore;
    private final FirebaseStorage fireStorage;

    public UserRepositoryImpl(FirebaseAuth firebaseAuth, FirebaseFirestore firesStore, FirebaseStorage fireStorage) {
        this.fireAuth = firebaseAuth;
        this.firesStore = firesStore;
        this.fireStorage = fireStorage;
    }

    @Override
    public boolean isLogin() {
        return fireAuth.getCurrentUser() != null;
    }

    @Override
    public void signIn(Activity activity) {
        if (fireAuth.getCurrentUser() != null) {
            navigateNoteScreen(activity);
        } else {
            signInWithGoogle(activity);
        }
    }

    @Override
    public void navigateNoteScreen(Activity activity) {
        activity.startActivity(new Intent(activity, NoteActivity.class));
        activity.finish();
    }

    @Override
    public void navigateLoginScreen(Activity activity) {
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }

    @Override
    public void logOut(Activity activity, List<Note> notes) {
        Gson gson = new Gson();
        String uid = fireAuth.getCurrentUser().getUid();

        StorageReference ref =  fireStorage.getReference().child("notes/" + uid + ".txt");
        String fileData = gson.toJson(notes);
        byte[] bytes = fileData.getBytes();

        UploadTask uploadTask = ref.putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("FIRE STORAGE", "String data uploaded successfully!");
                fireAuth.signOut();
                gsc = GoogleSignIn.getClient(activity.getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
                gsc.signOut();
                navigateLoginScreen(activity);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("FIRE STORAGE", "Error uploading string data:", exception);
            }
        });
    }

    private void signInWithGoogle(Activity activity) {
        gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(activity.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
        gsc = GoogleSignIn.getClient(activity, gso);

        Intent signInIntent = gsc.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void firebaseAuthWithGoogle(Activity activity, String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fireAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        Log.d("FIRE AUTH", "signInWithCredential:success");
                        navigateNoteScreen(activity);
                        FirebaseUser user = fireAuth.getCurrentUser();
                        assert user != null;
                        saveUserData(user);
                    } else {
                        Log.w("FIRE AUTH", "signInWithCredential:failure", task.getException());
                    }
                });
    }


    @Override
    public void sync(List<Note> notes) {
        Gson gson = new Gson();
        String uid = fireAuth.getCurrentUser().getUid();

        StorageReference ref =  fireStorage.getReference().child("notes/" + uid + ".txt");
        String fileData = gson.toJson(notes);
        byte[] bytes = fileData.getBytes();

        UploadTask uploadTask = ref.putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("FIRE STORAGE", "String data uploaded successfully!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("FIRE STORAGE", "Error uploading string data:", exception);
            }
        });
    }

    @Override
    public String getUID() {
        return fireAuth.getCurrentUser().getUid();
    }

    private void saveUserData(FirebaseUser fuser) {
        User user = new User(fuser.getUid(),
                fuser.getDisplayName(),
                fuser.getEmail(),
                Objects.requireNonNull(fuser.getPhotoUrl()).toString());

        firesStore.collection(USER_PATH)
                .document(user.getUid())
                .set(user)
                .addOnSuccessListener(v-> {
                    Log.d("FIRE STORE", "saveUserData:success");

                })
                .addOnFailureListener(e -> Log.w("FIRE STORE", "saveUserData:failure" + e.getMessage()));
    }
}
