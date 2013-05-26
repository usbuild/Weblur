package com.lecoding.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by usbuild on 13-5-26.
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (activeNetworkInfo == null && mobNetInfo == null) {
            Toast.makeText(context, "网络已断开，请连接网络以使用功能", Toast.LENGTH_LONG).show();
            context.startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
        }
    }
}
