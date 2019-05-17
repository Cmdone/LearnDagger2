package com.example.case11.one

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.case11.R
import com.example.case11.VirtualData
import com.example.case11.application.ApplicationModule
import com.example.case11.application.Case11Application
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_one.*
import javax.inject.Inject

class OneActivity : DaggerAppCompatActivity() {
    @field:[Inject OneActivityModule.Title]
    lateinit var title: String

    @set:Inject
    @setparam:ApplicationModule.ActivityColor
    var color: Int = 0

    @field:[Inject ApplicationModule.ActivityData]
    lateinit var data: VirtualData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one)

//        (application as Case11Application)
//            .component
//            .newOneActivityComponentBuilder()
//            .build()
//            .inject(this)
//        AndroidInjection.inject(this)

        setTitle(title)
        container.setBackgroundColor(color)
        content.text = data.toString()
    }
}