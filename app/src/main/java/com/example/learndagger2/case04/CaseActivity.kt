package com.example.learndagger2.case04

import android.os.Bundle
import com.example.learndagger2.BaseActivity
import com.example.learndagger2.R
import com.example.learndagger2.case04.computer.Computer
import com.example.learndagger2.case04.computer.ComputerModule
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

//        CaseActivityBridge.inject(this) // 不再需要我们自己写的Bridge了
        DaggerCaseActivityComponent
            .builder()
            .computerModule(ComputerModule("Windows", 6666))
//            .timestampModule(TimestampModule())
            .build()
            .inject(this)

//        computer.cpu = CPU_Factory.newInstance()
//        computer.memory = MemoryModule_ProvideMemoryFactory.provideMemory(MemoryModule(8192))

        show()
    }

    private fun show() {
        val builder = StringBuilder(timestamp.toString()).append("\n")
        computer.execute(builder)
        text_view.text = builder.toString()
    }
}
