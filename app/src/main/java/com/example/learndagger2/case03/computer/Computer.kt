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
package com.example.learndagger2.case03.computer

import com.example.learndagger2.case03.cpu.CPU
import com.example.learndagger2.case03.memory.Memory
import javax.inject.Inject

class Computer @Inject constructor(private val os: String, private val price: Int) {
    lateinit var cpu: CPU
    lateinit var memory: Memory

    fun execute(builder: StringBuilder) { // CPU执行时除了返回自身信息，还要将CPU和Memory的信息一并返回
        builder.append("Computer OS: ").append(os).append("\n")
        builder.append("Computer Price: ").append(price).append("\n")
        cpu.execute(builder)
        memory.execute(builder)
    }
}