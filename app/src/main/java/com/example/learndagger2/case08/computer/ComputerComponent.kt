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
package com.example.learndagger2.case08.computer

import com.example.learndagger2.case08.devices.DeviceModule
import com.example.learndagger2.case08.disk.DiskModule
import com.example.learndagger2.case08.memory.MemoryModule
import dagger.Component

@Component(modules = [MemoryModule::class, DiskModule::class, DeviceModule::class])
interface ComputerComponent {
    fun inject(target: Computer)
}