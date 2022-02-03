package gsshop.mobile.v2.sso.utils;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

public class Encrypter {
    private static final String TAG = "Encrypter";

    private KeyStore.Entry keyEntry;
    private static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";

    public Encrypter(Context context) {
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            if (!ks.containsAlias(context.getPackageName())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    initAndroidM(context.getPackageName());
                } else {
                    initAndroidK(context);
                }
            }

            keyEntry = ks.getEntry(context.getPackageName(), null);
        } catch (Exception e) {
            Log.d(TAG, "Initialize fail", e);
        }
    }

    private void initAndroidM(String alias) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
                kpg.initialize(
                        new KeyGenParameterSpec
                                .Builder(alias,KeyProperties.PURPOSE_ENCRYPT |KeyProperties.PURPOSE_DECRYPT)
                                .setAlgorithmParameterSpec(new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4))
                                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                                .setDigests(KeyProperties.DIGEST_SHA512, KeyProperties.DIGEST_SHA384, KeyProperties.DIGEST_SHA256)
                                .setUserAuthenticationRequired(false)
                                .build()
                );
                kpg.generateKeyPair();

                Log.d(TAG, "initAndroidM - RSA Initialize");
            }
        } catch (Exception e) {
            Log.d(TAG, "이 디바이스는 관련 알고리즘을 지원하지 않음.", e);
        }
    }

    private void initAndroidK(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 25);

                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                kpg.initialize(
                        new KeyPairGeneratorSpec
                                .Builder(context)
                                .setKeySize(2048)
                                .setAlias(context.getPackageName())
                                .setSubject(new X500Principal("CN=myKey"))
                                .setSerialNumber(BigInteger.ONE)
                                .setStartDate(start.getTime())
                                .setEndDate(end.getTime())
                                .build()
                );
                kpg.generateKeyPair();

                Log.d(TAG, "initAndroidK - RSA Initialize");
            }
        } catch (Exception e) {
            Log.d(TAG, "이 디바이스는 관련 알고리즘을 지원하지 않음.", e);
        }
    }

    public String encrypt(String plain) {
        try {
            byte[] bytes = plain.getBytes("UTF-8");

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

            // Public Key로 암호화
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    ((KeyStore.PrivateKeyEntry) keyEntry).getCertificate().getPublicKey()
            );

            byte[] encryptedBytes = cipher.doFinal(bytes);

            Log.d(TAG, "Encrypted Text : " + new String(Base64.encode(encryptedBytes, Base64.DEFAULT)));

            return new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
        } catch (Exception e) {
            Log.d(TAG, "Encrypt fail", e);
            return plain;
        }
    }

    public String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

            // Private Key로 복호화
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    ((KeyStore.PrivateKeyEntry) keyEntry).getPrivateKey()
            );

            byte[] base64Bytes = encryptedText.getBytes("UTF-8");
            byte[] decryptedBytes = Base64.decode(base64Bytes, Base64.DEFAULT);

            Log.d(TAG, "Decrypted Text : " + new String(cipher.doFinal(decryptedBytes)));

            return new String(cipher.doFinal(decryptedBytes));
        } catch (Exception e) {
            Log.d(TAG, "Decrypt fail", e);
            return encryptedText;
        }
    }
}
