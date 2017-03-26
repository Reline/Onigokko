package com.reline.tag.injection.component

import com.reline.tag.injection.module.NetworkModule
import com.reline.tag.login.LoginPresenter
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(NetworkModule::class))
interface PresenterComponent {
    fun inject(presenter: LoginPresenter)
}