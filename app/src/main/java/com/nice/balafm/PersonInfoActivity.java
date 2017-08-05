package com.nice.balafm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.nice.balafm.util.HttpUtilKt;
import com.nice.balafm.util.JsonUtilKt;
;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PersonInfoActivity extends AppCompatActivity implements View.OnClickListener{
    EditText name;
    TextView sex;
    TextView birth;
    EditText brief;
    CircleImageView icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);

         name=findViewById(R.id.person_setting_name);
         icon=findViewById(R.id.person_setting_icon);
         brief=findViewById(R.id.person_setting_brief);
        sex=findViewById(R.id.person_setting_sex);
        birth=findViewById(R.id.person_setting_birth);

        init();
        findViewById(R.id.person_setting_birth).setOnClickListener(this);
    }
    public void init(){
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("uid",AppKt.getGlobalUid());
            Toast.makeText(this,jsonObject.toString(),Toast.LENGTH_SHORT);
            HttpUtilKt.postJsonRequest(this, HttpUtilKt.getHOST_ADDRESS() + "/getUserInfo", jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PersonInfoActivity.this,"服务器错误",Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resBody=response.body().toString();
                    if(JsonUtilKt.isGoodJson(resBody)){
                        try {
                            JSONObject res=new JSONObject(resBody);
                            if(res.getInt("result")==0){
                                JSONObject userinfo=res.getJSONObject("userInfo");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            name.setText(userinfo.getString("name"));
                                            sex.setText(SexIntToString(userinfo.getInt("sex")));
                                            brief.setText(userinfo.getString("sign"));
                                            birth.setText(userinfo.getString("birth"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //设置头像
    }
    public void saveInfo()
    {
        try {
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("uid",AppKt.getGlobalUid());
            jsonObject.put("name",name.getText().toString());
            jsonObject.put("sex",sex.getText());
            jsonObject.put("sign",brief.getText().toString());
            jsonObject.put("birth",birth.getText());
            Toast.makeText(this,jsonObject.toString(),Toast.LENGTH_SHORT);
            HttpUtilKt.postJsonRequest(this, HttpUtilKt.getHOST_ADDRESS() + "/getUserInfo", jsonObject.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resBody=response.body().toString();
                    if(JsonUtilKt.isGoodJson(resBody)){
                        try {
                            JSONObject res=new JSONObject(resBody);
                            if(res.getInt("result")==0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PersonInfoActivity.this,"修改数据成功",Toast.LENGTH_SHORT);
                                    }
                                });
                            }
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PersonInfoActivity.this,"出错？！0.0",Toast.LENGTH_SHORT);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void bye(){
        setResult(RESULT_OK);

        finish();
    }
    public void onClick(View view)
    {
        switch (view.getId()){
            case R.id.person_setting_birth:
                bye();
                break;
        }
    }
    public String SexIntToString(int s){
        switch (s){
            case 0:return "保密";
            case 1:return "男";
            case 2:return "女";
            default:return "此次应该有BUG";
        }
    }
}
