/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * MainActivity.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-14, Cheng Mingde, Create file
 */
package com.example.case11

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.case11.one.OneActivity
import com.example.case11.two.TwoActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        one.setOnClickListener { startActivity(Intent(this, OneActivity::class.java)) }
        two.setOnClickListener { startActivity(Intent(this, TwoActivity::class.java)) }
    }
}