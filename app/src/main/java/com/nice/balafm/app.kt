package com.nice.balafm

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.os.Build
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.algebra.sdk.API

private const val TAG = "app_global"

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        val result = API.init(applicationContext)
        Log.d(TAG, "API init finished ? $result")

        globalUid = PreferenceManager.getDefaultSharedPreferences(applicationContext).getInt(KEY_LAST_UID, 0)
    }
}

const val KEY_LAST_UID = "last_uid"

val accountApi
    get() = API.getAccountApi()
//    get() {
//        var api = API.getAccountApi()
//        while (api == null) {
//            Log.d(APP_TAG, "api = null")
//            api = API.getAccountApi()
//        }
//        return api
//    }

val channelApi
    get() = API.getChannelApi()

val sessionApi
    get() = API.getSessionApi()

val deviceApi
    get() = API.getDeviceApi()

val contact
    get() = accountApi.whoAmI()


fun AppCompatActivity.setStatusBarLightMode(dark: Boolean): Boolean {
    var result = false
    if (window != null) {
        val clazz = window::class.java
        try {
            var darkModeFlag = 0
            val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            darkModeFlag = field.getInt(layoutParams)
            val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
            if (dark) {
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag)//状态栏透明且黑色字体
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag)//清除黑色字体
            }
            result = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                if (dark) {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    return result
}

fun AppCompatActivity.setFillStatusBar() {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    window.statusBarColor = Color.TRANSPARENT
}

const val RESULT_SUCCESS = 0

internal var globalUid = 14
    set(value) {
        field = value
        Log.d(TAG, "uid has changed: uid = $value")
    }
