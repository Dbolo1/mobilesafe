package com.bolo1.mobilesafe.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bolo1.mobilesafe.R;
import com.bolo1.mobilesafe.util.StreamUtil;
import com.bolo1.mobilesafe.util.ToastUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SplashActivity extends AppCompatActivity {

    private static final int UPDATE_VERSION = 100;
    private static final int ENTER_HOME = 101;
    private static final int URL_EXCEPTION = 102;
    private static final int IO_EXCEPTION = 103;
    private static final int JSON_EXCEPTION = 104;
    private TextView tv_version_name;
    private int mLocalversionCode;
    private String versionCode;
    private String versionDos;
    protected static final String tag = "SplashActivity";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    enterHome();
                    ToastUtil.show(SplashActivity.this, "进入主页");
                    break;
                case URL_EXCEPTION:
                    ToastUtil.show(SplashActivity.this, "url异常");
                    enterHome();
                    break;
                case JSON_EXCEPTION:
                    ToastUtil.show(SplashActivity.this, "JSON异常");
                    enterHome();
                    break;
                case IO_EXCEPTION:
                    ToastUtil.show(SplashActivity.this, "IO异常");
                    enterHome();
                    break;
            }
        }
    };

    /**
     * 弹出对话框提示用户更新
     */
    protected void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.e_163);
        builder.setTitle("版本更新啊！");
        builder.setMessage(versionDos);
        builder.setPositiveButton("立刻更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    //点击更新应用
                download();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        builder.show();

    }

    private void download() {

    }

    protected void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //初始化UI
        initUi();
        //获取数据
        initData();

    }

    /**
     * 更新UI
     */
    private void initUi() {
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
    }

    /**
     * 获取数据
     */
    private void initData() {
        tv_version_name.setText("当前版本号:" + getVersionName());
        mLocalversionCode = getVersionCode();
        checkUpdate();

    }

    /**
     * 检查应用是否需要更新
     */
    private void checkUpdate() {
        new Thread() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                Message msg = Message.obtain();
                try {
                    URL url = new URL("http://192.168.56.1/update2.json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(4000);
                    connection.setReadTimeout(4000);
                    if (connection.getResponseCode() == 200) {
                        InputStream is = connection.getInputStream();
                        String json = StreamUtil.StreamToString(is);
                        Log.d(tag, json);
                        JSONObject jsonobject = new JSONObject(json);
                        String versionName = jsonobject.getString("versionName");
                        versionDos = jsonobject.getString("versionDos");
                        versionCode = jsonobject.getString("versionCode");
                        String downloadUrl = jsonobject.getString("downloadUrl");
                        Log.i(tag, versionName);
                        Log.i(tag, versionDos);
                        Log.i(tag, versionCode);
                        Log.i(tag, downloadUrl);
                        if(mLocalversionCode<Integer.parseInt(versionCode)){
                            msg.what=UPDATE_VERSION;
                        }else{
                            msg.what=ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    msg.what = URL_EXCEPTION;
                    e.printStackTrace();
                } catch (IOException e) {
                    msg.what = IO_EXCEPTION;
                    e.printStackTrace();
                } catch (JSONException e) {
                    msg.what = JSON_EXCEPTION;
                    e.printStackTrace();
                } finally {
                    //让线程睡四秒中
                    long endTime = System.currentTimeMillis();
                    if ((endTime - startTime) < 4000) {
                        try {
                            sleep(4000 - (endTime - startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                }
            }
        }.start();

    }

    /**
     * 获取版本名字
     *
     * @return 应用版本号  返回null为异常
     */
    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(this.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(this.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
