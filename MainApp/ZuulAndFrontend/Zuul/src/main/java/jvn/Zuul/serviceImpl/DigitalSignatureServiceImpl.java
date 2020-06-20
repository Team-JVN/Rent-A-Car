package jvn.Zuul.serviceImpl;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Service
public class DigitalSignatureServiceImpl {

    @Value("${KEY_STORE_PATH:tls/certs/zuul/keystore/zuul.keystore.p12}")
    private String keyStorePath;

    @Value("${PASSWORD:zuul_pass}")
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

    private PrivateKey getPrivateKey(KeyStore keyStore, String alias, String keyPassword) {
        char[] keyPassArray = keyPassword.toCharArray();
        try {
            return (PrivateKey) keyStore.getKey(alias, keyPassArray);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
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

    public byte[] encrypt(byte[] messageBytes) {
        KeyStore keyStore = getKeyStore(keyStorePath, password);
        PrivateKey privateKey = getPrivateKey(keyStore, alias, password);
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

}
