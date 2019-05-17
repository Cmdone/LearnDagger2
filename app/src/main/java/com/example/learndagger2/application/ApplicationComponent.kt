/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ApplicationComponent.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-09, Cheng Mingde, Create file
 */
package com.example.learndagger2.application

import com.example.learndagger2.case10.timestamp.TimestampModule
import com.example.learndagger2.case10.CaseActivityComponent
import com.example.learndagger2.case10.computer.ComputerComponent
import dagger.Component

/** 直到case07才添加的类 */
@ApplicationScope
@Component(modules = [ApplicationModule::class, TimestampModule::class])
interface ApplicationComponent {
    @ApplicationModule.ApplicationName
    fun getAppName(): String

    fun caseActivityComponentBuilder(): CaseActivityComponent.Builder
//    fun caseActivityComponent(
//        computerModule: ComputerModule,
//        monitorModule: MonitorModule
//    ): CaseActivityComponent

    fun computerComponentBuilder(): ComputerComponent.Builder
}