package com.example.learndagger2.case03

import android.os.Bundle
import com.example.learndagger2.BaseActivity
import com.example.learndagger2.R
import com.example.learndagger2.case03.computer.Computer
import kotlinx.android.synthetic.main.activity_case.*
import java.util.*
import javax.inject.Inject

class CaseActivity : BaseActivity() {
    @field:Inject
    lateinit var computer: Computer
    @set:Inject
    lateinit var timestamp: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case)

        CaseActivityBridge.inject(this)

        show()
    }

    private fun show() {
        val builder = StringBuilder(timestamp.toString()).append("\n")
        computer.execute(builder)
        text_view.text = builder.toString()
    }
}
