/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * BlueToothModule.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-13, Cheng Mingde, Create file
 */
package com.example.learndagger2.case10.bluetooth

import dagger.Module
import dagger.Provides
import javax.inject.Qualifier

@Module
class BlueToothModule {
    @Qualifier
    annotation class BlueToothVersion

    @Provides
    fun provideBlueTooth(@BlueToothVersion version: String) = BlueTooth(version)
}