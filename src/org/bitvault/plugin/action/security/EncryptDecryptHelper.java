package org.bitvault.plugin.action.security;

import org.bouncycastle.jce.spec.IESParameterSpec;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;

/**
 * Class works as helper for the encryption class
 */

class EncryptDecryptHelper {

    final private static String SYMALGORITHM = "AES/CBC/PKCS5Padding";
    final private static int ITERATION_COUNT = 65536;
    final private static int KEY_LENGTH = 256;
    final private static byte[] SALT = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };

    // IV - 16 bytes static
    final private static byte[] ivBytes = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03,
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };
    final private static String ASYMALGORITHM = "ECIES";
    final private static String ECurve = "secp256k1";
    private final static byte[] derivation = Hex.decode("404122232425262728292a2b2c2d2e2f");
    private final static byte[] encoding = Hex.decode("303132333435363738393a3b3c3d3e3f");
    private final static int MACKeySize = 128;
    private static final AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);

    /**
     * Generate Symmetric Keys
     *
     * @param Password
     * @return
     * @throws Exception
     */
    public static SecretKey genSymmetricKey(final String Password) throws Exception {
        final SecretKeyFactory efactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        final KeySpec espec = new PBEKeySpec(Password.toCharArray(), SALT, ITERATION_COUNT, KEY_LENGTH);
        final SecretKey etmp = efactory.generateSecret(espec);
        return new SecretKeySpec(etmp.getEncoded(), "AES");
    }

    /**
     * Get EC Public Key from BTC public key
     *
     * @param pubKeyHex
     * @return
     * @throws Exception
     */
    public static PublicKey getBTCPublicKey(final String pubKeyHex) throws Exception {
        final byte[] keyRaw = Hex.decode(pubKeyHex);
        final BigInteger xInt = new BigInteger(1, Arrays.copyOfRange(keyRaw, 1, 33));
        final BigInteger yInt = new BigInteger(1, Arrays.copyOfRange(keyRaw, 33, 65));
        final ECPoint pubPoint = new ECPoint(xInt, yInt);
        final AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC", "BC");
        parameters.init(new ECGenParameterSpec(ECurve));
        final ECParameterSpec ecParameters = parameters.getParameterSpec(ECParameterSpec.class);
        final ECPublicKeySpec pubSpec = new ECPublicKeySpec(pubPoint, ecParameters);
        final KeyFactory kf = KeyFactory.getInstance("EC", "BC");
        return kf.generatePublic(pubSpec);
    }

    /**
     * Get EC Private Key from BTC private key
     *
     * @param privKeyHex
     * @return
     * @throws Exception
     */
    public static PrivateKey getBTCPrivateKey(final String privKeyHex) throws Exception {
        final byte[] keyRaw = Hex.decode(privKeyHex);
        final BigInteger privInt = new BigInteger(1, keyRaw);
        final AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC", "BC");
        parameters.init(new ECGenParameterSpec(ECurve));
        final ECParameterSpec ecParameters = parameters.getParameterSpec(ECParameterSpec.class);
        final ECPrivateKeySpec privSpec = new ECPrivateKeySpec(privInt, ecParameters);
        final KeyFactory kf = KeyFactory.getInstance("EC", "BC");
        return kf.generatePrivate(privSpec);
    }

    /**
     * Generate Session Key - AES
     *
     * @return
     * @throws Exception
     */
    public static Key genSessionKey() throws Exception {
        final KeyGenerator SessionGenerator = KeyGenerator.getInstance("AES", "BC");
        SessionGenerator.init(256);
        return SessionGenerator.generateKey();
    }

    /**
     * Convert String to Session Key - AES
     *
     * @param StrKey
     * @return
     */
    public static Key convertStringToKey(final String StrKey) {
        final byte[] keyRaw = Hex.decode(StrKey);
        return new SecretKeySpec(keyRaw, 0, keyRaw.length, "AES");
    }

    /**
     * Symmetric Encryption - AES
     *
     * @param input
     * @param length
     * @param SymKey
     * @return
     * @throws Exception
     */
    public static byte[][] symEncryption(final byte[] input, final int length, final Key SymKey) throws Exception {
        final Cipher ecipher = Cipher.getInstance(SYMALGORITHM, "BC");
        ecipher.init(Cipher.ENCRYPT_MODE, SymKey, ivSpec);
        final byte[] cipherText = new byte[ecipher.getOutputSize(length)];
        int ctLength = ecipher.update(input, 0, length, cipherText, 0);
        ctLength += ecipher.doFinal(cipherText, ctLength);
        final byte[][] result = new byte[2][];
        result[0] = cipherText;
        result[1] = BigInteger.valueOf(ctLength).toByteArray();
        return result;
    }

    /***
     * Symmetric Decryption - AES
     *
     * @param cipherText
     * @param length
     * @param SymKey
     * @return
     * @throws Exception
     */
    public static byte[][] symDecryption(final byte[] cipherText, final int length, final Key SymKey) throws Exception {
        final Cipher dcipher = Cipher.getInstance(SYMALGORITHM, "BC");
        dcipher.init(Cipher.DECRYPT_MODE, SymKey, ivSpec);
        final byte[] plainText = new byte[dcipher.getOutputSize(length)];
        int ptLength = dcipher.update(cipherText, 0, length, plainText, 0);
        ptLength += dcipher.doFinal(plainText, ptLength);
        final byte[][] result = new byte[2][];
        result[0] = plainText;
        result[1] = BigInteger.valueOf(ptLength).toByteArray();
        return result;
    }

    /**
     * Asymmetric Encryption - ECIES
     *
     * @param input
     * @param PubKey
     * @return
     * @throws Exception
     */
    public static byte[] asymEncryption(final byte[] input, final Key PubKey) throws Exception {
        byte[] cipherText = null;
        try {
            final Cipher ecipher = Cipher.getInstance(ASYMALGORITHM, "BC");
            ecipher.init(Cipher.ENCRYPT_MODE, PubKey);
            cipherText = ecipher.doFinal(input, 0, input.length);
        } catch (final InvalidKeyException ignored) {
        }
        return cipherText;
    }

    /**
     * Asymmetric Decryption - ECIES
     *
     * @param cipherText
     * @param PrivKey
     * @return
     * @throws Exception
     */
    public static byte[] asymDecryption(final byte[] cipherText, final Key PrivKey) throws Exception {
        final IESParameterSpec params = new IESParameterSpec(derivation, encoding, MACKeySize);
        final Cipher dcipher = Cipher.getInstance(ASYMALGORITHM, "BC");
        dcipher.init(Cipher.DECRYPT_MODE, PrivKey, params);
        return dcipher.doFinal(cipherText);
    }

}
