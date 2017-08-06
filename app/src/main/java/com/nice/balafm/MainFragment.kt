package com.nice.balafm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import cn.bingoogolapple.bgabanner.BGABanner
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nice.balafm.bean.FragmentMainProgram
import com.nice.balafm.util.HOST_ADDRESS
import com.nice.balafm.util.isGoodJson
import com.nice.balafm.util.postJsonRequest
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


class MainFragment : Fragment() {

    private var mActivity : Activity ?= null

    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    private val COLUMN_NAME_TO_VIEW = HashMap<String, RecyclerView>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mView = inflater.inflate(R.layout.fragment_main, container, false)

        initView(mView)

        refreshProgramList()
        return mView
    }

    private fun initView(view: View) {
        mActivity = activity
        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.fragment_main_swipe_refresh_layout)
        val carouselFigureView = view.findViewById<BGABanner>(R.id.banner_content_view)
        for ((name, id) in COLUMN_NAME_TO_ID) {
            val recycleView = view.findViewById<RecyclerView>(id)

            recycleView.layoutManager = object : GridLayoutManager(mActivity, 2) {
                override fun canScrollHorizontally() = false
                override fun canScrollVertically() = false
            }

            recycleView.adapter = FragmentMainGuideAdapter()
            COLUMN_NAME_TO_VIEW.put(name, recycleView)
        }

        carouselFigureView.setAdapter { _, itemView, model, _ ->
            Glide.with(this@MainFragment)
                    .load(model as String)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .dontAnimate()
                    .into(itemView as ImageView)
        }
        carouselFigureView.setData(listOf("http://fdfs.xmcdn.com/group31/M0B/AD/15/wKgJX1l5YT_irLynAAMMvfwVZ1A073.jpg", "http://pic.qingting.fm/goods/2017/08/02/90476ccb5c5b044c7d12c18c17dbe562.jpg!800", "http://fdfs.xmcdn.com/group30/M07/56/92/wKgJXll5ZKKAXFBSABYlZr-9aPw826.jpg"), null)
        carouselFigureView.setDelegate { banner, _, _, position -> Toast.makeText(banner.context, "点击了第${position + 1}个图", Toast.LENGTH_SHORT).show() }

        val skipScanQRCodeImageView = view.findViewById<ImageView>(R.id.fragment_main_skip_scan_qrcode)

        skipScanQRCodeImageView.setOnClickListener {
            mActivity?.startActivity(Intent(mActivity, ScanQRCodeActivity::class.java))
        }

        val skipSearchLinnerLayout = view.findViewById<LinearLayout>(R.id.fragment_main_skip_search)
        skipSearchLinnerLayout.setOnClickListener { startActivity(Intent(mActivity, SearchActivity::class.java)) }

        swipeRefreshLayout?.setColorSchemeResources(R.color.swipeRefreshLayoutScheme)
        swipeRefreshLayout?.setOnRefreshListener {
            refreshProgramList()
        }

    }

    private fun refreshProgramList() {
        activity.postJsonRequest(URL_GET_PROGRAM_LIST, "", object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val json = response.body()!!.string()
                if (isGoodJson(json)) {
                    val dataList = Gson().fromJson<List<FragmentMainProgram>>(json, object : TypeToken<List<FragmentMainProgram>>() {}.type)
                    mActivity?.runOnUiThread {
                        Log.d(TAG, "dataList: ${dataList.joinToString (separator = ", "){ it.columnName }}")
                        dataList.forEach {
                            val guideAdapter = COLUMN_NAME_TO_VIEW[it.columnName]!!.adapter as FragmentMainGuideAdapter
                            guideAdapter.guides = it.programList.filter { it != null }
                            guideAdapter.notifyDataSetChanged()
                            swipeRefreshLayout?.isRefreshing = false
                        }
                    }
                } else {
                    showRequestError("服务器内部错误")
                }
            }

            override fun onFailure(call: Call, e: IOException){
                showRequestError("网络请求出错")
            }
        })
    }

    private fun showRequestError(message: String) {
        mActivity?.runOnUiThread {
            swipeRefreshLayout!!.isRefreshing = false
            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private val TAG = "MainFragment"
        private val URL_GET_PROGRAM_LIST = HOST_ADDRESS + "/index"

        private val COLUMN_NAME_TO_ID = mapOf("猜你喜欢" to R.id.recycle_view_guess_you_like, "正在热播" to R.id.recycle_view_hot_hit, "学霸世界" to R.id.recycle_view_scholar_world, "校花校草" to R.id.recycle_view_campus_people)
    }
}
