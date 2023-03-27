package com.example.takenote.data.repository.impl;

import static com.example.takenote.data.constant.NOTE_PATH;
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
import com.example.takenote.view.NoteActivity;
import com.example.takenote.view.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserRepositoryImpl implements UserRepository {
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private final FirebaseAuth fireAuth;
    private final FirebaseFirestore firesStore;

    public UserRepositoryImpl(FirebaseAuth firebaseAuth, FirebaseFirestore firesStore) {
        this.fireAuth = firebaseAuth;
        this.firesStore = firesStore;
    }

    @Override
    public boolean isLogin() {
        return fireAuth.getCurrentUser() != null;
    }

    @Override
    public void signIn(Activity activity) {
        if (fireAuth.getCurrentUser() != null) {
            navigateHomeScreen(activity);
        } else {
            signInWithGoogle(activity);
        }
    }

    @Override
    public void navigateHomeScreen(Activity activity) {
        activity.startActivity(new Intent(activity, NoteActivity.class));
        activity.finish();
    }

    @Override
    public void navigateLoginScreen(Activity activity) {
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }

    @Override
    public void logOut(Activity activity) {


        fireAuth.signOut();
        gsc = GoogleSignIn.getClient(activity.getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
        gsc.signOut();
        navigateLoginScreen(activity);
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
                        FirebaseUser user = fireAuth.getCurrentUser();
                        assert user != null;
                        saveUserData(activity, user);
                    } else {
                        Log.w("FIRE AUTH", "signInWithCredential:failure", task.getException());
                    }
                });
    }


    @Override
    public void sync(List<Note> notes) {
        String uid = fireAuth.getCurrentUser().getUid();
        firesStore.collection(NOTE_PATH).document(uid).collection("notes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    doc.getReference().delete();
                }
            }
        });

        List<Map<String, Object>> noteList = new ArrayList<>();
        for (Note note : notes) {
            Map<String, Object> noteMap = new HashMap<>();
            noteMap.put("title", note.getTitle());
            noteMap.put("description", note.getDescription());
            noteMap.put("priority", note.getPriority());
            noteMap.put("created", note.getCreatedDate());
            noteMap.put("updated", note.getUpdatedDate());
            noteList.add(noteMap);
        }

        for (Map<String, Object> note : noteList) {
            firesStore.collection(NOTE_PATH)
                    .document(uid)
                    .collection("notes")
                    .add(note).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            Log.w("FIRE STORE", "onSuccess: success");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("FIRE STORE", "onFailure: " + e.getMessage());
                        }
                    });

        }
    }

    @Override
    public String getUID() {
        return fireAuth.getCurrentUser().getUid();
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
