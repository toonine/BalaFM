package com.nice.balafm

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class DiscoverFragment : Fragment() {

    private var mActivity = activity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mView = inflater.inflate(R.layout.fragment_discover, container, false)
        val skipScanQRCodeImageView = mView.findViewById<ImageView>(R.id.fragment_discover_skip_scan_qrcode)
        val skipSearchImageView = mView.findViewById<ImageView>(R.id.fragment_discover_skip_search)
        skipScanQRCodeImageView.setOnClickListener { startActivity(Intent(mActivity, ScanQRCodeActivity::class.java)) }
        skipSearchImageView.setOnClickListener { startActivity(Intent(mActivity, SearchActivity::class.java)) }
        return mView
    }
}
