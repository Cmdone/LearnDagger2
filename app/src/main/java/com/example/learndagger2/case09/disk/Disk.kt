/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * Disk.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-09, Cheng Mingde, Create file
 */
package com.example.learndagger2.case09.disk

class Disk(private val type: Type, private val capacity: Capacity) {
    fun mount(builder: StringBuilder) {
        builder.append(capacity.size).append("G ").append(type.name).append(" mounted").append("\n")
    }

    enum class Type {
        HARD, SSD
    }

    enum class Capacity(val size: Int) {
        SMALL(256), NORMAL(512), HUGE(1024)
    }
}