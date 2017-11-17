package com.example.xiaojin20135.myespapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.xiaojin20135.esptouch.demo_activity.EspWifiAdminSimple;
import com.example.xiaojin20135.esptouch.handler.ResultHandler;
import com.example.xiaojin20135.esptouch.task.EsptouchAsyncTask3;
import com.example.xiaojin20135.esptouch.task.__IEsptouchTask;
import com.example.xiaojin20135.esptouch.util.EspHelper;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private TextView mTvApSsid;
    private EditText mEdtApPassword;
    private Button mBtnConfirm;
    private EspWifiAdminSimple mWifiAdmin;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG,"msg.obj = " + msg.obj);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWifiAdmin = new EspWifiAdminSimple(this);
        mTvApSsid = (TextView) findViewById(R.id.tvApSssidConnected);
        mEdtApPassword = (EditText) findViewById(R.id.edtApPassword);
        mBtnConfirm = (Button) findViewById(R.id.btnConfirm);
        mBtnConfirm.setOnClickListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 显示连接到的AP的SSID
        String apSsid = mWifiAdmin.getWifiConnectedSsid();
        if (apSsid != null) {
            mTvApSsid.setText(apSsid);
        } else {
            mTvApSsid.setText("");
        }
        // 检查WIFI是否连接
        boolean isApSsidEmpty = TextUtils.isEmpty(apSsid);
        mBtnConfirm.setEnabled(!isApSsidEmpty);
    }

    @Override
    public void onClick(View v) {
        //开始连接配置
        if (v == mBtnConfirm) {
            String apSsid = mTvApSsid.getText().toString();  //SSID
            String apPassword = mEdtApPassword.getText().toString(); //登陆密码
            String apBssid = mWifiAdmin.getWifiConnectedBssid(); //MAC地址
            String taskResultCountStr = Integer.toString(1);
            if (__IEsptouchTask.DEBUG) {
                Log.d(TAG, "开始配置  mEdtApSsid = " + apSsid + ", " + " mEdtApPassword = " + apPassword);
                Log.d(TAG, "开始配置  apBssid = " + apBssid + ", " + " taskResultCountStr = " + taskResultCountStr);
            }
            EspHelper.ESP_HELPER.setActivity(MainActivity.this);
            EspHelper.ESP_HELPER.setmWifiAdmin(mWifiAdmin);
            ResultHandler.RESULT_HANDLER.setHandler(handler);
            new EsptouchAsyncTask3().execute(apSsid, apBssid, apPassword, taskResultCountStr);
        }
    }



}

