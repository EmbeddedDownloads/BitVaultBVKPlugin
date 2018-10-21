package org.bitvault.plugin.action.security;
/**
 * Class for performing encryption and decryption
 */

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.PublicKey;
import java.security.Security;


public class EncryptDecryptData {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }


    /**
     * @param rawData   raw data which needs to be encrypted
     * @param strPubKey -- public key
     * @return
     */
    private byte[] encryptData(final byte[] rawData, final String strPubKey) {

        // Generate Symmetric Keys from TXID
        final SecretKey SymKey = null;
        try {

            // Get Bitcoin Keys from receiver address
            final PublicKey pubKey = EncryptDecryptHelper.getBTCPublicKey(strPubKey);

            // Encrypt session key with public key
            return EncryptDecryptHelper.asymEncryption(rawData, pubKey);
//            byte[] EncryptedSessionKey = Base64.getEncoder().encode(kcipher);


        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /* ----------------------- RECEIVING ------------------ *//*
    *//*Method used to decrypt data for BitVault to BitVault and Desktop to Desktop case *//*
    public void decryptMessage(final String txId, final String EncryptedSessionKey, final byte[] decryptedInput, DecryptDataCallback decryptDataCallback, final MatchTransactionModel matchTransactionModel) {

        try {
            PrivateKey PrivKey = org.bitvault.plugin.action.security.EncryptDecryptHelper.getBTCPrivateKey(BTCprivKey);

            // Decrypt session key
            byte[] dSessionKey = org.bitvault.plugin.action.security.EncryptDecryptHelper.asymDecryption(Base64.decode(EncryptedSessionKey), PrivKey);
            Key RcvSessionKey = new SecretKeySpec(dSessionKey, 0, dSessionKey.length, "AES");

            byte[] input = Base64.decode(decryptedInput);

            // Decrypt message with  decrypted session key
            byte[][] mplain = org.bitvault.plugin.action.security.EncryptDecryptHelper.symDecryption(input, input.length, RcvSessionKey);
            SDKUtils.showLog("mplain", "" + mplain.toString());

            // Again Decrypt message with TxID
            SecretKey RegenSymKey = org.bitvault.plugin.action.security.EncryptDecryptHelper.genSymmetricKey(txId);
            SDKUtils.showLog("RegenSymKey", "" + RegenSymKey);

            mplain = org.bitvault.plugin.action.security.EncryptDecryptHelper.symDecryption(mplain[0], new BigInteger(mplain[1]).intValue(), RegenSymKey);

            mplain[0] = Arrays.copyOfRange(mplain[0], 0, new BigInteger(mplain[1]).intValue());

            String DecryptedMsg = new String(mplain[0]);
            SDKUtils.showLog(SDKHelper.DECRYPTION_END, "" + System.currentTimeMillis());
            SDKUtils.showLog(SDKHelper.DECRYPTION_END, DecryptedMsg);
            return DecryptedMsg;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }*/

    /**
     * Method to encrypt data on the basis of public key
     *
     * @param sourceFile
     * @return
     */
    public byte[] encryptData(final String sourceFile, final String publicKey) {
        final File file = new File(sourceFile);
        try {
            return encryptData(Files.readAllBytes(file.toPath()), publicKey.trim());
        } catch (final IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }
}

