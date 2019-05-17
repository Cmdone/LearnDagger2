/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * MonitorModule.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-09, Cheng Mingde, Create file
 */
package com.example.learndagger2.case08.monitor

import android.content.Context
import android.widget.TextView
import dagger.Module
import dagger.Provides
import javax.inject.Scope

@Module
class MonitorModule(private val context: Context, private val textView: TextView) {
    @[Provides MonitorScope]
    fun provideMonitor() = Monitor(context, textView)
}

@Scope
annotation class MonitorScope