package com.lfsolutions.paymentslibrary.paytm.crypto;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


//public class AesEncryption implements Encryption {
//
//    private final byte[] ivParamBytes = {64, 64, 64, 64, 38, 38, 38, 38, 35, 35, 35, 35, 36, 36, 36, 36};
//
//    public String encrypt(String toEncrypt, String key) throws Exception {
//        Cipher cipher = Cipher.getInstance(EncryptConstants.ALGTHM_CBC_PAD_AES);
//        cipher.init(1, new SecretKeySpec(key.getBytes(), EncryptConstants.ALGTHM_TYPE_AES), new IvParameterSpec(this.ivParamBytes));
//        return Base64Utils.encode(cipher.doFinal(toEncrypt.getBytes()));
//    }
//
//    public String decrypt(String toDecrypt, String key) throws Exception {
//        Cipher cipher = Cipher.getInstance(EncryptConstants.ALGTHM_CBC_PAD_AES);
//        cipher.init(2, new SecretKeySpec(key.getBytes(), EncryptConstants.ALGTHM_TYPE_AES), new IvParameterSpec(this.ivParamBytes));
//        return new String(cipher.doFinal(Base64Utils.decode(toDecrypt)));
//    }
//}
