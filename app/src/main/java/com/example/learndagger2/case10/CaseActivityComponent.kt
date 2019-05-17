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
package com.example.learndagger2.case10

import com.example.learndagger2.application.ApplicationModule
import com.example.learndagger2.case10.computer.ComputerModule
import com.example.learndagger2.case10.cpu.AMDCPUModule
import com.example.learndagger2.case10.cpu.CPU
import com.example.learndagger2.case10.monitor.Monitor
import com.example.learndagger2.case10.monitor.MonitorModule
import com.example.learndagger2.case10.monitor.MonitorScope
import dagger.MembersInjector
import dagger.Subcomponent
import javax.inject.Provider

@MonitorScope
//@Component(
//    modules = [ComputerModule::class, TimestampModule::class, MonitorModule::class, AMDCPUModule::class],
//    dependencies = [ApplicationComponent::class]
//)
@Subcomponent(
    modules = [ComputerModule::class, MonitorModule::class, AMDCPUModule::class
        // TimestampModule::class // ApplicationComponent中已有其依赖
    ]
)
interface CaseActivityComponent {
    fun inject(target: CaseActivity) // 最简单的注入接口
    fun injectAndReturn(target: CaseActivity): CaseActivity // 注入后返回，以方便链式调用
    fun activityInjector(): MembersInjector<CaseActivity> // 返回注入器

    fun getCPU(): CPU
    fun getLazyCPU(): dagger.Lazy<CPU>
    fun getProviderCPU(): Provider<CPU>

    fun getMonitor(): Monitor

    @ApplicationModule.ApplicationName
    fun getAppName(): String

//    @Component.Builder // 自定义Builder时，这个注解是不能缺少的
//    interface Builder { // 接口名称不一定时Builder
//        fun computerModule(module: ComputerModule): Builder // 方法名也不一定是xxxModule
//        fun monitorModule(module: MonitorModule): Builder
//        fun applicationComponent(component: ApplicationComponent): Builder
//        fun build(): CaseActivityComponent // 一定要有一个无参方法并且返回对应Component，方法名也不一定为build
//    }

    @Subcomponent.Builder // 注意这里需要改为@Subcomponent
    interface Builder { // 接口名称不一定时Builder
        fun computerModule(module: ComputerModule): Builder // 方法名也不一定是xxxModule
        fun monitorModule(module: MonitorModule): Builder
        //        fun applicationComponent(component: ApplicationComponent): Builder // 不再需要提供applicationComponent的set方法
        fun build(): CaseActivityComponent // 一定要有一个无参方法并且返回对应Component，方法名也不一定为build
    }
}

