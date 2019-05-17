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
package com.example.learndagger2.case03.memory

import com.example.learndagger2.case05.memory.Memory
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier

@Module
class MemoryModule {
    @Provides
    fun provideMemory(size: Int) = Memory(size)
}

