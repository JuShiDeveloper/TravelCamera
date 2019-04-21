package com.jushi.library.base

import android.app.Application
import android.content.Context

abstract class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = baseContext
    }

    companion object {
        lateinit var context: Context
    }
}