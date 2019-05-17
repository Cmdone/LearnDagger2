/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * CaseActivityComponent.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-06, Cheng Mingde, Create file
 */
package com.example.learndagger2.case04

import com.example.learndagger2.case04.computer.ComputerModule
import com.example.learndagger2.case04.timestamp.TimestampModule
import dagger.Component

@Component(modules = [ComputerModule::class, TimestampModule::class])
interface CaseActivityComponent {
    fun inject(target: CaseActivity)
}

