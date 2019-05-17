package com.example.learndagger2.case05

import android.os.Bundle
import com.example.learndagger2.BaseActivity
import com.example.learndagger2.R
import com.example.learndagger2.case05.computer.Computer
import com.example.learndagger2.case05.computer.ComputerModule
import kotlinx.android.synthetic.main.activity_case.*
import java.util.*
import javax.inject.Inject

class CaseActivity : BaseActivity() {
    @field:[Inject ComputerModule.WindowsComputerQualifier]
    lateinit var windows: Computer
    @field:[Inject ComputerModule.LinuxComputerQualifier]
    lateinit var linux: Computer
    @set:Inject
    lateinit var timestamp: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case)

        DaggerCaseActivityComponent
            .builder()
            .computerModule(ComputerModule(6666, 8888))
            .build()
            .inject(this)

//        DaggerCaseActivityComponent
//            .builder()
//            .computerModule(ComputerModule("Windows", 6666))
//            .build()
//            .inject(this)

        show()
    }

    private fun show() {
        val builder = StringBuilder(timestamp.toString()).append("\n")
        windows.execute(builder)
        linux.execute(builder)
        text_view.text = builder.toString()
    }
}
