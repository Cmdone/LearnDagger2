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
package com.example.learndagger2.case02.cpu

import javax.inject.Inject
import kotlin.random.Random

class CPU @Inject constructor() {
    private val id = Random.nextInt()

    fun execute(builder: StringBuilder) { // CPU执行时返回自身序列号信息
        builder.append("CPU Id: ").append(id).append("\n")
    }
}