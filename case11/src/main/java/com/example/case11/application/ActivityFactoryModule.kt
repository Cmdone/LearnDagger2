/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ActivityFactoryModule.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-16, Cheng Mingde, Create file
 */
package com.example.case11.application

import com.example.case11.one.OneActivity
import com.example.case11.one.OneActivityModule
import com.example.case11.two.TwoActivity
import com.example.case11.two.TwoActivityComponent
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [/*OneActivityComponent::class, */TwoActivityComponent::class])
abstract class ActivityFactoryModule {
    @ContributesAndroidInjector(modules = [OneActivityModule::class])
    abstract fun contributeOneActivityInjector(): OneActivity

//    @[Binds IntoMap ClassKey(OneActivity::class)]
//    abstract fun bindOneActivityInjectorFactory(
//        factory: OneActivityComponent.Factory
//    ): AndroidInjector.Factory<*>

    @[Binds IntoMap ClassKey(TwoActivity::class)]
    abstract fun bindTwoActivityInjectorFactory(
        factory: TwoActivityComponent.Factory
    ): AndroidInjector.Factory<*>
}