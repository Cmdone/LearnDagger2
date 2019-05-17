/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * TimestampModule.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-05, Cheng Mingde, Create file
 */
package com.example.learndagger2.case07.timestamp

import dagger.Module
import dagger.Provides
import java.util.*

@Module
class TimestampModule {
    @Provides
    fun provideTimestamp() = Date(System.currentTimeMillis())
}