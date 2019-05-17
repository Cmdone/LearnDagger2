/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * CPUModule.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-10, Cheng Mingde, Create file
 */
package com.example.learndagger2.case10.cpu

import dagger.Binds
import dagger.BindsOptionalOf
import dagger.Module
import dagger.Provides

@Module
abstract class IntelCPUModule {
    @Binds
    abstract fun bindIntelCPU(cpu: Intel): CPU
}

@Module
abstract class AMDCPUModule {
    @Binds
    abstract fun bindIntelCPU(cpu: AMD): CPU
}

@Module
abstract class OptionalCPUModule {
    @BindsOptionalOf
    abstract fun optionalCPU(): CPU
}

@Module
class CPUModule {
    @Provides
    fun provideCPU(): CPU = Intel()
}