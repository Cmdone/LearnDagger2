/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * Computer.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-05, Cheng Mingde, Create file
 */
package com.example.learndagger2.case09.computer

import android.util.Log
import com.example.learndagger2.case09.bluetooth.BlueTooth
import com.example.learndagger2.case09.cpu.CPU
import com.example.learndagger2.case09.devices.Device
import com.example.learndagger2.case09.disk.Disk
import com.example.learndagger2.case09.memory.Memory
import com.example.learndagger2.case09.memory.MemoryModule
import javax.inject.Inject

abstract class Computer(private val os: String, private val price: Int) {
    @set:Inject
    lateinit var cpu: CPU
    //    lateinit var cpu: Optional<CPU>
    @set:Inject
    @setparam:MemoryModule.MemoryType(vendor = MemoryModule.Vendor.Samsung)
    lateinit var memory: Memory

    @set:Inject
    lateinit var disks: Set<Disk>
    @set:Inject
    lateinit var devices: Map<String, @JvmSuppressWildcards Device>

    @set:Inject
    lateinit var blueTooth: BlueTooth

    init {
        @Suppress("LeakingThis")
//        DaggerComputerComponent.create().inject(this)
        DaggerComputerComponent
            .builder()
//            .blueTooth(BlueTooth("4.0"))
            .blueToothVersion("2.3")
            .build()
            .inject(this)
    }

    fun execute(builder: StringBuilder) { // CPU执行时除了返回自身信息，还要将CPU和Memory的信息一并返回
        builder.append("Computer OS: ").append(os).append("\n")
        builder.append("Computer Price: ").append(price).append("\n")
        cpu.execute(builder)
//        if (cpu.isPresent) cpu.get().execute(builder) else builder.append("None CPU exist!\n")
        memory.execute(builder)

        disks.forEach { it.mount(builder) }
        devices.forEach { (name, device) ->
            builder.append(name).append(": ")
            device.connect(builder)
        }

        blueTooth.info(builder)
    }
}

class WindowsComputer(price: Int) : Computer("Windows", price) {
    init {
        Log.i("Computer", "WindowsComputer: init")
    }
}

class LinuxComputer(price: Int) : Computer("Linux", price) {
    init {
        Log.i("Computer", "LinuxComputer: init")
    }
}
