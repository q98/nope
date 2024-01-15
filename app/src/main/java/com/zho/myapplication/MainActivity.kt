package com.zho.myapplication

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.zho.myapplication.bot.ScriptManager

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private val REQUEST_CODE_SCREEN_CAPTURE = 100
    private val REQUEST_CODE_OVERLAY_PERMISSION = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            startOverlayService()
            startScreenCapture()
        }
        stopButton = findViewById(R.id.stopButton)
        stopButton.setOnClickListener {
            stopServices()
        }


        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION)
        }
    }

    private fun startScreenCapture() {
        val mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_SCREEN_CAPTURE)
    }
    private fun stopServices() {
        Log.d("STOPPING", "STOPPING")
        val stopScreenCaptureIntent = Intent(this, ScreenCaptureService::class.java)
        stopService(stopScreenCaptureIntent)

        val stopOverlayIntent = Intent(this, OverlayService::class.java)
        stopService(stopOverlayIntent)

    }



    private fun startOverlayService() {
        startService(Intent(this, OverlayService::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SCREEN_CAPTURE && resultCode == Activity.RESULT_OK) {
            val intent = Intent(this, ScreenCaptureService::class.java).apply {
                putExtra("resultCode", resultCode)
                putExtra("data", data)
            }
            startService(intent)
        }
    }
}
