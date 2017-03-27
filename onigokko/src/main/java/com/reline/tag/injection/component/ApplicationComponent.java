package com.reline.tag.injection.component;

import android.content.Context;
import android.content.SharedPreferences;

import com.reline.tag.injection.module.ApplicationModule;

import dagger.Component;

@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    Context context();
    SharedPreferences preferences();
}
