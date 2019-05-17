/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ApplicationModule.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-16, Cheng Mingde, Create file
 */
package com.example.case11.application

import androidx.annotation.ColorInt
import com.example.case11.VirtualData
import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.random.Random

//@Module(subcomponents = [OneActivityComponent::class, TwoActivityComponent::class])
@Module
class ApplicationModule {
    @Qualifier
    annotation class ActivityColor

    @[Provides ColorInt]
    fun provideActivityColor(@[ActivityColor ColorInt] color: Int) = color

    @Qualifier
    annotation class ActivityData

    @[Provides Singleton ActivityData]
    fun provideActivityData() = VirtualData(Date().toString(), Random.nextInt())
}