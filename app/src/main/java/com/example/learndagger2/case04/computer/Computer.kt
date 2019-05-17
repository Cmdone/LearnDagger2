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
package com.example.learndagger2.case04.computer

import com.example.learndagger2.case04.cpu.CPU
import com.example.learndagger2.case04.memory.Memory
import com.example.learndagger2.case04.memory.MemoryModule
import javax.inject.Inject

class Computer(private val os: String, private val price: Int) {
    @set:Inject
    lateinit var cpu: CPU
    @set:Inject
    lateinit var memory: Memory

    init {
        DaggerComputerComponent
            .builder()
            .memoryModule(MemoryModule(8192))
            .build()
            .inject(this)
    }

    fun execute(builder: StringBuilder) { // CPU执行时除了返回自身信息，还要将CPU和Memory的信息一并返回
        builder.append("Computer OS: ").append(os).append("\n")
        builder.append("Computer Price: ").append(price).append("\n")
        cpu.execute(builder)
        memory.execute(builder)
    }
}