package com.nice.balafm.util

import android.text.TextUtils
import android.util.Log
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import org.json.JSONObject

internal fun <V> mapToJson(map: Map<String, V>): String {
    val jsonObject = JSONObject()
    for ((key, value) in map) {
        jsonObject.put(key, value)
    }
    val json = jsonObject.toString()
    return json
}

internal fun <V> mapToJson(key: String, value: V): String {
    val json = JSONObject().put(key, value).toString()
    return json
}

internal fun isGoodJson(json: String): Boolean {
    if (TextUtils.isEmpty(json)) {
        return false
    }
    try {
        JsonParser().parse(json)
        Log.d("response_json", "json: " + json)
        return true
    } catch (e: JsonParseException) {
        Log.d("bad_json", "bad json: " + json)
        return false
    }
}
