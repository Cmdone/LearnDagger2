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
package com.example.learndagger2.case08.computer

import android.util.Log
import com.example.learndagger2.case08.cpu.CPU
import com.example.learndagger2.case08.devices.Device
import com.example.learndagger2.case08.disk.Disk
import com.example.learndagger2.case08.memory.Memory
import com.example.learndagger2.case08.memory.MemoryModule
import javax.inject.Inject

abstract class Computer(private val os: String, private val price: Int) {
    @set:Inject
    lateinit var cpu: CPU
    @set:Inject
    @setparam:MemoryModule.MemoryType(vendor = MemoryModule.Vendor.Samsung)
    lateinit var memory: Memory

    @set:Inject
    lateinit var disks: Set<Disk>
    @set:Inject
    lateinit var devices: Map<String, @JvmSuppressWildcards Device>

    init {
        @Suppress("LeakingThis")
        DaggerComputerComponent.create().inject(this)
    }

    fun execute(builder: StringBuilder) { // CPU执行时除了返回自身信息，还要将CPU和Memory的信息一并返回
        builder.append("Computer OS: ").append(os).append("\n")
        builder.append("Computer Price: ").append(price).append("\n")
        cpu.execute(builder)
        memory.execute(builder)

        disks.forEach { it.mount(builder) }
        devices.forEach { (name, device) ->
            builder.append(name).append(": ")
            device.connect(builder)
        }
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
