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
    private static final int PERSON_INFO_SETTING_CODE=1;
    private static final int USER_SAFE_CODE=2;
    private static final int REPORT_QUESTIONG_CODE=3;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_person,container,false);
        name=view.findViewById(R.id.my_name);
       // icon=activity.findViewById(R.id.my_icon);
        words=view.findViewById(R.id.my_words);
        concern=view.findViewById(R.id.my_concern);
        fans=view.findViewById(R.id.my_fans);
        safeBar=view.findViewById(R.id.bar_safe);
        listenHistoryBar=view.findViewById(R.id.bar_history);
        setInfoBar=view.findViewById(R.id.set_person_info);
        adviceBar=view.findViewById(R.id.bar_advice);
        subscribeBar=view.findViewById(R.id.bar_subscribe);

        adviceBar.setOnClickListener(this);
        safeBar.setOnClickListener(this);
        setInfoBar.setOnClickListener(this);
        listenHistoryBar.setOnClickListener(this);
        subscribeBar.setOnClickListener(this);

        return view;
    }
    public void init()
    {
        try {
            JSONObject param=new JSONObject();
            param.put("uid",AppKt.getGlobalUid());
            HttpUtilKt.postJsonRequest(getContext(), HttpUtilKt.getHOST_ADDRESS() + "/getUserInfo", param.toString(), new Callback() {
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
                    String body=response.body().toString();
                    if(JsonUtilKt.isGoodJson(body)){
                        try {
                            JSONObject res=new JSONObject(body);
                            JSONObject userinfo=res.getJSONObject("userInfo");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        name.setText(userinfo.getString("name"));
                                        words.setText(userinfo.getString("sign"));
                                        concern.setText("关注：" + userinfo.getString("concern"));

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
    public void set(String yourName,ImageView iconUrl,String yourWords,int concernNum,int fansNum)
    {
        if(yourName!=null) name.setText(yourName);
        if(iconUrl!=null);
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
        Toast.makeText(getActivity(),"babababab",Toast.LENGTH_SHORT).show();
        Log.d("MainActivity","setting start...");
        Intent intent=new Intent(this.getActivity(),PersonInfoActivity.class);
        startActivityForResult(intent,PERSON_INFO_SETTING_CODE);
    }

    @Override
    public void onClick(View view) {
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

                        return;
                }
                break;
            case USER_SAFE_CODE:
                break;
            case REPORT_QUESTIONG_CODE:
                Toast.makeText(getActivity(),"感谢您的建议~~",Toast.LENGTH_SHORT).show();
                break;
        }
        return;
    }
}
