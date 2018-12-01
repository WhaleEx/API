package com.whaleex.api.client.util;

import com.whaleex.api.client.util.crypto.digest.Sha256;
import com.whaleex.api.client.util.crypto.ec.EcDsa;
import com.whaleex.api.client.util.crypto.ec.EcSignature;
import com.whaleex.api.client.util.crypto.ec.EosPrivateKey;

public class Ecc {

    public static String Sign(byte[] data,String privateKey){
        EosPrivateKey eosPrivateKey = new EosPrivateKey(privateKey);
        EcSignature signature = EcDsa.sign(Sha256.from(data), eosPrivateKey);
        return signature.toString();
    }

    public static String Sign(String data,String privateKey){
        EosPrivateKey eosPrivateKey = new EosPrivateKey(privateKey);
        EcSignature signature = EcDsa.sign(Sha256.from(data.getBytes()), eosPrivateKey);
        return signature.toString();
    }
}
