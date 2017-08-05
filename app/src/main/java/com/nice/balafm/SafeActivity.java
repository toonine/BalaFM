package com.nice.balafm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SafeActivity extends Activity implements View.OnClickListener{
    private View pass;
    private View phone;
    private View mail;

    private static final int CHANGE_PASSWORD_CODE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe);
        pass=findViewById(R.id.safe_pass);
        phone=findViewById(R.id.safe_phone);
        mail=findViewById(R.id.safe_mail);

        findViewById(R.id.safe_pass_bar).setOnClickListener(this);
        findViewById(R.id.safe_phone_bar).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.safe_pass_bar:
                startChangePasswordActivity();
                break;
        }

    }

    public void startChangePasswordActivity(){
        Intent intent=new Intent(this,ChangePasswordActivity.class);
        startActivityForResult(intent,CHANGE_PASSWORD_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CHANGE_PASSWORD_CODE:
                if(resultCode==RESULT_OK){
                    Toast.makeText(this,"修改密码成功",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
