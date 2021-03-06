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
package com.example.learndagger2.case06.computer

import android.util.Log
import com.example.learndagger2.case06.cpu.CPU
import com.example.learndagger2.case06.memory.Memory
import com.example.learndagger2.case06.memory.MemoryModule
import javax.inject.Inject

abstract class Computer(private val os: String, private val price: Int) {
    @set:Inject
    lateinit var cpu: CPU
    @set:Inject
    @setparam:MemoryModule.MemoryType(vendor = MemoryModule.Vendor.Samsung)
    lateinit var memory: Memory

    init {
        @Suppress("LeakingThis")
        DaggerComputerComponent.create().inject(this)
    }

    fun execute(builder: StringBuilder) { // CPU执行时除了返回自身信息，还要将CPU和Memory的信息一并返回
        builder.append("Computer OS: ").append(os).append("\n")
        builder.append("Computer Price: ").append(price).append("\n")
        cpu.execute(builder)
        memory.execute(builder)
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
