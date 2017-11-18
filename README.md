# MyEspApplication
基于官方提供的esptouch制作，方便集成；
使用步骤:

1、使用前配置：

Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:


	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	

Step 2. Add the dependency

    dependencies {
         compile 'com.github.lxj-helloworld:MyEspApplication:1.2'  
    }


2、分配权限：

    <!-- 常规权限 -->
    <uses-permission android:name="android.permission.ACCESS_SUPERUSER"/>
    <!-- 网络访问 -->
    <uses-permission android:name="android.permission.INTERNET"/>
    <!-- 访问GSM网络信息 -->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <!-- 获取当前WiFi接入状态以及WLAN热点信息 -->
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    <uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE"/>
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
    

3、调用说明：

布局文件：


    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:orientation="horizontal" >
        <TextView
            android:id="@+id/tvApSsid"
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:text="@string/tvApSsidTitle"
            android:textSize="24sp" />
        <TextView
            android:id="@+id/tvApSssidConnected"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="24sp"
            android:ems="10"
            android:maxLines="1">
        </TextView>
    </LinearLayout>
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:orientation="horizontal" >
        <TextView
            android:id="@+id/tvApPassword"
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:text="@string/tvPasswordTitle"
            android:textSize="24sp" />
        <EditText
            android:id="@+id/edtApPassword"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:ems="10"
            android:inputType="text"
            android:singleLine="true"
            android:text=""
            tools:ignore="Deprecated,LabelFor,TextFields" >
        </EditText>
    </LinearLayout>
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical" >
        <Button
            android:id="@+id/btnConfirm"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/btnConfirmTitle"
            android:textSize="24sp" />
    </LinearLayout>




Activity调用：

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
        setContentView(R.layout.activity_esp_touch_acitivity);
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
            EspHelper.ESP_HELPER.setActivity(EspTouchAcitivity.this);
            EspHelper.ESP_HELPER.setmWifiAdmin(mWifiAdmin);
            ResultHandler.RESULT_HANDLER.setHandler(handler);
            new EsptouchAsyncTask3().execute(apSsid, apBssid, apPassword, taskResultCountStr);
        }
    }



