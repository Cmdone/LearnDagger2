package com.example.learndagger2.case01

import android.os.Bundle
import com.example.learndagger2.BaseActivity
import com.example.learndagger2.R
import com.example.learndagger2.case01.computer.Computer
import kotlinx.android.synthetic.main.activity_case.*

class CaseActivity : BaseActivity() {
    lateinit var computer: Computer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case)

//        computer = Computer("Windows", 6666)
//        computer.cpu = CPU()
//        computer.memory = Memory(8192)
        CaseActivityInjector.inject(this)

        show()
    }

    private fun show() {
        val builder = StringBuilder()
        computer.execute(builder)
        text_view.text = builder.toString()
    }
}
