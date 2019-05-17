/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ComputerComponent.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-06, Cheng Mingde, Create file
 */
package com.example.learndagger2.case10.computer

import com.example.learndagger2.case10.bluetooth.BlueTooth
import com.example.learndagger2.case10.bluetooth.BlueToothModule
import com.example.learndagger2.case10.cpu.IntelCPUModule
import com.example.learndagger2.case10.devices.DeviceMapModule
import com.example.learndagger2.case10.devices.DeviceModule
import com.example.learndagger2.case10.disk.DiskModule
import com.example.learndagger2.case10.disk.DiskSetModule
import com.example.learndagger2.case10.memory.MemoryModule
import dagger.BindsInstance
import dagger.Subcomponent

//@Component(
@Subcomponent(
    modules = [MemoryModule::class,
        DiskSetModule::class, DiskModule::class, // 去掉对DiskModule的依赖也没问题
        DeviceMapModule::class, DeviceModule::class, // 去掉对DeviceModule的依赖也没问题
        IntelCPUModule::class /*, BlueToothModule::class */]
)
interface ComputerComponent {
    fun inject(target: Computer)
    fun getBlueTooth(): BlueTooth

    @Subcomponent.Builder
    interface Builder {
        fun build(): ComputerComponent
        @BindsInstance
        fun blueToothVersion(@BlueToothModule.BlueToothVersion version: String): Builder
    }
}