/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * BlueTooth.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-13, Cheng Mingde, Create file
 */
package com.example.learndagger2.case10.bluetooth

class BlueTooth(val version: String) {
    fun info(builder: StringBuilder) {
        builder.append("Bluetooth Version: ").append(version).append("\n")
    }
}