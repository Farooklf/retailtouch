package com.lfsolutions.paymentslibrary.paytm.merchant;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class CheckSumServiceHelper {
    private static CheckSumServiceHelper checkSumServiceHelper;

    private CheckSumServiceHelper() {
    }

    public static String getVersion() {
        return "1.0";
    }

    public static CheckSumServiceHelper getCheckSumServiceHelper() {
        if (checkSumServiceHelper == null) {
            checkSumServiceHelper = new CheckSumServiceHelper();
        }
        return checkSumServiceHelper;
    }

//    public String genrateCheckSum(String Key, TreeMap<String, String> paramap) throws Exception {
//        StringBuilder response = checkSumServiceHelper.getCheckSumString(paramap);
//        try {
//            Encryption encryption = EncryptionFactory.getEncryptionInstance(EncryptConstants.ALGTHM_TYPE_AES);
//            String randomNo = CryptoUtils.generateRandomString(4);
//            response.append(randomNo);
//            String checkSumValue = encryption.encrypt(CryptoUtils.getSHA256(response.toString()).concat(randomNo), Key);
//            if (checkSumValue != null) {
//                return checkSumValue.replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "");
//            }
//            return checkSumValue;
//        } catch (SecurityException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public StringBuilder getCheckSumString(TreeMap<String, String> paramMap) throws Exception {
        Set<String> keys = paramMap.keySet();
        StringBuilder checkSumStringBuffer = new StringBuilder("");
        TreeSet<String> parameterSet = new TreeSet<>();
        for (String key : keys) {
            if (!"CHECKSUMHASH".equalsIgnoreCase(key)) {
                parameterSet.add(key);
            }
        }
        Iterator<String> it = parameterSet.iterator();
        while (it.hasNext()) {
            String value = paramMap.get(it.next());
            if (value == null || value.trim().equalsIgnoreCase("NULL")) {
                value = "";
            }
            checkSumStringBuffer.append(value.trim()).append("|");
        }
        return checkSumStringBuffer;
    }

//    public boolean verifycheckSum(String masterKey, TreeMap<String, String> paramap, String responseCheckSumString) throws Exception {
//        StringBuilder response = checkSumServiceHelper.getCheckSumString(paramap);
//        String responseCheckSumHash = EncryptionFactory.getEncryptionInstance(EncryptConstants.ALGTHM_TYPE_AES).decrypt(responseCheckSumString, masterKey);
//        String payTmCheckSumHash = calculateRequestCheckSum(getLastNChars(responseCheckSumHash, 4), response.toString());
//        if (responseCheckSumHash == null || payTmCheckSumHash == null || !responseCheckSumHash.equals(payTmCheckSumHash)) {
//            return false;
//        }
//        return true;
//    }

//    private String calculateRequestCheckSum(String randomStr, String checkSumString) throws Exception {
//        return CryptoUtils.getSHA256(checkSumString.concat(randomStr)).concat(randomStr);
//    }

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
