package com.nice.balafm

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.algebra.sdk.API
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarLightMode(true)
        setContentView(R.layout.activity_main)

        //测试mTabContainerView
        initTabContainerView()

    }

    private fun initTabContainerView() {
        val fragments = arrayOf(MainFragment(), ClassifyFragment(), DiscoverFragment(), PersonFragment())
        val textArray = resources.getStringArray(R.array.titleTextArray)
        val iconImageArray = intArrayOf(R.mipmap.ic_main_fragment, R.mipmap.ic_classify_fragment, R.mipmap.ic_discover_fragment, R.mipmap.ic_home_fragment)
        val selectedImageArray = intArrayOf(R.mipmap.ic_main_fragment_selected, R.mipmap.ic_classify_fragment_selected, R.mipmap.ic_discover_fragment_selected, R.mipmap.ic_home_fragment_selected)
        mTabContainerView.setAdapter(CustomAdapter(this, fragments, supportFragmentManager, textArray, iconImageArray, selectedImageArray))
    }


    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(KEY_LAST_UID, globalUid).apply()
        API.leave()
    }
}
