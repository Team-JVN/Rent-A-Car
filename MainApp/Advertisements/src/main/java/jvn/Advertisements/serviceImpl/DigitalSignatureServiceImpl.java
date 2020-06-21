package jvn.Advertisements.serviceImpl;

import jvn.Advertisements.service.DigitalSignatureService;
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

    @Value("${KEYSTORE:tls/certs/advertisements/keystore/advertisements.keystore.p12}")
    private String keyStorePath;

    @Value("${KEYSTORE_PASSWORD:advertisements_pass}")
    private String keyStorePassword;

    @Value("${KEYSTORE_ALIAS:advertisements}")
    private String alias;

    @Value("${TRUSTSTORE:tls/certs/advertisements/keystore/advertisements.truststore.p12}")
    private String trustStorePath;

    @Value("${TRUSTSTORE_PASSWORD:advertisements_pass}")
    private String trustStorePassword;

    public DigitalSignatureServiceImpl() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public byte[] encrypt(byte[] messageBytes) {
        KeyStore keyStore = getKeyStore(keyStorePath, keyStorePassword);
        PrivateKey privateKey = getPrivateKey(keyStore, alias, keyStorePassword);
        Signature signature;
        try {
            signature = Signature.getInstance("SHA256withRSAandMGF1", BouncyCastleProvider.PROVIDER_NAME);
            signature.initSign(privateKey);
            signature.update(messageBytes);
            return signature.sign();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean decrypt(String alias, byte[] messageBytes, byte[] encryptedMessageBytes) {
        KeyStore trustStore = getKeyStore(trustStorePath, trustStorePassword);
        PublicKey publicKey = getPublicKey(trustStore, alias);
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

    private PrivateKey getPrivateKey(KeyStore keyStore, String alias, String keyPassword) {
        char[] keyPassArray = keyPassword.toCharArray();
        try {
            return (PrivateKey) keyStore.getKey(alias, keyPassArray);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PublicKey getPublicKey(KeyStore trustStore, String alias) {
        PublicKey publicKey = null;
        try {
            Certificate certificate = trustStore.getCertificate(alias);
            publicKey = certificate.getPublicKey();
        } catch (KeyStoreException | NullPointerException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

}
