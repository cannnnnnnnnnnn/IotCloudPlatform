package com.example.IotCloudPlatform;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.IotCloudPlatform.tools.LoginDataBaseHelper;

public class UserActivity extends AppCompatActivity {
    TextView ac_tv, sex_tv, phone_tv, e_tv;
    String ac, sex, phone, email, result;

    // 创建数据库对象
    LoginDataBaseHelper mySqliteHelper = new LoginDataBaseHelper(UserActivity.this, "user.db", null, 1);


    @SuppressLint("Range")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ((TextView) findViewById(R.id.account_type)).setText("普通帐号");
        ac_tv = (TextView) findViewById(R.id.account_name);
        sex_tv = (TextView) findViewById(R.id.sex);
        phone_tv = (TextView) findViewById(R.id.phone_number);
        e_tv = (TextView) findViewById(R.id.email);

        // 数据库读取判断操作
        // 接收查询的全部数据
        Cursor cursor = mySqliteHelper.getWritableDatabase().query("users", null, "login = ?", new String[]{"1"}, null, null, null);
        if (cursor.getCount() > 0) {
            //移动到首位
            cursor.moveToFirst();
            // 获取数据并更新
            ac_tv.setText(cursor.getString(cursor.getColumnIndex("account")));
            sex_tv.setText(cursor.getString(cursor.getColumnIndex("sex")));
            phone_tv.setText(cursor.getString(cursor.getColumnIndex("phone")));
            e_tv.setText(cursor.getString(cursor.getColumnIndex("email")));
            ((TextView)findViewById(R.id.place)).setText("同大公寓");
            ((TextView)findViewById(R.id.ic)).setText("Action");
            ((TextView)findViewById(R.id.ot)).setText("Action");
        }
        setListener();
    }

    public void setListener() {
        ((TextView) findViewById(R.id.tv_back)).setOnClickListener(new View.OnClickListener
                () {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(UserActivity.this);
            }
        });
    }
}