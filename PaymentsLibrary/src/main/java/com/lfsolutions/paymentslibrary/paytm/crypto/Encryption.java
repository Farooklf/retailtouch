package com.lfsolutions.paymentslibrary.paytm.crypto;

public interface Encryption {
    String decrypt(String str, String str2) throws Exception;

    String encrypt(String str, String str2) throws Exception;
}
