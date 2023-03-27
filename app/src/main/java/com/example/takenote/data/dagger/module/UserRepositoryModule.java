package com.example.takenote.data.dagger.module;

import com.example.takenote.data.repository.UserRepository;
import com.example.takenote.data.repository.impl.UserRepositoryImpl;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = {FirebaseModule.class})
public class UserRepositoryModule {
    @Singleton
    @Provides
    public UserRepository provideUserRepository(FirebaseAuth fireAuth,
                                                FirebaseFirestore fireStore, FirebaseStorage fireStorage) {
        return new UserRepositoryImpl(fireAuth, fireStore, fireStorage);
    }
}
