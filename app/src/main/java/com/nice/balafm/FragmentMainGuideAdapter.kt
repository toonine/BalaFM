package com.nice.balafm

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.nice.balafm.bean.FragmentMainGuide

class FragmentMainGuideAdapter(var guides: List<FragmentMainGuide> = ArrayList()) : RecyclerView.Adapter<FragmentMainGuideAdapter.ViewHolder>() {

    private var mContext : Context? = null

    class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val pictureImageView = mView.findViewById<ImageView>(R.id.guide_picture)
        val titleTextView = mView.findViewById<TextView>(R.id.guide_classify_and_title)
        val anchorNameTextview = mView.findViewById<TextView>(R.id.guide_anchor_name)
        val onLineNumberTextView = mView.findViewById<TextView>(R.id.guide_online_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (mContext == null) {
            mContext = parent.context
        }
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_fragment_main_guide, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val guide = guides[position]
        Glide.with(mContext).load(guide.pictureUrl).into(holder.pictureImageView)
        holder.anchorNameTextview.text= guide.anchorName
        holder.onLineNumberTextView.text = guide.onLineNumber.toString()
        if (guide.classify.isNotBlank()) {
            val span = "#${guide.classify}# "
            val builder = SpannableStringBuilder(span + guide.title)
            builder.setSpan(ForegroundColorSpan(Color.parseColor("#43cce2")), 0, span.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder.titleTextView.text = builder
        } else {
            holder.titleTextView.text = guide.title
        }
        holder.mView.setOnClickListener{
            val intent = Intent(mContext, LiveActivity::class.java)
            intent.putExtra(LiveActivity.PARAM_CHANNEL_ID, guide.channelID)
            mContext?.startActivity(intent)
        }
    }

    override fun getItemCount() = guides.size
}