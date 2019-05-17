/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * Memory.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-05, Cheng Mingde, Create file
 */
package com.example.learndagger2.case09.memory

class Memory(private val size: Int, private val vendor: String) {
    fun execute(builder: StringBuilder) { // Memory执行时返回自身容量信息
        builder.append("MEmory Vendor: ").append(vendor).append("\n")
        builder.append("Memory Size: ").append(size).append("MB\n")
    }
}