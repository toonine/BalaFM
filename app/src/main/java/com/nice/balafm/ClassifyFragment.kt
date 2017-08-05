package com.nice.balafm

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.nice.balafm.bean.Classify

class ClassifyFragment : Fragment() {

    private var mActivity : Activity? = null

    val classifyList = ArrayList<Classify>()

    private var bigClassifyIcon : List<Int> = listOf(1)

    var classifyLinearLayout: LinearLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mActivity = activity
        val mView = inflater.inflate(R.layout.fragment_classify, container, false)
        classifyLinearLayout = mView.findViewById<LinearLayout>(R.id.classify_linear_layout)
        bigClassifyIcon = listOfNotNull(R.drawable.ic_fragment_main_column_test10, R.drawable.ic_fragment_main_column_test6, R.drawable.ic_fragment_main_column_test3, R.drawable.ic_fragment_main_column_test2, R.drawable.ic_fragment_main_column_test20, R.drawable.ic_fragment_main_column_test11)
        classifyList.clear()
        bigClassifyIcon.forEach {
            classifyList.add(Classify())
        }
        addClassify(classifyList)
        return mView
    }

    fun addClassify(classifyList: List<Classify>) {

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

}
