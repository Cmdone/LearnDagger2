/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * DiskModule.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-09, Cheng Mingde, Create file
 */
package com.example.learndagger2.case09.disk

import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import dagger.multibindings.IntoSet
import dagger.multibindings.Multibinds

@Module
class DiskModule {
    @[Provides IntoSet]
    fun provideSmallHardDisk() = Disk(Disk.Type.HARD, Disk.Capacity.SMALL)

    @[Provides IntoSet]
    fun provideHugeHardDisk() = Disk(Disk.Type.HARD, Disk.Capacity.HUGE)

    @[Provides ElementsIntoSet]
    fun provideSSD() = setOf(
        Disk(Disk.Type.SSD, Disk.Capacity.SMALL),
        Disk(Disk.Type.SSD, Disk.Capacity.NORMAL)
    )
}

@Module
abstract class DiskSetModule {
    @Multibinds
    abstract fun diskSet(): Set<Disk>
}
