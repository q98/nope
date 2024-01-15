package com.zho.myapplication.bot

interface ScriptActionListener {
    fun onStartScript(script: AutomationScript)
    fun onHideSettingsOverlay()
}