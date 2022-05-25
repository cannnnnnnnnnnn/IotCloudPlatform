package com.example.IotCloudPlatform;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.IotCloudPlatform.tools.LoginDataBaseHelper;
import com.example.IotCloudPlatform.tools.WebServiceHelper;


public class RegisterActivity extends AppCompatActivity {
    private Button registered_button;
    private EditText id_edit, psw_edit, phone_edit, email_edit;
    private String id, psw, phone, sex, email, result;
    private RadioGroup radioGroup;

    // 创建数据库对象
    LoginDataBaseHelper mySqliteHelper = new LoginDataBaseHelper(RegisterActivity.this, "user.db", null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registered_button = findViewById(R.id.btn_registered);
        id_edit = findViewById(R.id.registered_id);
        psw_edit = findViewById(R.id.registered_psw);
        phone_edit = findViewById(R.id.registered_phone);
        email_edit = findViewById(R.id.registered_phone);
        radioGroup = findViewById(R.id.rg_sex);
        ((TextView) findViewById(R.id.tv_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.finishAfterTransition(RegisterActivity.this);
            }
        });
        // 按钮监听事件
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rg_male:
                        sex = "男";
                        break;
                    case R.id.rg_female:
                        sex = "女";
                        break;
                }
            }
        });
        // 注册按钮
        registered_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = id_edit.getText().toString().trim();
                psw = psw_edit.getText().toString().trim();
                phone = phone_edit.getText().toString().trim();
                email = email_edit.getText().toString().trim();
                if (id.equals("") && psw.equals("")) {
                    Toast.makeText(RegisterActivity.this, R.string.account_toast_text1, Toast.LENGTH_SHORT).show();
                } else {
                    //把数据添加到ContentValues
                    ContentValues values = new ContentValues();
                    values.put("account", id);
                    values.put("password", psw);
                    values.put("phone", phone);
                    values.put("email", email);
                    values.put("sex", sex);
                    //添加数据到数据库
                    long index = mySqliteHelper.getWritableDatabase().insert("users", null, values);

                    if (index > 0) {
                        Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "注册失败！", Toast.LENGTH_SHORT).show();
                    }

                    // 清空数据
                    id_edit.setText("");
                    psw_edit.setText("");
                    phone_edit.setText("");
                    email_edit.setText("");
                    // 清除选中
                    radioGroup.clearCheck();
                }
            }
        });
    }
}
