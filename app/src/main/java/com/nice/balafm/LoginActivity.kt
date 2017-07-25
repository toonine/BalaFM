package com.nice.balafm

import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), OnAccountListenerDefault {

    private val prefs by lazy { PreferenceManager.getDefaultSharedPreferences(this) } //因为this对象还不存在，所以需要懒加载

    //读取上次的登录信息
    private val account by lazy { prefs.getString(SAVED_ACCOUNT, "") }
    private val password by lazy { prefs.getString(SAVED_PASSWORD, "") }
    private val headPicUrl by lazy { prefs.getString(SAVED_HEAD, "") }

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
        setLoginButtonEnabled()
        loginAccountEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setLoginButtonEnabled()
                if (account != "" && loginAccountEditText.text.toString() == account) {
                    Glide.with(this@LoginActivity).load(headPicUrl).placeholder(R.drawable.head_default).error(R.drawable.head_default).into(headPicImageView)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(p0: Editable?) { }
        })

        loginPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setLoginButtonEnabled()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(p0: Editable?) { }
        })
    }

    private fun setLoginButtonEnabled() {
        if (loginAccountEditText.text.toString() == "" || loginPasswordEditText.text.toString() == "") {
            loginButton.isEnabled = false
            loginButton.background = getDrawable(R.drawable.bg_login_button_disabled)
        } else {
            loginButton.isEnabled = true
            loginButton.background = getDrawable(R.drawable.bg_login_button_enabled)
        }

//        val enabledDrawable =
//                if (loginAccountEditText.text.toString() == "" || loginPasswordEditText.text.toString() == "") {
//                    loginButton.isEnabled = false
//                    getDrawable(R.drawable.bg_login_button_disabled)
//                } else {
//                    loginButton.isEnabled = true
//                    getDrawable(R.drawable.bg_login_button_enabled)
//                }
//        loginButton.background = enabledDrawable
    }

    //    private fun addActionListener() {
//        loginButton.setOnClickListener { handleLoginAction() }
//        registerTextView.setOnClickListener { startActivity(Intent(this@LoginActivity, RegisterActivity::class.java)) }
//    }
//
//    private fun handleLoginAction() {
//        val loginAccount = loginAccountEditText.text.toString()
//        val loginPassword = loginPasswordEditText.text.toString()
//
//        fun validate(arg: String, fieldName: String) =
//                if (isEmpty(arg)) {
//                    Toast.makeText(this, "请输入$fieldName", Toast.LENGTH_SHORT).show()
//                    false
//                } else true
//
//        if (validate(loginAccount, "账号") && validate(loginPassword, "密码")) {
//            loginButton.text = "登录中..."
//            loginButton.isEnabled = false
//            accountApi!!.setOnAccountListener(this)
//            Log.d(TAG, "accountApi = $accountApi")
//            accountApi.login(loginAccount, API.md5(loginPassword))
//            //接下来会回调onLogin(int uid, int result, UserProfile uProfile)
//        }
//    }
//
//    override fun onLogin(uid: Int, result: Int, uProfile: UserProfile?) {
//        Log.d(TAG, "onLogin: uid = $uid, Result = $result")
//        runOnUiThread {
//            val loginResultMsg = when (result) {
//                0, 7 -> {
//                    setResult(RESULT_OK)
//                    finish()
//                    "登录成功"
//                }
//                2 -> {
//                    "账号密码不正确"
//                }
//                else -> {
//                    "登录出错,请稍后重试"
//                }
//            }
//            loginButton.isEnabled = true
//            loginButton.text = "登录"
//            Toast.makeText(this, loginResultMsg, Toast.LENGTH_SHORT).show()
//        }
//
//
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

//    override fun onStart() {
//        super.onStart()
//        accountApi?.setOnAccountListener(this)
//    }
//
//    override fun onStop() {
//        super.onStop()
//        accountApi?.setOnAccountListener(null)
//    }

    companion object {
        private val TAG = "LoginActivity"
        private val SAVED_ACCOUNT = "saved_account" //保存的账号
        private val SAVED_PASSWORD = "saved_password" //保存的密码
        private val SAVED_HEAD = "saved_head" //保存的头像
    }
}
