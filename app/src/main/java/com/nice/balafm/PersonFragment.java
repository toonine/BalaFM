package com.nice.balafm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nice.balafm.model.AnchorModel;
import com.nice.balafm.model.UserModel;
import com.nice.balafm.util.HttpUtilKt;
import com.nice.balafm.util.JsonUtilKt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class PersonFragment extends Fragment implements View.OnClickListener{
    private TextView name;
    private ImageView icon;
    private TextView words;
    private TextView concern;
    private TextView fans;
    private View setInfoBar;
    private View safeBar;
    private View adviceBar;
    private View subscribeBar;
    private View listenHistoryBar;
    private View loginButton;
    private View exitButton;
    private View anchorStudioButton;
    private View becomeAnchorButton;
    private View content;
    private static final int PERSON_INFO_SETTING_CODE=1;
    private static final int USER_SAFE_CODE=2;
    private static final int REPORT_QUESTIONG_CODE=3;
    private static final int PERSON_LOGIN_CODE = 4;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_person,container,false);
        name=view.findViewById(R.id.my_name);
        icon = view.findViewById(R.id.my_icon);
        words=view.findViewById(R.id.my_words);
        concern=view.findViewById(R.id.my_concern);
        fans=view.findViewById(R.id.my_fans);
        safeBar=view.findViewById(R.id.bar_safe);
        listenHistoryBar=view.findViewById(R.id.bar_history);
        setInfoBar=view.findViewById(R.id.set_person_info);
        adviceBar=view.findViewById(R.id.bar_advice);
        subscribeBar=view.findViewById(R.id.bar_subscribe);
        loginButton = view.findViewById(R.id.person_login);
        exitButton = view.findViewById(R.id.person_exit);
        anchorStudioButton = view.findViewById(R.id.person_ahchor_studio);
        becomeAnchorButton = view.findViewById(R.id.person_become_anchor);
        content = view.findViewById(R.id.person_content);

        adviceBar.setOnClickListener(this);
        safeBar.setOnClickListener(this);
        setInfoBar.setOnClickListener(this);
        listenHistoryBar.setOnClickListener(this);
        subscribeBar.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);
        init();
        return view;
    }

    public void login() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent, PERSON_LOGIN_CODE);

    }

    public void exit() {
        AppKt.setGlobalUid(0);
        login();
    }

    public boolean isLogin() {
        return AppKt.getGlobalUid() != 0;
    }
    public void init()
    {
        if (AppKt.getGlobalUid() == 0) {
            name.setText("游客");
            words.setText("这个家伙很懒，什么都没有留下");
            setDefaultHead();
            setState("none");
            return;
        }
        try {
            JSONObject param=new JSONObject();
            param.put("uid",AppKt.getGlobalUid());
            HttpUtilKt.postJsonRequest(getContext(), HttpUtilKt.getHOST_ADDRESS() + "/me/info/get", param.toString(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"服务器出错",Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String body = response.body().string();
                    Log.d("PF init:", body);
                    if(JsonUtilKt.isGoodJson(body)){
                        try {
                            JSONObject res=new JSONObject(body);
                            JSONObject userinfo=res.getJSONObject("userInfo");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        UserModel user = new UserModel();
                                        user.name = userinfo.getString("name");
                                        user.sign = userinfo.getString("sign");
                                        user.icon = userinfo.getString("icon");
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                name.setText(user.name);
                                                words.setText(user.sign);
                                                Glide.with(getContext()).load(user.icon).into(icon);
                                                PersonFragment.this.setState("user");
                                            }
                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("PersonFragmetn","In init():json can't be understand");
                            }
                        });
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setDefaultHead() {
        Glide.with(getActivity()).load(R.drawable.ic_user_default_head).into(icon);
    }

    public void setState(String root) {
        if (root.equals("none")) {
            becomeAnchorButton.setVisibility(View.VISIBLE);
            anchorStudioButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
            exitButton.setVisibility(View.GONE);
            content.setVisibility(View.GONE);
            setDefaultHead();
        } else if (root.equals("user")) {
            becomeAnchorButton.setVisibility(View.VISIBLE);
            anchorStudioButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            exitButton.setVisibility(View.VISIBLE);
            content.setVisibility(View.VISIBLE);
        } else if (root.equals("anchor")) {
            becomeAnchorButton.setVisibility(View.GONE);
            anchorStudioButton.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
            exitButton.setVisibility(View.VISIBLE);
            content.setVisibility(View.VISIBLE);
        }
    }

    public void set(String yourName, String iconUrl, String yourWords, int concernNum, int fansNum)
    {
        if(yourName!=null) name.setText(yourName);
        if (iconUrl != null) Glide.with(icon.getContext()).load(iconUrl).into(icon);
        if(yourWords!=null) words.setText(yourWords);
        if(concernNum>=0) concern.setText("关注："+concernNum);
        if(fansNum>=0) fans.setText("粉丝："+fansNum);
    }
    
    public void reportQuestion()
    {
        Intent intent=new Intent(getActivity(),GetAdvicesActivity.class);
        startActivityForResult(intent,REPORT_QUESTIONG_CODE);
    }

    public void settiing()
    {
        Log.d("MainActivity","setting start...");
        Intent intent=new Intent(this.getActivity(),PersonInfoActivity.class);
        startActivityForResult(intent,PERSON_INFO_SETTING_CODE);
    }

    @Override
    public void onClick(View view) {
        if (!isLogin()) {
            login();
            return;
        }
        switch (view.getId())
        {
            case R.id.set_person_info:
                settiing();
                break;
            case R.id.bar_safe:
                startSafeActivity();
                break;
            case R.id.bar_advice:
                startAdviceActivity();
                break;
            case R.id.bar_history:
                startListenHistoryActivity();
                break;
            case R.id.bar_subscribe:
                startOrderChannelActivity();
                break;
            case R.id.person_login:
                login();
                break;
            case R.id.person_exit:
                exit();
                break;
        }
    }
    public void startSafeActivity(){
        Intent intent=new Intent(getActivity(),SafeActivity.class);
        startActivityForResult(intent,USER_SAFE_CODE);
    }
    public void startAdviceActivity(){
        Intent intent=new Intent(getActivity(),GetAdvicesActivity.class);
        startActivityForResult(intent,REPORT_QUESTIONG_CODE);
    }
    public void startListenHistoryActivity(){
        Intent intent=new Intent(getActivity(),ListenHistoryActivity.class);
        startActivity(intent);
    }
    public void startOrderChannelActivity(){
        Intent intent=new Intent(getActivity(),OrderChannelActivity.class);
        startActivity(intent);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PERSON_INFO_SETTING_CODE:
                switch (resultCode){
                    case RESULT_OK:
                        init();
                        return;
                }
                break;
            case USER_SAFE_CODE:
                break;
            case REPORT_QUESTIONG_CODE:
                Toast.makeText(getActivity(),"感谢您的建议~~",Toast.LENGTH_SHORT).show();
                break;
            case PERSON_LOGIN_CODE:
                if (AppKt.getGlobalUid() == 0) {
                    Toast.makeText(getActivity(), "未登陆", Toast.LENGTH_LONG).show();
                }
                break;
        }
        return;
    }
}
