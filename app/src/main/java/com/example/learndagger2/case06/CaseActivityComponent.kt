/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * CaseActivityComponent.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-06, Cheng Mingde, Create file
 */
package com.example.learndagger2.case06

import com.example.learndagger2.case06.computer.ComputerModule
import com.example.learndagger2.case06.cpu.CPU
import com.example.learndagger2.case06.timestamp.TimestampModule
import dagger.Component
import dagger.MembersInjector
import javax.inject.Provider

@Component(modules = [ComputerModule::class, TimestampModule::class])
interface CaseActivityComponent {
    fun inject(target: CaseActivity) // 最简单的注入接口
    fun injectAndReturn(target: CaseActivity): CaseActivity // 注入后返回，以方便链式调用
    fun activityInjector(): MembersInjector<CaseActivity> // 返回注入器

    fun getCPU(): CPU
    fun getLazyCPU(): dagger.Lazy<CPU>
    fun getProviderCPU(): Provider<CPU>
}

