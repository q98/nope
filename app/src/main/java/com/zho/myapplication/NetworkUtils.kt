package com.zho.myapplication

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import java.io.IOException

object NetworkUtils {

    private val client = OkHttpClient()

    fun sendImageToServer(imageBytes: ByteArray, onResponse: (List<Detection>) -> Unit) {
        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageBytes)
        val request = Request.Builder()
            .url("http://<your_python_backend_url>/detect")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { resp ->
                    val responseBody = resp.body?.string() // Get the response body as a string
                    if (responseBody != null) {
                        val detections = parseDetections(responseBody)
                        onResponse(detections)
                    }
                }
            }
        })
    }



    private fun parseDetections(jsonResponse: String): List<Detection> {
        val detections = mutableListOf<Detection>()
        val jsonArray = JSONArray(jsonResponse)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val x = jsonObject.getInt("x")
            val y = jsonObject.getInt("y")
            val width = jsonObject.getInt("width")
            val height = jsonObject.getInt("height")
            val confidence = jsonObject.getDouble("confidence").toFloat()
            detections.add(Detection(x, y, width, height, confidence))
        }
        return detections
    }
}

data class Detection(val x: Int, val y: Int, val width: Int, val height: Int, val confidence: Float)
