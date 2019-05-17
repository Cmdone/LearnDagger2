/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * Case11Application.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-14, Cheng Mingde, Create file
 */
package com.example.case11.application

import android.graphics.Color
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class Case11Application : DaggerApplication() {
//    @field:Inject
//    lateinit var activityInjectorDispatcher: DispatchingAndroidInjector<Activity>

//    private lateinit var _component: ApplicationComponent
//    val component get() = _component

//    override fun onCreate() {
//        super.onCreate()
//
//        DaggerApplicationComponent
//            .builder()
//            .activityColor(Color.CYAN)
//            .build()
//            .inject(this)
//    }
//
//    override fun activityInjector(): AndroidInjector<Activity> = activityInjectorDispatcher

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
        DaggerApplicationComponent
            .builder()
            .activityColor(Color.CYAN)
            .build()
}