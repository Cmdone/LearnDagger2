/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * CaseActivityBridge.java
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
import java.util.*

object CaseActivityBridge {
    fun inject(target: CaseActivity) {
        // 创建依赖
        val computer = Computer_Factory.newInstance("Windows", 6666).apply {
            cpu = CPU_Factory.newInstance()
            memory = Memory_Factory.newInstance(8192)
        }
        val timestamp = Date(System.currentTimeMillis())
        // 使用注入器注入
        CaseActivity_MembersInjector.injectComputer(target, computer)
        CaseActivity_MembersInjector.injectSetTimestamp(target, timestamp)
    }
}