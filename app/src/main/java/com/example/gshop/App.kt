package com.example.gshop

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        contextOrNull = applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var contextOrNull: Context? = null
            private set

        val context: Context
            get() {
                checkNotNull(contextOrNull) { "Context is not initialized. Context cannot be accessed before Application has started" }
                return contextOrNull!!
            }

        val prefs: SharedPreferences by lazy {
            context.getSharedPreferences("main",
                Context.MODE_PRIVATE)
        }
    }
}