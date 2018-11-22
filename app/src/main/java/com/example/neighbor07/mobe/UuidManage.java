package com.example.neighbor07.mobe;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.UUID;

public class UuidManage{

    private Context mcontext=null;

    public String getUniqueID(Context applicationContext) {
        this.mcontext=applicationContext;

        final TelephonyManager tm = (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice;
        final String tmSerial;
        final String androidId;

        if (ActivityCompat.checkSelfPermission(mcontext, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return null;
        }
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();

        androidId = "" + android.provider.Settings.Secure.getString(applicationContext.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();

        Log.d("단말기고유번호","단말기고유번호 = "+ deviceUuid);

        return ((MainActivity)mcontext).getUniqueID(applicationContext);
    }
}
