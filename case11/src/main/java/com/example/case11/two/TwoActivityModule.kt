/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * OneActivityModule.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-16, Cheng Mingde, Create file
 */
package com.example.case11.two

import dagger.Module
import dagger.Provides
import javax.inject.Qualifier

@Module
class TwoActivityModule {
    @Qualifier
    annotation class Title

    @[Provides Title]
    fun provideTitle() = "Two Activity"
}