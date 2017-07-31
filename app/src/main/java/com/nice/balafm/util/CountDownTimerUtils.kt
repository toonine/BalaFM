package com.nice.balafm.util

import android.graphics.Color
import android.os.CountDownTimer
import android.widget.Button


//获取验证码工具类
internal class CountDownTimerUtils(private val mButton: Button, millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

    override fun onTick(millisUntilFinished: Long) {
        mButton.isClickable = false //设置不可点击
        mButton.text = (millisUntilFinished / 1000).toString() + "s后重发"  //设置倒计时时间
        mButton.setBackgroundColor(Color.parseColor("#b8b8b8")) //设置按钮为灰色，这时是不能点击的

        /**
         * 超链接 URLSpan
         * 文字背景颜色 BackgroundColorSpan
         * 文字颜色 ForegroundColorSpan
         * 字体大小 AbsoluteSizeSpan
         * 粗体、斜体 StyleSpan
         * 删除线 StrikethroughSpan
         * 下划线 UnderlineSpan
         * 图片 ImageSpan
         * http://blog.csdn.net/ah200614435/article/details/7914459
         */
//        val spannableString = SpannableString(mButton.text.toString())  //获取按钮上的文字
//        val span = ForegroundColorSpan(Color.RED)
        /**
         * public void setSpan(Object what, int start, int end, int flags) {
         * 主要是start跟end，start是起始位置,无论中英文，都算一个。
         * 从0开始计算起。end是结束位置，所以处理的文字，包含开始位置，但不包含结束位置。
         */
//        spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)//将倒计时的时间设置为红色
//        mButton.text = spannableString
    }

    override fun onFinish() {
        mButton.text = "获取验证码"
        mButton.isClickable = true//重新获得点击
        mButton.setBackgroundColor(Color.parseColor("#ff8922"))  //还原背景色
    }
}