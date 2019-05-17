/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ApplicationComponent.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-16, Cheng Mingde, Create file
 */
package com.example.case11.application

import androidx.annotation.ColorInt
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, AndroidInjectionModule::class, ActivityFactoryModule::class])
interface ApplicationComponent : AndroidInjector<Case11Application> {
//    fun newOneActivityComponentBuilder(): OneActivityComponent.Builder
//    fun newTwoActivityComponentBuilder(): TwoActivityComponent.Builder

//    fun inject(target: Case11Application)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun activityColor(@[ApplicationModule.ActivityColor ColorInt] color: Int): Builder

        fun build(): ApplicationComponent
    }
}