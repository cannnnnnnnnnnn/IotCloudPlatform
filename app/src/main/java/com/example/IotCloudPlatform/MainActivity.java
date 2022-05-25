package com.example.IotCloudPlatform;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.IotCloudPlatform.tools.CloudHelper;
import com.example.IotCloudPlatform.tools.DataBaseHelper;
import com.example.IotCloudPlatform.tools.SmartFactoryApplication;
import com.example.IotCloudPlatform.tools.WebServiceHelper;
import com.google.android.material.navigation.NavigationView;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // 温度、湿度、光照强度、人体感应
    TextView tempView;
    TextView humView;
    TextView lightView;
    TextView bodyView;
    // 存储文本信息
    String tempValue;
    String humValue;
    String lightValue;
    String bodyValue;
    // 选项框
    private Spinner spVentilation;
    private Spinner spAc;
    private Spinner spLight;
    // 图片显示对象
    private ImageView img_fs;
    private ImageView img_airs;
    private ImageView img_lt;
    // 标题框对象
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private AlertDialog.Builder dialog;

    // 工具类对象
    CloudHelper cloudHelper;
    SmartFactoryApplication smartFactory;
    DataBaseHelper databaseHelper;

    // 判断返回
    static final private int GET_CODE = 0;
    static final private int REQUEST_PERMISSION_OK = 1;

    // 格式化输出对象
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");

    // 加载动画类
    private Animation rotate;

    // 创建消息线程
    final Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    lightView.setText(lightValue);
                    tempView.setText(tempValue);
                    humView.setText(humValue);
                    bodyView.setText(bodyValue);
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
        bodyView = findViewById(R.id.tv_break_value);

        // ImageView对象(动画显示)
        img_fs = findViewById(R.id.img_fan);
        img_airs = findViewById(R.id.img_air);
        img_lt = findViewById(R.id.img_light);

        // 获取文本信息
        tempValue = tempView.getText().toString().trim();
        humValue = humView.getText().toString().trim();
        lightValue = lightView.getText().toString().trim();

        tempView.setOnClickListener((View.OnClickListener) MainActivity.this);
        humView.setOnClickListener((View.OnClickListener) MainActivity.this);
        lightView.setOnClickListener((View.OnClickListener) MainActivity.this);
        bodyView.setOnClickListener((View.OnClickListener) MainActivity.this);

        // 注销框对象的初始化
        dialog = new AlertDialog.Builder(this);

        // 导航设置
        navigationView = findViewById(R.id.main_nav);
        drawerLayout = findViewById(R.id.main_drawer);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //导航按钮
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        //同步状态
        mDrawerToggle.syncState();
        drawerLayout.addDrawerListener(mDrawerToggle);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        drawerLayout.closeDrawers();//点击侧滑菜单,关闭抽屉，隐藏菜单
                        switch (item.getItemId()) {
                            case R.id.setting_app:
                                startActivity(new Intent(MainActivity.this, UserActivity.class));
                                break;
                            case R.id.language_app:
                                startActivity(new Intent(MainActivity.this, LanguageActivity.class));
                                break;
                            case R.id.about_app:
                                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                                break;
                            case R.id.change_acc:
                                // 切换用户
                                logOut();
                                break;
                            case R.id.close_app:
                                exitDialog();
                                break;
                        }
                        return true;
                    }
                });

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

        // 绑定数据源(空调)
        spAc = (Spinner) findViewById(R.id.sp_air_control);
        spAc.setAdapter(adapter);

        // 绑定数据源(灯光)
        spLight = (Spinner) findViewById(R.id.sp_light_control);
        spLight.setAdapter(adapter);

        // 风扇动画对象绑定
        rotate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate_anim);

        // 风扇控制温度
        spVentilation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view,
                                       int position,
                                       long id) {
                Context c = getApplicationContext();
                String address = smartFactory.getServerAddress();
                String projLabel = smartFactory.getProjectLabel();
                String controllerId = smartFactory.getVentilationControllerId().trim();
                String status = spVentilation.getItemAtPosition(position).toString();
                if (cloudHelper.getToken() != "") {
                    // 绑定动画对象
                    img_fs.setAnimation(rotate);
                    switch (status) {
                        case "打开":
                            cloudHelper.onOff(c, address, projLabel, controllerId, 1);
                            // 开启动画
                            img_fs.startAnimation(rotate);
                            break;
//                        case "关闭":
//                            cloudHelper.onOff(c, address, projLabel, controllerId, 0);
//                            // 关闭动画
//                            img_fs.clearAnimation();
//                            break;
                        case "自动":
                            if (Float.parseFloat(tempValue) > smartFactory.getTempThresholdValue()) {
                                cloudHelper.onOff(c, address, projLabel, controllerId, 1);
                                // 开启动画
                                img_fs.startAnimation(rotate);
                            } else {
                                cloudHelper.onOff(c, address, projLabel, controllerId, 0);
                                // 关闭动画
                                img_fs.clearAnimation();
                            }
                            break;
                        default:
                            cloudHelper.onOff(c, address, projLabel, controllerId, 0);
                            // 关闭动画
                            img_fs.clearAnimation();
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spVentilation.setSelection(1, true);


        // 空调控制湿度
        spAc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view,
                                       int position,
                                       long id) {
                Context c = getApplicationContext();
                String address = smartFactory.getServerAddress();
                String projLabel = smartFactory.getProjectLabel();
                String controllerId = smartFactory.getAirControllerId().trim();
                String status = spAc.getItemAtPosition(position).toString();
                if (cloudHelper.getToken() != "") {
                    // 绑定空调动画对象
                    img_airs.setImageResource(R.drawable.frame_anim);
                    AnimationDrawable ad = (AnimationDrawable) img_airs.getDrawable();
                    switch (status) {
                        case "打开":
                            cloudHelper.onOff(c, address, projLabel, controllerId, 1);
                            // 开启动画
                            ad.start();
                            break;
//                        case "关闭":
//                            cloudHelper.onOff(c, address, projLabel, controllerId, 0);
//                            break;
                        case "自动":
                            if (Float.parseFloat(humValue) > smartFactory.getHumThresholdValue()) {
                                cloudHelper.onOff(c, address, projLabel, controllerId, 1);
                                // 开启动画
                                ad.start();
                            } else {
                                cloudHelper.onOff(c, address, projLabel, controllerId, 0);
                                // 关闭动画
                                ad.stop();
                                img_airs.setImageResource(R.drawable.air1);
                            }
                            break;
                        default:
                            // 关闭动画
                            ad.stop();
                            img_airs.setImageResource(R.drawable.air1);
                            cloudHelper.onOff(c, address, projLabel, controllerId, 0);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spAc.setSelection(1, true);

        // 灯泡动画显示
//        final ObjectAnimator oa = ObjectAnimator.ofFloat(img_lt, "alpha", 1f, 0f, 1f).setDuration(2000);
//        oa.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                img_lt.setImageResource(R.drawable.dp);
//            }
//        });
        Animator animator = AnimatorInflater.loadAnimator(this, R.animator.anim);   // 使用适配器类来展现动画
        animator.setTarget(img_lt);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                img_lt.setImageResource(R.drawable.dp);
            }
        });

        // 灯泡控制光照强度
        spLight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view,
                                       int position,
                                       long id) {
                Context c = getApplicationContext();
                String address = smartFactory.getServerAddress();
                String projLabel = smartFactory.getProjectLabel();
                String controllerId = smartFactory.getLightControllerId();
                String status = spLight.getItemAtPosition(position).toString();
                if (cloudHelper.getToken() != "") {
                    switch (status) {
                        case "打开":
                            cloudHelper.onOff(c, address, projLabel, controllerId, 1);
                            // 开启动画
//                            oa.start();
                            animator.start();
                            break;
//                        case "关闭":
//                            cloudHelper.onOff(c, address, projLabel, controllerId, 0);
//                            break;
                        case "自动":
                            if (Float.parseFloat(lightValue) < smartFactory.getLightThresholdValue()) {
                                cloudHelper.onOff(c, address, projLabel, controllerId, 1);
                                // 开启动画
//                                oa.start();
                                animator.start();
                            } else {
                                cloudHelper.onOff(c, address, projLabel, controllerId, 0);
                                // 关闭动画
                                img_lt.setImageResource(R.drawable.light_off);
                            }
                            break;
                        default:
                            // 关闭动画
                            img_lt.setImageResource(R.drawable.light_off);
                            cloudHelper.onOff(c, address, projLabel, controllerId, 0);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spLight.setSelection(1, true);

//        // 检查权限
//        checkPermission();

        // 初始化数据库
        databaseHelper = new DataBaseHelper(MainActivity.this);


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
                // 实现数据的保存
                Bundle bundle = new Bundle();
                bundle.putString("uid", MainActivity.this.toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, GET_CODE);
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
                                    Log.d("lightValue", s);

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
                    cloudHelper.getSensorData(getApplicationContext(),
                            smartFactory.getServerAddress(),
                            smartFactory.getProjectLabel(),
                            smartFactory.getBodySensorId(),
                            new CloudHelper.DCallback() {
                                @Override
                                public void trans(String s) {
                                    bodyValue = s;
                                    Log.d("bodyValue", s);
                                }
                            });
                    if (!((tempValue == null) && (humValue == null) && (lightValue == null)))
                        databaseHelper.insert(MainActivity.this, tempValue, humValue, lightValue);
                    if (bodyValue != null) {
                        if (bodyValue.equals("0")) {
                            bodyValue = getResources().getString(R.string.breaking_abnormal);
                            WebServiceHelper.SaveInfo(dateFormat.format(new Date()) + " " + bodyValue);
                        } else {
                            bodyValue = getResources().getString(R.string.breaking_normal);
                        }
                    }

                    handler.sendEmptyMessage(1);

                }
            }
        }, 0, 5000);
    }

    // 当前Activity返回时调用该方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_CODE) {
            if (resultCode == RESULT_OK) {
                loadCloudData();
            }
        }
    }


    // 点击监听事件
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, DataChartActivity.class);
        switch (view.getId()) {
            case R.id.tv_temp_value:
                intent.putExtra("type", "温度");
                startActivity(intent);
                break;
            case R.id.tv_hum_value:
                intent.putExtra("type", "湿度");
                startActivity(intent);
                break;
            case R.id.tv_light_value:
                intent.putExtra("type", "光照");
                startActivity(intent);
            case R.id.tv_break_value:       // 人体传感器数据跳转
                startActivity(new Intent(MainActivity.this, WarnListActivity.class));
                break;
        }
    }

    // 登出功能
    public void logOut() {
        dialog.setTitle(R.string.account_out);
        dialog.setMessage(R.string.toast_3);
        dialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton(R.string.btn_cancel, null);
        dialog.show();
    }

    // 推出软件
    public void exitDialog() {
        dialog.setTitle(R.string.btn_out);
        dialog.setMessage(R.string.toast_4);
        dialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (cloudHelper.getToken() != "")
                    cloudHelper.signOut();
                finish();
                System.exit(0);
            }
        });
        dialog.setNegativeButton(R.string.btn_cancel, null);
        dialog.show();
    }


//
//    // 权限动态申请
//    @Override
//    protected void onResume() {
//        super.onResume();
//        checkPermission();
//    }
//
//    // 检查权限状态
//    private void checkPermission() {
//        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_OK);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_PERMISSION_OK) {
//            if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "存储权限已开通", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "存储权限被拒绝", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }


}