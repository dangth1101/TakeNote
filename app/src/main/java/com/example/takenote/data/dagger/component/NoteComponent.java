package com.example.takenote.data.dagger.component;
import com.example.takenote.data.dagger.module.NoteRepositoryModule;
import com.example.takenote.data.dagger.module.RoomModule;
import com.example.takenote.data.dagger.module.UserRepositoryModule;
import com.example.takenote.view.NoteActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {NoteRepositoryModule.class, RoomModule.class, UserRepositoryModule.class})
public interface NoteComponent {
    void inject(NoteActivity activity);
}
