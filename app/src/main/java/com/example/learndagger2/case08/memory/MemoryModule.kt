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
package com.example.learndagger2.case08.memory

import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier

@Module
class MemoryModule {
    enum class Vendor { Kingston, Samsung }
    @Qualifier
    annotation class MemoryType(val size: Int = 4096, val vendor: Vendor = Vendor.Kingston)

    @[Provides MemoryType]
    fun provideKingston4G() = Memory(4096, "Kingston")

    @[Provides MemoryType(size = 8192)]
    fun provideKingston8G() = Memory(8192, "Kingston")

    @[Provides MemoryType(vendor = Vendor.Samsung)]
    fun provideSamsung4G() = Memory(4096, "Samsung")

    @[Provides MemoryType(size = 8192, vendor = Vendor.Samsung)]
    fun provideSamsung8G() = Memory(8192, "Samsung")
}