/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ApplicationModule.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-09, Cheng Mingde, Create file
 */
package com.example.learndagger2.application

import com.example.learndagger2.case10.CaseActivityComponent
import com.example.learndagger2.case10.computer.ComputerComponent
import com.example.learndagger2.case10.devices.Device
import com.example.learndagger2.case10.devices.Sound
import com.example.learndagger2.case10.disk.Disk
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import dagger.multibindings.StringKey
import javax.inject.Qualifier
import javax.inject.Scope

/** 直到case07才添加的类 */
@Module(subcomponents = [CaseActivityComponent::class, ComputerComponent::class])
class ApplicationModule(private val appName: String) {
    @Qualifier
    annotation class ApplicationName

    @[Provides ApplicationName ApplicationScope]
    fun provideAppName() = appName

    @[Provides IntoSet]
    fun provideHugeSSD() = Disk(Disk.Type.SSD, Disk.Capacity.HUGE)

    @[Provides IntoMap StringKey("Spare Sound")]
    fun provideSound(): Device = Sound()
}

@Scope
annotation class ApplicationScope