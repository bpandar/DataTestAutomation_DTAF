package bmo.Cipher;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

public final class CryptoUtils {
    public static final String ALGORITHM = "AES";
    public static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    public static final String KEY_VALUE = "SaccrDatabaseKey";
    public static final int KEY_LENGTH = 16;

    public static void main(String[] args) throws Exception {
        String plainText = args[0];
        String encrypted = CryptoUtils.encrypt(plainText);
        String decrypted = CryptoUtils.decrypt(encrypted);
        System.out.println("Plain text    =" + plainText);
        System.out.println("Encrypted text=" + encrypted);
        System.out.println("Decrypted text=" + decrypted);
    }

    private static javax.crypto.Cipher getCipher(int mode, byte[] keyValue) throws NoSuchAlgorithmException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {
        final javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(TRANSFORMATION);
        final SecretKeySpec key = new SecretKeySpec(keyValue, ALGORITHM);
        cipher.init(mode, key);
        return cipher;
    }

    public static String encrypt(String plainText, String keyValue)
            throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        if (null == keyValue || keyValue.isEmpty())
            keyValue = KEY_VALUE;
        byte[] properKey = new byte[16];
        Arrays.fill(properKey, (byte) 0x2E);
        System.arraycopy(keyValue.getBytes(), 0, properKey, 0,
                (keyValue.length() < KEY_LENGTH) ? keyValue.length() : KEY_LENGTH);
        javax.crypto.Cipher cipher = getCipher(javax.crypto.Cipher.ENCRYPT_MODE, properKey);
        String encryptedText = Base64.encodeBase64String(cipher.doFinal(plainText.getBytes()));
        return encryptedText;
    }

    public static String encrypt(String plainText) throws NoSuchAlgorithmException, NoSuchProviderException,
            NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return encrypt(plainText, KEY_VALUE);
    }

    public static String decrypt(String encryptedText, String keyValue)
            throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        if (null == keyValue || keyValue.isEmpty())
            keyValue = KEY_VALUE;
        byte[] properKey = new byte[16];
        Arrays.fill(properKey, (byte) 0x2E);
        System.arraycopy(keyValue.getBytes(), 0, properKey, 0,
                (keyValue.length() < KEY_LENGTH) ? keyValue.length() : KEY_LENGTH);
        javax.crypto.Cipher cipher = getCipher(Cipher.DECRYPT_MODE, properKey);
        String plainText = new String(cipher.doFinal(Base64.decodeBase64(encryptedText)));
        return plainText;
    }

    public static String decrypt(String encryptedText)
            throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        return decrypt(encryptedText, KEY_VALUE);
    }
}