package com.example.takenote.data.dagger.component;

import com.example.takenote.data.dagger.module.UserRepositoryModule;
import com.example.takenote.view.NoteActivity;
import com.example.takenote.view.LoginActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {UserRepositoryModule.class})
public interface UserComponent {
    void inject(LoginActivity activity);
}
