package com.zho.myapplication.bot


class ScriptManager(private val script: AutomationScript) {
    @Volatile private var isRunning = false
    private var isPaused = false

    fun start() {
        isRunning = true
        script.onStart()
        Thread {
            while (isRunning) {
                if (!isPaused) {
                    script.onLoop()
                }
                Thread.sleep(script.onLoop())
            }
        }.start()
    }

    fun pause() {
        isPaused = true
        script.onPause()
    }

    fun resume() {
        isPaused = false
    }

    fun stop() {
        isRunning = false
        script.onExit()
    }
}
