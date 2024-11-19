package com.lfsolutions.paymentslibrary.paytm.crypto;


import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* renamed from: com.paytm.pg.merchant.PaytmChecksum */
public class PaytmChecksum {
    private static final String ALGTHM_CBC_PAD_AES = "AES/CBC/PKCS5PADDING";
    private static final String ALGTHM_PROVIDER_BC = "SunJCE";
    private static final String ALGTHM_TYPE_AES = "AES";
    private static final byte[] ivParamBytes = {64, 64, 64, 64, 38, 38, 38, 38, 35, 35, 35, 35, 36, 36, 36, 36};

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encrypt(String input, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGTHM_CBC_PAD_AES);
        cipher.init(1, new SecretKeySpec(key.getBytes(), ALGTHM_TYPE_AES), new IvParameterSpec(ivParamBytes));
        return new String(Base64.getEncoder().encode(cipher.doFinal(input.getBytes())));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String input, String key) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, IOException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGTHM_CBC_PAD_AES);
        cipher.init(2, new SecretKeySpec(key.getBytes(), ALGTHM_TYPE_AES), new IvParameterSpec(ivParamBytes));
        return new String(cipher.doFinal(Base64.getDecoder().decode(input)));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String generateSignature(TreeMap<String, String> params, String key) throws Exception {
        return generateSignature(getStringByParams(params), key);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String generateSignature(String params, String key) throws Exception {
        return calculateChecksum(params, key, generateRandomString(4));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean verifySignature(TreeMap<String, String> params, String key, String checksum) throws Exception {
        return verifySignature(getStringByParams(params), key, checksum);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean verifySignature(String params, String key, String checksum) throws Exception {
        String paytm_hash = decrypt(checksum, key);
        return paytm_hash.equals(calculateHash(params, paytm_hash.substring(paytm_hash.length() - 4)));
    }

    private static String generateRandomString(int length) {
        StringBuilder random = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            random.append("9876543210ZYXWVUTSRQPONMLKJIHGFEDCBAabcdefghijklmnopqrstuvwxyz!@#$&_".charAt((int) (Math.random() * ((double) "9876543210ZYXWVUTSRQPONMLKJIHGFEDCBAabcdefghijklmnopqrstuvwxyz!@#$&_".length()))));
        }
        return random.toString();
    }

    private static String getStringByParams(TreeMap<String, String> params) {
        if (params == null) {
            return "";
        }
        Set<String> keys = params.keySet();
        StringBuilder string = new StringBuilder();
        Iterator<String> it = new TreeSet<>(keys).iterator();
        while (it.hasNext()) {
            String paramName = it.next();
            string.append(params.get(paramName) == null ? "" : params.get(paramName)).append("|");
        }
        return string.substring(0, string.length() - 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String calculateChecksum(String params, String key, String salt) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException {
        String checksum = encrypt(calculateHash(params, salt), key);
        if (checksum != null) {
            return checksum.replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "");
        }
        return checksum;
    }

    private static String calculateHash(String params, String salt) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        Formatter hash = new Formatter();
        byte[] digest = messageDigest.digest((params + "|" + salt).getBytes());
        int length = digest.length;
        for (int i = 0; i < length; i++) {
            hash.format("%02x", new Object[]{Byte.valueOf(digest[i])});
        }
        return hash.toString().concat(salt);
    }
}