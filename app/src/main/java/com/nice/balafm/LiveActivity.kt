package com.nice.balafm

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import com.nice.balafm.util.HOST_ADDRESS
import io.socket.client.IO
import kotlinx.android.synthetic.main.activity_live.*


class LiveActivity : AppCompatActivity(), OnSessionListenerDefault, OnChannelListenerDefault, OnAccountListenerDefault, OnMediaListenerDefault {

    private var channelId = 0

    private val TLUid = 0

    private val mProgressDialog by lazy { ProgressDialog(this) }

    private val mSocket = IO.socket(HOST_ADDRESS)

    private val liveMessageList = ArrayList<LiveMessage>()

    private val liveMessageAdapter by lazy { LiveMessageAdapter(this, liveMessageList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFillStatusBar()

        channelId = intent.getIntExtra(CHANNEL_ID, 0)
        if (channelId == 0) {
            Log.e(TAG, "未获取到频道Id")
            finish()
        }

        mSocket.connect()

        setContentView(R.layout.activity_live)


//        getLiveInfoFromSever()

        initView()
//        enterChannelById(1066)
    }


    private fun initView() {
        backToMainImageView.setOnClickListener { finish() }

        //设置hint
        setHintTextSize(sendMessageEditText, "  说点什么吧 。。。", 12)

        //监听键盘弹出和隐藏
        LiveActivityLinearLayout.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            LiveActivityLinearLayout.getWindowVisibleDisplayFrame(rect)
            //键盘高度
            val invisibleHeight = LiveActivityLinearLayout.rootView.height - rect.bottom

            if (invisibleHeight > 100) {
                toggleSoftInputLinearLayout.visibility = View.GONE
                sendMessageLinearLayout.visibility = View.VISIBLE
                sendMessageEditText.requestFocus()
            } else {
                sendMessageLinearLayout.visibility = View.GONE
                toggleSoftInputLinearLayout.visibility = View.VISIBLE
            }
        }

        liveMessageRecycleView.layoutManager = LinearLayoutManager(this)
        liveMessageRecycleView.adapter = liveMessageAdapter

        sendMessageEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                if (p0.isEmpty()) {
                    sendMessageButton.isClickable = false
                    sendMessageButton.background = getDrawable(R.drawable.bg_send_message_button_disabled)
                    sendMessageButton.setTextColor(Color.parseColor("#DEDEDE"))
                } else {
                    sendMessageButton.isClickable = true
                    sendMessageButton.background = getDrawable(R.drawable.bg_send_message_button_enabled)
                    sendMessageButton.setTextColor(Color.parseColor("#393737"))
                }
            }

            override fun afterTextChanged(p0: Editable?) { }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        })

        sendMessageButton.setOnClickListener {
            
        }
    }

    // 单独设置EditText控件中hint文本的字体大小，可能与EditText文字大小不同
    // @param editText 输入控件
    // @param hintText hint的文本内容
    // @param textSize hint的文本的文字大小（以dp为单位设置即可）
    private fun setHintTextSize(editText: EditText, hintText: String, textSize: Int) {
        // 新建一个可以添加属性的文本对象
        val ss = SpannableString(hintText)
        // 新建一个属性对象,设置文字的大小
        val ass = AbsoluteSizeSpan(textSize, true)
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // 设置hint
        editText.hint = SpannedString(ss) // 一定要进行转换,否则属性会消失
    }

    override fun onStart() {
        super.onStart()
        sessionApi.setOnMediaListener(this)
        sessionApi.setOnSessionListener(this)
        accountApi.setOnAccountListener(this)
        channelApi.setOnChannelListener(this)
    }

    override fun onStop() {
        super.onStop()
        sessionApi.setOnMediaListener(null)
        sessionApi.setOnSessionListener(null)
        accountApi.setOnAccountListener(null)
        channelApi.setOnChannelListener(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
//        mSocket.off("new message", onNewMessage)
    }
//
//    private fun enterChannelById(channelId: Int) {
//
//    }
//
//    private fun showUserInfo() {
//        Log.d(TAG, "showUserInfo: uid = ${contact?.id}, result = ${contact?.name}, isPresent = ${contact?.isPresent}")
//        if (!(contact?.visitor ?: true)) {
//        }
//        channelApi.channelListGet(5513)
//    }
//
//    private fun confirmUser() {
//        //未登录
//        if (contact == null) {
//            accountApi.createVisitor(API.getDeviceID(this), "test_12345")
//        }
//    }
//
//    override fun onChannelListGet(uid: Int, dfltChannel: Channel?, chs: MutableList<Channel>?) {
//        Log.d(TAG, "uid = $uid, channel.info = ${chs?.joinToString(", ", "[", "]") { "${it.cid.id}-${it.name}" }}")
//        channel = chs!![0]
//        sessionApi.sessionCall(contact!!.id, channel!!.cid.type, channel!!.cid.id)
//        runOnUiThread {
//            talkButton.visibility = View.VISIBLE
//            talkButton.setOnClickListener {
//                if (isTalking) {
//                    Log.d(TAG, "结束讲话")
//                    sessionApi.talkRelease(contact!!.id, channel!!.cid.type, channel!!.cid.id)
//                    isTalking = false
//                    talkButton.text = "开始讲话"
//                } else {
//                    Log.d(TAG, "开始讲话")
//                    sessionApi.talkRequest(contact!!.id, channel!!.cid.type, channel!!.cid.id)
//                    isTalking = true
//                    talkButton.text = "结束讲话"
//                }
//            }
//        }
//    }
//
//
//    override fun onCreateUser(uid: Int, result: Int, account: String?) {
//        Log.d(TAG, "onCreateUser: uid = $uid, result = $result, account = $account, contact = $contact")
//    }
//
//    override fun onSessionEstablished(selfUserId: Int, type: Int, sessionId: Int) {
//        Log.d(TAG, "会话连接成功。type = $type, sessionId = $sessionId")
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        sessionApi.sessionBye(contact!!.id, channel!!.cid.type, channel!!.cid.id)
//    }

    internal companion object {
        private const val TAG = "LiveActivity"

        private const val CHANNEL_ID = "channel_id"

        private const val CHANNEL_TYPE = 2

        internal fun actionStart(context: Context, channelId: Int) {
            val intent = Intent(context, LiveActivity::class.java)
            intent.putExtra(CHANNEL_ID, channelId)
            context.startActivity(intent)
        }

        enum class UserType {
            VISITOR, AUDIENCE, ANCHOR
        }

        private val URL_LIVE_INFO = HOST_ADDRESS + ""

        private class LiveInfo {
            private val userType = ""
            private val isStar = false
            private val isStore = true
        }

        private class AnchorInfo
    }
}
