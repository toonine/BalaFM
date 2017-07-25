package com.nice.balafm

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.algebra.sdk.API
import com.algebra.sdk.entity.Channel
import kotlinx.android.synthetic.main.activity_live.*

class LiveActivity : AppCompatActivity(), OnSessionListenerDefault, OnChannelListenerDefault, OnAccountListenerDefault, OnMediaListenerDefault {

    companion object {
        private val TAG = "LiveActivity"
    }

    var isTalking = false

    var channel: Channel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFillStatusBar()
        setContentView(R.layout.activity_live)
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        accountApi.setOnAccountListener(this)
        sessionApi.setOnSessionListener(this)
        sessionApi.setOnMediaListener(this)
        channelApi.setOnChannelListener(this)
        //confirm用户
        confirmUser()

        showUserInfo()

        enterChannelById(1066)
    }

    private fun enterChannelById(channelId: Int) {

    }

    private fun showUserInfo() {
        Log.d(TAG, "showUserInfo: uid = ${contact?.id}, result = ${contact?.name}, isPresent = ${contact?.isPresent}")
        if (!(contact?.visitor ?: true)) {
        }
        channelApi.channelListGet(5513)
    }

    private fun confirmUser() {
        //未登录
        if (contact == null) {
            accountApi.createVisitor(API.getDeviceID(this), "test_12345")
        }
    }

    override fun onChannelListGet(uid: Int, dfltChannel: Channel?, chs: MutableList<Channel>?) {
        Log.d(TAG, "uid = $uid, channel.info = ${chs?.joinToString(", ", "[", "]") { "${it.cid.id}-${it.name}" }}")
        channel = chs!![0]
        sessionApi.sessionCall(contact!!.id, channel!!.cid.type, channel!!.cid.id)
        runOnUiThread {
            talkButton.visibility = View.VISIBLE
            talkButton.setOnClickListener {
                if (isTalking) {
                    Log.d(TAG, "结束讲话")
                    sessionApi.talkRelease(contact!!.id, channel!!.cid.type, channel!!.cid.id)
                    isTalking = false
                    talkButton.text = "开始讲话"
                } else {
                    Log.d(TAG, "开始讲话")
                    sessionApi.talkRequest(contact!!.id, channel!!.cid.type, channel!!.cid.id)
                    isTalking = true
                    talkButton.text = "结束讲话"
                }
            }
        }
    }


    override fun onCreateUser(uid: Int, result: Int, account: String?) {
        Log.d(TAG, "onCreateUser: uid = $uid, result = $result, account = $account, contact = $contact")
    }

    override fun onSessionEstablished(selfUserId: Int, type: Int, sessionId: Int) {
        Log.d(TAG, "会话连接成功。type = $type, sessionId = $sessionId")
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionApi.sessionBye(contact!!.id, channel!!.cid.type, channel!!.cid.id)
    }
}
