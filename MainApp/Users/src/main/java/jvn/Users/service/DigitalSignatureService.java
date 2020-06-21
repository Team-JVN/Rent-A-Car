package jvn.Users.service;

public interface DigitalSignatureService {

    byte[] encrypt(byte[] messageBytes);

    boolean decrypt(String alias, byte[] messageBytes, byte[] encryptedMessageBytes);

}
