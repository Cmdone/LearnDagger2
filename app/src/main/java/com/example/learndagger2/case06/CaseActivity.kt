package com.example.learndagger2.case06

import android.os.Bundle
import android.os.Handler
import com.example.learndagger2.BaseActivity
import com.example.learndagger2.R
import com.example.learndagger2.case06.computer.Computer
import com.example.learndagger2.case06.computer.ComputerModule
import kotlinx.android.synthetic.main.activity_case.*
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class CaseActivity : BaseActivity() {
    @field:[Inject ComputerModule.WindowsComputerQualifier]
    lateinit var windows: dagger.Lazy<Computer>
    @field:[Inject ComputerModule.LinuxComputerQualifier]
    lateinit var linux: dagger.Lazy<Computer>
    @set:Inject
    lateinit var timestamp: Provider<Date>
    private var isDestroy = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case)

        DaggerCaseActivityComponent
            .builder()
            .computerModule(ComputerModule(6666, 8888))
            .build()
            .inject(this)

        show()
        startRefresh()
    }

    private fun show() {
//        val builder = StringBuilder(timestamp.toString()).append("\n")
//        windows.execute(builder)
//        linux.execute(builder)

        val builder = StringBuilder()
        if (System.currentTimeMillis() % 2 == 0L) {
            linux.get().execute(builder)
        } else {
            windows.get().execute(builder)
        }
        text_view.text = builder.toString()
    }

    private fun startRefresh() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                val builder = StringBuilder(text_view.text)
                builder.append(timestamp.get().toString()).append("\n")
                text_view.text = builder.toString()
                if (!isDestroy) {
                    handler.postDelayed(this, 1000)
                }
            }
        }, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        isDestroy = true
    }
}
