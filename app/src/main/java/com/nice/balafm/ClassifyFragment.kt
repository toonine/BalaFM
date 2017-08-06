package com.nice.balafm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nice.balafm.bean.Classify
import com.nice.balafm.util.HOST_ADDRESS
import com.nice.balafm.util.isGoodJson
import com.nice.balafm.util.postJsonRequest
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ClassifyFragment : Fragment() {

    private var mActivity : Activity? = null

    private var classifyList: List<Classify> = listOfNotNull()

    private var bigClassifyIcon: List<Int> = listOfNotNull()

    var classifyLinearLayout: LinearLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mActivity = activity
        val mView = inflater.inflate(R.layout.fragment_classify, container, false)
        classifyLinearLayout = mView.findViewById<LinearLayout>(R.id.classify_linear_layout)
        val skipScanQRCodeImageView = mView.findViewById<ImageView>(R.id.fragment_classify_skip_scan_qrcode)
        val skipSearchImageView = mView.findViewById<ImageView>(R.id.fragment_classify_skip_search)
        skipScanQRCodeImageView.setOnClickListener { startActivity(Intent(mActivity, ScanQRCodeActivity::class.java)) }
        skipSearchImageView.setOnClickListener { startActivity(Intent(mActivity, SearchActivity::class.java)) }
        bigClassifyIcon = listOfNotNull(R.drawable.ic_fragment_main_column_test10, R.drawable.ic_fragment_main_column_test6, R.drawable.ic_fragment_main_column_test3, R.drawable.ic_fragment_main_column_test2, R.drawable.ic_fragment_main_column_test20, R.drawable.ic_fragment_main_column_test11)
        getClassifyListFromSever()
        return mView
    }

    private fun getClassifyListFromSever() {
        mActivity?.postJsonRequest(URL_GET_CLASSIFY, "", object : Callback {
            override fun onResponse(call: Call?, response: Response) {
                val json = response.body()!!.string()
                if (isGoodJson(json)) {
                    classifyList = Gson().fromJson(JSONObject(json).getString("classifyArr"), object : TypeToken<List<Classify>>() {}.type)
                    mActivity?.runOnUiThread { addClassify(classifyList) }
                } else {
                    showRequestError("获取分类列表失败")
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                showRequestError("获取分类列表失败")
            }

        })
    }

    private fun showRequestError(message: String) {
        mActivity?.runOnUiThread {
            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addClassify(classifyList: List<Classify>) {

        fun addSmallClassify(gridLayout: GridLayout, smallClassifyList: List<Classify>) {
            smallClassifyList.forEach {
                val view = LayoutInflater.from(mActivity).inflate(R.layout.item_small_classify, gridLayout, false)
                val textView = view.findViewById<TextView>(R.id.small_classify_text_view)
                textView.text = it.classify
                gridLayout.addView(view)
            }
        }

        for ((index, classify) in classifyList.withIndex()) {
            val view = LayoutInflater.from(mActivity).inflate(R.layout.item_whole_classify, classifyLinearLayout, false)
            val bigClassifyImageView = view.findViewById<ImageView>(R.id.big_classify_icon_image_view)
            val bigClassifyText = view.findViewById<TextView>(R.id.big_classify_text_view)
            val smallClassifyGridLayout = view.findViewById<GridLayout>(R.id.small_classify_grid_layout)

            Glide.with(this).load(bigClassifyIcon[index]).into(bigClassifyImageView)
            bigClassifyText.text = classify.classify
            addSmallClassify(smallClassifyGridLayout, classify.children)
            classifyLinearLayout?.addView(view)
        }
    }

    private companion object {
        private val TAG = "ClassifyFragment"
        internal val URL_GET_CLASSIFY = HOST_ADDRESS + "/classify"
    }
}
