package com.bolo1.mobilesafe.util;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by 菠萝 on 2017/7/3.
 */

public class ToastUtil  {
    public static void show(Context cxt, String msg) {
        Toast.makeText(cxt,msg,Toast.LENGTH_LONG).show();
    }
}
