package com.nice.balafm

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.algebra.sdk.API
import com.algebra.sdk.entity.UserProfile
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.nice.balafm.bean.LiveMessage
import com.nice.balafm.bean.Type
import com.nice.balafm.util.HOST_ADDRESS
import com.nice.balafm.util.getFormatTime
import com.nice.balafm.util.isGoodJson
import com.nice.balafm.util.mapToJson
import io.socket.client.IO
import kotlinx.android.synthetic.main.activity_live.*
import org.json.JSONObject
import java.util.*

/**
 *  channelId 要改
 *  3、globalUid 要改
 */

class LiveActivity : AppCompatActivity(), OnSessionListenerDefault, OnChannelListenerDefault, OnAccountListenerDefault, OnMediaListenerDefault {

    private var channelId = 0

    private val mConnectSocketProgressDialog by lazy { ProgressDialog(this) }

    private val mConnectSessionProgressDialog by lazy { ProgressDialog(this) }

    private val mSocket = IO.socket(HOST_ADDRESS)

    private val liveMessageList = ArrayList<LiveMessage>()

    private val liveMessageAdapter = LiveMessageAdapter(liveMessageList)

    private var userInfo = UserInfo()

    private var anchorInfo = AnchorInfo()

    private var liveInfo = LiveInfo()

    private val mHandler = Handler()

    private var isEnterSession = false

    private val apiIsNull
        get() = accountApi == null || sessionApi == null || channelApi == null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFillStatusBar()

        channelId = intent.getIntExtra(PARAM_CHANNEL_ID, 0)
        if (channelId == 0) {
            Log.e(TAG, "未获取到频道Id")
            finish()
        }
        channelId = 1066
        setContentView(R.layout.activity_live)

        mConnectSessionProgressDialog.setTitle("")
        mConnectSessionProgressDialog.setMessage("加入语音频道中...")
        mConnectSessionProgressDialog.show()

        if (apiIsNull) {
            mHandler.postDelayed(object : Runnable {
                override fun run() {
                    if (apiIsNull) {
                        API.init(this@LiveActivity)
                        mHandler.postDelayed(this, 300)
                    } else
                        userTLApi()
                }
            }, 300)
        } else
            userTLApi()


        initSocketAndGetLiveInfo()

