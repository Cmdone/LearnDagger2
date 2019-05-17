/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * LearnDaggerApplication.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-09, Cheng Mingde, Create file
 */
package com.example.learndagger2.application

import android.app.Application
import com.example.learndagger2.R

/** 直到case07才添加的类 */
class LearnDaggerApplication : Application() {
    private lateinit var _component: ApplicationComponent
    val component: ApplicationComponent
        get() = _component

    override fun onCreate() {
        super.onCreate()

        val name = resources.getString(R.string.app_name)
        _component = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(name))
            .build()

        application = this
    }

    companion object {
        lateinit var application: LearnDaggerApplication
    }
}