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
package com.example.learndagger2.case04

import com.example.learndagger2.case04.computer.ComputerModule
import com.example.learndagger2.case04.computer.ComputerModule_ProvideComputerFactory
import com.example.learndagger2.case04.cpu.CPU_Factory
import com.example.learndagger2.case04.memory.MemoryModule
import com.example.learndagger2.case04.memory.MemoryModule_ProvideMemoryFactory
import com.example.learndagger2.case04.timestamp.TimestampModule
import com.example.learndagger2.case04.timestamp.TimestampModule_ProvideTimestampFactory

object CaseActivityBridge {
    fun inject(target: CaseActivity) {
        // 创建Computer依赖并注入
//        val computer = Computer_Factory.newInstance("Windows", 6666).apply {
//            cpu = CPU_Factory.newInstance()
//            memory = MemoryModule_ProvideMemoryFactory.provideMemory(MemoryModule(), 8192)
//        }
//        CaseActivity_MembersInjector.injectComputer(target, computer)

        // 创建数据仓库
        val computerModule = ComputerModule("Windows", 6666)
        val memoryModule = MemoryModule(8192)
        // 创建computer依赖
        val computer = ComputerModule_ProvideComputerFactory.provideComputer(computerModule)
        computer.cpu = CPU_Factory.newInstance()
        computer.memory = MemoryModule_ProvideMemoryFactory.provideMemory(memoryModule)
        // 使用注入器注入
        CaseActivity_MembersInjector.injectComputer(target, computer)


        // 创建数据仓库
        val timestampModule = TimestampModule()
        // 创建timestamp依赖
        val timestamp = TimestampModule_ProvideTimestampFactory.provideTimestamp(timestampModule)
        // 使用注入器注入
        CaseActivity_MembersInjector.injectSetTimestamp(target, timestamp)
    }
}