        initView()
//        getLiveInfoFromSever()
//        enterChannelById(1066)
    }

    private fun userTLApi() {
        accountApi.setOnAccountListener(this)
        channelApi.setOnChannelListener(this)
        sessionApi.setOnMediaListener(this)
        sessionApi.setOnSessionListener(this)

        if (contact == null) {
            Log.d(TAG, "login() : 开始登录")
//            accountApi.login("00662719", API.md5("177949"))
            accountApi.createVisitor(API.getDeviceID(this), "test_${Random().nextInt(10000) + 1}")
        }

        mHandler.postDelayed(object : Runnable {
            override fun run() {
                if (contact == null) {
                    mHandler.postDelayed(this, 300)
                } else {
                    Log.d(TAG, "获得用户id:${contact.id}, 是游客吗? ${contact.visitor}")
                    channelApi.focusPublicChannel(contact.id, CHANNEL_TYPE, channelId, "")
                }
            }
        }, 300)
    }

    override fun onPubChannelFocusResult(uid: Int, reason: Int) {
        if (uid > 0) {
            Log.d(TAG, "关注到语音频道, uid = $uid")
            sessionApi.sessionCall(contact.id, CHANNEL_TYPE, channelId)
        } else {
            runOnUiThread{
                mConnectSessionProgressDialog.dismiss()
                Toast.makeText(this, "关注语音频道失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSessionEstablished(selfUserId: Int, type: Int, sessionId: Int) {
        if (selfUserId > 0) {
            Log.d(TAG, "会话连接成功")
            runOnUiThread {
                if (livePlayImageView.isClickable) {
                    Toast.makeText(this, "进入语音频道成功", Toast.LENGTH_SHORT).show()
                    mConnectSessionProgressDialog.dismiss()

                } else {
                    livePlayImageView.isClickable = true
                }
                isEnterSession = true
                Glide.with(this).load(R.drawable.live_btn_pause).into(livePlayImageView)
            }
        } else {
            runOnUiThread{
                livePlayImageView.isClickable = true
                Toast.makeText(this, "进入语音频道失败", Toast.LENGTH_SHORT).show()
                mConnectSessionProgressDialog.dismiss()
            }
        }
    }

    override fun onLogin(uid: Int, result: Int, uProfile: UserProfile?) {
        Log.d(TAG, "onLogin: uid = [$uid], result = [$result], uProfile = [$uProfile]")
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
        mSocket.off(EVENT_NEW_MESSAGE)
        mSocket.off(EVENT_CHANNEL_INFO)
        if (contact != null) {
            Log.d(TAG,"logout(): 退出会话")
            accountApi.logout(contact.id)
        }
    }

    private fun initSocketAndGetLiveInfo() {
        mConnectSocketProgressDialog.setTitle("")
        mConnectSocketProgressDialog.setMessage("获取直播信息中...")
        mConnectSocketProgressDialog.show()


        mSocket.on(EVENT_CHANNEL_INFO) { args ->
            val json = (args[0] as JSONObject).toString()
            Log.d(TAG, "on $EVENT_CHANNEL_INFO : $json")
            if (isGoodJson(json)) {
                val liveInfo = Gson().fromJson(json, LiveInfo::class.java)
                runOnUiThread {
                    this@LiveActivity.liveInfo = liveInfo
                    userInfo = liveInfo.userInfo
                    userInfo.type = numberOfType(userInfo.userType)
                    anchorInfo = liveInfo.anchorInfo
                    Glide.with(this)
                            .load(anchorInfo.headPic)
                            .placeholder(R.drawable.ic_user_default_head)
                            .error(R.drawable.ic_user_default_head)
                            .into(anchor_head_pic)
                    anchorInfoRelativeLayout.visibility = View.VISIBLE
                    anchor_nick_name.text = anchorInfo.nickName

                    //直播时长的逻辑
                    fun countLiveTime() {
                        var liveTime = System.currentTimeMillis() - liveInfo.liveStartTime
                        mHandler.postDelayed({
                            liveTime += 1000
                            liveTimeTextView.text = getFormatTime(liveTime)
                            countLiveTime()
                        }, 1000)
                    }
                    countLiveTime()

                    if(liveInfo.isConcerned) {
                        concernedTextView.isClickable = true
                        concernedTextView.background = getDrawable(R.drawable.bg_live_concerned_down)
                        concernedTextView.text = "已关注"
                    }

                    if (liveInfo.isSubscribed) {
                        subscribedImageView.isClickable = true
                        Glide.with(this).load(R.drawable.ic_live_star_channel_down).into(subscribedImageView)
                    }

                    onLineNumberTextView.text = "${liveInfo.onLineNumber}人在听"
                    mConnectSocketProgressDialog.dismiss()
                }
            } else
                runOnUiThread { Toast.makeText(this, "服务器内部错误", Toast.LENGTH_SHORT).show() }
        }
        mSocket.on(EVENT_NEW_MESSAGE) { args ->
            val json = (args[0] as JSONObject).toString()
            Log.d(TAG, "on $EVENT_NEW_MESSAGE : $json")
            if (isGoodJson(json)) {
                val jsonObject = JSONObject(json)
                val liveMessage = LiveMessage(Type.RECEIVED, jsonObject.getString("message"), jsonObject.getString("headPic"), jsonObject.getString("nickName"))
                runOnUiThread{ insertNewMessage(liveMessage) }
            } else
                runOnUiThread { Toast.makeText(this, "服务器内部错误", Toast.LENGTH_SHORT).show() }
        }
        mSocket.on(EVENT_CONCERN_REPLY) { args ->
            val jsonObject = args[0] as JSONObject
            Log.d(TAG, "on $EVENT_CONCERN_REPLY: $jsonObject")
            runOnUiThread {
                concernedTextView.isClickable = true
                if (jsonObject.getInt("operateResult") == 0) {
                    when (jsonObject.getInt("operateType")) {
                        0 -> {
                            concernedTextView.text = "已关注"
                            concernedTextView.background = getDrawable(R.drawable.bg_live_concerned_down)
                            liveInfo.isConcerned = true
                            Toast.makeText(this, "关注成功", Toast.LENGTH_SHORT).show()
                        }
                        1 -> {
                            concernedTextView.text = "＋关注 "
                            concernedTextView.background = getDrawable(R.drawable.bg_live_concerned_normal)
                            liveInfo.isConcerned = false
                            Toast.makeText(this, "取消关注成功", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
        mSocket.on(EVENT_SUBSCRIBE_REPLY) { args->
            val jsonObject = args[0] as JSONObject
            Log.d(TAG, "on $EVENT_SUBSCRIBE_REPLY: $jsonObject")
            runOnUiThread{
                subscribedImageView.isClickable = true
                if (jsonObject.getInt("operateResult") == 0) {
                    when (jsonObject.getInt("operateType")){
                        0 -> {
                            Glide.with(this).load(R.drawable.ic_live_star_channel_down).into(subscribedImageView)
                            liveInfo.isSubscribed = true
                            Toast.makeText(this, "订阅成功", Toast.LENGTH_SHORT).show()
                        }
                        1 -> {
                            Glide.with(this).load(R.drawable.ic_live_star_channel_normal).into(subscribedImageView)
                            liveInfo.isSubscribed = false
                            Toast.makeText(this, "退订频道成功", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
        mSocket.on(EVENT_UPDATE_LISTENERS_NUMBER) { args ->
            val jsonObject = args[0] as JSONObject
            Log.d(TAG, "on $EVENT_UPDATE_LISTENERS_NUMBER: $jsonObject")
            runOnUiThread { onLineNumberTextView.text = "${jsonObject.getInt("count")}人在听" }
        }
        mSocket.connect()
        val sendJson = mapToJson(mapOf("uid" to globalUid, "channelID" to channelId))
        Log.d(TAG, "on $EVENT_USER_JOIN_CHANNEL :  $sendJson")
        mSocket.emit(EVENT_USER_JOIN_CHANNEL, sendJson)
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

        val layoutManager = LinearLayoutManager(this)
        liveMessageRecycleView.layoutManager = layoutManager
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

            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        sendMessageButton.setOnClickListener {
            val liveMessage = LiveMessage(Type.SENT, sendMessageEditText.text.toString(), userInfo.headPic, userInfo.nickName)
            insertNewMessage(liveMessage)
            val emitJson = mapToJson(mapOf("uid" to globalUid, "message" to sendMessageEditText.text.toString()))
            sendMessageEditText.setText("")
            Log.d(TAG, "on $EVENT_SEND_MESSAGE : $emitJson")
            mSocket.emit(EVENT_SEND_MESSAGE, emitJson)
        }

        livePlayImageView.setOnClickListener {
            livePlayImageView.isClickable = false
            if (isEnterSession) {
                sessionApi.sessionBye(contact.id, CHANNEL_TYPE, channelId)
            } else {
                sessionApi.sessionCall(contact.id, CHANNEL_TYPE, channelId)
            }
        }

        concernedTextView.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                    .setTitle("")
                    .setMessage( if(liveInfo.isConcerned) "确定不再关注此人?" else "关注主播, 将会收到主播的开播提醒")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定") { _, _ ->
                        concernedTextView.isClickable = false
                        val emitJson = mapToJson(mapOf("uid" to globalUid, "channelID" to channelId))
                        Log.d(TAG, "on $EVENT_CONCERN: $emitJson")
                        mSocket.emit(EVENT_CONCERN, emitJson)
                    }
                    .create()
            dialog.show()
            dialog.findViewById<TextView>(android.R.id.message).setTextColor(Color.parseColor("#b3aeae"))
        }

        subscribedImageView.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                    .setTitle("")
                    .setMessage(if (liveInfo.isSubscribed) "确定退订此频道?" else "订阅该频道, 将在第一时间获得该频道的开播提醒")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定") { _, _ ->
                        subscribedImageView.isClickable = false
                        val emitJson = mapToJson(mapOf("uid" to globalUid, "channelID" to channelId))
                        Log.d(TAG, "on $EVENT_SUBSCRIBE: $emitJson")
                        mSocket.emit(EVENT_SUBSCRIBE, emitJson)
                    }
                    .create()
            dialog.show()
            dialog.findViewById<TextView>(android.R.id.message).setTextColor(Color.parseColor("#b3aeae"))
        }
    }

    override fun onSessionReleased(selfUserId: Int, sessionType: Int, sessionId: Int) {
        Glide.with(this).load(R.drawable.live_btn_play).into(livePlayImageView)
        livePlayImageView.isClickable = true
        isEnterSession = false
    }

    private fun insertNewMessage(liveMessage: LiveMessage) {
        liveMessageList.add(liveMessage)
        liveMessageAdapter.notifyItemInserted(liveMessageList.size - 1)
        liveMessageRecycleView.scrollToPosition(liveMessageList.size - 1)
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
    }

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

    internal companion object {
        private const val TAG = "LiveActivity"

        internal const val PARAM_CHANNEL_ID = "channel_id"

        private const val CHANNEL_TYPE = 2

        internal fun actionStart(context: Context, channelId: Int) {
            val intent = Intent(context, LiveActivity::class.java)
            intent.putExtra(PARAM_CHANNEL_ID, channelId)
            context.startActivity(intent)
        }

        enum class UserType(val numberOfType: Int) {
            VISITOR(0), AUDIENCE(1), ANCHOR(2);
        }

        fun numberOfType(number: Int): UserType {
            UserType.values()
                    .filter { number == it.numberOfType }
                    .forEach { return it }
            return UserType.VISITOR
        }

        data class LiveInfo(var isConcerned: Boolean = false, var isSubscribed: Boolean = false, val anchorInfo: AnchorInfo = AnchorInfo(), val userInfo: UserInfo = UserInfo(), val onLineNumber:Int = 0, val liveStartTime: Long = 0L)

        data class AnchorInfo(val startTime: Long = 0L, val nickName: String = "", val headPic: String = "")

        data class UserInfo(val userType: Int = 0, val nickName: String = "", val headPic: String = "") {
            var type = UserType.VISITOR
        }

        private const val EVENT_USER_JOIN_CHANNEL = "userJoinChannel"
        private const val EVENT_CHANNEL_INFO = "channelInfo"
        private const val EVENT_SEND_MESSAGE = "sendMessage"
        private const val EVENT_NEW_MESSAGE = "newMessage"
        private const val EVENT_CONCERN = "concern"
        private const val EVENT_SUBSCRIBE = "subscribe"
        private const val EVENT_CONCERN_REPLY = "concernReply"
        private const val EVENT_SUBSCRIBE_REPLY = "subscribeReply"
        private const val EVENT_UPDATE_LISTENERS_NUMBER = "updateListenersNumber"
    }
}
