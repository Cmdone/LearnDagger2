/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * ComputerComponent.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-06, Cheng Mingde, Create file
 */
package com.example.learndagger2.case04.computer

import com.example.learndagger2.case04.memory.MemoryModule
import dagger.Component

@Component(modules = [MemoryModule::class])
interface ComputerComponent {
    fun inject(target: Computer)
}