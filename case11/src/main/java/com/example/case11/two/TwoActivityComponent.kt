/*
 * Copyright (C) 2019, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * OneActivityComponent.java
 *
 * Description
 *
 * Author Cheng Mingde
 *
 * Ver 1.0, 2019-05-16, Cheng Mingde, Create file
 */
package com.example.case11.two

import dagger.Subcomponent
import dagger.android.AndroidInjector

//@Subcomponent(modules = [TwoActivityModule::class])
//interface TwoActivityComponent {
//    fun inject(target: TwoActivity)
//
//    @Subcomponent.Builder
//    interface Builder {
//        fun build(): TwoActivityComponent
//    }
//}

@Subcomponent(modules = [TwoActivityModule::class])
interface TwoActivityComponent : AndroidInjector<TwoActivity> {
    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<TwoActivity>
}