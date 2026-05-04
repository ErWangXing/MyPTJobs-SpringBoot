package com.MyPTJobs.Class;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AESUtil {

    final String algorithm = "AES/CBC/PKCS5Padding";
//    @Value("${AES.secretKey}")
    private final String secretKey = "NzU4NWUzOTAzYTU5NzY3NzE5NzZhYTVlOWRiMWQ0MTE=";
//    @Value("${AES.ivKey}")
    private final String ivKey = "1234123412341234";

    private final String jobSeekerKey= "13ZKEVvfjBQdKWVBzJl0nFZnxsq4oR3w";
    private final String employerKey= "kUd03I812zvyupglATLvbGYvzEhi1jfuZ5eSNc9u";

    private final String adminKey= "bJaqnhOz5GJp9bmFXZwShqeYKDeOWpG13Zn2B6QK";

    public String getJobSeekerKey() {
        return jobSeekerKey;
    }
    public String getEmployerKey() {
        return employerKey;
    }

    public String getAdminKey() {
        return adminKey;
    }

    public static String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public static String decrypt(String algorithm, String cipherText, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }

    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    public static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        return secret;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static void encryptFile(String algorithm, SecretKey key, IvParameterSpec iv,
                                   File inputFile, File outputFile) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }

    public static void decryptFile(String algorithm, SecretKey key, IvParameterSpec iv,
                                   File encryptedFile, File decryptedFile) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        FileInputStream inputStream = new FileInputStream(encryptedFile);
        FileOutputStream outputStream = new FileOutputStream(decryptedFile);
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] output = cipher.doFinal();
        if (output != null) {
            outputStream.write(output);
        }
        inputStream.close();
        outputStream.close();
    }

    public static SealedObject encryptObject(String algorithm, Serializable object, SecretKey key,
                                             IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, IOException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        SealedObject sealedObject = new SealedObject(object, cipher);
        return sealedObject;
    }

    public static Serializable decryptObject(String algorithm, SealedObject sealedObject, SecretKey key,
                                             IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, ClassNotFoundException,
            BadPaddingException, IllegalBlockSizeException, IOException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        Serializable unsealObject = (Serializable) sealedObject.getObject(cipher);
        return unsealObject;
    }

    public static String encryptPasswordBased(String plainText, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return Base64.getEncoder()
                .encodeToString(cipher.doFinal(plainText.getBytes()));
    }

    public static String decryptPasswordBased(String cipherText, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return new String(cipher.doFinal(Base64.getDecoder()
                .decode(cipherText)));
    }

    public String encryptAES(String str)
            throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {

        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
//        // rebuild key using SecretKeySpec
        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
//        System.out.println(ivKey);
//        System.out.println(secretKey);
        byte[] b = ivKey.getBytes(StandardCharsets.UTF_8);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(b);
//
//
        String cipherText = AESUtil.encrypt(algorithm, str, key, ivParameterSpec);
//        String plainText = AESUtil.decrypt(algorithm, cipherText, key, ivParameterSpec);
//        System.out.println(cipherText);
//        System.out.println(plainText);
        return cipherText;

    }

    public String dencryptAES(String cipherText)
            throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {

        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        // rebuild key using SecretKeySpec
        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        byte[] b = ivKey.getBytes(StandardCharsets.UTF_8);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(b);

        String algorithm = "AES/CBC/PKCS5Padding";
//        String cipherText = AESUtil.encrypt(algorithm, input, key, ivParameterSpec);
        String plainText = AESUtil.decrypt(algorithm, cipherText, key, ivParameterSpec);
//        System.out.println(cipherText);
//        System.out.println(plainText);
        return plainText;

    }

}