package com.example.takenote.data.dagger.component;

import com.example.takenote.data.dagger.module.UserRepositoryModule;
import com.example.takenote.view.HomeActivity;
import com.example.takenote.view.LoginActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {UserRepositoryModule.class})
public interface UserRepositoryComponent {
    void inject(LoginActivity activity);
    void inject(HomeActivity activity);
}
