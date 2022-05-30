package com.example.IotCloudPlatform;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

import com.example.IotCloudPlatform.tools.LoginDataBaseHelper;
import com.example.IotCloudPlatform.tools.WebServiceHelper;


public class LoginActivity extends AppCompatActivity {
    private EditText ed_id, ed_pwd;
    private TextView tv_register;
    private Button btn_login;
    private SharedPreferences spPreference;

    // 创建数据库对象
    LoginDataBaseHelper mySqliteHelper = new LoginDataBaseHelper(LoginActivity.this, "user.db", null, 1);


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, R.string.account_toast_text2, Toast.LENGTH_SHORT).show();
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ed_id = findViewById(R.id.account_id);
        ed_pwd = findViewById(R.id.account_psw);
        tv_register = findViewById(R.id.tv_registered);
        btn_login = findViewById(R.id.btn_loginUp);
        spPreference = getSharedPreferences("loginSet", MODE_PRIVATE);
        ed_id.setText(spPreference.getString("user", ""));
        ed_pwd.setText(spPreference.getString("pwd", ""));
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUp(ed_id.getEditableText().toString(), ed_pwd.getEditableText().toString());
            }
        });
    }

    private void loginUp(String userName, String password) {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, R.string.account_toast_text1, Toast.LENGTH_SHORT).show();
            return;
        }
        // 接收查询的全部数据
        Cursor cursor = mySqliteHelper.getWritableDatabase().query("users", null, "account = ?", new String[]{userName}, null, null, null);
        if (cursor.getCount() > 0) {
            //移动到首位
            cursor.moveToFirst();
            @SuppressLint("Range") String pass2 = cursor.getString(cursor.getColumnIndex("password"));
            // 验证密码
            if (pass2.equals(password)) {
                Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();
                //把数据添加到ContentValues
                ContentValues values = new ContentValues();
                values.put("login", "1");
                // 执行更新操作
                mySqliteHelper.getWritableDatabase().update("users", values, "account = ?", new String[]{userName});

                handler.sendEmptyMessage(1);
            } else {
                handler.sendEmptyMessage(0);
            }
        }
    }
}
