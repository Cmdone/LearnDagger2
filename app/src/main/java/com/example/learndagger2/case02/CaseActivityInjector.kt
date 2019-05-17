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
package com.example.learndagger2.case02

import com.example.learndagger2.case02.computer.Computer_Factory
import com.example.learndagger2.case02.cpu.CPU_Factory
import com.example.learndagger2.case02.memory.Memory_Factory

object CaseActivityInjector {
//    fun inject(target: CaseActivity) {
//        target.computer = Computer("Windows", 6666).apply {
//            cpu = CPU()
//            memory = Memory(8192)
//        }
//    }

    fun inject(target: CaseActivity) {
        target.computer = Computer_Factory.newInstance("Windows", 6666).apply {
            cpu = CPU_Factory.newInstance()
            memory = Memory_Factory.newInstance(8192)
        }
    }
}