/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * CaseActivityInjector.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-05, Cheng Mingde, Create file
 */
package com.example.learndagger2.case01

import com.example.learndagger2.case01.computer.Computer
import com.example.learndagger2.case01.cpu.CPU
import com.example.learndagger2.case01.memory.Memory

object CaseActivityInjector {
    fun inject(target: CaseActivity) {
        target.computer = Computer("Windows", 6666).apply {
            cpu = CPU()
            memory = Memory(8192)
        }
    }
}