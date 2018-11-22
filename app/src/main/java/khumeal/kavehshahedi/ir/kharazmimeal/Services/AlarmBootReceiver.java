package khumeal.kavehshahedi.ir.kharazmimeal.Services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import khumeal.kavehshahedi.ir.kharazmimeal.SplashActivity;

public class AlarmBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SplashActivity.setupNotification(context);


        }
    }
}
