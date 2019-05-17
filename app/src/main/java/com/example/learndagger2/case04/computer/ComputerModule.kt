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
package com.example.learndagger2.case04.computer

import dagger.Module
import dagger.Provides

@Module
class ComputerModule(private val os: String, private val price: Int) {
    @Provides
    fun provideComputer() = Computer(os, price)
}