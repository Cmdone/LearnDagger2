/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * devices.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-09, Cheng Mingde, Create file
 */
package com.example.learndagger2.case08.devices

interface Device {
    fun connect(builder: StringBuilder)
}

class Mouse : Device {
    override fun connect(builder: StringBuilder) {
        builder.append("move").append("\n")
    }
}

class Keyboard : Device {
    override fun connect(builder: StringBuilder) {
        builder.append("press").append("\n")
    }
}

class Sound : Device {
    override fun connect(builder: StringBuilder) {
        builder.append("play").append("\n")
    }
}