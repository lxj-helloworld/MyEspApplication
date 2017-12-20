package com.example.xiaojin20135.esptouch.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.example.xiaojin20135.esptouch.EsptouchTask;
import com.example.xiaojin20135.esptouch.IEsptouchListener;
import com.example.xiaojin20135.esptouch.IEsptouchResult;
import com.example.xiaojin20135.esptouch.IEsptouchTask;
import com.example.xiaojin20135.esptouch.demo_activity.EspWifiAdminSimple;
import com.example.xiaojin20135.esptouch.handler.ResultHandler;
import com.example.xiaojin20135.esptouch.util.EspHelper;

import java.util.List;

/**
 * Created by xiaojin20135 on 2017-11-17.
 */

public class EsptouchAsyncTask3  extends AsyncTask<String, Void, List<IEsptouchResult>> {
    private static final String TAG = "EsptouchAsyncTask3";
    private EspWifiAdminSimple mWifiAdmin;
    private Activity activity;
    private ProgressDialog mProgressDialog;
    private IEsptouchTask mEsptouchTask;
    // without the lock, if the user tap confirm and cancel quickly enough,
    // the bug will arise. the reason is follows:
    // 0. task is starting created, but not finished
    // 1. the task is cancel for the task hasn't been created, it do nothing
    // 2. task is created
    // 3. Oops, the task should be cancelled, but it is running
    private final Object mLock = new Object();

    /**
     * 在任务没有被线程池执行之前调用，运行在UI线程中；
     */
    @Override
    protected void onPreExecute() {
        Log.d(TAG,"in onPreExecute");
        //初始化值
        mWifiAdmin = EspHelper.ESP_HELPER.getmWifiAdmin();
        activity = EspHelper.ESP_HELPER.getActivity();
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setMessage("正在配置中，请等待...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                synchronized (mLock) {
                    if (__IEsptouchTask.DEBUG) {
                        Log.i(TAG, "progress dialog is canceled");
                    }
                    if (mEsptouchTask != null) {
                        mEsptouchTask.interrupt();
                    }
                }
            }
        });
        mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "请等待...", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        mProgressDialog.show();
        mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
    }

    /**
     * 在任务被线程池执行时调用，运行在子线程，在此处理比较耗时的操作；
     * @param params  泛型类型，启动任务执行需要输入的参数
     * @return
     */
    @Override
    protected List<IEsptouchResult> doInBackground(String... params) {
        Log.d(TAG,"in doInBackground");
        int taskResultCount = -1;
        synchronized (mLock) {
            // !!!NOTICE
            String apSsid = mWifiAdmin.getWifiConnectedSsidAscii(params[0]);
            String apBssid = params[1];
            String apPassword = params[2];
            String taskResultCountStr = params[3];
            Log.d(TAG,"apSsid = " + apSsid);
            Log.d(TAG,"apBssid = " + apBssid);
            Log.d(TAG,"apPassword = " + apPassword);
            Log.d(TAG,"taskResultCountStr = " + taskResultCountStr);
            taskResultCount = Integer.parseInt(taskResultCountStr);
            mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, activity);
            mEsptouchTask.setEsptouchListener(myListener);
        }
        List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(taskResultCount);
        return resultList;
    }

    /**
     * 线程池执行任务结束时调用，回调给UI主线程结果，
     * @param result
     */
    @Override
    protected void onPostExecute(List<IEsptouchResult> result) {
        Log.d(TAG,"in onPostExecute");
        mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
        mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText("确认");
        IEsptouchResult firstResult = result.get(0);
        // check whether the task is cancelled and no results received
        if (!firstResult.isCancelled()) {
            int count = 0;
            // max results to be displayed, if it is more than maxDisplayCount, just show the count of redundant ones
            final int maxDisplayCount = 5;
            // the task received some results including cancelled while executing before receiving enough results
            if (firstResult.isSuc()) {
                StringBuilder sb = new StringBuilder();
                for (IEsptouchResult resultInList : result) {
                    sb.append("配置成功, bssid = " + resultInList.getBssid() + ",InetAddress = " + resultInList.getInetAddress().getHostAddress()  + "\n");
                    count++;
                    if (count >= maxDisplayCount) {
                        break;
                    }
                }
                if (count < result.size()) {
                    sb.append("\nthere's " + (result.size() - count) + " more result(s) without showing\n");
                }
                mProgressDialog.setMessage(sb.toString());
                mProgressDialog.dismiss();
            } else {
                mProgressDialog.setMessage("设置失败！");
            }
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        Log.d(TAG,"in onProgressUpdate");
    }

    @Override
    protected void onCancelled(List<IEsptouchResult> iEsptouchResults) {
        super.onCancelled(iEsptouchResults);
        Log.d(TAG,"in onCancelled iEsptouchResults");
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d(TAG,"in onCancelled");
    }

    private IEsptouchListener myListener = new IEsptouchListener() {
        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result) {
            ResultHandler.RESULT_HANDLER.sendMessage(EspHelper.ESP_SUCCESS,result.getBssid()+";;"+result.getInetAddress());
        }
    };
}
