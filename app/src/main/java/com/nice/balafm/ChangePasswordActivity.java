package com.nice.balafm;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nice.balafm.util.HttpUtilKt;
import com.nice.balafm.util.JsonUtilKt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {
    private EditText oldPass;
    private EditText newPass;
    private EditText affirmPass;
    private TextView errorOldPass;
    private TextView errorNewPass;
    private TextView errorAffirmPass;
    private ImageView newPassSign;
    private ImageView oldPassSign;
    private ImageView affirmPassSign;
    private View yes;
    private boolean oldFlag=false;
    private boolean newFlag=false;
    private boolean affirmFlag=false;
    private static final String REGEX_PASSWORD="^[a-zA-Z0-9]{6,20}$";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppKt.setStatusBarLightMode(this, true);
        setContentView(R.layout.activity_change_password);
        oldPass=findViewById(R.id.change_pass_old);
        oldPassSign=findViewById(R.id.change_pass_old_sign);
        errorOldPass=findViewById(R.id.change_pass_error_old);

        newPass=findViewById(R.id.change_pass_new);
        newPassSign=findViewById(R.id.change_pass_new_sign);
        errorNewPass=findViewById(R.id.change_pass_error_new);


        affirmPass=findViewById(R.id.change_pass_affirm);
        affirmPassSign=findViewById(R.id.change_pass_affirm_sign);
        errorAffirmPass=findViewById(R.id.change_pass_error_affirm);
        yes=findViewById(R.id.change_pass_yes);
        oldPass.setOnFocusChangeListener(this);
        newPass.setOnFocusChangeListener(this);
        affirmPass.setOnFocusChangeListener(this);
        affirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkForAffirmPass(newPass.getText().toString(),affirmPass.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        yes.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.change_pass_yes:
                resetPass();
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if(b) {
            switch (view.getId()){
                case R.id.change_pass_old:
                    oldPassSign.setImageDrawable(getClearIcon());
                    oldPassSign.setClickable(true);
                    oldPassSign.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            oldPass.setText("");
                        }
                    });
                    break;
                case R.id.change_pass_new:
                    newPassSign.setImageDrawable(getClearIcon());
                    newPassSign.setClickable(true);
                    newPassSign.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            newPass.setText("");
                        }
                    });
                    break;
                case R.id.change_pass_affirm:
                    affirmPassSign.setImageDrawable(getClearIcon());
                    affirmPassSign.setClickable(true);
                    affirmPassSign.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            affirmPass.setText("");
                        }
                    });
                    break;
            }
        }
        else{
                switch (view.getId()) {
                    case R.id.change_pass_old:
                        oldPassSign.setOnClickListener(null);
                        oldPassSign.setClickable(false);
                        checkForOldPass(oldPass.getText().toString());
                        break;
                    case R.id.change_pass_new:
                        newPassSign.setOnClickListener(null);
                        newPassSign.setClickable(false);
                        checkForNewPass(newPass.getText().toString());
                        if(!affirmPass.getText().equals("")){
                            checkForAffirmPass(newPass.getText().toString(), affirmPass.getText().toString());
                        }
                        break;
                    case R.id.change_pass_affirm:
                        affirmPassSign.setOnClickListener(null);
                        affirmPassSign.setClickable(false);
                        break;
                }
        }
    }
    private int getUid(){
        return 15;
    }
    private void ShowError(EditText edit)
    {
        edit.setBackgroundColor(0xff3333);
    }
    private void disableYes(){
        yes.setClickable(false);
        yes.setBackground(getResources().getDrawable(R.drawable.bg_change_pass_yes_disable));
    }
    private  void enableYes(){
        yes.setClickable(true);
        yes.setBackground(getResources().getDrawable(R.drawable.bg_change_pass_yes_enable));
    }
    private void rightNewPass(){
        this.errorNewPass.setVisibility(View.GONE);
        this.newPassSign.setImageDrawable(getRightIcon());
        newFlag=true;
        if(isAllRight()) enableYes();
        return;
    }
    private void rightOldPass() {
        this.errorOldPass.setVisibility(View.GONE);
        this.oldPassSign.setImageDrawable(getRightIcon());
        oldFlag=true;
        if(isAllRight()) enableYes();
        return;
    }
    private void rightAffirmPass() {
        this.errorAffirmPass.setVisibility(View.GONE);
        this.affirmPassSign.setImageDrawable(getRightIcon());
        affirmFlag=true;
        if(isAllRight()) enableYes();
    }
    private boolean isAllRight(){
        return affirmFlag&&oldFlag&&newFlag;
    }
    private void setErrorAffirmPass(String msg) {
        this.errorAffirmPass.setText('*'+msg);
        this.errorAffirmPass.setVisibility(View.VISIBLE);
        this.affirmPassSign.setImageDrawable(getErrorIcon());
        affirmFlag=false;
        disableYes();
        ShowError(affirmPass);
    }
    private void setErrorNewPass(String msg) {
        this.errorNewPass.setText('*'+msg);
        this.errorNewPass.setVisibility(View.VISIBLE);
        this.newPassSign.setImageDrawable(getErrorIcon());
        newFlag=false;
        disableYes();
        ShowError(newPass);
    }
    private void setErrorOldPass(String msg) {
        this.errorOldPass.setText('*'+msg);
        this.errorOldPass.setVisibility(View.VISIBLE);
        this.oldPassSign.setImageDrawable(getErrorIcon());
        oldFlag=false;
        disableYes();
        ShowError(oldPass);
    }
    private Drawable getErrorIcon()
    {
        return getResources().getDrawable(R.drawable.ic_close);
    }
    private Drawable getRightIcon() { return getResources().getDrawable(R.drawable.ic_history);}
    private Drawable getClearIcon() { return getResources().getDrawable(R.drawable.ic_hot);}
    private void checkForOldPass(String pass) {
        try {
            Log.d("ChangePasswordActivity", "checkForOldPass() called with: pass = [" + pass + "]");
            JSONObject param=new JSONObject();
            param.put("uid",getUid());
            param.put("pass",pass);
            HttpUtilKt.postJsonRequest(this, HttpUtilKt.getHOST_ADDRESS() + "/me/password/check", param.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChangePasswordActivity.this,"服务器错误",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    });
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String body=response.body().string();
                    if(JsonUtilKt.isGoodJson(body)){
                        try {
                            int res=new JSONObject(body).getInt("result");
                            boolean checkResult=(res==0);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(checkResult){
                                        rightOldPass();
                                    }else{
                                        setErrorOldPass("旧密码可能不太正确");
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ChangePasswordActivity.this,"返回结果错误",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    private void checkForNewPass(String pass){
        if(Pattern.matches(REGEX_PASSWORD,pass)){
            rightNewPass();
        }
        else{
            setErrorNewPass("输入的密码格式不正确，应为6-20位包含数字或字母的字符");
        }
    }
    private void checkForAffirmPass(String newPass,String affirmPass){
        if(newPass.equals(affirmPass)){
            rightAffirmPass();
        }
        else{
            setErrorAffirmPass("两次输入的新密码不相同");
        }
    }
    private void resetPass(){
        try {
            JSONObject param=new JSONObject();
            param.put("uid",getUid());
            param.put("newPass",newPass.getText().toString());

            HttpUtilKt.postJsonRequest(this, HttpUtilKt.getHOST_ADDRESS() + "/me/password/update", param.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChangePasswordActivity.this,"服务器出错",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String body=response.body().string();
                    if(JsonUtilKt.isGoodJson(body)){
                        try {
                            int res=new JSONObject(body).getInt("result");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(res==0){
                                        Toast.makeText(ChangePasswordActivity.this,"修改密码成功",Toast.LENGTH_SHORT).show();
                                        setResult(RESULT_OK);
                                        finish();
                                    }else{
                                        Toast.makeText(ChangePasswordActivity.this,"修改密码失败",Toast.LENGTH_LONG).show();
                                        setResult(RESULT_CANCELED);
                                        finish();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ChangePasswordActivity.this,"不是一个好json",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
