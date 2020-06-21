package jvn.Logger.serviceImpl;

import jvn.Logger.service.DigitalSignatureService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

@Service
public class DigitalSignatureServiceImpl implements DigitalSignatureService {

    @Value("${TRUSTSTORE:tls/certs/logger/keystore/logger.truststore.p12}")
    private String keyStorePath;

    @Value("${TRUSTSTORE_PASSWORD:logger_pass}")
    private String password;

    public DigitalSignatureServiceImpl() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public boolean decrypt(String alias, byte[] messageBytes, byte[] encryptedMessageBytes) {
        KeyStore keyStore = getKeyStore(keyStorePath, password);
        PublicKey publicKey = getPublicKey(keyStore, alias);
        try {
            Signature signature = Signature.getInstance("SHA256withRSAandMGF1", BouncyCastleProvider.PROVIDER_NAME);
            signature.initVerify(publicKey);
            signature.update(messageBytes);
            return signature.verify(encryptedMessageBytes);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return false;
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
        PublicKey publicKey = null;
        try {
            Certificate certificate = keyStore.getCertificate(alias);
            publicKey = certificate.getPublicKey();
        } catch (KeyStoreException | NullPointerException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

}
