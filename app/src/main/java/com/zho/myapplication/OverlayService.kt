package com.zho.myapplication

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.zho.myapplication.bot.AutomationScript
import com.zho.myapplication.bot.ScriptActionListener
import com.zho.myapplication.bot.ScriptManager
import com.zho.myapplication.bot.scripts.TreeChopper

class OverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private lateinit var settingsOverlayView: View
    private var scriptManager: ScriptManager? = null
    private var isScriptRunning = false
    private var settingsOverlayShown = false

    private val scriptActionListener = object : ScriptActionListener {
        override fun onStartScript(script: AutomationScript) {
            scriptManager = ScriptManager(script).apply { start() }
            updateButtonState(true)
        }

        override fun onHideSettingsOverlay() {
            if (settingsOverlayShown) {
                windowManager.removeView(settingsOverlayView)
                settingsOverlayShown = false
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        setupOverlays()
        setupStartStopButton()
    }

    private fun setupOverlays() {
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)
        settingsOverlayView =
            LayoutInflater.from(this).inflate(R.layout.script_settings_overlay_layout, null)

        val overlayParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        windowManager.addView(overlayView, overlayParams)
    }

    private fun setupStartStopButton() {
        val scriptSpinner: Spinner = overlayView.findViewById(R.id.scriptSpinner)
        val startStopButton: Button = overlayView.findViewById(R.id.startStopScriptButton)

        val scripts = listOf(TreeChopper()) // Add other scripts here
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            scripts.map { it.toString() })
        scriptSpinner.adapter = adapter

        startStopButton.setOnClickListener {
            val selectedScript = scripts[scriptSpinner.selectedItemPosition] as AutomationScript

            if (!isScriptRunning) {
                selectedScript.setActionListener(scriptActionListener)
                showSettingsOverlay(selectedScript)
            } else {
                stopScript()
            }
        }



        fun onDestroy() {
            super.onDestroy()
            if (::overlayView.isInitialized) {
                windowManager.removeView(overlayView)
            }
            if (::settingsOverlayView.isInitialized && settingsOverlayShown) {
                windowManager.removeView(settingsOverlayView)
            }
        }
    }

    fun stopScript() {
        scriptManager?.stop()
        updateButtonState(false)
    }

    private fun updateButtonState(isRunning: Boolean) {
        val startStopButton: Button = overlayView.findViewById(R.id.startStopScriptButton)
        startStopButton.text = if (isRunning) "Stop" else "Start"
        isScriptRunning = isRunning
    }

    private fun showSettingsOverlay(selectedScript: AutomationScript) {
        val settingsView = selectedScript.createSettingsView(LayoutInflater.from(this), null)
        (settingsOverlayView as ViewGroup).removeAllViews()
        (settingsOverlayView as ViewGroup).addView(settingsView)

        if (!settingsOverlayShown) {
            val settingsParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.CENTER
            }

            windowManager.addView(settingsOverlayView, settingsParams)
            settingsOverlayShown = true
        }
    }
}


