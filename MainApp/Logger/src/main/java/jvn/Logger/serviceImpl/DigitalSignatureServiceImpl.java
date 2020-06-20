package jvn.Logger.serviceImpl;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

@Service
public class DigitalSignatureServiceImpl {

    @Value("${KEY_STORE_PATH:tls/certs/logger/keystore/logger.truststore.p12}")
    private String keyStorePath;

    @Value("${PASSWORD:logger_pass}")
    private String password;

    @Value("${ALIAS:zuul}")
    private String alias;

    public DigitalSignatureServiceImpl() {
        Security.addProvider(new BouncyCastleProvider());
    }

    private KeyStore getKeyStore(String keyStorePath, String keyStorePassword) {
        char[] keyStorePassArray = keyStorePassword.toCharArray();
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(keyStorePath), keyStorePassArray);
        } catch (CertificateException | NoSuchAlgorithmException | IOException | KeyStoreException e) {
            e.printStackTrace();
        }

        return keyStore;
    }

    private PublicKey getPublicKey(KeyStore keyStore, String alias) {
        Certificate certificate = null;
        try {
            certificate = keyStore.getCertificate(alias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return certificate.getPublicKey();
    }

    public boolean decrypt(byte[] messageBytes, byte[] encryptedMessageBytes) {
        KeyStore keyStore = getKeyStore(keyStorePath, password);
        PublicKey publicKey = getPublicKey(keyStore, alias);
        Signature signature;
        try {
            signature = Signature.getInstance("SHA256withRSAandMGF1", BouncyCastleProvider.PROVIDER_NAME);
            signature.initVerify(publicKey);
            signature.update(messageBytes);
            return signature.verify(encryptedMessageBytes);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return false;
    }

}
