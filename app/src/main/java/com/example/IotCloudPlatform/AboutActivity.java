package com.example.IotCloudPlatform;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class AboutActivity extends AppCompatActivity {
    private TextView version_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
    }

    void initView() {
        version_info = (TextView) findViewById(R.id.version_id);
        version_info.setText(R.string.version);
        ((TextView) findViewById(R.id.tv_title)).setText("关于");
        //返回后退
        ((TextView) findViewById(R.id.tv_back)).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.finishAfterTransition(AboutActivity.this);
                    }
                });
    }
}