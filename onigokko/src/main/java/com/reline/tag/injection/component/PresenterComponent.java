package com.reline.tag.injection.component;

import com.reline.tag.injection.module.NetworkModule;
import com.reline.tag.login.LoginPresenter;

import dagger.Component;

@Component(modules = NetworkModule.class, dependencies = ApplicationComponent.class)
public interface PresenterComponent {
    void inject(LoginPresenter presenter);
}
