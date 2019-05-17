/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * Monitor.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-09, Cheng Mingde, Create file
 */
package com.example.learndagger2.case08.monitor

import android.content.Context
import android.widget.TextView
import android.widget.Toast
import com.example.learndagger2.case08.computer.Computer
import java.util.*

class Monitor(private val context: Context, private val textView: TextView) {
    fun show(computer: Computer) {
        val builder = StringBuilder()
        computer.execute(builder)
        textView.text = builder.toString()
    }

    fun startRefresh(timestamp: Date) {
        val builder = StringBuilder(textView.text)
        builder.append(timestamp.toString()).append("\n")
        textView.text = builder.toString()
    }

    fun toastInfo() {
        val builder = StringBuilder()
        builder.append("Monitor: ").append(this.hashCode()).append("\n")
        builder.append("Context: ").append(context.hashCode()).append("\n")
        builder.append("TextView: ").append(textView.hashCode())
        Toast.makeText(context, builder.toString(), Toast.LENGTH_SHORT).show()
    }
}