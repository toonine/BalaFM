package com.nice.balafm

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.nice.balafm.util.*
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class RegisterActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //为了适配状态栏的操作
        window.statusBarColor = Color.parseColor("#f9f9f9")
        setStatusBarLightMode(true)

        setContentView(R.layout.activity_register)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initView()
    }

    private fun initView() {
        judgeRegisterButtonEnabled()
        registerPhoneEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                judgeRegisterButtonEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        registerPassWordEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                judgeRegisterButtonEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        registerVerificationEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                judgeRegisterButtonEnabled()
            }

            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        getVerificationButton.setOnClickListener {

            //合法性验证
            when {
                registerPhoneEditText.text.toString().isBlank() -> {
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                !validatePhoneNumber(registerPhoneEditText.text.toString()) -> {
                    Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val ReqJson = mapToJson(mapOf("phone" to registerPhoneEditText.text.toString(), "type" to "register"))

            //等待请求button不可点击
            getVerificationButton.isClickable = false
            getVerificationButton.text = "请稍后..."
            getVerificationButton.setBackgroundColor(Color.parseColor("#b8b8b8"))

            postJsonRequest(URL_SEND_VERIFICATION, ReqJson, object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val RespJson = response.body()!!.string()
                    Log.d(TAG, "cookie : ${response.header("Set-Cookie", "")}")
                    if (isGoodJson(RespJson)) {
                        val jsonObject = JSONObject(RespJson)
                        if (!jsonObject.isNull("result")) {
                            val result = jsonObject.getInt("result")
                            Log.d(TAG, "onSendVerification: result = $result")
                            if (result == RESULT_SUCCESS) {
                                runOnUiThread {
                                    val timer = CountDownTimerUtils(getVerificationButton, 60000, 1000)
                                    timer.start()
                                }
                            } else runOnUiThread { Toast.makeText(this@RegisterActivity, ERROR_MESSAGE_SEND_VERIFICATION[result], Toast.LENGTH_SHORT).show() }
                        }
                    } else runOnUiThread { Toast.makeText(this@RegisterActivity, "服务器内部错误", Toast.LENGTH_SHORT).show() }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.d(TAG, "error: $e")
                    runOnUiThread {
                        getVerificationButton.isClickable = true
                        getVerificationButton.text = "获取验证码"
                        getVerificationButton.setBackgroundColor(Color.parseColor("#ff8922"))
                        Toast.makeText(this@RegisterActivity, "访问服务器出错, 请稍后重试", Toast.LENGTH_SHORT).show()
                    }
                }

            }, okhttpClient)
        }

        registerButton.setOnClickListener {

            //合法性验证
            when {
                !validatePhoneNumber(registerPhoneEditText.text.toString()) -> {
                    Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                !validatePassword(registerPassWordEditText.text.toString()) -> {
                    Toast.makeText(this, "密码必须是6-20位字母或数字", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                !validateVerificationCode(registerVerificationEditText.text.toString()) -> {
                    Toast.makeText(this, "验证码必须是6位数字", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            setRegisterButtonEnabled(false)
            registerButton.text = "注册中..."
            val ReqJson = mapToJson(mapOf("phone" to registerPhoneEditText.text.toString(), "password" to registerPassWordEditText.text.toString(), "verificationCode" to registerVerificationEditText.text.toString()))
            postJsonRequest(URL_REGISTER, ReqJson, object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val RespJson = response.body()!!.string()

                    if (isGoodJson(RespJson)) {
                        val jsonObject = JSONObject(RespJson)
                        val result = jsonObject.getInt("result")
                        if (result == RESULT_SUCCESS) {
                            val uid = jsonObject.getInt("uid")
                            setResult(Activity.RESULT_OK)
                            runOnUiThread {
                                PreferenceManager.getDefaultSharedPreferences(this@RegisterActivity).edit()
                                        .putString(LoginActivity.SAVED_ACCOUNT, registerPhoneEditText.text.toString())
                                        .putString(LoginActivity.SAVED_PASSWORD, registerPassWordEditText.text.toString())
                                        .apply()
                                globalUid = uid
                                finish()
                            }
                        } else
                            runOnUiThread {
                                setRegisterButtonEnabled(true)
                                registerButton.text = "注册并登录"
                                Toast.makeText(this@RegisterActivity, ERROR_MESSAGE_REGISTER[result], Toast.LENGTH_SHORT).show()
                            }
                    } else
                        runOnUiThread {
                            setRegisterButtonEnabled(true)
                            registerButton.text = "注册并登录"
                            Toast.makeText(this@RegisterActivity, "服务器内部错误", Toast.LENGTH_SHORT).show()
                        }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.d(TAG, "error: $e")
                    runOnUiThread {
                        setRegisterButtonEnabled(true)
                        registerButton.text = "注册并登录"
                        Toast.makeText(this@RegisterActivity, "访问服务器出错, 请稍后重试", Toast.LENGTH_SHORT).show()
                    }
                }
            }, okhttpClient)
        }
    }

    private fun judgeRegisterButtonEnabled() {
        val empty = registerPhoneEditText.text.toString().isBlank() || registerPassWordEditText.text.toString().isBlank() || registerVerificationEditText.text.toString().isBlank()
        setRegisterButtonEnabled(!empty)
    }

    private fun setRegisterButtonEnabled(enabled: Boolean) {
        if (enabled) {
            registerButton.isEnabled = true
            registerButton.background = getDrawable(R.drawable.bg_login_button_enabled)
        } else {
            registerButton.isEnabled = false
            registerButton.background = getDrawable(R.drawable.bg_login_button_disabled)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    companion object {
        private val TAG = "RegisterActivity"

        internal val URL_SEND_VERIFICATION = HOST_ADDRESS + "/sendVerificationCode"
        private val URL_REGISTER = HOST_ADDRESS + "/register"

        private val ERROR_MESSAGE_SEND_VERIFICATION = arrayOf("", "该手机号已经绑定过", "发送的手机号有误")
        private val ERROR_MESSAGE_REGISTER = arrayOf("", "验证码不正确", "手机号已经绑定过", "发送的手机号有误", "密码格式不正确", "验证码已过期")
    }
}