package com.example.IotCloudPlatform.tools;

import android.app.Application;

// 工具类负责存储变量
public class SmartFactoryApplication extends Application {

    // 信息获取
    private String serverAddress = "";
    private String projectLabel = "";
    private String cloudAccount = "";
    private String cloudAccountPassword = "";
    private String cameraAddress = "";
    private int tempSensorId = 0;
    private float tempThresholdValue = 0;
    private int humSensorId = 0;
    private float humThresholdValue = 0;
    private int lightSensorId = 0;
    private float lightThresholdValue = 0;
    private int bodySensorId = 0;
    private int lightControllerId = 0;
    private int ventilationControllerId = 0;
    private int airControllerId = 0;
    private boolean isLogin = false;

    // 程序的入口
    @Override
    public void onCreate() {
        super.onCreate();
    }

    // 创建每个变量的get和set方法
    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getProjectLabel() {
        return projectLabel;
    }

    public void setProjectLabel(String projectLabel) {
        this.projectLabel = projectLabel;
    }

    public String getCloudAccount() {
        return cloudAccount;
    }

    public void setCloudAccount(String cloudAccount) {
        this.cloudAccount = cloudAccount;
    }

    public String getCloudAccountPassword() {
        return cloudAccountPassword;
    }

    public void setCloudAccountPassword(String cloudAccountPassword) {
        this.cloudAccountPassword = cloudAccountPassword;
    }

    public String getCameraAddress() {
        return cameraAddress;
    }

    public void setCameraAddress(String cameraAddress) {
        this.cameraAddress = cameraAddress;
    }

    public int getTempSensorId() {
        return tempSensorId;
    }

    public void setTempSensorId(int tempSensorId) {
        this.tempSensorId = tempSensorId;
    }

    public float getTempThresholdValue() {
        return tempThresholdValue;
    }

    public void setTempThresholdValue(float tempThresholdValue) {
        this.tempThresholdValue = tempThresholdValue;
    }

    public int getHumSensorId() {
        return humSensorId;
    }

    public void setHumSensorId(int humSensorId) {
        this.humSensorId = humSensorId;
    }

    public float getHumThresholdValue() {
        return humThresholdValue;
    }

    public void setHumThresholdValue(float humThresholdValue) {
        this.humThresholdValue = humThresholdValue;
    }

    public int getLightSensorId() {
        return lightSensorId;
    }

    public void setLightSensorId(int lightSensorId) {
        this.lightSensorId = lightSensorId;
    }

    public float getLightThresholdValue() {
        return lightThresholdValue;
    }

    public void setLightThresholdValue(float lightThresholdValue) {
        this.lightThresholdValue = lightThresholdValue;
    }

    public int getBodySensorId() {
        return bodySensorId;
    }

    public void setBodySensorId(int bodySensorId) {
        this.bodySensorId = bodySensorId;
    }

    public int getLightControllerId() {
        return lightControllerId;
    }

    public void setLightControllerId(int lightControllerId) {
        this.lightControllerId = lightControllerId;
    }

    public int getVentilationControllerId() {
        return ventilationControllerId;
    }

    public void setVentilationControllerId(int ventilationControllerId) {
        this.ventilationControllerId = ventilationControllerId;
    }

    public int getAirControllerId() {
        return airControllerId;
    }

    public void setAirControllerId(int airControllerId) {
        this.airControllerId = airControllerId;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }
}