package com.example.xiaojin20135.esptouch.util;

import android.app.Activity;

import com.example.xiaojin20135.esptouch.demo_activity.EspWifiAdminSimple;

/**
 * Created by xiaojin20135 on 2017-11-17.
 */

public enum EspHelper {
    ESP_HELPER;
    private Activity activity;
    private EspWifiAdminSimple mWifiAdmin;
    public static final int ESP_SUCCESS = 100;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public EspWifiAdminSimple getmWifiAdmin() {
        return mWifiAdmin;
    }

    public void setmWifiAdmin(EspWifiAdminSimple mWifiAdmin) {
        this.mWifiAdmin = mWifiAdmin;
    }
}
