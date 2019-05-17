/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ComputerModule.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-06, Cheng Mingde, Create file
 */
package com.example.learndagger2.case05.computer

import dagger.Module
import dagger.Provides
import javax.inject.Qualifier

@Module
class ComputerModule(private val windowsPrice: Int, private val linuxPrice: Int) {
    @Qualifier
    annotation class WindowsComputerQualifier

    @[Provides WindowsComputerQualifier]
    fun provideWindowsComputer(): Computer = WindowsComputer(windowsPrice)

    @Qualifier
    annotation class LinuxComputerQualifier

    @[Provides LinuxComputerQualifier]
    fun provideLinuxComputer(): Computer = LinuxComputer(linuxPrice)
}