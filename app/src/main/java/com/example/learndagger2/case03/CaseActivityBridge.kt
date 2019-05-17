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
package com.example.learndagger2.case03

import com.example.learndagger2.case03.computer.Computer_Factory
import com.example.learndagger2.case03.cpu.CPU_Factory
import com.example.learndagger2.case03.memory.MemoryModule
import com.example.learndagger2.case03.memory.MemoryModule_ProvideMemoryFactory
import com.example.learndagger2.case03.timestamp.TimestampModule
import com.example.learndagger2.case03.timestamp.TimestampModule_ProvideTimestampFactory

object CaseActivityBridge {
    fun inject(target: CaseActivity) {
        // 创建Computer依赖并注入
        val computer = Computer_Factory.newInstance("Windows", 6666).apply {
            cpu = CPU_Factory.newInstance()
//            memory = Memory_Factory.newInstance(8192)
            memory = MemoryModule_ProvideMemoryFactory.provideMemory(MemoryModule(), 8192)
        }
        CaseActivity_MembersInjector.injectComputer(target, computer)

//        val timestamp = Date(System.currentTimeMillis())

        // 创建数据仓库
        val timestampModule = TimestampModule()
        // 创建timestamp依赖
        val timestamp = TimestampModule_ProvideTimestampFactory.provideTimestamp(timestampModule)
        // 使用注入器注入
        CaseActivity_MembersInjector.injectSetTimestamp(target, timestamp)
    }
}