package com.nice.balafm

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.algebra.sdk.API
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnAccountListenerDefault {

    companion object {
        private val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarLightMode(true)
        setContentView(R.layout.activity_main)

        //测试mTabContainerView
        initTabContainerView()

        //测试扫描二维码
        skipScanQRCodeButton.setOnClickListener { startActivity(Intent(this, ScanQRCodeActivity::class.java)) }
    }

    private fun initTabContainerView() {
        val fragments = arrayOf(MainFragment(), ClassifyFragment(), DiscoverFragment(), HomeFragment())
        val textArray = resources.getStringArray(R.array.titleTextArray)
        val iconImageArray = intArrayOf(R.mipmap.ic_main_fragment, R.mipmap.ic_classify_fragment, R.mipmap.ic_discover_fragment, R.mipmap.ic_home_fragment)
        val selectedImageArray = intArrayOf(R.mipmap.ic_main_fragment_selected, R.mipmap.ic_classify_fragment_selected, R.mipmap.ic_discover_fragment_selected, R.mipmap.ic_home_fragment_selected)
        mTabContainerView.setAdapter(CustomAdapter(this, fragments, supportFragmentManager, textArray, iconImageArray, selectedImageArray))
    }

    override fun onLogout() {
        runOnUiThread {
            Toast.makeText(this, "退出登录", Toast.LENGTH_SHORT).show()
            skipLoginButton.text = "登录"
            skipLoginButton.setOnClickListener { startActivity(Intent(this@MainActivity, LoginActivity::class.java)) }
        }
    }

    override fun onStart() {
        super.onStart()
        accountApi?.setOnAccountListener(this)
        if (contact != null) {
            Log.d(TAG, "name = ${contact?.name}, id = ${contact?.id}, state = ${contact?.state}")
            skipLoginButton.text = "注销"
            skipLoginButton.setOnClickListener { logout() }
        } else {
            skipLoginButton.text = "登录"
            skipLoginButton.setOnClickListener { startActivity(Intent(this@MainActivity, LoginActivity::class.java)) }
        }
    }

    private fun logout() {
        if (contact != null && contact!!.id != 0) {
            Log.d(TAG, "执行了logout(), Uid = ${contact?.id}")
            accountApi.setOnAccountListener(this)
            accountApi.logout(contact!!.id)
            Toast.makeText(this, "退出登录", Toast.LENGTH_SHORT).show()
            skipLoginButton.text = "登录"
            skipLoginButton.setOnClickListener { startActivity(Intent(this@MainActivity, LoginActivity::class.java)) }
        }
    }

    override fun onStop() {
        super.onStop()
        accountApi?.setOnAccountListener(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        logout()
        API.leave()
    }
}
