/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * deviceModule.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-09, Cheng Mingde, Create file
 */
package com.example.learndagger2.case10.devices

import com.example.learndagger2.case10.bluetooth.BlueToothModule
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.Multibinds
import dagger.multibindings.StringKey

@Module(includes = [BlueToothModule::class])
class DeviceModule {
    @[Provides IntoMap StringKey("Mouse")]
    fun provideMouse(): Device = Mouse()

    @[Provides IntoMap StringKey("Keyboard")]
    fun provideKeyboard(): Device = Keyboard()

    @[Provides IntoMap StringKey("Sound")]
    fun provideSound(): Device = Sound()
}

@Module
abstract class DeviceMapModule {
    @Multibinds
    abstract fun deviceMap(): Map<String, Device>
}