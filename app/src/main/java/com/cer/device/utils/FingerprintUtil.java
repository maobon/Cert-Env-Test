package com.cer.device.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;


public class FingerprintUtil {

    private static final String TAG = "FingerprintUtils";

    private static FingerprintUtil instance;
    private FingerprintManager fingerprintManager;

    public static FingerprintUtil getInstance(Context context) throws Exception {
        if (instance == null) {
            instance = new FingerprintUtil(context);
        }
        return instance;
    }

    private FingerprintUtil(Context context) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                Class.forName("android.hardware.fingerprint.FingerprintManager");
            } catch (Exception e) {
                throw new Exception("can not find class of android.hardware.fingerprint.FingerprintManager");
            }

            if (context.checkSelfPermission(Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
                fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            } else
                throw new Exception("fingerprint permission not granted");

        } else
            throw new Exception("device android version below Android 7.0 Nougat can not support cert");
    }

    public boolean isHardwareDetected() {
        if (fingerprintManager != null)
            return fingerprintManager.isHardwareDetected();

        return false;
    }

}
