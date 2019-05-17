/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * MemoryModule.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-05, Cheng Mingde, Create file
 */
package com.example.learndagger2.case04.memory

import dagger.Module
import dagger.Provides

@Module
class MemoryModule(private val size: Int) {
    @Provides
    fun provideMemory() = Memory(size)
}