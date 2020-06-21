package jvn.Logger.service;

public interface DigitalSignatureService {

    boolean decrypt(String alias, byte[] messageBytes, byte[] encryptedMessageBytes);

}
