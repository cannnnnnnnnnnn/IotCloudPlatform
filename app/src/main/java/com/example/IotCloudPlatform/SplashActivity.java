package com.example.IotCloudPlatform;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    //    Handler 是SDK中处理异步消息的核心类。主要接受子线程发送的数据， 并用此数据配合主线程更新UI
    Handler handler = new Handler();
    //    全局计时变量
    private int second = 6;
    //    是否跳过标志位
    private boolean skipping = false;
    //    声明取消变量事件
    TextView cancel;
    //    操作倒计时文本框
    TextView count;
    //    记录程序是否失去“焦点”
    private boolean wasRunning;
    //     activity运行状态
    private boolean running = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        在这里遇到一个BUG，当将findViewById方法写在TextView对象后面会导致整个程序崩溃
        cancel = findViewById(R.id.ty_cancel);
        count = findViewById(R.id.count_tv);

//        记录程序失去焦点状态
//        如果是第一次运行程序savedInstanceState为空
        if (savedInstanceState != null) {
//            获取记录信息
            second = savedInstanceState.getInt("seconds");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }
        goToMain();
//        在这里使用绑定点击监听事件(区别于onclick方法)
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                监听到点击事件
                skipping = true;
            }
        });
    }

    private void goToMain() {
//        在这里区别于postDelayed()方法
        handler.post(new Runnable() {
            @Override
            public void run() {
//                格式化字符串
                String time = String.format("%d" + "s", second);
                count.setText(time);
//                当计时到零时或者点击取消时
                if ((second == 0) || (skipping == true)) {
//                    使用intent实现MainActivity跳转
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    // 销毁此页面(在这里调整细节点)
                    finish();
                } else {
                    second--;
//                    延时一秒，this屏蔽了Runnable对象的操作对象（同：(Runnable) SplashActivity.this）
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

//    @Override
//    public void onSaveInstanceState(Bundle onSaveInstanceState) {
////        由于安卓版本的原因，所以在这里需要调用父类的onSaveInstanceState方法
//        super.onSaveInstanceState(onSaveInstanceState);
//    }

    // 存储失去焦点信息
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        // 存储信息
        outState.putInt("seconds", second);
        outState.putBoolean("wasRunning", wasRunning);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (wasRunning) {
            running = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        wasRunning = running;
        running = false;
    }
}