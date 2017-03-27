package com.reline.tag;

import android.app.Application;

import com.reline.tag.injection.component.ApplicationComponent;
import com.reline.tag.injection.component.DaggerApplicationComponent;
import com.reline.tag.injection.module.ApplicationModule;


public class Onigokko extends Application {

    private static ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public static ApplicationComponent component() {
        return component;
    }
}
