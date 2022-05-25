package com.example.IotCloudPlatform;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity2 extends AppCompatActivity {

    private AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        dialog = new AlertDialog.Builder(this);

        dialog.setTitle("注销");
        dialog.setMessage("此操作将会退出当前账号！");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent();
                intent.setClass(MainActivity2.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.show();
    }
}