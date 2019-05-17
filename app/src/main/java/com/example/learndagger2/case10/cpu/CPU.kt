/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * cpu.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-05, Cheng Mingde, Create file
 */
package com.example.learndagger2.case10.cpu

import javax.inject.Inject
import kotlin.random.Random

open class CPU {
    private val id = Random.nextInt()

    open fun execute(builder: StringBuilder) { // CPU执行时返回自身序列号信息
        builder.append("CPU Id: ").append(id).append("\n")
    }
}

class Intel @Inject constructor() : CPU() {
    override fun execute(builder: StringBuilder) {
        builder.append("Intel's ")
        super.execute(builder)
    }
}

class AMD @Inject constructor() : CPU() {
    override fun execute(builder: StringBuilder) {
        builder.append("AMD's ")
        super.execute(builder)
    }
}