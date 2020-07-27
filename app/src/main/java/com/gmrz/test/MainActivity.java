package com.gmrz.test;

import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cer.device.HwManager;
import com.cer.device.utils.FingerprintUtil;
import com.cer.device.utils.Logger;
import com.huawei.shieldtest.R;

import java.security.KeyPairGenerator;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "gmrz";

    private TextView tvCheckSE, tvCheckFingerprintSensor, tvCheckKeyStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        boolean deviceSupportSe = isDeviceSupportSe();
        Log.wtf(TAG, "deviceSupportSe= " + deviceSupportSe);

        tvCheckSE.setText(String.format("支持SE: %s", deviceSupportSe ? "支持" : "不支持"));

        try {
            boolean hardwareDetected = FingerprintUtil.getInstance(this).isHardwareDetected();
            Log.wtf(TAG, "fingerprint sensor hardware detected: " + hardwareDetected);
            tvCheckFingerprintSensor.setText(String.format("指纹硬件: %s", hardwareDetected ? "有" : "无"));

        } catch (Exception e) {
            Log.wtf(TAG, "fingerprint sensor hardware detected: " + e.getMessage());
            tvCheckFingerprintSensor.setText(String.format("指纹硬件: %s", e.getMessage()));
        }

        try {
            boolean ret = keystoreCertCheckSupport();
            Log.wtf(TAG, "keystore generate keypair result: " + ret);
            tvCheckKeyStore.setText(String.format("秘钥对生成: %s", ret ? "通过" : "未通过"));

        } catch (Exception e) {
            Log.wtf(TAG, "keystore generate keypair exception: " + e.getMessage());
            tvCheckKeyStore.setText(String.format("秘钥对生成: %s", e.getMessage()));
        }
    }

    private void initViews() {
        tvCheckSE = findViewById(R.id.tv_check_se);
        tvCheckFingerprintSensor = findViewById(R.id.tv_check_fingerprint_sensor);
        tvCheckKeyStore = findViewById(R.id.tv_keystore_generate_keypair);
    }


    private boolean isDeviceSupportSe() {
        if (HwManager.getUkeyManager() == null) {
            Logger.e(TAG, "device is not HUAWEI");
            return false;
        }
        if (HwManager.getUKeyVersion() < 2) {
            Logger.e(TAG, "device not support HUAWEI v2 SE unit");
            return false;
        }
        return true;
    }

    private boolean keystoreCertCheckSupport() throws Exception {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            return false;

        KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");

        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(UUID.randomUUID().toString(), KeyProperties.PURPOSE_SIGN)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                .setKeySize(2048)
                .setAttestationChallenge(new byte[1])
                .setUserAuthenticationRequired(false);

        kpGenerator.initialize(builder.build());
        kpGenerator.generateKeyPair();

        return true;
    }
}