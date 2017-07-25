package com.nice.balafm

import android.content.Context
import android.widget.Toast
import okhttp3.*


private val client = OkHttpClient()

private val JSON = MediaType.parse("application/json; charset=utf-8")

internal val HostAddress = ""

fun Context.postJsonReequest(url: String, json: String, callback: Callback) {

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
    client.newCall(request).enqueue(callback)
}
