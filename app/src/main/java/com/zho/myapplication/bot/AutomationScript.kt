package com.zho.myapplication.bot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

interface AutomationScript {
    fun onStart()
    fun onLoop(): Long
    fun onPause()
    fun onExit()
    fun createSettingsView(inflater: LayoutInflater, container: ViewGroup?): View
    fun setScriptSettings(settings: Map<String, Any?>)
    fun setActionListener(listener: ScriptActionListener)

}

