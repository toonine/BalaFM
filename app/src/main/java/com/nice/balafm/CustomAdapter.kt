package com.nice.balafm

import android.content.Context
import android.graphics.Color
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.fe.library.adapter.BaseAdapter
import com.fe.library.widget.AbsTab

class CustomAdapter(context: Context, fragmentArray: Array<Fragment>, fragmentManager: FragmentManager, private val mTextArray: Array<String>, private val mIconImageArray: IntArray, private val mSelectedIconImageArray: IntArray, private val mSelectedTextColor: Int = Color.parseColor("#f028b1f5")) : BaseAdapter(context, fragmentArray, fragmentManager) {
    private val mTextColor = Color.parseColor("#ce848383")

    override fun getTab(index: Int): AbsTab {
        val customTab = CustomTab(this.mContext, index)
        customTab.setText(this.mTextArray[index])
        customTab.setTextColor(this.mTextColor, this.mSelectedTextColor)
        customTab.setIconImage(this.mIconImageArray[index], this.mSelectedIconImageArray[index])
        return customTab
    }
}