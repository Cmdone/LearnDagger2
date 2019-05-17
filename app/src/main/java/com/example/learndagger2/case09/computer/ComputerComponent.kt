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
package com.example.learndagger2.case09.computer

import com.example.learndagger2.case09.bluetooth.BlueTooth
import com.example.learndagger2.case09.bluetooth.BlueToothModule
import com.example.learndagger2.case09.cpu.IntelCPUModule
import com.example.learndagger2.case09.devices.DeviceMapModule
import com.example.learndagger2.case09.devices.DeviceModule
import com.example.learndagger2.case09.disk.DiskModule
import com.example.learndagger2.case09.disk.DiskSetModule
import com.example.learndagger2.case09.memory.MemoryModule
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [MemoryModule::class,
        DiskSetModule::class, DiskModule::class, // 去掉对DiskModule的依赖也没问题
        DeviceMapModule::class, DeviceModule::class, // 去掉对DeviceModule的依赖也没问题
        IntelCPUModule::class, BlueToothModule::class]
)
//@Component(
//    modules = [MemoryModule::class, DiskModule::class, DeviceModule::class,
//        OptionalCPUModule::class, CPUModule::class] // 如果去掉CPUModule的依赖，则Optional中包裹的是空实例
//)
interface ComputerComponent {
    fun inject(target: Computer)
    fun getBlueTooth(): BlueTooth

    @Component.Builder
    interface Builder {
        fun build(): ComputerComponent
        //        @BindsInstance
//        fun blueTooth(blueTooth: BlueTooth): Builder
        @BindsInstance
        fun blueToothVersion(@BlueToothModule.BlueToothVersion version: String): Builder
    }
}