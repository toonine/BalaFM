package com.nice.balafm

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.nice.balafm.util.*
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity(), OnAccountListenerDefault {

    //发送同一个cookie的client
    private var okhttpClient = OkHttpClient.Builder()
            .cookieJar(object : CookieJar {
                private val cookieStore = HashMap<String, List<Cookie>>()

                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookieStore.put(url.host(), cookies)
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    val cookies = cookieStore[url.host()]
                    return cookies ?: ArrayList<Cookie>()
                }
            })
            .build()

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(this) } //因为this对象还不存在，所以需要懒加载

    //读取上次的登录信息
    private val account by lazy { prefs.getString(SAVED_ACCOUNT, "") }
    private val password by lazy { prefs.getString(SAVED_PASSWORD, "") }
    private val headPicUrl by lazy { prefs.getString(SAVED_HEAD, "") }

    private val mProgressDialog by lazy { ProgressDialog(this) }

    //登录方式
    private enum class LoginMode(private val description: String, internal val uri: String, internal val json_key: String, internal val errMessage: List<String>) {
        PASSWORD("使用密码登录", "/login", "password", listOf("", "账号不存在", "密码不正确")), CODE("忘记密码?", "/login/ByCode", "verificationCode", listOf("", "账号不存在", "验证码不正确", "验证码已过期"));

        override fun toString(): String {
            return description
        }
    }

    private var loginMode = LoginMode.PASSWORD

    private val URL_LOGIN
        get() = HOST_ADDRESS + loginMode.uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //为了适配状态栏的操作
        window.statusBarColor = Color.parseColor("#f9f9f9")
        setStatusBarLightMode(true)
        setContentView(R.layout.activity_login)

        //toolBar的一些操作
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        initView()

        //addActionListener()
    }

    private fun initView() {

        //加载缓存数据
        if (headPicUrl == "") Glide.with(this).load(R.drawable.head_default).into(headPicImageView) else Glide.with(this).load(headPicUrl).placeholder(R.drawable.head_default).error(R.drawable.head_default).into(headPicImageView)
        loginAccountEditText.setText(account)
        loginPasswordEditText.setText(password)
        judgeLoginButtonEnabled()

        addLayoutListener(loginActivityLinearLayout, loginButton)//使键盘弹出时不遮挡登录按钮

        loginAccountEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                judgeLoginButtonEnabled()
                if (account != "" && loginAccountEditText.text.toString() == account) {
                    Glide.with(this@LoginActivity).load(headPicUrl).placeholder(R.drawable.head_default).error(R.drawable.head_default).into(headPicImageView)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {}
        })
        loginPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                judgeLoginButtonEnabled()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {}
        })
        loginVerificationEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                judgeLoginButtonEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        registerTextView.setOnClickListener { startActivityForResult(Intent(this, RegisterActivity::class.java), REQUEST_CODE_REGISTER) }
        changeModeTextView.setOnClickListener {
            changeModeTextView.text = loginMode.toString()
            when (loginMode) {
                LoginMode.PASSWORD -> {
                    loginMode = LoginMode.CODE
                    passwordLinearLayout.visibility = View.GONE
                    verificationLinearLayout.visibility = View.VISIBLE

                }
                LoginMode.CODE -> {
                    loginMode = LoginMode.PASSWORD
                    verificationLinearLayout.visibility = View.GONE
                    passwordLinearLayout.visibility = View.VISIBLE
                    loginVerificationEditText.setText("")
                }
            }
            judgeLoginButtonEnabled()
        }

        getVerificationButton.setOnClickListener {
            //合法性验证
            when {
                loginAccountEditText.text.toString().isBlank() -> {
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                !validatePhoneNumber(loginAccountEditText.text.toString()) -> {
                    Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val ReqJson = mapToJson(mapOf("phone" to loginAccountEditText.text.toString(), "type" to "login"))


            fun setGetVerificationButtonEnable(enabled: Boolean) {
                if (enabled) {
                    getVerificationButton.isClickable = true
                    getVerificationButton.text = "获取验证码"
                    getVerificationButton.setBackgroundColor(Color.parseColor("#ff8922"))
                } else {
                    getVerificationButton.isClickable = false
                    getVerificationButton.text = "请稍后..."
                    getVerificationButton.setBackgroundColor(Color.parseColor("#b8b8b8"))
                }
            }

            //等待请求button不可点击
            setGetVerificationButtonEnable(false)

            postJsonRequest(RegisterActivity.URL_SEND_VERIFICATION, ReqJson, object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val RespJson = response.body()!!.string()
                    if (isGoodJson(RespJson)) {
                        val jsonObject = JSONObject(RespJson)
                        val result = jsonObject.getInt("result")
                        if (result == RESULT_SUCCESS) {
                            runOnUiThread {
                                val timer = CountDownTimerUtils(getVerificationButton, 60000, 1000)
                                timer.start()
                            }
                        } else
                            runOnUiThread {
                                setGetVerificationButtonEnable(true)
                                Toast.makeText(this@LoginActivity, ERROR_MESSAGE_SEND_VERIFICATION[result], Toast.LENGTH_SHORT).show()
                            }
                    } else
                        runOnUiThread {
                            setGetVerificationButtonEnable(true)
                            Toast.makeText(this@LoginActivity, "服务器内部错误", Toast.LENGTH_SHORT).show()
                        }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.d(TAG, "error: $e")
                    runOnUiThread {
                        setGetVerificationButtonEnable(true)
                        Toast.makeText(this@LoginActivity, "访问服务器出错, 请稍后重试", Toast.LENGTH_SHORT).show()
                    }
                }

            }, okhttpClient)
        }

        loginButton.setOnClickListener {
            when {
                !validatePhoneNumber(loginAccountEditText.text.toString()) -> {
                    Toast.makeText(this, "不是正确的手机号", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                loginMode == LoginMode.PASSWORD && !validatePassword(loginPasswordEditText.text.toString()) -> {
                    Toast.makeText(this, "密码必须是6-20位字母或数字", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                loginMode == LoginMode.CODE && !validateVerificationCode(loginVerificationEditText.text.toString()) -> {
                    Toast.makeText(this, "验证码必须是6位数字", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            mProgressDialog.setTitle("")
            mProgressDialog.setMessage("登录中...")
            mProgressDialog.setCancelable(false)
            mProgressDialog.show()

            val checkCode =
                    when (loginMode) {
                        LoginMode.PASSWORD -> loginPasswordEditText.text.toString()
                        LoginMode.CODE -> loginVerificationEditText.text.toString()
                    }
            val reqJson = mapToJson(mapOf("phone" to loginAccountEditText.text.toString(), loginMode.json_key to checkCode))
            postJsonRequest(URL_LOGIN, reqJson, object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val respJson = response.body()!!.string()
                    if (isGoodJson(respJson)) {
                        val jsonObject = JSONObject(respJson)
                        val result = jsonObject.getInt("result")
                        if (result == RESULT_SUCCESS) {
                            val uid = jsonObject.getInt("uid")
                            runOnUiThread {
                                globalUid = uid
                                prefs.edit()
                                        .putString(SAVED_ACCOUNT, loginAccountEditText.text.toString())
                                        .putString(SAVED_PASSWORD, when(loginMode) {
                                            LoginMode.PASSWORD -> loginPasswordEditText.text.toString()
                                            LoginMode.CODE -> ""
                                        })
                                        .apply()
                                mProgressDialog.dismiss()
                                finish()
                            }
                        } else
                            runOnUiThread { loadingError(loginMode.errMessage[result]) }
                    } else
                        runOnUiThread { loadingError("服务器内部错误") }
                }

                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread { loadingError("网络访问出错, 请稍后再试") }
                }

            }, okhttpClient)
        }
    }

    private fun addLayoutListener(main: View, scroll: View) {
        main.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            main.getWindowVisibleDisplayFrame(rect)
            val mainInvisibleHeight = main.rootView.height - rect.bottom
            if (mainInvisibleHeight > 100) {
                val location = intArrayOf(0, 0)
                scroll.getLocationInWindow(location)
                val scrollHeight = (location[1] + scroll.height - rect.bottom)
                main.scrollTo(0, scrollHeight)
            } else {
                main.scrollTo(0, 0)
            }
        }
    }

    private fun judgeLoginButtonEnabled() {
        when {
            loginAccountEditText.text.toString().isBlank() ||
                    (loginMode == LoginMode.PASSWORD && loginPasswordEditText.text.toString().isBlank()) ||
                    (loginMode == LoginMode.CODE && loginVerificationEditText.text.toString().isBlank()) -> {
                loginButton.isEnabled = false
                loginButton.background = getDrawable(R.drawable.bg_login_button_disabled)
            }

            else -> {
                loginButton.isEnabled = true
                loginButton.background = getDrawable(R.drawable.bg_login_button_enabled)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_REGISTER -> if (resultCode == RESULT_OK) finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    private fun loadingError(message: String): Unit {
        mProgressDialog.dismiss()
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val TAG = "LoginActivity"
        internal val SAVED_ACCOUNT = "saved_account" //保存的账号
        internal val SAVED_PASSWORD = "saved_password" //保存的密码
        private val SAVED_HEAD = "saved_head" //保存的头像

        private val ERROR_MESSAGE_SEND_VERIFICATION = arrayOf("", "手机号还未注册", "手机号不正确")

        private val REQUEST_CODE_REGISTER = 1

    }
}
