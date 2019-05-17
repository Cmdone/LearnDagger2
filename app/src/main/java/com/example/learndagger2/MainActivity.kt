package com.example.learndagger2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import com.example.learndagger2.case01.CaseActivity as Case01
import com.example.learndagger2.case02.CaseActivity as Case02
import com.example.learndagger2.case03.CaseActivity as Case03
import com.example.learndagger2.case04.CaseActivity as Case04
import com.example.learndagger2.case05.CaseActivity as Case05
import com.example.learndagger2.case06.CaseActivity as Case06
import com.example.learndagger2.case07.CaseActivity as Case07
import com.example.learndagger2.case08.CaseActivity as Case08
import com.example.learndagger2.case09.CaseActivity as Case09
import com.example.learndagger2.case10.CaseActivity as Case10

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        case_01.setOnClickListener { startActivity(Intent(this, Case01::class.java)) }
        case_02.setOnClickListener { startActivity(Intent(this, Case02::class.java)) }
        case_03.setOnClickListener { startActivity(Intent(this, Case03::class.java)) }
        case_04.setOnClickListener { startActivity(Intent(this, Case04::class.java)) }
        case_05.setOnClickListener { startActivity(Intent(this, Case05::class.java)) }
        case_06.setOnClickListener { startActivity(Intent(this, Case06::class.java)) }
        case_07.setOnClickListener { startActivity(Intent(this, Case07::class.java)) }
        case_08.setOnClickListener { startActivity(Intent(this, Case08::class.java)) }
        case_09.setOnClickListener { startActivity(Intent(this, Case09::class.java)) }
        case_10.setOnClickListener { startActivity(Intent(this, Case10::class.java)) }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

object Singleton {
    const val NAME = "Cmd"
    val AGE = 24
    fun info() = "$NAME-$AGE"
}