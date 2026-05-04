package com.MyPTJobs.Class;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class test {
    public static void main(String[] args)
    throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException,
                BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {

            String input = "baeldung";
            SecretKey key = AESUtil.generateKey(128);
        System.out.println(key);
            IvParameterSpec ivParameterSpec = AESUtil.generateIv();
        System.out.println(ivParameterSpec);
            String algorithm = "AES/CBC/PKCS5Padding";
            String cipherText = AESUtil.encrypt(algorithm, input, key, ivParameterSpec);
            String plainText = AESUtil.decrypt(algorithm, cipherText, key, ivParameterSpec);
            System.out.println(cipherText);
            System.out.println(plainText);

    }
}
