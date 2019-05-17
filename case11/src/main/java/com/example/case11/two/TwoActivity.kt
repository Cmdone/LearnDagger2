package com.example.case11.two

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.case11.R
import com.example.case11.VirtualData
import com.example.case11.application.ApplicationModule
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_two.*
import javax.inject.Inject

class TwoActivity : AppCompatActivity() {
    @field:[Inject TwoActivityModule.Title]
    lateinit var title: String

    @set:Inject
    @setparam:ApplicationModule.ActivityColor
    var color: Int = 0

    @field:[Inject ApplicationModule.ActivityData]
    lateinit var data: VirtualData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two)

//        (application as Case11Application)
//            .component
//            .newTwoActivityComponentBuilder()
//            .build()
//            .inject(this)
        AndroidInjection.inject(this)

        setTitle(title)
        container.setBackgroundColor(color)
        content.text = data.toString()
    }
}