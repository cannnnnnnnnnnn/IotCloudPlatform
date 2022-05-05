package com.example.IotCloudPlatform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.IotCloudPlatform.tools.CloudHelper;
import com.example.IotCloudPlatform.tools.SmartFactoryApplication;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // 温度、湿度、光照强度
    TextView tempView;
    TextView humView;
    TextView lightView;
    // 存储文本信息
    String tempValue;
    String humValue;
    String lightValue;
    private Spinner spVentilation;
    private Spinner spAc;
    private Spinner spLight;
    CloudHelper cloudHelper;
    SmartFactoryApplication smartFactory;

    // 创建消息线程
    final Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    lightView.setText(lightValue);
                    tempView.setText(tempValue);
                    humView.setText(humValue);
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 绑定控件信息
        tempView = findViewById(R.id.tv_temp_value);
        humView = findViewById(R.id.tv_hum_value);
        lightView = findViewById(R.id.tv_light_value);

        // 获取文本信息
        tempValue = tempView.getText().toString().trim();
        humValue = humView.getText().toString().trim();
        lightValue = lightView.getText().toString().trim();

        // 通知系统刷新menu
        invalidateOptionsMenu();

        // 资源属性对象
        Resources res = getResources();
        String[] controlStatus = res.getStringArray(R.array.control_status);

        // 数据源适配器
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, controlStatus);

        // 绑定数据源(风扇)
        spVentilation = (Spinner) findViewById(R.id.sp_ventilation_control);
        spVentilation.setAdapter(adapter);

        // 绑定数据源(风扇)
        spAc = (Spinner) findViewById(R.id.sp_air_control);
        spAc.setAdapter(adapter);

        // 绑定数据源(风扇)
        spLight = (Spinner) findViewById(R.id.sp_light_control);
        spLight.setAdapter(adapter);

        // 加载数据
        loadCloudData();

    }

    // 添加选项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //    设置图标显示(相当于图片显示开关,在menu文件里设置)
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {

            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    // activity bar点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_setting:   // activity跳转
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                // 传输信息
//                intent.putExtra("tempValue", tempValue);
//                intent.putExtra("humValue", humValue);
//                intent.putExtra("lightValue", lightValue);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    // 加载云平台数据
    private void loadCloudData() {
        smartFactory = (SmartFactoryApplication) getApplication();
        cloudHelper = new CloudHelper();
        if (smartFactory != null &&
                smartFactory.getServerAddress() != "" &&
                smartFactory.getCloudAccount() != "" &&
                smartFactory.getCloudAccountPassword() != "") {
            cloudHelper.signIn(getApplicationContext(),
                    smartFactory.getServerAddress(),
                    smartFactory.getCloudAccount(),
                    smartFactory.getCloudAccountPassword());
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (cloudHelper.getToken() != "") {
                    cloudHelper.getSensorData(getApplicationContext(),
                            smartFactory.getServerAddress(),
                            smartFactory.getProjectLabel(),
                            String.valueOf(smartFactory.getLightSensorId()),
                            new CloudHelper.DCallback() {
                                @Override
                                public void trans(String s) {
                                    lightValue = s;
                                    //Log.d("lightValue", s);

                                }
                            });
                    cloudHelper.getSensorData(getApplicationContext(),
                            smartFactory.getServerAddress(),
                            smartFactory.getProjectLabel(),
                            String.valueOf(smartFactory.getTempSensorId()),
                            new CloudHelper.DCallback() {
                                @Override
                                public void trans(String s) {
                                    tempValue = s;
                                    Log.d("tempValue", s);

                                }
                            });
                    cloudHelper.getSensorData(getApplicationContext(),
                            smartFactory.getServerAddress(),
                            smartFactory.getProjectLabel(),
                            String.valueOf(smartFactory.getHumSensorId()),
                            new CloudHelper.DCallback() {
                                @Override
                                public void trans(String s) {
                                    humValue = s;
                                    Log.d("humValue", s);

                                }
                            });
                    handler.sendEmptyMessage(1);
                }
            }
        }, 0, 5000);
    }


}