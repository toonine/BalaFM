package com.nice.balafm

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import cn.bingoogolapple.qrcode.core.QRCodeView
import kotlinx.android.synthetic.main.activity_scan_qrcode.*

class ScanQRCodeActivity : AppCompatActivity(), QRCodeView.Delegate {

    private var isOpenFlashLight: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qrcode)
        setStatusBarLightMode(true)
        mQRCodeView.setDelegate(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.scanqrcode_menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.scanqrcode_menu_openflashlight -> if (!isOpenFlashLight) {
                mQRCodeView!!.openFlashlight()
                isOpenFlashLight = true
            } else {
                mQRCodeView!!.closeFlashlight()
                isOpenFlashLight = false
            }
            android.R.id.home -> finish()
        }
        return true
    }

    //手机振动
    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200)
    }

    override fun onScanQRCodeSuccess(result: String) {
        vibrate()//振动
        //延迟1秒
        mQRCodeView.startSpotDelay(1000)
        Log.d(TAG, "onScanQRCodeSuccess: ")
        //扫描二维码的逻辑
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
//            skipToResultActivity(result)
    }

//    private fun skipToResultActivity(result: String) {
//        mQRCodeView.stopSpot()
//        ResultActivity.actionStart(this@ScanQRCodeActivity, result)
//    }

    override fun onScanQRCodeOpenCameraError() {
        Toast.makeText(this, "打开摄像头异常", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onStart() {
        super.onStart()
        mQRCodeView.startCamera()
        mQRCodeView.showScanRect()
        mQRCodeView.startSpot()//开始扫描
    }

    override fun onStop() {
        mQRCodeView.stopSpot()
        mQRCodeView.stopCamera()
        super.onStop()
    }

    override fun onDestroy() {
        mQRCodeView.onDestroy()
        super.onDestroy()
    }

    companion object {
        private val TAG = "ScanQRCodeActivity"
    }
}
