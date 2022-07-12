package com.example.gshop.model

import androidx.core.content.edit
import com.example.gshop.App

private const val STATE_KEY = "state"

object Database {
    fun writeJsonState(json: String) {
        App.prefs.edit { putString(STATE_KEY, json) }
    }

    fun readJsonState(): String? {
        return App.prefs.getString(STATE_KEY, null)
    }
}