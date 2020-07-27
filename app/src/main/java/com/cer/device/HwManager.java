package com.cer.device;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import com.cer.device.utils.RsaUtil;
import com.cer.device.utils.UtilByte;
import com.cer.device.utils.Logger;

import org.ukey.android.manager.IUKeyManager;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.cer.device.Constants.HW_PRI_KEY_D_STR;
import static com.cer.device.Constants.HW_PRI_KEY_D_STR_HEX;
import static com.cer.device.Constants.HW_PRI_KEY_N_STR;
import static com.cer.device.Constants.HW_PRI_KEY_N_STR_HEX;
import static com.cer.device.Constants.HW_SP_ID;
import static com.cer.device.Constants.HW_SSD_AID;

/**
 * Created by zhangchao on 2018/7/4.
 */

public class HwManager {

    private static final String TAG = "HwManager";

    private static final String COMMAND_CREATE = "CREATESSDACTION";
    private static final String COMMAND_DELETE = "DELETESSDACTION";
    private static final String COMMAND_SYNC = "ESEINFOSYNC";


    public static String getPackageHash(Context mContext) {
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            Logger.e(TAG, "apkSignHashAry length:" + signatures.length);

            String[] apkSignHashAry = new String[signatures.length];
            for (int i = 0; i < signatures.length; i++) {
                try {
                    CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                    X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signatures[i].toByteArray()));
                    byte[] sign = cert.getSignature();
                    apkSignHashAry[i] = sha256(sign);
                    Logger.e(TAG, "apkSignHashAry:" + apkSignHashAry[i]);
                    return apkSignHashAry[i];
                } catch (CertificateException e) {
                    Logger.e(TAG, "compareHashcode Error !!!");
                    return null;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String sha256(byte[] data) {
        if (data == null) {
            return null;
        }
        byte[] digest;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data);
            digest = md.digest();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return bytesToString(digest);
    }

    private static String bytesToString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] chars = new char[bytes.length * 2];
        int byteValue;
        for (int j = 0; j < bytes.length; j++) {
            byteValue = bytes[j] & 0xFF;
            chars[j * 2] = hexChars[byteValue >>> 4];
            chars[j * 2 + 1] = hexChars[byteValue & 0x0F];
        }
        return new String(chars).toUpperCase(Locale.US);
    }

    public static IUKeyManager getUkeyManager() {
        try {
            Class classz = Class.forName("org.ukey.android.manager.UKeyManager");
            Method m = classz.getDeclaredMethod("getInstance");
            return (IUKeyManager) m.invoke(null);
        } catch (Exception ex) {
            Logger.e(TAG, ex.getMessage(), ex);
            return null;
        }
    }

    private static String getTimeStamp() {
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date(System.currentTimeMillis()));
    }

    public static int getSDKVersion() {
        Logger.d(TAG, "getSDKVersion");
        int result = -1;
        IUKeyManager manager = getUkeyManager();
        if (manager != null) {
            result = manager.getSDKVersion();
        }
        Logger.d(TAG, "getSDKVersion result:" + result);
        return result;
    }

    public static int getUKeyVersion() {
        Logger.d(TAG, "getUKeyVersion");
        int result = -1;
        IUKeyManager manager = getUkeyManager();
        if (manager != null) {
            result = manager.getUKeyVersion();
        }
        Logger.d(TAG, "getUKeyVersion result:" + result);
        return result;
    }

    public static int getUKeyStatus(Context mContext) {
        Logger.d(TAG, "getUKeyStatus");
        String packageName = mContext.getPackageName();
        Logger.d(TAG, "getUKeyStatus-packageName= " + packageName);

        int result = -1;
        IUKeyManager manager = getUkeyManager();
        if (manager != null) {
            result = manager.getUKeyStatus(packageName);
        }
        Logger.d(TAG, "getUKeyStatus result:" + result);
        return result;
    }

    public static void requestUKeyPermission(Context mContext, int requestCode) {
        Logger.d(TAG, "requestUKeyPermission");
        int result = -1;
        IUKeyManager manager = getUkeyManager();
        if (manager != null) {
            manager.requestUKeyPermission(mContext, requestCode);
            Logger.d(TAG, "requestUKeyPermission success");
        } else {
            Logger.d(TAG, "requestUKeyPermission failed");
        }
    }

    public static int createUKey() {
        Logger.d(TAG, "createUKey");
        int result = -1;
        IUKeyManager manager = getUkeyManager();
        if (manager != null) {
            String timeStamp = getTimeStamp();

            PrivateKey privateKey = RsaUtil.getPrivateKey(UtilByte.hex2byte(HW_PRI_KEY_N_STR_HEX), UtilByte.hex2byte(HW_PRI_KEY_D_STR_HEX));
            String signData = COMMAND_CREATE + "|" + timeStamp;
            Logger.d(TAG, "createUKey data:" + signData);
            String sign = RsaUtil.sign(signData, privateKey);
            Logger.d(TAG, "createUKey sign:" + sign);
            result = manager.createUKey(HW_SP_ID, HW_SSD_AID, sign, timeStamp);
        }
        Logger.d(TAG, "createUKey result :" + result);
        return result;
    }

    public static int deleteUKey() {
        Logger.d(TAG, "deleteUKey");
        int result = -1;
        IUKeyManager manager = getUkeyManager();
        if (manager != null) {
            String timeStamp = getTimeStamp();

            PrivateKey privateKey = RsaUtil.getPrivateKey(Base64.decode(HW_PRI_KEY_N_STR, Base64.NO_WRAP), Base64.decode(HW_PRI_KEY_D_STR, Base64.NO_WRAP));
            String signData = COMMAND_DELETE + "|" + timeStamp;
            String sign = RsaUtil.sign(signData, privateKey);
            Logger.d(TAG, "deleteUKey sign:" + sign);
            result = manager.deleteUKey(HW_SP_ID, HW_SSD_AID, sign, timeStamp);
        }
        Logger.d(TAG, "deleteUKey result :" + result);
        return result;
    }

    public static int syncUKey() {
        Logger.d(TAG, "syncUKey");
        int result = -1;
        IUKeyManager manager = getUkeyManager();
        if (manager != null) {
            String timeStamp = getTimeStamp();

            PrivateKey privateKey = RsaUtil.getPrivateKey(Base64.decode(HW_PRI_KEY_N_STR, Base64.NO_WRAP), Base64.decode(HW_PRI_KEY_D_STR, Base64.NO_WRAP));
            String signData = COMMAND_SYNC + "|" + timeStamp;

            Logger.d(TAG, "syncUKey signData:" + signData);

            String sign = RsaUtil.sign(signData, privateKey);
            Logger.d(TAG, "syncUKey sign:" + sign);
            result = manager.syncUKey(HW_SP_ID, sign, timeStamp);
        }
        Logger.d(TAG, "syncUKey result :" + result);
        return result;
    }

    public static String getUKeyID() {
        Logger.d(TAG, "getUKeyID");
        String result = null;
        IUKeyManager manager = getUkeyManager();
        if (manager != null) {
            result = manager.getUKeyID();
        }
        Logger.d(TAG, "getUKeyID result :" + result);
        return result;
    }

}
