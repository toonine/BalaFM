package com.nice.balafm.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import okhttp3.*


private val client = OkHttpClient()

private val JSON = MediaType.parse("application/json; charset=utf-8")

internal val HOST_ADDRESS = "http://115.159.67.95"

@JvmOverloads
fun Context.postJsonRequest(url: String, json: String, callback: Callback, okHttpClient: OkHttpClient = client) {

    fun isNetworkAvailable() = true

    if (!isNetworkAvailable()) {
        Toast.makeText(this, "网络连接不可用, 请连接网络后重试", Toast.LENGTH_SHORT).show()
        return
    }

    val body = RequestBody.create(JSON, json)
    val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
    Log.d("request_to_server", "URL: $url, JSON: $json")
    okHttpClient.newCall(request).enqueue(callback)
}
