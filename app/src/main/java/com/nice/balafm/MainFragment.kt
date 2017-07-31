package com.nice.balafm

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import cn.bingoogolapple.bgabanner.BGABanner
import com.bumptech.glide.Glide


class MainFragment : Fragment() {

    companion object {
        private val TAG = ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val carouselFigureView = view.findViewById<BGABanner>(R.id.banner_content_view)
        val skipLiveButton = view.findViewById<Button>(R.id.skipLiveButton)

        carouselFigureView.setAdapter { _, itemView, model, _ ->
            Glide.with(this@MainFragment)
                    .load(model as String)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .dontAnimate()
                    .into(itemView as ImageView)
        }
        carouselFigureView.setData(listOf("http://pic.58pic.com/58pic/13/56/99/88f58PICuBh_1024.jpg", "http://pic2.ooopic.com/12/01/38/01bOOOPIC1a_1024.jpg", "http://pic3.16pic.com/00/05/61/16pic_561410_b.jpg"), null)
        carouselFigureView.setDelegate { banner, _, _, position -> Toast.makeText(banner.context, "点击了第${position + 1}个图", Toast.LENGTH_SHORT).show() }

        //测试直播页面
        skipLiveButton.setOnClickListener { LiveActivity.actionStart(activity, 1066) }

        return view
    }
}
