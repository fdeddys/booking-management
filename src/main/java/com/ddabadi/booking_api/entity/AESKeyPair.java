package com.ddabadi.booking_api.entity;

import javax.crypto.SecretKey;
public class AESKeyPair {

    private final SecretKey key;
    private final byte[] iv;

    public AESKeyPair(SecretKey key, byte[] iv) {
        this.key = key;
        this.iv = iv;
    }

    public SecretKey getKey() {
        return key;
    }

    public byte[] getIv() {
        return iv;
    }


}
