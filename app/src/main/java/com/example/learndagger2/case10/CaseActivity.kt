package com.example.learndagger2.case10

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.example.learndagger2.BaseActivity
import com.example.learndagger2.R
import com.example.learndagger2.application.DaggerApplicationComponent
import com.example.learndagger2.application.LearnDaggerApplication
import com.example.learndagger2.case10.computer.Computer
import com.example.learndagger2.case10.computer.ComputerModule
import com.example.learndagger2.case10.monitor.MonitorModule
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

    private lateinit var component: CaseActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case)

//        component = DaggerCaseActivityComponent
//            .builder()
//            .computerModule(ComputerModule(6666, 8888))
//            .monitorModule(MonitorModule(this, text_view))
//            .applicationComponent((applicationContext as LearnDaggerApplication).component)
//            .build()

        component = (applicationContext as LearnDaggerApplication)
            .component
            .caseActivityComponentBuilder()
            .computerModule(ComputerModule(6666, 8888))
            .monitorModule(MonitorModule(this, text_view))
            .build()

        component.inject(this)

        show()
        startRefresh()

        text_view.setOnClickListener { component.getMonitor().toastInfo() }

//        val name = (applicationContext as LearnDaggerApplication).component.getAppName()
        Toast.makeText(this, component.getAppName(), Toast.LENGTH_SHORT).show()
    }

    private fun show() {
        val computer = if (System.currentTimeMillis() % 2 == 0L) linux.get() else windows.get()
        component.getMonitor().show(computer)
    }

    private fun startRefresh() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                val date = timestamp.get()
                component.getMonitor().startRefresh(date)
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
