package jvn.Users.serviceImpl;

import jvn.Users.service.DigitalSignatureService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Service
public class DigitalSignatureServiceImpl implements DigitalSignatureService {

    @Value("${KEYSTORE:tls/certs/users/keystore/users.keystore.p12}")
    private String keyStorePath;

    @Value("${KEYSTORE_PASSWORD:users_pass}")
    private String password;

    @Value("${KEYSTORE_ALIAS:users}")
    private String alias;

    public DigitalSignatureServiceImpl() {
        Security.addProvider(new BouncyCastleProvider());
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

}
