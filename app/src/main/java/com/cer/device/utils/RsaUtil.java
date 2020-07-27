package com.cer.device.utils;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by zhangchao on 2018/7/10.
 */

public class RsaUtil {
    
    private static final String TAG = "RsaUtil";
    
    public static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";
    public static final String SIGN_ALGORITHMS = "SHA256WithRSA";

    
    public static PrivateKey getPrivateKey(byte[] nBytes, byte[] dBytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            BigInteger n = new BigInteger(1, nBytes);
            BigInteger d = new BigInteger(1, dBytes);
            RSAPrivateKeySpec spec = new RSAPrivateKeySpec(n, d);
            RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(spec);
            return privateKey;
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, "encode NoSuchAlgorithmException e:" + e.getMessage());
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            Log.d(TAG, "encode InvalidKeySpecException e:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static String sign(String content, PrivateKey priKey) {
        String charset = "utf-8";
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes(charset));
            return Base64.encodeToString(signature.sign(), Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean doCheck(String content, String sign, String publicKey) {
        try {
            PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(publicKey, Base64.NO_WRAP)));
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.decode(sign, Base64.NO_WRAP));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static PublicKey getPublicKey(String rsaKey) {
        PublicKey publicKey = null;
        try {
            publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(rsaKey, Base64.NO_WRAP)));
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "NoSuchAlgorithmException");
        } catch (InvalidKeySpecException e2) {
            Log.e(TAG, "InvalidKeySpecException");
        } catch (Exception e3) {
            Log.e(TAG, "getPublicKey Exception");
        }
        return publicKey;
    }

    public static String encrypt(String content, String rsakey) {
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(rsakey)) {
            return null;
        }
        PublicKey publicKey = getPublicKey(rsakey);
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(1, publicKey);
            return Base64.encodeToString(cipher.doFinal(content.getBytes(StandardCharsets.UTF_8)), Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "NoSuchAlgorithmException");
            return null;
        } catch (NoSuchPaddingException e2) {
            Log.e(TAG, "NoSuchPaddingException");
            return null;
        } catch (Exception e3) {
            Log.e(TAG, "encrypt Exception");
            return null;
        }
    }
}
