package com.example.IotCloudPlatform.tools;


import android.content.Context;
import android.widget.Toast;

import cn.com.newland.nle_sdk.requestEntity.SignIn;
import cn.com.newland.nle_sdk.responseEntity.SensorInfo;
import cn.com.newland.nle_sdk.responseEntity.User;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;

// 云服务工具类
public class CloudHelper {

    // token的获取成功表明云平台登录成功
    private String token = "";

    public String getToken() {
        return CloudHelper.this.token;
    }

    public void signIn(final Context c, String address, String account, String pwd) {
        NetWorkBusiness nb = new NetWorkBusiness("", address);
        nb.signIn(new SignIn(account, pwd), new NCallBack<BaseResponseEntity<User>>(c) {
            @Override
            protected void onResponse(BaseResponseEntity<User> response) {
                if (response.getStatus() == 0) {
                    token = response.getResultObj().getAccessToken();
                } else {
                    Toast.makeText(c, response.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onOff(final Context c, String address, String prjLabel,
                      String controllerId, int state) {
        NetWorkBusiness nb = new NetWorkBusiness(token, address);
        nb.control(prjLabel, controllerId, state,
                new NCallBack<BaseResponseEntity>(c) {
                    @Override
                    protected void onResponse(BaseResponseEntity response) {
                        Toast.makeText(c, "控制器操作成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 接口
    public interface DCallback {
        void trans(String s);
    }

    // 获取传感器数据
    public void getSensorData(Context c, String address, String prjLabel,
                              String sensorId, final DCallback dCallback) {
        NetWorkBusiness nb = new NetWorkBusiness(token, address);
        nb.getSensor(prjLabel, sensorId, new NCallBack<BaseResponseEntity<SensorInfo>>(c) {
            @Override
            protected void onResponse(BaseResponseEntity<SensorInfo> arg0) {
                if (arg0 != null && arg0.getResultObj() != null && arg0.getResultObj().getValue() != null)
                    dCallback.trans(arg0.getResultObj().getValue());
            }
        });
    }

    public void signOut() {
        token = "";
    }
}
