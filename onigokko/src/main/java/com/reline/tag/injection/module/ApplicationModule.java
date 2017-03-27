package com.reline.tag.injection.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private static final String SHARED_PREFS_NAME = "onigokko";
    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    Context provideContext() {
        return application;
    }

    @Provides
    SharedPreferences provideSharedPrefs() {
        return application.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }
}
