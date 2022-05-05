package com.example.IotCloudPlatform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.IotCloudPlatform.tools.SmartFactoryApplication;

public class SettingActivity extends AppCompatActivity {

    // 文本编辑框对象
    private EditText serverAddress;             //服务器地址
    private EditText projectLabel;              //云平台项目标识
    private EditText cloudAccount;              //云平台账号
    private EditText cloudAccountPassword;      //云平台密码
    private EditText cameraAddress;             //摄像头地址
    private EditText tempSensorId;              //温度设备ID
    private EditText tempThresholdValue;        //温度阈值
    private EditText humSensorId;               //湿度设备ID
    private EditText humThresholdValue;         //湿度阈值
    private EditText lightSensorId;             //光照设备ID
    private EditText lightThresholdValue;       //光照阈值
    private EditText bodySensorId;              //人体感应ID
    private EditText lightControllerId;         //光照控制设备ID
    private EditText ventilationControllerId;   //通风控制设备ID
    private EditText airControllerId;           //空调控制设备ID

    // 保存信息按钮
    private Button submit;

    // 工厂类对象
    private SmartFactoryApplication smartFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 保存信息控件
        submit = findViewById(R.id.btn_save_params);

        // 初始化对象并获取信息
        smartFactory = (SmartFactoryApplication) getApplication();

        initView();

        // 按钮监听事件
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSave();
            }
        });


    }

    //初始化参数列表
    private void initView() {
        submit = findViewById(R.id.btn_save_params);
        serverAddress = findViewById(R.id.et_server_address);
        projectLabel = findViewById(R.id.et_project_label);
        cloudAccount = findViewById(R.id.et_cloud_account);
        cloudAccountPassword = findViewById(R.id.et_cloud_account_password);
        cameraAddress = findViewById(R.id.et_camera_address);
        tempSensorId = findViewById(R.id.et_temp_sensor_id);
        tempThresholdValue = findViewById(R.id.et_temp_threshold_value);
        humSensorId = findViewById(R.id.et_hum_sensor_id);
        humThresholdValue = findViewById(R.id.et_hum_threshold_value);
        lightSensorId = findViewById(R.id.et_light_sensor_id);
        lightThresholdValue = findViewById(R.id.et_light_threshold_value);
        bodySensorId = findViewById(R.id.et_body_sensor_id);
        lightControllerId = findViewById(R.id.et_light_controller_id);
        ventilationControllerId = findViewById(R.id.et_ventilation_controller_id);
        airControllerId = findViewById(R.id.et_air_controller_id);
        //初始化温度、湿度、光照值
        Intent intent = getIntent();
        Float tempValue = intent.getFloatExtra("tempValue", 0);
        Float humValue = intent.getFloatExtra("humValue", 0);
        Float lightValue = intent.getFloatExtra("lightValue", 0);
        tempThresholdValue.setText(tempValue.toString());
        humThresholdValue.setText(humValue.toString());
        lightThresholdValue.setText(lightValue.toString());
    }

    // 信息保存
    public void onClickSave() {
        // 获取文本信息
        smartFactory.setServerAddress(serverAddress.getText().toString().trim());
        smartFactory.setProjectLabel(projectLabel.getText().toString().trim());
        smartFactory.setCloudAccount(cloudAccount.getText().toString().trim());
        smartFactory.setCloudAccountPassword(cloudAccountPassword.getText().toString().trim());
        smartFactory.setCameraAddress(cameraAddress.getText().toString().trim());
        smartFactory.setTempSensorId(tempSensorId.getText().toString().trim());
        smartFactory.setTempThresholdValue(Float.parseFloat(tempThresholdValue.getText().toString().trim()));
        smartFactory.setHumSensorId(humSensorId.getText().toString().trim());
        smartFactory.setHumThresholdValue(Float.parseFloat(humThresholdValue.getText().toString().trim()));
        smartFactory.setLightSensorId(lightSensorId.getText().toString().trim());
        smartFactory.setLightThresholdValue(Float.parseFloat(lightThresholdValue.getText().toString().trim()));
        smartFactory.setBodySensorId(bodySensorId.getText().toString().trim());
        smartFactory.setLightControllerId(lightControllerId.getText().toString().trim());
        smartFactory.setVentilationControllerId(ventilationControllerId.getText().toString().trim());
        smartFactory.setAirControllerId(airControllerId.getText().toString().trim());

        if (!checkInput(smartFactory)) {
            return;
        } else {
            SharedPreferences sharedPref = getSharedPreferences("params", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("server_address", smartFactory.getServerAddress());
            editor.putString("project_label", smartFactory.getProjectLabel());
            editor.putString("cloud_account", smartFactory.getCloudAccount());
            editor.putString("cloud_account_password", smartFactory.getCloudAccountPassword());
            editor.putString("camera_address", smartFactory.getCameraAddress());
            editor.putString("temp_sensor_id", smartFactory.getTempSensorId());
            editor.putFloat("temp_threshold_value", smartFactory.getTempThresholdValue());
            editor.putString("hum_sensor_id", smartFactory.getHumSensorId());
            editor.putFloat("hum_threshold_value", smartFactory.getTempThresholdValue());
            editor.putString("light_sensor_id", smartFactory.getLightSensorId());
            editor.putFloat("light_threshold_value", smartFactory.getLightThresholdValue());
            editor.putString("body_sensor_id", smartFactory.getBodySensorId());
            editor.putString("light_controller_id", smartFactory.getLightControllerId());
            editor.putString("ventilation_controller_id", smartFactory.getVentilationControllerId());
            editor.putString("air_controller_id", smartFactory.getAirControllerId());
            editor.commit();
            //显示保存成功提示信息
            showToast(R.string.save_params_success);
            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
//        setResult(RESULT_OK, (new Intent()).setAction(uid));
    }

    // 检查关键信息输入是否为空
    private boolean checkInput(SmartFactoryApplication smartFactory) {
        boolean result = true;
        if (smartFactory.getServerAddress().equals("")) {
            showToast(R.string.server_address_empty);
            return false;
        }
        if (smartFactory.getProjectLabel().equals("")) {
            showToast(R.string.cloud_project_empty);
            return false;
        }
        if (smartFactory.getCloudAccount().equals("")) {
            showToast(R.string.cloud_account_empty);
            return false;
        }
        if (smartFactory.getCloudAccountPassword().equals("")) {
            showToast(R.string.cloud_account_password_empty);
            return false;
        }
        if (smartFactory.getCameraAddress().equals("")) {
            showToast(R.string.camera_address_empty);
            return false;
        }
        return result;
    }

    // 信息提示框
    private void showToast(int resId) {
        Toast showToast;
        showToast = Toast.makeText(SettingActivity.this, resId, Toast.LENGTH_SHORT);
        showToast.setGravity(Gravity.CENTER, 0, 0);
        showToast.show();
    }

}