package com.lfsolutions.paymentslibrary.paytm.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class CryptoUtils {
    public static String getHashFromSHA(String value) throws Exception {
        return byteArray2Hex(MessageDigest.getInstance(EncryptConstants.ALGTHM_TYPE_HASH_SHA_256).digest(value.getBytes()));
    }

    private static String byteArray2Hex(byte[] hash) {
        Formatter formatter = new Formatter();
        int length = hash.length;
        for (int i = 0; i < length; i++) {
            formatter.format("%02x", new Object[]{Byte.valueOf(hash[i])});
        }
        return formatter.toString();
    }

    public static String generateRandomString(int length) {
        StringBuffer sb = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            sb.append("9876543210ZYXWVUTSRQPONMLKJIHGFEDCBAabcdefghijklmnopqrstuvwxyz!@#$&_".charAt((int) (Math.random() * ((double) "9876543210ZYXWVUTSRQPONMLKJIHGFEDCBAabcdefghijklmnopqrstuvwxyz!@#$&_".length()))));
        }
        return sb.toString();
    }

    public static String getSHA256(String value) throws SecurityException {
        try {
            return byteArray2Hex(MessageDigest.getInstance(EncryptConstants.ALGTHM_TYPE_HASH_SHA_256).digest(value.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(e.getMessage(), (Exception) e);
        }
    }

    public static String getLastNChars(String inputString, int subStringLength) {
        if (inputString == null || inputString.length() <= 0) {
            return "";
        }
        int length = inputString.length();
        if (length <= subStringLength) {
            return inputString;
        }
        return inputString.substring(length - subStringLength);
    }
}
