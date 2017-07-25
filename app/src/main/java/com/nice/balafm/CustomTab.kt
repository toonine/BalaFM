package com.nice.balafm

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.fe.library.widget.AbsTab
import com.fe.library.widget.MessageCircle

class CustomTab(context: Context, index: Int) : AbsTab(context, index) {
    private var mIvIcon: ImageView? = null
    private var mTvText: TextView? = null
    private var mMessageCircleTip: MessageCircle? = null
    private var mTextColor: Int = 0
    private var mSelectedTextColor: Int = 0
    private var mIconImage: Int = 0
    private var mSelectedIconImage: Int = 0

    init {
        this.inflaterView(this, R.layout.tab_default)
    }

    public override fun tabSelected(isSelected: Boolean) {
        if (this.mIsSelected != isSelected) {
            this.mIvIcon!!.setImageResource(if (isSelected) this.mSelectedIconImage else this.mIconImage)
            this.mTvText!!.setTextColor(if (isSelected) this.mSelectedTextColor else this.mTextColor)
            this.mIsSelected = isSelected
        }
    }

    override fun showMessageTip(show: Boolean, count: Int) {
        this.mMessageCircleTip!!.visibility = if (show) 0 else 8
        if (count == -1) {
            this.mMessageCircleTip!!.setText("")
        } else {
            this.mMessageCircleTip!!.setText(if (count >= 1000) "999+" else count.toString() + "")
        }

    }

    override fun initView(rootView: View) {
        this.mIvIcon = rootView.findViewById<View>(R.id.iv_icon) as ImageView
        this.mTvText = rootView.findViewById<View>(R.id.tv_title) as TextView
        this.mMessageCircleTip = rootView.findViewById<View>(R.id.mc_circle) as MessageCircle
    }

    fun setTextColor(textColor: Int, selectedTextColor: Int) {
        this.mTextColor = textColor
        this.mSelectedTextColor = selectedTextColor
        this.mTvText!!.setTextColor(this.mTextColor)
    }

    fun setText(text: String) {
        this.mTvText!!.text = text
    }

    fun setIconImage(iconImage: Int, selectedIconImage: Int) {
        this.mSelectedIconImage = selectedIconImage
        this.mIconImage = iconImage
        this.mIvIcon!!.setImageResource(iconImage)
    }
}