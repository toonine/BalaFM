package com.nice.balafm

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.nice.balafm.bean.LiveMessage
import com.nice.balafm.bean.Type
import de.hdodenhof.circleimageview.CircleImageView


class LiveMessageAdapter(private val messageList: List<LiveMessage>) : RecyclerView.Adapter<LiveMessageAdapter.ViewHolder>() {

    private var mContext: Context? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val meMessageLayout = view.findViewById<RelativeLayout>(R.id.me_message_layout)!!
        val otherMessageLayout = view.findViewById<RelativeLayout>(R.id.other_message_layout)!!
        val meMessageTextView = view.findViewById<TextView>(R.id.me_message_text_view)!!
        val otherMessageTextView = view.findViewById<TextView>(R.id.other_message_text_view)!!
        val meNickNameTextView = view.findViewById<TextView>(R.id.me_nick_name)!!
        val otherNickName = view.findViewById<TextView>(R.id.other_nick_name)!!
        val meHeadPicImage = view.findViewById<CircleImageView>(R.id.me_head_pic)!!
        val otherHeadPicImage = view.findViewById<CircleImageView>(R.id.other_head_pic)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (mContext == null) {
            mContext = parent.context
        }
        val view = LayoutInflater.from(mContext).inflate(R.layout.live_message_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val liveMessage = messageList[position]
        when (liveMessage.type) {
            Type.SENT -> {
                holder.meMessageLayout.visibility = View.VISIBLE
                holder.otherMessageLayout.visibility = View.GONE
                Glide.with(mContext).load(liveMessage.heedPic).placeholder(R.drawable.ic_user_default_head).error(R.drawable.ic_user_default_head).into(holder.meHeadPicImage)
                holder.meNickNameTextView.text = liveMessage.nickName
                holder.meMessageTextView.text = liveMessage.content
            }

            Type.RECEIVED -> {
                holder.otherMessageLayout.visibility = View.VISIBLE
                holder.meMessageLayout.visibility = View.GONE
                Glide.with(mContext).load(liveMessage.heedPic).placeholder(R.drawable.ic_user_default_head).error(R.drawable.ic_user_default_head).into(holder.otherHeadPicImage)
                holder.otherNickName.text = liveMessage.nickName
                holder.otherMessageTextView.text = liveMessage.content
            }
        }
    }

    override fun getItemCount() = messageList.size
}
